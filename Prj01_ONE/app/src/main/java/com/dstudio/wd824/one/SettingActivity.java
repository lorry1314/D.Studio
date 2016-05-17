package com.dstudio.wd824.one;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dstudio.wd824.one.Data.LocalCache;
import com.dstudio.wd824.one.Data.LocalData;

public class SettingActivity extends Activity implements View.OnClickListener
{
    private Context mContext;

    private Button btnLeft;
    private Button btnClnCache;
    private TextView txtTitle;
    private TextView txtCacheSize;
    private TextView txtVerName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);

        mContext = SettingActivity.this;

        btnLeft = (Button) findViewById(R.id.left_button);
        btnClnCache = (Button) findViewById(R.id.clean_cache);
        btnLeft.setOnClickListener(this);
        btnClnCache.setOnClickListener(this);
        txtTitle = (TextView) findViewById(R.id.top_title);
        txtCacheSize = (TextView) findViewById(R.id.txt_cache_size);
        txtVerName = (TextView) findViewById(R.id.txt_version);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        btnLeft.setTypeface(iconfont);
        btnLeft.setText(R.string.arrow_left);

        txtTitle.setText("设置");
        txtCacheSize.setText(LocalCache.getSize(mContext, "Bitmap"));

        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            txtVerName.setText(info.versionName);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.clean_cache:
                LocalCache.cleanCache(mContext, "BitMap");
                txtCacheSize.setText("0MB");
                LocalData.delete(mContext);
                Toast.makeText(mContext, "缓存已清除 (～￣▽￣)～*", Toast.LENGTH_SHORT).show();
                break;
            case R.id.left_button:
                finish();
                break;
            default:
                break;
        }
    }
}
