package com.dstudio.wd.one.entity;

import java.util.List;

/**
 * Created by wd824 on 2016/6/19.
 */
public class Root
{
    private int res;

    private List<Data> data ;

    public void setRes(int res)
    {
        this.res = res;
    }

    public int getRes()
    {
        return this.res;
    }

    public void setData(List<Data> data)
    {
        this.data = data;
    }

    public List<Data> getData()
    {
        return this.data;
    }
}