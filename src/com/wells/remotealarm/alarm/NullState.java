package com.wells.remotealarm.alarm;

import com.wells.remotealarm.alarm.SteppedAlarm.AlarmEnvironment;

/**
 * AlarmState that does nothing.  Used to represent alarm in a resting state.
 * 
 * @author wellska1
 *
 */
public class NullState extends AlarmState {

	public NullState(AlarmEnvironment env) {
		super(env);
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
	public long getDuration() {
		return -1;
	}

	@Override
	protected void setProgress(int step) {
		//ignore
	}

	@Override
	protected boolean vibrateEnabled() {
		return false;
	}

	@Override
	protected long[] vibratePattern() {
		return null;
	}

	@Override
	protected int vibrateRepeateIndex() {
		return 0;
	}

}
