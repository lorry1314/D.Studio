package com.dstudio.wd.one.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dstudio.wd.one.R;
import com.dstudio.wd.one.entity.Detail;
import com.dstudio.wd.one.util.HttpCallbackListener;
import com.dstudio.wd.one.util.HttpUtil;
import com.dstudio.wd.one.util.LocalData;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by wd824 on 2016/6/17.
 */
public class DetailFragment extends Fragment
{
    private Context mContext;

    private RequestQueue mQueue;

    private ScrollView scrollView;
    private CardView cardDetail;
    private TextView txtMaketTime;
    private TextView txtHpTitle;
    private ImageView imgHp;
    private TextView txtHpAuthor;
    private TextView txtHpContent;
    private ImageButton btnPraise;
    private TextView txtPraiseNum;

    private String postParam;   // 点赞POST请求参数
    private String hpContentId;  // 当前期数的id
    private String hpLastUpdate;  // 当前期数的最后更新时间
    private boolean isPraised = false; // 当前期数是否已赞
    private String webUrl;
    private String imgUrl;

    private static final String TAG = "DetailFragment";
    private static final int SHOW_CONTENT = 0;
    private static final int SHOW_IMG = 1;
    private static final int SHOW_PRAISE = 2;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == SHOW_CONTENT)
            {
                Detail detail = (Detail) msg.obj;
                txtMaketTime.setText(detail.getHpMaketTime());
                txtHpTitle.setText(detail.getHpTitle());
                txtHpAuthor.setText(detail.getHpAuthor());
                txtHpContent.setText(detail.getHpContent());
                hpLastUpdate = detail.getLastUpdate().replace(" ", "%20");
                webUrl = detail.getWebUrl();
                imgUrl = detail.getHpImgUrl();
                getPraiseNum();
            }
            else if (msg.what == SHOW_PRAISE)
            {
                txtPraiseNum.setText((String) msg.obj);
                if (isPraised)
                {
                    btnPraise.setImageResource(R.drawable.ic_favorite_red_22dp);
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mContext = getActivity().getApplicationContext();
        if (getArguments() != null)
        {
            hpContentId = getArguments().getString("hpid");
        }
        mQueue = Volley.newRequestQueue(mContext);

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        txtMaketTime = (TextView) view.findViewById(R.id.makettime);
        txtHpTitle = (TextView) view.findViewById(R.id.hp_title);
        imgHp = (ImageView) view.findViewById(R.id.img);
        txtHpAuthor = (TextView) view.findViewById(R.id.hp_author);
        txtHpContent = (TextView) view.findViewById(R.id.hp_content);
        txtPraiseNum = (TextView) view.findViewById(R.id.praise_num);
        btnPraise = (ImageButton) view.findViewById(R.id.add_praise);
        btnPraise.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!isPraised)
                {
                    addPraise();
                    setPraised();
                }
                else
                {
                    int praiseNum = Integer.parseInt(txtPraiseNum.getText().toString()) - 1;
                    txtPraiseNum.setText(praiseNum + "");
                    isPraised = false;
                    btnPraise.setImageResource(R.drawable.ic_favorite_black_22dp);
                }
            }
        });
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        cardDetail = (CardView) view.findViewById(R.id.detail_card_view);
        registerForContextMenu(cardDetail);

        if (!"".equals(hpContentId))
        {
            getContent();
        }
        return view;
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        MenuInflater inflater = new MenuInflater(mContext);
        inflater.inflate(R.menu.mean_share, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if (getUserVisibleHint())
        {
            switch (item.getItemId())
            {
                case R.id.share_img:
                    HttpUtil.showShare(mContext, txtHpTitle.getText().toString(), txtHpContent.getText().toString(),
                            webUrl, imgUrl);
                    break;
                case R.id.save_img:
                    imgHp.setDrawingCacheEnabled(true);
                    Bitmap img = ((BitmapDrawable) imgHp.getDrawable()).getBitmap();
                    saveImageToGallery(mContext, img, txtHpTitle.getText().toString());
                    imgHp.setDrawingCacheEnabled(false);
                    break;
            }
            return true;
        }
        return false;
    }

    public void getContent()
    {
        SharedPreferences sp = mContext.getSharedPreferences(hpContentId, 0);
        if (!hpContentId.equals(sp.getString("hpContentId", null)))
        {
            sendRequest(hpContentId);
        }
        else
        {
            String localData = LocalData.load(getString(R.string.file_name_detail) + hpContentId, mContext);
            parseForDetail(localData);
        }

        isPraised = getPraised();
        if (isPraised)
        {
            btnPraise.setImageResource(R.drawable.ic_favorite_red_22dp);
        }
        else
        {
            btnPraise.setImageResource(R.drawable.ic_favorite_black_22dp);
        }

    }

    public boolean getPraised()
    {
        SharedPreferences sp = mContext.getSharedPreferences(hpContentId, 0);
        return sp.getBoolean("isPraised", false);
    }

    public void setPraised()
    {
        isPraised = true;
        SharedPreferences.Editor editor = mContext.getSharedPreferences(hpContentId, Context.MODE_PRIVATE).edit();
        editor.putBoolean("isPraised", isPraised);
        editor.commit();
    }

    public void sendRequest(String hpContentId)
    {
        String api = getString(R.string.detail_api) + hpContentId;

        JsonObjectRequest request = new JsonObjectRequest(api, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject jsonObject)
                    {
                        parseForDetail(jsonObject.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        if (volleyError != null)
                        {
                            Log.e(TAG, volleyError.toString());
                        }
                    }
                });
        mQueue.add(request);

        SharedPreferences.Editor editor = mContext.getSharedPreferences(hpContentId, Context.MODE_PRIVATE).edit();
        editor.putString("hpContentId", hpContentId);
        editor.commit();
    }

    public void parseForDetail(String response)
    {
        try
        {
            JSONObject result = new JSONObject(response);
            String res = result.getString("res");
            if (res.equals("0"))
            {
                JSONObject data = result.getJSONObject("data");
                hpContentId = data.getString("hpcontent_id");
                LocalData.save(response, getString(R.string.file_name_detail) + hpContentId, mContext);

                String imgUrl = data.getString("hp_img_url");
                ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
                ImageLoader.ImageListener listener = ImageLoader.getImageListener(imgHp, R.drawable.loading, R.drawable.loading);
                imageLoader.get(imgUrl, listener, 1000, 1000);

                Detail detail = new Detail();
                detail.setHpTitle(data.getString("hp_title"));
                detail.setHpAuthor(data.getString("hp_author"));
                detail.setHpContent(data.getString("hp_content"));
                detail.setHpImgUrl(data.getString("hp_img_url"));
                detail.setWebUrl(data.getString("web_url"));
                detail.setLastUpdate(data.getString("last_update_date"));
                detail.setPraiseNum(data.getString("praisenum"));
                java.util.Date date= java.sql.Date.valueOf(data.getString("hp_makettime").substring(0, 10));
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
                detail.setHpMaketTime(sdf.format(date));

                Message message = new Message();
                message.what = SHOW_CONTENT;
                message.obj = detail;
                handler.sendMessage(message);
            }
            else
            {
                Log.e(TAG, "error code is: " + res);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void addPraise()
    {
        try
        {
            TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = manager.getDeviceId();
            String simSerialNumber = manager.getSimSerialNumber();
            String androidId = android.provider.Settings.Secure.getString(mContext.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) deviceId.hashCode() << 32) | simSerialNumber.hashCode());

            postParam = "itemid=" + hpContentId + "&type=hpcontent&deviceid=" + deviceUuid.toString() + "&devicetype=android";
            HttpUtil.sendPost(getString(R.string.add_praise_api), postParam, new HttpCallbackListener()
            {
                @Override
                public void onFinish(String response)
                {
                    Log.d(TAG, response);
                    getPraiseNum();
                }

                @Override
                public void onError(Exception e)
                {
                    e.printStackTrace();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(mContext, "发送数据失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void getPraiseNum()
    {
        String url = getString(R.string.get_praise_num) + hpContentId + "/" + hpLastUpdate;
        HttpUtil.sendGet(url, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                try
                {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("res").equals("0"))
                    {
                        JSONObject data = result.getJSONObject("data");
                        Message message = new Message();
                        message.what = SHOW_PRAISE;
                        message.obj = data.getString("praisenum");
                        handler.sendMessage(message);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    public static void saveImageToGallery(Context context, Bitmap bmp, String fileName)
    {
        // 首先保存图片
        String savePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
        {
            savePath = Environment.getExternalStorageDirectory().getPath();
        }
        else
        {
            savePath = context.getFilesDir().getPath();
        }
        File appDir = new File(savePath, Environment.DIRECTORY_PICTURES + "/ONE");
        if (!appDir.exists())
        {
            appDir.mkdir();
        }
        fileName = fileName + ".jpg";
        File file = new File(appDir, fileName);
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try
        {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        String path = savePath + "/" + Environment.DIRECTORY_PICTURES + "/ONE";
        Log.d(TAG, path);
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path + "/" + fileName)));
        Toast.makeText(context, "图片已保存在" + path, Toast.LENGTH_LONG).show();
    }

    public class BitmapCache implements ImageLoader.ImageCache
    {
        private LruCache<String, Bitmap> mCache;

        public BitmapCache()
        {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize)
            {
                @Override
                protected int sizeOf(String key, Bitmap value)
                {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String s)
        {
            return mCache.get(s);
        }

        @Override
        public void putBitmap(String s, Bitmap bitmap)
        {
            mCache.put(s, bitmap);
        }
    }

    public void onResume()
    {
        super.onResume();
        MobclickAgent.onPageStart("DetailFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPageEnd("DetailFragment");
    }
}
