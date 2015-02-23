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
public class VlcVideoViewActivity extends Activity {

    @InjectView(R.id.vvv_main)
    VlcVideoView mVvvMain;
    @InjectView(R.id.btn_play)
    Button mBtnPlay;
    @InjectView(R.id.btn_stop)
    Button mBtnStop;

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
        setContentView(R.layout.activity_video_vlc);
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

    @OnClick(R.id.btn_play)
    public void btnPlayOnClick() {
        mVvvMain.playMRL(mUrl);
    }

    @OnClick(R.id.btn_stop)
    public void btnStopOnClick() {
        mVvvMain.stop();
    }
}
