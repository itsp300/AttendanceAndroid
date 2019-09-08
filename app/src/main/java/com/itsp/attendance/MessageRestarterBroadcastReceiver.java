package com.itsp.attendance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessageRestarterBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(MessageRestarterBroadcastReceiver.class.getSimpleName(), "Message service Stopped.");
        context.startService(new Intent(context, MessageService.class));;
    }
}