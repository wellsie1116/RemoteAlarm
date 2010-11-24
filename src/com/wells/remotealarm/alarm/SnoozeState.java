package com.wells.remotealarm.alarm;

import com.wells.remotealarm.alarm.SteppedAlarm.AlarmEnvironment;

public class SnoozeState extends NullState {

	public SnoozeState(AlarmEnvironment env) {
		super(env);
	}
	
	@Override
	public long getDuration() {
		return 30 * 1000;
	}

}
