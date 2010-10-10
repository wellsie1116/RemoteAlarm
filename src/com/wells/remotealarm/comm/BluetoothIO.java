package com.wells.remotealarm.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothIO {
	
	public static void sendCommand(OutputStream sout, String command) throws IOException {
		byte[] data = (command + "\n").getBytes();
		sout.write(data);
	}
	
	public static String readCommand(InputStream sin) throws IOException{
		byte buf[] = new byte[256];
		StringBuilder command = new StringBuilder();
		int len = 0;
		while ((len = sin.read(buf)) > 0 && buf[len-1] != '\n')
			command.append(new String(buf, 0, len));
		command.append(new String(buf, 0, len - 1)); //don't copy the newline
		return command.toString();
	}
	
}
