package com.wells.remotealarm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class AReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent serviceCall = new Intent(context, AlarmService.class);
		serviceCall.putExtra("call", "timer_elapsed");
		context.startService(serviceCall);
        //NOT ALLOWED: context.bindService(new Intent(context, AlarmService.class), mSvcConn, 0);
	}

}
