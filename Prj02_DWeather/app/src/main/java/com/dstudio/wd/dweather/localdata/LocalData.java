package com.dstudio.wd.dweather.localdata;

import android.content.Context;
import android.util.Log;

import com.dstudio.wd.dweather.http.HttpUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by wd824 on 2016/5/23.
 */
public class LocalData
{
    public static void save(String data, String parm, Context context)
    {
        try
        {
            FileOutputStream outStream = context.openFileOutput(parm + ".txt", Context.MODE_PRIVATE);
            outStream.write(data.getBytes());
            outStream.close();
            Log.d("debug", "save ok!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String load(String parm, Context context)
    {
        FileInputStream inStream;
        ByteArrayOutputStream stream = null;
        try
        {
            inStream = context.openFileInput(parm + ".txt");
            stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while((length = inStream.read(buffer)) != -1)
            {
                stream.write(buffer, 0, length);
            }
            stream.close();
            inStream.close();
            return stream.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    // 删除本地所有数据
    public static void delete(Context context)
    {
        String[] fileList = context.fileList();
        for(int i = 0; i < fileList.length; i++)
        {
            context.deleteFile(fileList[i]);
        }
    }
}
