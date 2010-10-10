package com.wells.remotealarm.alarm;

import java.io.IOException;

import android.R;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.wells.remotealarm.AlarmActivity;
import com.wells.remotealarm.comm.BluetoothClient;

public class SteppedAlarm {
	
	private Context context;
	
	private AlarmEnvironment env;
	
	public NotificationManager svcNotificationManager;
	
	public class AlarmEnvironment {
		public MediaPlayer audio;
		
		public Vibrator svcVibrator;
		public AudioManager svcAudioManager;
		
		
		private AlarmEnvironment() {
			audio = new MediaPlayer();
			svcVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
			svcAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			
		}
	}
	
	private void makeStates() {
		AlarmEnvironment env = new AlarmEnvironment();
		
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
	
	public SteppedAlarm(Context context) {
	
		this.context = context;
	
		this.env = new AlarmEnvironment();
		
		svcNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//vibrator.vibrate(new long[] {1000, 500, 400}, 1);
	
        //manager.setStreamVolume(AudioManager.STREAM_ALARM, manager.getStreamMaxVolume(AudioManager.STREAM_ALARM)/*/6*/, 0);
        
        /*
        
        try {
			player.setDataSource("/sdcard/media/audio/ringtones/Audio_House.mp3");
//        	player.setDataSource(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
		} catch (IllegalArgumentException e) {
			mBtClient.alarm_fail(e.toString());
		} catch (IllegalStateException e) {
			mBtClient.alarm_fail(e.toString());
		} catch (IOException e) {
			mBtClient.alarm_fail(e.toString());
		}
		player.setAudioStreamType(AudioManager.STREAM_ALARM);
        player.setLooping(true);
        try {
			player.prepare();
		} catch (IllegalStateException e) {
			mBtClient.alarm_fail(e.toString());
		} catch (IOException e) {
			mBtClient.alarm_fail(e.toString());
		}
        player.start();
        mBtClient.alarm_sounding();*/
		
	}

}
