package com.example.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.coolweather.model.City;
import com.example.coolweather.model.CoolWeatherDB;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;

import org.json.JSONObject;

/**
 * Created by lenovo on 2016/5/1.
 */
public class Utility
{
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            String[] allProvinces=response.split(",");
            if (allProvinces!=null&&allProvinces.length>0)
            {
                for (String p:allProvinces)
                {
                    String[] provincePart=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceName(provincePart[1]);
                    province.setProvinceCode(provincePart[0]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId)
    {
        if (!TextUtils.isEmpty(response))
        {
            String[] allCities=response.split(",");
            if (allCities!=null&&allCities.length>0)
            {
                for (String c : allCities)
                {
                    String[] cityPart = c.split("\\|");
                    City city = new City();
                    city.setCityName(cityPart[1]);
                    city.setCityCode(cityPart[0]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId)
    {
        if (!TextUtils.isEmpty(response))
        {
            String[] allCounties=response.split(",");
            if (allCounties!=null&&allCounties.length>0)
            {
                for (String c:allCounties)
                {
                    String[] countyPart=c.split("\\|");
                    County county=new County();
                    county.setCountyName(countyPart[1]);
                    county.setCountyCode(countyPart[0]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
    public static void handleWeatherResponse(Context context,String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject=new JSONObject(response);
                JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
                String countyName=weatherInfo.getString("city");
                String weatherCode=weatherInfo.getString("cityid");
                String temp1=weatherInfo.getString("temp1");
                String temp2=weatherInfo.getString("temp2");
                String dec=weatherInfo.getString("weather");
                String publishTime=weatherInfo.getString("ptime");
                saveWeatherInfo(context,countyName,weatherCode,temp1,temp2,dec,publishTime);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(context, "weatherResponse is null", Toast.LENGTH_SHORT).show();
    }
    private static void saveWeatherInfo(Context context,String countyName, String weatherCode, String temp1, String temp2, String dec, String ptime)
    {
        SharedPreferences sp=context.getSharedPreferences("weatherInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("county_selected",true);
        editor.putString("countyName",countyName);
        editor.putString("weatherCode",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("dec",dec);
        editor.putString("publishTime",ptime);
        editor.commit();
    }
}

