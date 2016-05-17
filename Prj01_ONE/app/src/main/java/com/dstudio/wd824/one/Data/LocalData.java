package com.dstudio.wd824.one.Data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by wd824 on 2016/5/5.
 */
public class LocalData
{
    public static void save(String data, String which, Context context)
    {
        try
        {
            FileOutputStream outStream = context.openFileOutput(which + "data.txt", Context.MODE_PRIVATE);
            outStream.write(data.getBytes());
            outStream.close();
            FileOutputStream outputStream = context.openFileOutput("time.txt", Context.MODE_PRIVATE);
            outputStream.write(HttpUtil.getCurrentDate("hour", 0).getBytes());
            outputStream.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String load(String which, Context context)
    {
        FileInputStream inStream;
        ByteArrayOutputStream stream = null;
        try
        {
            inStream = context.openFileInput(which + "data.txt");
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

    public static void delete(Context context)
    {
        String[] fileList = context.fileList();
        for(int i = 0; i < fileList.length; i++)
        {
            context.deleteFile(fileList[i]);
        }
    }
}
