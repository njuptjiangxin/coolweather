package com.example.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.coolweather.R;
import com.example.coolweather.service.AutoUpdateService;
import com.example.coolweather.util.HttpCallBackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

/**
 * Created by lenovo on 2016/5/1.
 */
public class WeatherInformation extends AppCompatActivity
{
    private TextView countyNameText;
    private TextView publishTimeText;
    private TextView descriptionText;
    private TextView tempText;
    private Button switchCounty;
    private Button update;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        countyNameText=(TextView) findViewById(R.id.county_name);
        publishTimeText=(TextView) findViewById(R.id.publish_time);
        descriptionText=(TextView) findViewById(R.id.description);
        tempText=(TextView) findViewById(R.id.temp);
        switchCounty=(Button) findViewById(R.id.switch_county);
        update=(Button) findViewById(R.id.update);
        switchCounty.setOnClickListener(myListener);
        update.setOnClickListener(myListener);
        String countyCode=getIntent().getStringExtra("countyCode");
        if (!TextUtils.isEmpty(countyCode))
        {
            queryWeatherCode(countyCode);
        }
        else
        {
            queryWeatherInfo(getSharedPreferences("weatherInfo",MODE_PRIVATE).getString("cityid",""));
        }

    }
    private View.OnClickListener myListener=new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.switch_county:
                    Intent intent=new Intent(WeatherInformation.this,ChooseArea.class);
                    intent.putExtra("fromWeatherInformation",true);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.update:
                    publishTimeText.setText("Updating......");
                    SharedPreferences sp=getSharedPreferences("weatherInfo",MODE_PRIVATE);
                    String weatherCode=sp.getString("weatherCode","");
                    if (!TextUtils.isEmpty(weatherCode))
                    {
                        queryWeatherInfo(weatherCode);
                    }
                    break;
            }
        }
    };
    private void queryWeatherCode(String countyCode)
    {
//        Toast.makeText(WeatherInformation.this, "countyCode is:"+countyCode, Toast.LENGTH_SHORT).show();
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address,"countyCode");
    }
    private void queryWeatherInfo(String weatherCode)
    {
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"weatherCode");
    }
    private void queryFromServer(String address, final String type)
    {
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response)
            {
                if (type.equals("countyCode"))
                {
                    if (!TextUtils.isEmpty(response))
                    {
                        String[] array=response.split("\\|");
                        if (array!=null&&array.length==2)
                        {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }
                else if (type.equals("weatherCode"))
                {
                    Utility.handleWeatherResponse(WeatherInformation.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            showWeather();
                        }
                    });
                }

            }

            @Override
            public void onError(Exception e)
            {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        descriptionText.setText("Loading Failed!!!");
                    }
                });
            }
        });
    }
    private void showWeather()
    {
        SharedPreferences sp=getSharedPreferences("weatherInfo",MODE_PRIVATE);
        String countyName=sp.getString("countyName","");
        String temp1=sp.getString("temp1","");
        String temp2=sp.getString("temp2","");
        String dec=sp.getString("dec","");
        String ptime=sp.getString("publishTime","");
        countyNameText.setText(countyName);
        publishTimeText.setText(ptime+"  published");
        descriptionText.setText(dec);
        tempText.setText(temp1+"~"+temp2);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
