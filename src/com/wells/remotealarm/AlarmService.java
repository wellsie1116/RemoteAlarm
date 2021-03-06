package com.wells.remotealarm;
import java.util.Calendar;

import android.R;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.wells.remotealarm.alarm.SteppedAlarm;
import com.wells.remotealarm.alarm.SteppedAlarmStateListener;
import com.wells.remotealarm.comm.BluetoothClient;
import com.wells.remotealarm.listeners.AcknowledgedListener;


public class AlarmService extends Service {
	
	private static final String TAG = "AlarmService";

	private NotificationManager mNotifyManager;
	private AlarmManager mAlarmManager;
	private BluetoothClient mBtClient;
	
	
	@Override
	public void onCreate() {
		mNotifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		mBtClient = new BluetoothClient();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			if (intent.hasExtra("call")) {
				String method = intent.getStringExtra("call");
				if ("timer_elapsed".equals(method)) {		
					mBinder.timer_elapsed();
				} else if ("register".equals(method)) {
					int minutes = intent.getIntExtra("minutes", -1);
					int hours = intent.getIntExtra("hours", -1);
					mBinder.setMinutes(minutes);
					mBinder.setHours(hours);
					mBinder.activate();
				}
			}
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		mBinder.deactivate();
	}

	
	
	private void showNotification() {
        // This is who should be launched if the user selects the app icon in the notification.
        Intent appIntent = new Intent(this, AlarmActivity.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, appIntent, 0);
        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
 
        // choose the ticker text
        String tickerText = "An alarm for you, sir!";
 
        Notification nt = new Notification(R.drawable.ic_btn_speak_now, tickerText, System.currentTimeMillis());
        nt.when = System.currentTimeMillis();
        nt.setLatestEventInfo(this, "Remote Alarm Clock", "Alarm is currently sounding", pending);
        
        mNotifyManager.notify(135, nt);
        
        //open the AlarmActivity
        startActivity(appIntent);
    }

	
	
	
	
	
	
	
	private PendingIntent pending;
	
	public class LocalBinder extends Binder implements IAlarmService {
		
		AlarmService getService() {
			return AlarmService.this;
		}
		
		private AlarmServer server;
		
		private SteppedAlarm alarm;

		private int hours = 0;
		private int minutes = 25;
		
		public void setHours(int hours) {
			this.hours = hours;
		}
		
		public void setMinutes(int minutes) {
			this.minutes = minutes;
		}

		@Override
		public void activate() {
			server = new AlarmServer();
	        server.setAcknowledgedListener(new AcknowledgedListener() {
				
				@Override
				public void acknowledged(String message) {
					timer_stopped();
				}
			});
	        server.activate();
			
			
	        Calendar time = Calendar.getInstance();
	        if (this.hours > 0)
	        	time.add(Calendar.HOUR, this.hours);
	        if (this.minutes > 0)
	        	time.add(Calendar.MINUTE, this.minutes);
//	        time.add(Calendar.MINUTE, 2);
//	        time.add(Calendar.SECOND, 15);

	        Intent intent = new Intent(AlarmService.this, AReceiver.class);
	        pending = PendingIntent.getBroadcast(AlarmService.this, 951753, intent, 0);
	        mAlarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pending);
	        
	        //let the user know we are timing
	        Vibrator buzz = (Vibrator)getSystemService(VIBRATOR_SERVICE);
	        buzz.vibrate(1000);
		}
		
		@Override
		public void deactivate() {
			if (pending != null) {
				mAlarmManager.cancel(pending);
				pending = null;
				mNotifyManager.cancel(135);
			}
		}
		
		@Override
		public void timer_elapsed() {
			alarm = new SteppedAlarm(getApplicationContext());
			alarm.addStatusListener(new SteppedAlarmStateListener(){

				@Override
				public void stateChanged(int state) {
					Log.i(TAG, "State changed to " + state);
					
				}

				@Override
				public void stateProgressChanged(int progress) {
					Log.i(TAG, "Progress changed to " + progress);
					
				}

				@Override
				public void alarmStopped() {
					Log.i(TAG, "Alarm stopped");
				}});
			showNotification();
			alarm.activate();

	        mBtClient.alarm_sounding();
		}

		@Override
		public SteppedAlarm getAlarm() {
			return alarm;
		}
		
		private void timer_stopped() {
			alarm.deactivate();
			alarm = null;
			mBtClient.alarm_canceled();
			mNotifyManager.cancel(135);
			
			//let the user know we are timing
	        Vibrator buzz = (Vibrator)getSystemService(VIBRATOR_SERVICE);
	        buzz.vibrate(new long[] {300, 400, 300}, -1);
		}

	}
	
	
	private final LocalBinder mBinder = new LocalBinder();


	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	
	
	
	
}
