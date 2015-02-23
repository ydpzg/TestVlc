package com.example.TestVlc.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;

import com.example.TestVlc.R;

import org.videolan.VlcVideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by YDP on 2015/2/22.
 */
public class VlcVideoFullActivity extends Activity {

    @InjectView(R.id.vvv_main)
    VlcVideoView mVvvMain;

    private String mUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBundle();
        initView();
        initValue();
    }

    private void initBundle() {
        if (getIntent().getStringExtra("url") != null) {
            mUrl = getIntent().getStringExtra("url");
        }
    }

    private void initView() {
        setContentView(R.layout.activity_video_vlc_full);
        ButterKnife.inject(this);
    }

    private void initValue() {
        mVvvMain.playMRL(mUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVvvMain.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVvvMain.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVvvMain.onConfigurationChanged();
    }
}
