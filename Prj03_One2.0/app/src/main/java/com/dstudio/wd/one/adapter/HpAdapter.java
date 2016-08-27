package com.dstudio.wd.one.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dstudio.wd.one.R;
import com.dstudio.wd.one.entity.hp.Data;
import com.dstudio.wd.one.util.HttpUtil;
import com.dstudio.wd.one.util.LocalCache;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by wd824 on 2016/8/7.
 */
public class HpAdapter extends RecyclerView.Adapter<HpAdapter.ViewHolder>
{
    private LinkedList<Data> dataList;
    private DiskLruCache mDiskLruCache;
    private Set<BitmapWorkerTask> taskCollection;
    private LruCache<String, Bitmap> mMemoryCache;
    private RecyclerView photoWall;
    private MyItemClickListener itemClickListener;
    private int mItemHeight = 0;

    public HpAdapter(Context context, LinkedList<Data> dataList, RecyclerView photoWall)
    {
        super();
        this.dataList = dataList;
        this.photoWall = photoWall;
        taskCollection = new HashSet<BitmapWorkerTask>();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value)
            {
                return value.getByteCount();
            }
        };
        try
        {
            File cacheDir = getDiskCacheDir(context, "Bitmap");
            if (!cacheDir.exists())
            {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = View.inflate(parent.getContext(), R.layout.item_detail, null);
        ViewHolder holder = new ViewHolder(view, itemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        String imgUrl = getItem(position).getHpImgUrl();
        String hpTitle = getItem(position).getHpTitle();
        holder.imgDetail.setImageResource(R.drawable.loading);
        holder.imgDetail.setTag(imgUrl);
        loadBitmaps(holder, holder.imgDetail, imgUrl);
    }

    @Override
    public int getItemCount()
    {
        return dataList.size();
    }

    public Data getItem(int position)
    {
        return dataList.get(position);
    }

    public void setOnItemClickListener(MyItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }
    /*
    public void setTxtColor(ViewHolder holder)
    {
        holder.imgDetail.setDrawingCacheEnabled(true);
        if (holder.imgDetail.getDrawingCache() != null)
        {
            Bitmap bitmap = Bitmap.createBitmap(holder.imgDetail.getDrawingCache());
            Palette palette = Palette.generate(bitmap);
            Palette.Swatch swatch = palette.getLightVibrantSwatch();
            if (swatch != null)
            {
                holder.txtDetail.setTextColor(swatch.getBodyTextColor());
            }
        }
        holder.imgDetail.setDrawingCacheEnabled(false);
    }
    */

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public ImageView imgDetail;
        public TextView txtDetail;
        public RelativeLayout relativeLayout;
        public MyItemClickListener itemClickListener;

        public ViewHolder(View itemView, MyItemClickListener itemClickListener)
        {
            super(itemView);
            imgDetail = (ImageView) itemView.findViewById(R.id.detail_img);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.item_detail_layout);
            this.itemClickListener = itemClickListener;
            relativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if (itemClickListener != null)
            {
                itemClickListener.onItemClick(view, getPosition());
            }
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {
        if (getBimpaFromMemoryCache(key) == null)
        {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBimpaFromMemoryCache(String key)
    {
        return mMemoryCache.get(key);
    }

    public void loadBitmaps(ViewHolder holder, ImageView imageView, String imgUrl)
    {
        try
        {
            Bitmap bitmap = getBimpaFromMemoryCache(imgUrl);
            if (bitmap == null)
            {
                BitmapWorkerTask task = new BitmapWorkerTask();
                taskCollection.add(task);
                task.execute(imgUrl);
            }
            else
            {
                if (imageView != null && bitmap != null)
                {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void cancalAllTask()
    {
        if (taskCollection != null)
        {
            for (BitmapWorkerTask task : taskCollection)
            {
                task.cancel(false);
            }
        }
    }

    public File getDiskCacheDir(Context context, String uniqueName)
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

    public int getAppVersion(Context context)
    {
        try
        {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 1;
    }

    public void setItemHeight(int height)
    {
        if (height == mItemHeight)
        {
            return;
        }
        mItemHeight = height;
        notifyDataSetChanged();
    }

    public String hashKeyDisk(String key)
    {
        String cacheKey;
        try
        {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    public void flushCache()
    {
        if (mDiskLruCache != null)
        {
            try
            {
                mDiskLruCache.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private String bytesToHexString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
        {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1)
            {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>
    {

        private String imgUrl;

        @Override
        protected Bitmap doInBackground(String... params)
        {
            imgUrl = params[0];
            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            DiskLruCache.Snapshot snapshot = null;
            try
            {
                final String key = LocalCache.hashKeyForDisk(imgUrl);
                snapshot = mDiskLruCache.get(key);
                if (snapshot == null)
                {
                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if (editor != null)
                    {
                        OutputStream outputStream = editor.newOutputStream(0);
                        if (downloadUrlToStream(imgUrl, outputStream))
                        {
                            editor.commit();
                        }
                        else
                        {
                            editor.abort();
                        }
                    }
                    snapshot = mDiskLruCache.get(key);
                }
                if (snapshot != null)
                {
                    fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                }
                Bitmap bitmap = null;
                if (fileDescriptor != null)
                {
                    bitmap = compressBitmap(fileDescriptor);
                }
                if (bitmap != null)
                {
                    addBitmapToMemoryCache(key, bitmap);
                }
                return bitmap;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) photoWall.findViewWithTag(imgUrl);
            if (imageView != null && bitmap != null)
            {
                imageView.setImageBitmap(bitmap);
            }
            taskCollection.remove(this);
        }

        private boolean downloadUrlToStream(String imgUrl, OutputStream outputStream)
        {
            HttpURLConnection connection = null;
            BufferedOutputStream out = null;
            BufferedInputStream in = null;
            try
            {
                final URL url = new URL(imgUrl);
                connection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(connection.getInputStream(), 8 * 1024);
                out = new BufferedOutputStream(outputStream, 8 * 1024);
                int b;
                while ((b = in.read()) != -1)
                {
                    out.write(b);
                }
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (connection != null)
                {
                    connection.disconnect();
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
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            return false;
        }

        private Bitmap compressBitmap(FileDescriptor fileDescriptor)
        {
            Bitmap bitmap = null;
            BitmapFactory.Options ops = new BitmapFactory.Options();
            ops.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, ops);
            ops.inJustDecodeBounds = false;
            int width = ops.outWidth;
            int height = ops.outHeight;
            float fWidth = 400f;
            float fHeight = 300f;
            int be = 1;
            if (width > height && width > fWidth)
            {
                be = (int) (ops.outWidth / fWidth);
            }
            else if (width < height && height > fHeight)
            {
                be = (int) (ops.outHeight / fHeight);
            }
            if (be < 0)
            {
                be = 1;
            }
            ops.inSampleSize = be;
            ops.inPurgeable = true;
            ops.inInputShareable = true;
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, ops);
            return bitmap;
        }
    }
}
