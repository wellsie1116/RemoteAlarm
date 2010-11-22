package com.wells.remotealarm.alarm;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

public class SteppedAlarm {
	
	private static final String TAG = "SteppedAlarm";
	
	private Context context;
	
	private Timer ticker;
	
	private AlarmEnvironment env;
	private AlarmState[] states;
	private int state;
	private int progress;
	private int timeInState;
	
	public NotificationManager svcNotificationManager;
	
	private SteppedAlarmStateListener listener;
	
	public class AlarmEnvironment {
		public MediaPlayer audio;
		
		public Vibrators svcVibrator;
		public AudioManager svcAudioManager;
		
		private AlarmEnvironment() {
			audio = new MediaPlayer();
			svcVibrator = new Vibrators((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE));
			svcAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		}
		
		private void neutralize() {
			svcVibrator.cancel();
			audio.stop();			
		}
	}
	
	public SteppedAlarm(Context context) {
		
		this.context = context;
	
		this.env = new AlarmEnvironment();
		states = new AlarmState[] {
			new VibrateOnlyState(env),
			new LightAudioState(env),
			new LoudAudioState(env)
		};
		
		svcNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public void setStatusListener(SteppedAlarmStateListener listener) {
		this.listener = listener;
	}
	
	public void activate() {
		timeInState = 0;
		state = 0;
		progress = 1;
		
		notifyStateChanged();
		notifyStateProgressChanged();
		
		states[state].applyState(new NullState(env));
		
		ticker = new Timer();
		ticker.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				updateProgress();
			}
		}, 0, 100);
	}
	
	public void revertProgress(int delta) {
		int newProgressTotal = (state * 100 + progress) - delta;
		int newState = newProgressTotal / 100;
		int newProgress;
		if (newState < 0) {
			newState = 0;
			newProgress = 0;
		} else {
			newProgress = newProgressTotal % 100;
		}
		
		if (newState != state) {
			timeInState = 0;
			states[newState].applyState(states[state]);
			state = newState;
			notifyStateChanged();
		}
		if (newProgress != progress || newState != state) {
			progress = newProgress;
			notifyStateProgressChanged();
		}
	}
	
	public void deactivate() {
		env.neutralize();
		
		state = 0;
		progress = 1;
		
		ticker.cancel();
		ticker = null;
		
		notifyStateChanged();
		notifyStateProgressChanged();
	}
	
	private void notifyStateChanged() {
		if (listener != null)
			listener.stateChanged(state);
	}
	
	private void notifyStateProgressChanged() {
		if (listener != null)
			listener.stateProgressChanged(progress);
	}
	
	private void updateProgress() {
		timeInState += 100;

		if (states[state].getDuration() >= 0) {
			if (timeInState >= states[state].getDuration()) {
				int newState = state + 1;
				Log.d(TAG, String.format("Changing from state %d to state %d after %dms", state, newState, timeInState));
				states[newState].applyState(states[state]);
				state = newState;
				notifyStateChanged();
				
				timeInState = 0;
				progress = 1;
				states[state].setProgress(progress);
				notifyStateProgressChanged();
			} else {
				int newProgress = (int)(timeInState * 100.0f / states[state].getDuration());
				if (newProgress != progress) {
					progress = newProgress;
					states[state].setProgress(progress);
					notifyStateProgressChanged();
				}
			}
		}
	}	

}
