package com.dstudio.wd.one.entity;

import java.util.List;

/**
 * Created by wd824 on 2016/6/19.
 */
public class Data
{
    private String date;

    private List<Items> items ;

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getDate()
    {
        return this.date;
    }

    public void setItems(List<Items> items)
    {
        this.items = items;
    }

    public List<Items> getItems()
    {
        return this.items;
    }
}
