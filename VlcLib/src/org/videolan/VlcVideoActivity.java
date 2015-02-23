package org.videolan;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.libvlc.R;
import org.videolan.vlc.util.VLCInstance;

public class VlcVideoActivity extends Activity implements SurfaceHolder.Callback, IVideoPlayer {

    private final static String TAG = "[VlcVideoActivity]";

    private SurfaceView mSurfaceView;
    private LibVLC mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;

    private View mLoadingView;
    private View mBufferingView;

    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    private String mUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_video_vlc);

        if (getIntent().getStringExtra("url") != null) {
            mUrl = getIntent().getStringExtra("url");
        }

        mSurfaceView = (SurfaceView) findViewById(R.id.video);
        mLoadingView = findViewById(R.id.video_loading);
        mBufferingView = findViewById(R.id.video_buffering);
        try {
            mMediaPlayer = VLCInstance.getLibVlcInstance(getApplicationContext());
        } catch (LibVlcException e) {
            e.printStackTrace();
        }

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
        mSurfaceHolder.addCallback(this);

        mMediaPlayer.eventVideoPlayerActivityCreated(true);

        EventHandler em = EventHandler.getInstance();
        em.addHandler(mVlcHandler);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mSurfaceView.setKeepScreenOn(true);
        //		mMediaPlayer.setMediaList();
        //		mMediaPlayer.getMediaList().add(new Media(mMediaPlayer, "http://live.3gv.ifeng.com/zixun.m3u8"), false);
        //		mMediaPlayer.playIndex(0);
        mMediaPlayer.playMRL(mUrl);
        mHandler.sendEmptyMessage(HANDLER_LOADING_START);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mSurfaceView.setKeepScreenOn(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.eventVideoPlayerActivityCreated(false);

            EventHandler em = EventHandler.getInstance();
            em.removeHandler(mVlcHandler);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mMediaPlayer != null) {
            mSurfaceHolder = holder;
            mMediaPlayer.attachSurface(holder.getSurface(), this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
        if (mMediaPlayer != null) {
            mMediaPlayer.attachSurface(holder.getSurface(), this);//, width, height
        }
        if (width > 0) {
            mVideoHeight = height;
            mVideoWidth = width;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mMediaPlayer != null) {
            mMediaPlayer.detachSurface();
        }
    }

    @Override
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        mVideoHeight = height;
        mVideoWidth = width;
        mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth = visible_width;
        mSarNum = sar_num;
        mSarDen = sar_den;
        mHandler.removeMessages(HANDLER_SURFACE_SIZE);
        mHandler.sendEmptyMessage(HANDLER_SURFACE_SIZE);
    }

    private static final int HANDLER_LOADING_START = 1;
    private static final int HANDLER_LOADING_END = 2;
    private static final int HANDLER_BUFFERING_START = 3;
    private static final int HANDLER_BUFFERING_END = 4;
    private static final int HANDLER_SURFACE_SIZE = 5;

    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;

    private Handler mVlcHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null || msg.getData() == null)
                return;

            switch (msg.getData().getInt("event")) {
                case EventHandler.MediaPlayerTimeChanged:
                    Log.d(TAG, "MediaPlayerTimeChanged time=" + msg.getData().get("data"));
                    break;
                case EventHandler.MediaPlayerPositionChanged:
                    Log.d(TAG, "MediaPlayerPositionChanged position=" + msg.getData().get("data"));
                    break;
                case EventHandler.MediaPlayerPlaying:
                    Log.d(TAG, "MediaPlayerPlaying");
                    mHandler.removeMessages(HANDLER_LOADING_END);
                    mHandler.sendEmptyMessage(HANDLER_LOADING_END);
                    mHandler.removeMessages(HANDLER_BUFFERING_START);
                    mHandler.sendEmptyMessage(HANDLER_BUFFERING_START);
                    break;
                case EventHandler.MediaPlayerStopped:
                    Log.d(TAG, "MediaPlayerStopped");
                    mHandler.removeMessages(HANDLER_LOADING_END);
                    mHandler.sendEmptyMessage(HANDLER_LOADING_END);

                    mHandler.removeMessages(HANDLER_BUFFERING_END);
                    mHandler.sendEmptyMessage(HANDLER_BUFFERING_END);
                    break;
                case EventHandler.MediaPlayerBuffering:
                    Log.d(TAG, "MediaPlayerBuffering");
                    break;
                case EventHandler.MediaParsedChanged:
                    Log.d(TAG, "MediaParsedChanged");
                    break;
                case EventHandler.MediaPlayerLengthChanged:
                    break;
                case EventHandler.MediaPlayerEndReached:
                    //播放完成
                    break;
                case EventHandler.MediaPlayerVout:
                    mHandler.removeMessages(HANDLER_BUFFERING_END);
                    mHandler.sendEmptyMessage(HANDLER_BUFFERING_END);
                    Log.d(TAG, "MediaPlayerVout");
                    break;
                case EventHandler.MediaPlayerEncounteredError:
                    Log.i(TAG, "MediaPlayerEncounteredError");
                    break;
                case EventHandler.HardwareAccelerationError:
                    Log.i(TAG, "HardwareAccelerationError");
                    break;
                default:
                    Log.d(TAG, "mVLcHandler=" + msg.getData().getInt("event"));
            }

        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_LOADING_START:
                    showLoading();
                    break;
                case HANDLER_BUFFERING_START:
                    showBuffering();
                    break;
                case HANDLER_LOADING_END:
                    hideLoading();
                    break;
                case HANDLER_BUFFERING_END:
                    hideBuffering();
                    break;
                case HANDLER_SURFACE_SIZE:
                    changeSurfaceSize();
                    break;
            }
        }
    };

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void showBuffering() {
        mBufferingView.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
    }

    private void hideBuffering() {
        mBufferingView.setVisibility(View.GONE);
    }

    private void changeSurfaceSize() {
        // get screen size
        int dw = getWindowManager().getDefaultDisplay().getWidth();
        int dh = getWindowManager().getDefaultDisplay().getHeight();

        // calculate aspect ratio
        double ar = (double) mVideoWidth / (double) mVideoHeight;
        // calculate display aspect ratio
        double dar = (double) dw / (double) dh;

        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_FIT_HORIZONTAL:
                dh = (int) (dw / ar);
                break;
            case SURFACE_FIT_VERTICAL:
                dw = (int) (dh * ar);
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoHeight;
                dw = mVideoWidth;
                break;
        }

        mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
        ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
        lp.width = dw;
        lp.height = dh;
        mSurfaceView.setLayoutParams(lp);
        mSurfaceView.invalidate();
    }
}
