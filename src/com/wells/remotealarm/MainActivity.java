package com.wells.remotealarm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
    private TextView lblText;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      
        lblText = (TextView)findViewById(R.id.lblText);
        
        Button btnStart = (Button)findViewById(R.id.btnStart);
        
        btnStart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				btnStart_clicked();
			}
		});
        
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
    	
        Intent call = new Intent(this, AlarmService.class);
        call.putExtra("call", "register");
        startService(call);
    	
    }
    
}