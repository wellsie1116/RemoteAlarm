package com.wells.remotealarm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.wells.remotealarm.comm.BluetoothIO;
import com.wells.remotealarm.listeners.AcknowledgedListener;

public class AlarmServer {
	/* Socket Communication Examples
	 * 
	 * Deactivate Alarm
	 * Client: ack
	 * Server: ok
	 * 
	 * Query Alarm Status
	 * Client: inq
	 * Server: ok
	 * Server: [deactivated, pending [time remaining], alarming <time elapsed> <stage> <numstages> <progress>] 
	 * 
	 * 
	 */

	public AlarmServer() {
		bt = BluetoothAdapter.getDefaultAdapter();
		
	}
	
//	private boolean btWasOn = false;
	private BluetoothAdapter bt;
	
	private AcknowledgedListener acknowledgedListener;
	
	public void activate() {
		//We shouldn't modify the adapter state, it's not nice
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
	
	private boolean handleRequest(String cmd, OutputStream sout) throws IOException {
		//TODO: implement
		
		StringBuilder res = new StringBuilder();
		
		if ("ack".equals(cmd)) {
			acknowledgedListener.acknowledged(cmd); //TODO: threadsafe
		} else if ("inq".equals(cmd)) {
			res.append("Unknown\n");
		} else {
			BluetoothIO.sendError(sout, "Invalid command");
			return false;
		}
		
		BluetoothIO.sendSuccess(sout);
		sout.write(res.toString().getBytes());
		return true;
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
					OutputStream sout = sock.getOutputStream();
					while (true) {
						try {
							String cmd = BluetoothIO.readCommand(sin);
							if (!handleRequest(cmd, sout))
								break;
						} catch (IOException ex) {
							break;
						}
					}				
					sin.close();
					sout.close();
				}
				server.close();
			} catch (IOException ex) {
				//oops
			}
		}
	}
	
}
