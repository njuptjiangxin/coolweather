package com.example.coolweather.model;

/**
 * Created by lenovo on 2016/5/1.
 */
public class Province
{
    private int id;
    private String provinceName;
    private String provinceCode;

    public String getProvinceCode()
    {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode)
    {
        this.provinceCode = provinceCode;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getProvinceName()
    {
        return provinceName;
    }

    public void setProvinceName(String provinceName)
    {
        this.provinceName = provinceName;
    }
}
