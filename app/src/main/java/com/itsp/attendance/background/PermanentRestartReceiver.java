package com.itsp.attendance.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PermanentRestartReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(PermanentService.class.getSimpleName(), "Permanent service Stopped.");

        context.startService(new Intent(context, PermanentService.class));
    }
}
