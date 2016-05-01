package com.example.coolweather.util;

/**
 * Created by lenovo on 2016/5/1.
 */
public interface HttpCallBackListener
{
    void onFinish(String response);
    void onError(Exception e);
}
