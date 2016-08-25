package com.dstudio.wd.one.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wd824 on 2016/8/6.
 */
public class DateUtil
{
    public static String formatEngToNumeric(String dateEng, String engTemplate)
    {
        SimpleDateFormat format = new SimpleDateFormat(engTemplate, Locale.ENGLISH);
        Date date;
        try
        {
            date = format.parse(dateEng);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            return format1.format(date);
        }
        catch (ParseException e)
        {
            Log.e("DateUtil", e.getMessage());
            return null;
        }
    }
}
