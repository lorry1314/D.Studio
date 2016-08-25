package com.dstudio.wd.one.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dstudio.wd.one.R;
import com.dstudio.wd.one.entity.Detail;
import com.dstudio.wd.one.util.HttpCallbackListener;
import com.dstudio.wd.one.util.HttpUtil;
import com.dstudio.wd.one.util.LocalCache;
import com.dstudio.wd.one.util.LocalData;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
                getPraiseNum();
            }
            else if (msg.what == SHOW_IMG)
            {
                imgHp.setImageBitmap((Bitmap) msg.obj);
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
                    Toast.makeText(mContext, "你已经点过赞了", Toast.LENGTH_SHORT).show();
                }
            }
        });
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        cardDetail = (CardView) view.findViewById(R.id.detail_card_view);
        if (!"".equals(hpContentId))
        {
            getContent();
        }
        return view;
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

                // 图片加载线程
                final String imgUrl = data.getString("hp_img_url");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Bitmap bitmap = LocalCache.readImgCache(getActivity(), imgUrl);
                        if(bitmap == null)
                        {
                            LocalCache.writeCache(getActivity(), imgUrl, "Bitmap");
                            while (true)
                            {
                                if (LocalCache.writeCache(getActivity(), imgUrl, "Bitmap"))
                                {
                                    bitmap = LocalCache.readImgCache(getActivity(), imgUrl);
                                    break;
                                }
                            }
                        }
                        Message message1 = new Message();
                        message1.what = SHOW_IMG;
                        message1.obj = bitmap;
                        handler.sendMessage(message1);
                    }
                }).start();

                Detail detail = new Detail();
                detail.setHpTitle(data.getString("hp_title"));
                detail.setHpAuthor(data.getString("hp_author"));
                detail.setHpContent(data.getString("hp_content"));
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
}
