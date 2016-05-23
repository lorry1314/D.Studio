package com.dstudio.wd.dweather.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wd824 on 2016/5/7.
 */
public class LocalCache
{
    DiskLruCache diskLruCache = null;

    /**
     * 创建DiskLruCache实例
     * @param context
     * @param uniqueName
     * @return
     */
    public static DiskLruCache openDiskLruCache(Context context, String uniqueName)
    {
        DiskLruCache mDiskLruCache = null;
        try {
            File cacheDir = getDiskCacheDir(context, uniqueName);
            if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mDiskLruCache;
    }

    /**
     *
     * @param context
     * @param uniqueName 区分不同的数据
     * @return  缓存路径
     */
    public static File getDiskCacheDir(Context context, String  uniqueName)
    {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
            || !Environment.isExternalStorageRemovable())
        {
            cachePath = context.getExternalCacheDir().getPath();
        }
        else
        {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return 1;
    }

    private static boolean downloadUrlToStream(String urlString, OutputStream outputStream)
    {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try
        {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(8000);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1)
            {
                out.write(b);
            }

            return true;
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();

            }
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (final IOException e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static String hashKeyForDisk(String url)
    {
        String cachKey;
        try
        {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cachKey = bytesToHexString(mDigest.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            cachKey = String.valueOf(url.hashCode());
        }
        return cachKey;
    }

    private static String bytesToHexString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
        {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1)
            {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static boolean writeCache(final Context context, final String strUrl, final String uniqueName)
    {
        try
        {
            String key = hashKeyForDisk(strUrl);
            DiskLruCache mDiskLruCache = openDiskLruCache(context, uniqueName);
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null)
            {
                OutputStream outputStream = editor.newOutputStream(0);
                if(downloadUrlToStream(strUrl, outputStream))
                {
                    editor.commit();
                }
                else
                {
                    editor.abort();
                }
            }

            mDiskLruCache.flush();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Log.d("debug", "ok!!");
        return false;
    }

    public static Bitmap readImgCache(Context context, String imgUrl, String uniqueName)
    {
        Bitmap bitmap = null;
        String key = hashKeyForDisk(imgUrl);
        try
        {
            DiskLruCache mDiskLruCache = openDiskLruCache(context, uniqueName);
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot != null)
            {
                InputStream is = snapshot.getInputStream(0);
                bitmap = BitmapFactory.decodeStream(is);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
        return bitmap;
    }

    private static boolean cleanCache(Context context, String uniqueName)
    {
        boolean b;
        try
        {
            DiskLruCache mDiskLruCache = openDiskLruCache(context, uniqueName);
            mDiskLruCache.delete();
            b = true;

        }
        catch (IOException e)
        {
            b = false;
            e.printStackTrace();
        }
        return b;
    }

}
