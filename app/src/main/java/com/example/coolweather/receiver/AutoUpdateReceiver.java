package com.example.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.coolweather.service.AutoUpdateService;

/**
 * Created by lenovo on 2016/5/2.
 */
public class AutoUpdateReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent i=new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
