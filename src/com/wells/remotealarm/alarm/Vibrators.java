package com.wells.remotealarm.alarm;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Vibrator;
import android.util.Log;

/**
 * Manages vibrating to multiple vibrating sinks.
 * 
 * @author Kevin Wells
 *
 */
public class Vibrators {
	private static final String TAG = "Vibrators";
	
	private static final int MIN_IN_SECS = 60 * 1000; 
	
	private Vibrator buzz;
	private Timer rebuzz;
	
	private long[] pattern;
	private int repeat;
	
	public Vibrators(Vibrator vibrator) {
		buzz = vibrator;
	}
	
	public void vibrate(long[] pattern, int repeat) {
		this.pattern = pattern;
		this.repeat = repeat;
		Log.d(TAG, "Vibrating");
		buzz.vibrate(pattern, repeat);
		//FIXME: The issue is not a vibrator timeout.  The Vibrator simply stops when the screen is turned off.
		//http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
		if (rebuzz == null) {
			rebuzz = new Timer();
			rebuzz.schedule(new TimerTask() {
				@Override
				public void run() {
					revibrate();
				}},
				MIN_IN_SECS,
				MIN_IN_SECS);
		}
	}
	
	public void cancel() {
		if (rebuzz != null) {
			rebuzz.cancel();
			rebuzz = null;
		}
		buzz.cancel();
	}
	
	private void revibrate() {
		vibrate(pattern, repeat);
	}
	
}
