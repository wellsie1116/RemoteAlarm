package com.wells.remotealarm;

import com.wells.remotealarm.alarm.SteppedAlarm;

public interface IAlarmService {
	
	void activate();
	void deactivate();
	
	void timer_elapsed();
	
	SteppedAlarm getAlarm();

}
