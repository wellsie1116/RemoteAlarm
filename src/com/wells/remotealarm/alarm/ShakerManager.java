package com.wells.remotealarm.alarm;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakerManager {
	
	private static final String TAG = "ShakerManager";
	
	private static ShakerManager instance = null;
	
	private boolean active = false;
	
	private List<ShakerListener> listeners;

	private SensorEventListener sensorListener;
	
	private SensorManager sensorManager;
	private Sensor sensor;

	private ShakerManager(Context context) {
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		listeners = new LinkedList<ShakerListener>();
	}
	
	public static ShakerManager getInstance(Context context) {
		if (instance == null)
			instance = new ShakerManager(context.getApplicationContext());
		return instance;
	}
	
	public boolean addListener(ShakerListener listener) {
		listeners.add(listener);
		if (!active)
			return active = activate();
		return true;
	}
	
	public void removeListener(ShakerListener listener) {
		listeners.remove(listener);
		if (listeners.size() == 0)
			deactivate();		
	}
	
	private boolean activate() {
		initSensorListener();
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            return sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
        return false;
	}
	
	private void deactivate() {
		sensorManager.unregisterListener(sensorListener, sensor);
		sensor = null;
		active = false;
		initSensorListener(); 
	}
	
	private void initSensorListener() {
		sensorListener = new SensorEventListener() {
	    	public boolean first = true;
	    	long otime;
	    	float ox;
	    	float oy;
	    	float oz;
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				//boo-hoo
			}
			@Override
			public void onSensorChanged(SensorEvent event) {
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];
				
				if (first) {
					first = false;
				} else {
					long dtime = Math.max(1, event.timestamp - otime);
					float dx = x - ox;
					float dy = y - oy;
					float dz = z - oz;
					float amount = (float)Math.sqrt(dx*dx + dy*dy + dz*dz) / (dtime/10000000.0f);
					//Log.e(TAG, String.format("dTime: %d", dtime));
					for (ShakerListener listener : listeners) {
						listener.shakeReceived(amount);
					}
				}
				ox = x;
				oy = y;
				oz = z;
				otime = event.timestamp;
			}
		};
	}
	
}
