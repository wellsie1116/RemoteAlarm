package com.wells.remotealarm.alarm;

import com.wells.remotealarm.alarm.SteppedAlarm.AlarmEnvironment;

public class LoudAudioState extends LightAudioState {
	
	public LoudAudioState(AlarmEnvironment env) {
		super(env);
	}

	@Override
	protected void setProgress(int step) {
		 //ensure we don't ever set the volume
	}
	
	@Override
	public long getDuration() {
		return -1;
	}

	@Override
	protected long[] vibratePattern() {
		return new long[] {0, 300, 100};
	}
}
