package com.example.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.example.coolweather.receiver.AutoUpdateReceiver;
import com.example.coolweather.util.HttpCallBackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

/**
 * Created by lenovo on 2016/5/2.
 */
public class AutoUpdateService extends Service
{
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                updateWeather();
            }
        }).start();
        AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        int frequency=8*60*60*1000;
        long triggerTime= SystemClock.elapsedRealtime()+frequency;
        Intent i=new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this, 0,i,0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }
    private void updateWeather()
    {
        String weatherCode=getSharedPreferences("weatherInfo",MODE_PRIVATE).getString("cityid","");
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        HttpUtil.sendHttpRequest(address, new
                HttpCallBackListener() {
                    @Override
                    public void onFinish(String response)
                    {
                        Utility.handleWeatherResponse(AutoUpdateService.this, response);
                    }
                    @Override
                    public void onError(Exception e)
                    {
                        e.printStackTrace();
                    }
                });
    }
}
