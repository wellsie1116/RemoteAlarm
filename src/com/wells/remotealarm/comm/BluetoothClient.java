package com.wells.remotealarm.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothClient {
	
	private BluetoothAdapter bt;
	
	public BluetoothClient() {
		bt = BluetoothAdapter.getDefaultAdapter();
	}
	
	private BluetoothSocket makeSocket() throws IOException {
		//TODO: implement
		throw new IOException();
//		BluetoothDevice device = bt.getRemoteDevice("00:21:86:0A:CC:E0");
//		BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00e130e3-02f8-4652-9214-5fe6ef394c56"));
//		socket.connect();
//		return socket;
	}
	
	public boolean ping() {
		try {
			BluetoothSocket socket = makeSocket();
			OutputStream sout = socket.getOutputStream();
			InputStream sin = socket.getInputStream();
			
			BluetoothIO.sendCommand(sout, "ping");
			boolean res = "pong".equals(BluetoothIO.readCommand(sin));
			socket.close();
			
			return res;
		} catch (IOException ex) {
			return false;
		}
	}
	
	public void alarm_sounding() {
		try {
			BluetoothSocket socket = makeSocket();
			OutputStream sout = socket.getOutputStream();
			InputStream sin = socket.getInputStream();
			BluetoothIO.sendCommand(sout, "alarm_state active");
			assert("ok".equals(BluetoothIO.readCommand(sin))); //TODO: log
			socket.close();
			
		} catch (IOException ex) {
			Log.e("BluetoothClient", "error", ex);
			//TODO: log
		}
	}
	
	public void alarm_fail(String reason) {
		try {
			BluetoothSocket socket = makeSocket();
			OutputStream sout = socket.getOutputStream();
			InputStream sin = socket.getInputStream();
			
			BluetoothIO.sendCommand(sout, "alarm_state failed " + reason);
			assert("ok".equals(BluetoothIO.readCommand(sin))); //TODO: log
			socket.close();
			
		} catch (IOException ex) {
			Log.e("BluetoothClient", "error", ex);
			//TODO: log
		}
	}
	
	public void alarm_canceled() {
		try {
			BluetoothSocket socket = makeSocket();
			OutputStream sout = socket.getOutputStream();
			InputStream sin = socket.getInputStream();
			
			BluetoothIO.sendCommand(sout, "alarm_state canceled");
			assert("ok".equals(BluetoothIO.readCommand(sin))); //TODO: log
			socket.close();
			
		} catch (IOException ex) {
			Log.e("BluetoothClient", "error", ex);
			//TODO: log
		}
	}

}
