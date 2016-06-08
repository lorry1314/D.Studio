package com.dstudio.wd.dweather.database;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

/**
 * Created by wd824 on 2016/5/12.
 */
public class AutoCompleteAdapter extends SimpleCursorAdapter
{
    private String queryField;
    private Context context;

    public AutoCompleteAdapter(Context context, int layout, Cursor c, String[] from, int[] to)
    {
        super(context, layout, c, from, to);
        this.context = context;
        queryField = from[0];
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint)
    {
        if(constraint != null)
        {
            return new City(context).queryCity((String) constraint);
        }
        else
        {
            return null;
        }
    }

    @Override
    public CharSequence convertToString(Cursor cursor)
    {
        return cursor.getString(cursor.getColumnIndex(queryField));
    }

}
