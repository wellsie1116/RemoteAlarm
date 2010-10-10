package com.wells.remotealarm.alarm;

import android.media.AudioManager;

import com.wells.remotealarm.alarm.SteppedAlarm.AlarmEnvironment;

public class LightAudioState extends AlarmState {

	public LightAudioState(AlarmEnvironment env) {
		super(env);
	}
	
	@Override
	protected void setProgress(int step) {
		int max = env.svcAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		
		env.svcAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 
				step / (100 / max),
				0);
	}
	
	@Override
	public long getDuration() {
		return 20 * 1000; //20 seconds
	}

	@Override
	protected boolean audioEnabled() {
		return true;
	}

	@Override
	protected String audioPath() {
		return "/sdcard/media/audio/ringtones/Audio_House.mp3";
	}	

	@Override
	protected boolean vibrateEnabled() {
		return true;
	}

	@Override
	protected long[] vibratePattern() {
		return new long[] {0, 400, 200};
	}

	@Override
	protected int vibrateRepeateIndex() {
		return 1;
	}

}
