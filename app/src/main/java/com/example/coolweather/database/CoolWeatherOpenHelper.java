package com.example.coolweather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lenovo on 2016/5/1.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper
{
    private static final String CREATE_TABLE_PROVINCE="create table t_provice(" +
            "id integer primary key autoincrement," +
            "province_name varchar(20)," +
            "province_code varchar(10))";
    private static final String CREATE_TABLE_CITY="create table t_city(" +
            "id integer primary key autoincrement" +
            "city_name varchar(20)," +
            "city_code varchar(10)," +
            "province_id integer)";
    private static final String CREATE_TABLE_COUNTY="create table t_county(" +
            "id integer primary key autoincrement," +
            "county_name varchar(20)," +
            "county_code varchar(10)," +
            "city_id integer)";
    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_PROVINCE);
        db.execSQL(CREATE_TABLE_CITY);
        db.execSQL(CREATE_TABLE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table if exists t_province");
        db.execSQL("drop table if exists t_city");
        db.execSQL("drop table if exists t_county");
        onCreate(db);
    }
}
