package com.wells.remotealarm.alarm;

import java.util.Timer;
import java.util.TimerTask;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.wells.remotealarm.AlarmActivity;

public class SteppedAlarm {
	
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
		
		public Vibrator svcVibrator;
		public AudioManager svcAudioManager;
		
		private AlarmEnvironment() {
			audio = new MediaPlayer();
			svcVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
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
		
		ticker = new Timer();
		
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
		
		if (states[state].getDuration() >= 0 && timeInState >= states[state].getDuration()) {
			int newState = state + 1;
			states[newState].applyState(states[state]);
			state = newState;
			notifyStateChanged();
			
			timeInState = 0;
			progress = 0;
			notifyStateProgressChanged();
		} else {
			int newProgress = (int)(timeInState * 100.0f / states[state].getDuration());
			if (newProgress != progress) {
				progress = newProgress;
				notifyStateProgressChanged();
			}
		}
	}
	
	
	
	
	
	
	private void showNotification() {
        // This is who should be launched if the user selects the app icon in the notification.
        Intent appIntent = new Intent(context, AlarmActivity.class);
        PendingIntent pending = PendingIntent.getActivity(context, 0, appIntent, 0);
        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
 
        // choose the ticker text
        String tickerText = "An alarm for you, sir!";
 
        Notification nt = new Notification(R.drawable.ic_btn_speak_now, tickerText, System.currentTimeMillis());
        nt.when = System.currentTimeMillis();
        nt.setLatestEventInfo(context, "Remote Alarm Clock", "Alarm is currently sounding", pending);
        
        //mNotifyManager.notify(135, nt);
	}
	
	

}
