package com.dstudio.wd824.one;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.dstudio.wd824.one.fragments.FgAbout;
import com.dstudio.wd824.one.fragments.FgHome;
import com.dstudio.wd824.one.fragments.FgQuestion;
import com.dstudio.wd824.one.fragments.FgReading;

import cn.sharesdk.framework.ShareSDK;

public class MainActivity extends Activity implements View.OnClickListener
{

    private LinearLayout topBar;
    private LinearLayout tabLayout;

    private Button btnRight;

    private Button btnHome;
    private Button btnReading;
    private Button btnQuestion;
    private Button btnAbout;
    private ScrollView scrollView;

    private FrameLayout frameLayout;
    private FragmentManager manager;
    private Fragment fgHome;
    private Fragment fgReading;
    private Fragment fgQuestion;
    private Fragment fgAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ShareSDK.initSDK(this);
        topBar = (LinearLayout) findViewById(R.id.top_bar);
        tabLayout = (LinearLayout) findViewById(R.id.tab_layout);
        initView();
        selectFrag(1);
    }

    /**
     * 控件初始化
     */
    private void initView()
    {
        btnRight = (Button) findViewById(R.id.right_button);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        btnRight.setTypeface(iconfont);
        btnRight.setText(R.string.share);

        btnHome = (Button) findViewById(R.id.btn_home);
        btnReading = (Button) findViewById(R.id.btn_reading);
        btnQuestion = (Button) findViewById(R.id.btn_question);
        btnAbout = (Button) findViewById(R.id.btn_about);

        frameLayout = (FrameLayout) findViewById(R.id.fram_layout);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);

        btnHome.setOnClickListener(this);
        btnReading.setOnClickListener(this);
        btnQuestion.setOnClickListener(this);
        btnAbout.setOnClickListener(this);

        fgHome = new FgHome();
        fgReading = new FgReading();
        fgQuestion = new FgQuestion();
        fgAbout = new FgAbout();

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_home:
                selectFrag(1);
                break;
            case R.id.btn_reading:
                selectFrag(2);
                break;
            case R.id.btn_question:
                selectFrag(3);
                break;
            case R.id.btn_about:
                selectFrag(4);
                break;
            default:
                break;
        }

    }

    /**
     * fragment切换
     * @param i
     */
    public void selectFrag(int i)
    {
        setDefaultIcon();
        manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (i)
        {
            case 1:
                setDrawable(btnHome, R.drawable.home_picked);
                btnHome.setTextColor(getResources().getColor(R.color.btn_picked));
                transaction.replace(R.id.fram_layout, fgHome);
                break;
            case 2:
                setDrawable(btnReading, R.drawable.reading_picked);
                btnReading.setTextColor(getResources().getColor(R.color.btn_picked));
                transaction.replace(R.id.fram_layout, fgReading);
                break;
            case 3:
                setDrawable(btnQuestion, R.drawable.question_picked);
                btnQuestion.setTextColor(getResources().getColor(R.color.btn_picked));
                transaction.replace(R.id.fram_layout, fgQuestion);
                break;
            case 4:
                setDrawable(btnAbout, R.drawable.about_picked);
                btnAbout.setTextColor(getResources().getColor(R.color.btn_picked));
                transaction.replace(R.id.fram_layout, fgAbout);
        }
        transaction.commit();
    }

    public void setDefaultIcon()
    {
        setDrawable(btnHome, R.drawable.home);
        setDrawable(btnReading, R.drawable.reading);
        setDrawable(btnQuestion, R.drawable.question);
        setDrawable(btnAbout, R.drawable.about);
    }

    public void setDrawable(Button btn, int resId)
    {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btn.setCompoundDrawables(null, drawable, null, null);
        btnHome.setTextColor(getResources().getColor(R.color.btn_normal));
        btnReading.setTextColor(getResources().getColor(R.color.btn_normal));
        btnQuestion.setTextColor(getResources().getColor(R.color.btn_normal));
        btnAbout.setTextColor(getResources().getColor(R.color.btn_normal));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }
}
