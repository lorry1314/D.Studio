package com.dstudio.wd824.one.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dstudio.wd824.one.MainActivity;
import com.dstudio.wd824.one.R;
import com.dstudio.wd824.one.SettingActivity;


/**
 * Created by wd824 on 2016/5/5.
 */
public class FgAbout extends Fragment
{
    private TextView topTitle;
    private Button btnRight;
    private ScrollView scrollView;
    private ProgressBar bar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.view_about, container, false);
        btnRight = (Button) getActivity().findViewById(R.id.right_button);
        btnRight.setText(R.string.setting);
        btnRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!btnRight.getText().equals(""))
                {
                    Intent intent = new Intent(getActivity(), SettingActivity.class);
                    startActivity(intent);
                }
            }
        });

        topTitle = (TextView) getActivity().findViewById(R.id.top_title);
        topTitle.setText("关于");
        bar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        scrollView = (ScrollView) getActivity().findViewById(R.id.scroll_view);
        scrollView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                return false;
            }
        });
        return view;
    }
}
