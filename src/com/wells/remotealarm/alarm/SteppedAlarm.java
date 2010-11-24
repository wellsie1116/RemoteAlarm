package com.wells.remotealarm.alarm;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

public class SteppedAlarm {
	
	private static final String TAG = "SteppedAlarm";
	
	private static final int START_STATE = 1;
	private static final int START_PROGRESS = 1;
	
	private Context context;
	private Handler mHandler = new Handler();
	
	private Timer ticker;
	
	private AlarmEnvironment env;
	private AlarmState[] states;
	private int state;
	private int progress;
	private int timeInState;
	
	public NotificationManager svcNotificationManager;
	
	private List<SteppedAlarmStateListener> listeners;
	
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
			new SnoozeState(env),
			new VibrateOnlyState(env),
			new LightAudioState(env),
			new LoudAudioState(env)
		};
		
		svcNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		listeners = new LinkedList<SteppedAlarmStateListener>();
	}

	public void addStatusListener(SteppedAlarmStateListener listener) {
		this.listeners.add(listener);
		listener.stateChanged(state);
		listener.stateProgressChanged(progress);
	}
	
	public void removeStatusListener(SteppedAlarmStateListener listener) {
		this.listeners.remove(listener);
	}
	
	public void activate() {
		timeInState = 0;
		state = START_STATE;
		progress = START_PROGRESS;
		
		notifyStateChanged();
		notifyStateProgressChanged();
		
		states[state].applyState(new NullState(env));
		
		ticker = new Timer();
		ticker.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						updateProgress();
					}
				});
			}
		}, 0, 100);
	}
	
	public void revertProgress(int delta) {
		Log.d(TAG, String.format("Removing delta from progress of %d", delta));
		int newProgressTotal = (state * 100 + progress) - delta;
		int newState = newProgressTotal / 100;
		int newProgress;
		if (newProgressTotal < 0) {
			//snooze
			newState = 0;
			timeInState = 0;
			newProgress = START_PROGRESS;
		} else {
			newProgress = newProgressTotal % 100;
			//avoid ping-ponging between states
			if (newProgress > 95)
				newProgress = 95;
		}
		
		Log.d(TAG, String.format("Changing state:progress from %d:%02d to %d:%02d", state, progress, newState, newProgress));
		if (newState != state) {
			timeInState = 0;
			states[newState].applyState(states[state]);
			state = newState;
			notifyStateChanged();
		}
		if (newProgress != progress || newState != state) {
			progress = newProgress;
			if (states[newState].getDuration() >= 0) {
				timeInState = (int)(newProgress * (states[newState].getDuration() / 100.0f));	
			} else {
				timeInState = 0;
			}
			notifyStateProgressChanged();
		}
	}
	
	public void deactivate() {
		env.neutralize();
		
		state = START_STATE;
		progress = START_PROGRESS;
		
		ticker.cancel();
		ticker = null;
		
		notifyAlarmStopped();
	}
	
	private void notifyStateChanged() {
		for (SteppedAlarmStateListener listener : listeners)
			listener.stateChanged(state);
	}
	
	private void notifyStateProgressChanged() {
		for (SteppedAlarmStateListener listener : listeners)
			listener.stateProgressChanged(progress);
	}
	
	private void notifyAlarmStopped() {
		for (SteppedAlarmStateListener listener : listeners)
			listener.alarmStopped();
	}
	
	private void updateProgress() {
		timeInState += 100;
		
		if (states[state].getDuration() < 0)
			return;
		
		if (timeInState >= states[state].getDuration()) {
			int newState = state + 1;
			Log.d(TAG, String.format("Changing from state %d to state %d after %dms", state, newState, timeInState));
			states[newState].applyState(states[state]);
			state = newState;
			notifyStateChanged();
			
			timeInState = 0;
			progress = START_PROGRESS;
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
