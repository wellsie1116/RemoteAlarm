package com.wells.remotealarm.alarm;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;

public abstract class AlarmState {
	
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
		if (current.audioEnabled() != audioEnabled()) {
			if (!audioEnabled()) {
				//we do not use audio
				if (env.audio.isPlaying())
					env.audio.stop();
			} else {
				//we do use audio
				env.svcAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 
													env.svcAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
													0);
				env.audio.setDataSource(audioPath());
//	        	env.audio.setDataSource(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
				env.audio.setAudioStreamType(AudioManager.STREAM_ALARM);
		        env.audio.setLooping(true);
		        env.audio.prepare();
		        env.audio.start();
			}
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
			//TODO: Log error
		}
		applyVibratorState(current);
		
		setProgress(1);
	}

}
