package com.wells.remotealarm.alarm;

public interface SteppedAlarmStateListener {
	public void stateChanged(int state);
	public void stateProgressChanged(int progress);
}
