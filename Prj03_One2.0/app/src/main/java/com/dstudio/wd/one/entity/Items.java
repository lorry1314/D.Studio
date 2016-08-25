package com.dstudio.wd.one.entity;

import com.google.gson.JsonElement;

import org.json.JSONObject;

/**
 * Created by wd824 on 2016/6/19.
 */
public class Items
{
    private String time;

    private int type;

    private JsonElement content;

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getTime()
    {
        return this.time;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return this.type;
    }

    public void setContent(JsonElement content)
    {
        this.content = content;
    }

    public JsonElement getContent()
    {
        return this.content;
    }
}
