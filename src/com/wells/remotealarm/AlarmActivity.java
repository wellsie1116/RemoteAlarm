package com.wells.remotealarm;

import android.app.Activity;
import android.os.Bundle;

public class AlarmActivity extends Activity {
    
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        
        //SensorManager manager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
    }

}
