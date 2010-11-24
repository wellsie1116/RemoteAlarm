package com.wells.remotealarm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;

import com.wells.remotealarm.alarm.ShakerListener;
import com.wells.remotealarm.alarm.ShakerManager;
import com.wells.remotealarm.alarm.SteppedAlarm;
import com.wells.remotealarm.alarm.SteppedAlarmStateListener;

public class AlarmActivity extends Activity {
	
	private static final String TAG = "AlarmActivity";
	
	private SteppedAlarm mAlarm;

	private List<ProgressBar> mProgressBars;
	
	private ShakerManager shakerManager;
	private ShakerListener shakerListener;
	
	private SteppedAlarmStateListener alarmListener;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        
        //Get our alarm object
        Intent service = new Intent(this, AlarmService.class);
        if (!bindService(service, new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				obtainedService((IAlarmService)service);
				unbindService(this);
			}
			@Override public void onServiceDisconnected(ComponentName name) {}
			}, 0))
        	throw new RuntimeException("Unable to bind service");
        
        mProgressBars = new ArrayList<ProgressBar>();
        mProgressBars.add((ProgressBar)findViewById(R.id.progressSnooze));
        mProgressBars.add((ProgressBar)findViewById(R.id.progressVibrate));
        mProgressBars.add((ProgressBar)findViewById(R.id.progressFadeIn));
        mProgressBars.add((ProgressBar)findViewById(R.id.progressBlast));
    }
	
	private void obtainedService(IAlarmService service) {
		mAlarm = service.getAlarm();
		
		//No use in continuing on without an alarm object
		if (mAlarm == null) {
			finish();
			return;
		}
		
		mAlarm.addStatusListener(alarmListener = new SteppedAlarmStateListener() {
			private int state;
			@Override
			public void stateChanged(int state) {
				this.state = state;
				for (int i = 0; i < mProgressBars.size(); i++) {
					if (i < state) {
						ProgressBar progress = mProgressBars.get(i);
						progress.setProgress(100);
					} else {
						mProgressBars.get(i).setProgress(0);
					}
				}
				int lastI = mProgressBars.size() - 1;
				ProgressBar last = mProgressBars.get(lastI);
				last.setBackgroundColor(Color.BLACK);
//				last.setBackgroundColor(state != lastI ? Color.RED : Color.BLACK);
				last.setIndeterminate(state == lastI);
			}
			@Override
			public void stateProgressChanged(int progress) {
				if (state < 0) {
					return;
				}
				mProgressBars.get(state).setProgress(progress);
			}
			@Override
			public void alarmStopped() {
				finish();				
			}
		});
		
		shakerManager = ShakerManager.getInstance(this);
		shakerManager.addListener(shakerListener = new ShakerListener() {
			@Override
			public void shakeReceived(float amount) {
				//Log.v(TAG, String.format("Shake amount: %f", amount));
				int revertAmount = (int)amount;
				if (revertAmount > 0)
					mAlarm.revertProgress(revertAmount);
			}
		});
	}
	
	@Override
	public void onDestroy() {
		if (mAlarm != null && alarmListener != null) {
			mAlarm.removeStatusListener(alarmListener);
		}
		if (shakerListener != null) {
			shakerManager.removeListener(shakerListener);
		}
		super.onDestroy();
	}

}
