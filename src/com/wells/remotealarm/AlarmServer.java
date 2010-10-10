package com.wells.remotealarm;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.json.JSONTokener;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.wells.remotealarm.comm.BluetoothIO;
import com.wells.remotealarm.listeners.AcknowledgedListener;

public class AlarmServer {

	public AlarmServer() {
		bt = BluetoothAdapter.getDefaultAdapter();
		
	}
	
//	private boolean btWasOn = false;
	private BluetoothAdapter bt;
	
	private AcknowledgedListener acknowledgedListener;
	
	public void activate() {
		//We shouldn't modify the adapter state, its not nice
		/*btWasOn = bt.getState() == BluetoothAdapter.STATE_ON || bt.getState() == BluetoothAdapter.STATE_TURNING_ON;
        if (!btWasOn) {
        	bt.enable();
        }*/
        new BluetoothServer().start();
	}
	
	public void deactivate() {
		/*if (!btWasOn) {
			bt.disable();
		}*/
	}
	
	public void setAcknowledgedListener(AcknowledgedListener listener) {
		acknowledgedListener = listener;
	}
	
	private class BluetoothServer extends Thread {
		
		private BluetoothServerSocket server;
		
//		private BluetoothSocket activeSocket;
		
		@Override
		public void run() {
			try {
				server = bt.listenUsingRfcommWithServiceRecord("RemoteAlarm", UUID.fromString("1f96dba5-6384-44fd-a500-a5cf3147e1a6"));
				while (true) {
					BluetoothSocket sock;
					
					try {
						sock = server.accept();
					} catch (IOException ex) {
						break;
					}
					
					BluetoothDevice device = sock.getRemoteDevice();
					String name = device.getName();
					if (!("wellska1-1".equals(name) || "Kevin-Desktop".equals(name))) {
						sock.close();
						continue;
					}
					
//					activeSocket = sock;
					
					InputStream sin = sock.getInputStream();
					byte[] data = new byte[255];
					while (true) {
						try {
							String cmd = BluetoothIO.readCommand(sin);
							acknowledgedListener.acknowledged(cmd); //TODO: threadsafe
						} catch (IOException ex) {
							break;
						}
					}				
					sin.close();
				}
				server.close();
			} catch (IOException ex) {
				//oops
			}
		}
	}
	
}
