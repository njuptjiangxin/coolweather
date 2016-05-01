package com.example.coolweather.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.coolweather.database.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/5/1.
 */
public class CoolWeatherDB
{
    private static final String DB_NAME="db_coolWeather";
    private static final int VERSION=1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;
    private CoolWeatherDB(Context context)
    {
        CoolWeatherOpenHelper openHelper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=openHelper.getWritableDatabase();
    }
    public synchronized static CoolWeatherDB getInstance(Context context)
    {
        if (coolWeatherDB==null)
            coolWeatherDB=new CoolWeatherDB(context);
        return coolWeatherDB;
    }
    public void saveProvince(Province province)
    {
        if (province!=null)
            db.execSQL("insert into t_province(province_name,province_code) values(?,?)",
                    new String[]{province.getProvinceName(),province.getProvinceCode()});
    }
    public List<Province> loadProvinces()
    {
        List<Province> provinceList=new ArrayList<Province>();
        Cursor cursor=db.rawQuery("select * from t_province",null);
        if (cursor!=null)
        {
            int provinceIdIndex=cursor.getColumnIndex("id");
            int provinceNameIndex=cursor.getColumnIndex("province_name");
            int provinceCodeIndex=cursor.getColumnIndex("province_code");
            for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
            {
                Province province=new Province();
                province.setId(cursor.getInt(provinceIdIndex));
                province.setProvinceName(cursor.getString(provinceNameIndex));
                province.setProvinceCode(cursor.getString(provinceCodeIndex));
                provinceList.add(province);
            }
        }
        return provinceList;
    }
    public void saveCity(City city)
    {
        if (city!=null)
            db.execSQL("insert into t_city(city_name,city_code,province_id) values(?,?,?)",
                    new String[]{city.getCityName(),city.getCityCode(),city.getProvinceId()+""});
    }
    public List<City> loadCities(int provinId)
    {
        List<City> cityList=new ArrayList<City>();
        Cursor cursor=db.rawQuery("select * from t_city where province_id="+provinId,null);
        if (cursor!=null)
        {
            int cityIdIndex=cursor.getColumnIndex("id");
            int cityNameIndex=cursor.getColumnIndex("city_name");
            int cityCodeIndex=cursor.getColumnIndex("city_code");
            for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
            {
                City city=new City();
                city.setId(cursor.getInt(cityIdIndex));
                city.setCityName(cursor.getString(cityNameIndex));
                city.setCityCode(cursor.getString(cityCodeIndex));
                city.setProvinceId(provinId);
                cityList.add(city);
            }
        }
        return cityList;
    }
    public void saveCounty(County county)
    {
        if (county!=null)
            db.execSQL("insert into t_county(county_name,county_code,county_cityId values(?,?,?))",
                    new String[]{county.getCountyName(),county.getCountyCode(),county.getCityId()+""});
    }
    public List<County> loadcounties(int cityId)
    {
        List<County> countyList=new ArrayList<County>();
        Cursor cursor=db.rawQuery("select * from t_county where city_id="+cityId,null);
        if (cursor!=null)
        {
            int countyIdIndex=cursor.getColumnIndex("county_id");
            int countyNameIndex=cursor.getColumnIndex("county_name");
            int countyCodeIndex=cursor.getColumnIndex("county_code");
            for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
            {
                County county=new County();
                county.setId(cursor.getInt(countyIdIndex));
                county.setCountyName(cursor.getString(countyNameIndex));
                county.setCountyCode(cursor.getString(countyCodeIndex));
                county.setCityId(cityId);
                countyList.add(county);
            }
        }
        return countyList;
    }
}
