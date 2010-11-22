package com.wells.remotealarm.alarm;

import java.io.IOException;

import android.media.AudioManager;
import android.util.Log;

public abstract class AlarmState {
	
	private static final String TAG = "AlarmState";
	
	protected SteppedAlarm.AlarmEnvironment env;
	
	public AlarmState(SteppedAlarm.AlarmEnvironment env) {
		this.env = env;
	}
	
	public abstract long getDuration(); //in millis
	protected abstract void setProgress(int step);
	
	protected abstract boolean audioEnabled();
	protected abstract String audioPath();
	
	protected abstract boolean vibrateEnabled();
	protected abstract long[] vibratePattern();
	protected abstract int vibrateRepeateIndex();
	
	protected void applyAudioState(AlarmState current) throws IOException {
//		if (current.audioEnabled() != audioEnabled()) {
		if (audioEnabled()) {
			//we do use audio
			env.svcAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 
					env.svcAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
					0);
			if (current.audioEnabled() && current.audioPath().equals(audioPath())) {
				//don't interrupt the stream, just set some volume property				
			} else {
				if (current.audioEnabled()) {
					//stop whatever was playing first
					env.audio.reset();
				}
				env.audio.setDataSource(audioPath());
//	        	env.audio.setDataSource(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
				env.audio.setAudioStreamType(AudioManager.STREAM_ALARM);
		        env.audio.setLooping(true);
		        env.audio.prepare();
		        env.audio.start();				
			}			

		} else {
			//we do not use audio
			if (env.audio.isPlaying())
				env.audio.stop();	
		}
	}
	
	protected void applyVibratorState(AlarmState current) {
		env.svcVibrator.cancel();
		
		if (vibrateEnabled()) {
			env.svcVibrator.vibrate(vibratePattern(), vibrateRepeateIndex());
		}		
	}
	
	public void applyState(AlarmState current) {
		try {
			applyAudioState(current);	
		} catch (IOException ex) {
			Log.e(TAG, ex.toString());
			//TODO: log error (to server)
		}
		applyVibratorState(current);
		
		setProgress(1);
	}

}
