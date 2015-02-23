package com.example.TestVlc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.TestVlc.R;

import org.videolan.VlcVideoActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends Activity {
    @InjectView(R.id.et_url)
    EditText mEtUrl;
    @InjectView(R.id.btn_alone)
    Button mBtnAlone;
    @InjectView(R.id.btn_other)
    Button mBtnOther;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mEtUrl.setText("rtsp://192.168.0.103/live/myStream.sdp");
    }

    @OnClick(R.id.btn_alone)
    public void btnAloneOnClick() {
        Intent intent = new Intent(this, VlcVideoFullActivity.class);
        intent.putExtra("url", mEtUrl.getText().toString());
        startActivity(intent);
    }

    @OnClick(R.id.btn_other)
    public void btnOtherOnClick() {
        Intent intent = new Intent(this, VlcVideoViewActivity.class);
        intent.putExtra("url", mEtUrl.getText().toString());
        startActivity(intent);
    }
}
