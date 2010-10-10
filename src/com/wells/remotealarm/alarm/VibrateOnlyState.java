package com.wells.remotealarm.alarm;

import com.wells.remotealarm.alarm.SteppedAlarm.AlarmEnvironment;

public class VibrateOnlyState extends AlarmState {

	public VibrateOnlyState(AlarmEnvironment env) {
		super(env);
	}
	
	@Override
	protected void setProgress(int step) {
		//do nothing
	}
	
	@Override
	public long getDuration() {
		return 30 * 1000; //30 seconds
	}

	@Override
	protected boolean audioEnabled() {
		return false;
	}

	@Override
	protected String audioPath() {
		return null;
	}	

	@Override
	protected boolean vibrateEnabled() {
		return true;
	}

	@Override
	protected long[] vibratePattern() {
		return new long[] {1000, 500, 400};
	}

	@Override
	protected int vibrateRepeateIndex() {
		return 1;
	}

}
