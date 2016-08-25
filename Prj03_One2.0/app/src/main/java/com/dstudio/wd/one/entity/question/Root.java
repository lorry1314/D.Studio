package com.dstudio.wd.one.entity.question;

import com.dstudio.wd.one.entity.essay.Data;

/**
 * Created by wd824 on 2016/6/23.
 */
public class Root
{
    private int res;

    private com.dstudio.wd.one.entity.question.Data data;

    public void setRes(int res)
    {
        this.res = res;
    }

    public int getRes()
    {
        return this.res;
    }

    public void setData(com.dstudio.wd.one.entity.question.Data data)
    {
        this.data = data;
    }

    public com.dstudio.wd.one.entity.question.Data getData()
    {
        return this.data;
    }
}
