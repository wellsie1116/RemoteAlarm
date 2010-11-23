package com.wells.remotealarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wells.remotealarm.alarm.ShakerListener;
import com.wells.remotealarm.alarm.ShakerManager;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	
    private TextView lblText;
    private EditText txtHours;
    private EditText txtMinutes;
    
    private ShakerManager shakerManager;
    private ShakerListener shakerListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      
        lblText = (TextView)findViewById(R.id.lblText);
        txtHours = (EditText)findViewById(R.id.txtHours);
        txtMinutes = (EditText)findViewById(R.id.txtMinutes);
        
        Button btnStart = (Button)findViewById(R.id.btnStart);
        
        btnStart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				btnStart_clicked();
			}
		});
        
        shakerManager = new ShakerManager(getApplicationContext());
        shakerManager.addListener(shakerListener = new ShakerListener() {
			@Override
			public void shakeReceived(float amount) {
				Log.d(TAG, String.format("Shake amount: %f", amount));
			}
		});
    }
    
    @Override
    public void onDestroy() {
    	shakerManager.removeListener(shakerListener);
    	super.onDestroy();
    }
    
    
    /*private BluetoothDevice findMe(BluetoothAdapter bt) {
        for (BluetoothDevice device : bt.getBondedDevices()) {
        	if ("wellska1-1".equals(device.getName())) {
        		return device;
        	}
        }
        
        //*
        BluetoothDevice device = findMe(bt);

        
        
        try {
            BluetoothSocket sock = device.createRfcommSocketToServiceRecord(null);
            OutputStream sout = sock.getOutputStream();
            sout.write("Hello world".getBytes());
            sout.close();
            sock.close();        	
        } catch (IOException ex) {
        	//oops
        }/
        
        
        return null;
	}  */
    
    
    private void btnStart_clicked() {
    	
    	int minutes;
    	int hours;
    	try {
    		minutes = Integer.parseInt(txtMinutes.getText().toString());
    		hours = Integer.parseInt(txtHours.getText().toString());
        	Toast.makeText(getApplicationContext(), String.format("Time: %02d:%02d", hours, minutes), 1000).show();
    	} catch (NumberFormatException ex) {
    		Toast.makeText(getApplicationContext(), "Invalid minutes", 2000).show();
    		return;
    	}
    	
        Intent call = new Intent(this, AlarmService.class);
        call.putExtra("call", "register");
        call.putExtra("hours", hours);
        call.putExtra("minutes", minutes);
        startService(call);
    	
    }
    
}