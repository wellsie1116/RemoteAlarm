package com.wells.remotealarm;

public interface IAlarmService {
	
	void activate();
	void deactivate();
	
	void timer_elapsed();

}
