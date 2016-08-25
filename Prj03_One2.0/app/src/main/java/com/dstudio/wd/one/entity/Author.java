package com.dstudio.wd.one.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wd824 on 2016/6/19.
 */
public class Author
{
    @SerializedName("user_id")
    private String userId;

    @SerializedName("user_name")
    private String userNmae;

    @SerializedName("web_url")
    private String webUrl;

    @SerializedName("desc")
    private String desc;

    @SerializedName("wb_name")
    private String wbName;

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserNmae()
    {
        return userNmae;
    }

    public void setUserNmae(String userNmae)
    {
        this.userNmae = userNmae;
    }

    public String getWebUrl()
    {
        return webUrl;
    }

    public void setWebUrl(String webUrl)
    {
        this.webUrl = webUrl;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getWbName()
    {
        return wbName;
    }

    public void setWbName(String wbName)
    {
        this.wbName = wbName;
    }

    @Override
    public String toString()
    {
        return "Author{" +
                "userId='" + userId + '\'' +
                ", userNmae='" + userNmae + '\'' +
                ", webUrl='" + webUrl + '\'' +
                ", desc='" + desc + '\'' +
                ", wbName='" + wbName + '\'' +
                '}';
    }
}
