package com.example.coolweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.R;
import com.example.coolweather.model.City;
import com.example.coolweather.model.CoolWeatherDB;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;
import com.example.coolweather.util.HttpCallBackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/5/1.
 */
public class ChooseArea extends AppCompatActivity
{
    private static final int LEVEL_PROVINCE=0;
    private static final int LEVEL_CITY=1;
    private static final int LEVEL_COUNTY=2;
    private TextView titleText=null;
    private ListView listView=null;
    private ArrayAdapter<String> adapter;
    private List<String> items;
    private CoolWeatherDB coolWeatherDB;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private boolean fromWeatherInformation;
//    private County selectedCounty;
    private int currentLevel;
    private ProgressDialog progressDialog=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        fromWeatherInformation=getIntent().getBooleanExtra("fromWeatherInformation",false);
        SharedPreferences sp=getSharedPreferences("weatherInfo",MODE_PRIVATE);
        if (sp.getBoolean("county_selected",false)&&!fromWeatherInformation)
        {
            Intent intent=new Intent(this,WeatherInformation.class);
            startActivity(intent);
            finish();
            return;
        }
        titleText=(TextView) findViewById(R.id.title_text);
        listView=(ListView) findViewById(R.id.list_view);
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        items=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (currentLevel==LEVEL_PROVINCE)
                {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }
                else if (currentLevel==LEVEL_CITY)
                {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
                else if (currentLevel==LEVEL_COUNTY)
                {
                    String countyCode=countyList.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseArea.this,WeatherInformation.class);
                    intent.putExtra("countyCode",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }
    private void queryProvinces()
    {
        provinceList=coolWeatherDB.loadProvinces();
        if (provinceList.size()>0)
        {
            items.clear();
            for (Province province:provinceList)
            {
                String provinceName=province.getProvinceName();
                items.add(provinceName);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("China");
            currentLevel=LEVEL_PROVINCE;
        }
        else
            queryFromServer("","province");
    }
    private void queryCities()
    {
        cityList=coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size()>0)
        {
            items.clear();
            for (City city:cityList)
            {
                String cityName=city.getCityName();
                items.add(cityName);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }
        else
            queryFromServer(selectedProvince.getProvinceCode(),"city");
    }
    private void queryCounties()
    {
        countyList=coolWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size()>0)
        {
            items.clear();
            for (County county:countyList)
            {
                String countyName=county.getCountyName();
                items.add(countyName);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }
        else
            queryFromServer(selectedCity.getCityCode(),"county");

    }
    private void queryFromServer(String code, final String type)
    {
        String address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response)
            {
                boolean result=false;
                if (type.equals("province"))
                    result = Utility.handleProvincesResponse(coolWeatherDB, response);
                else if (type.equals("city"))
                    result=Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());
                else if (type.equals("county"))
                    result=Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                if (result)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            closeProgressDialog();
                            if (type.equals("province"))
                                queryProvinces();
                            else if (type.equals("city"))
                                queryCities();
                            else if (type.equals("county"))
                                queryCounties();
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        closeProgressDialog();
                        Toast.makeText(ChooseArea.this, "Loading Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
    private void showProgressDialog()
    {
        if (progressDialog==null)
        {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Loading.....");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog()
    {
        if (progressDialog!=null)
            progressDialog.dismiss();
    }

    @Override
    public void onBackPressed()
    {
        if (currentLevel==LEVEL_COUNTY)
            queryCities();
        else if (currentLevel==LEVEL_CITY)
            queryProvinces();
        else if (fromWeatherInformation)
        {
            Intent intent=new Intent(this,WeatherInformation.class);
            startActivity(intent);
        }
        else if (currentLevel==LEVEL_PROVINCE)
            finish();
    }
}