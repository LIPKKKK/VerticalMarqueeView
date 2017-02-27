package com.lip.verticalmarqueeviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weds.verticalmarqueeview.VerticalMarqueeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity implements View.OnClickListener {

    @Bind(R.id.tv_start_roll)
    TextView tvStartRoll;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;
    @Bind(R.id.lip_VerticalView)
    VerticalMarqueeView lipVerticalView;
    @Bind(R.id.tv_restart_roll)
    TextView tvRestartRoll;
    @Bind(R.id.tv_pause_roll)
    TextView tvPauseRoll;
    @Bind(R.id.tv_next)
    TextView tvNext;
    private String[] strings;

    private final int START_ROLL = 1000;
    private final int NEXT = 1001;
    private final int PAUSE_ROLL = 1002;
    private final int RESTART_ROLL = 1003;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_ROLL:
                    startRoll();
                    break;
                case NEXT:
                    playNext();
                    break;
                case PAUSE_ROLL:
                    pauseRoll();
                    break;
                case RESTART_ROLL:
                    restartRoll();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        strings = new String[3];
        strings[0] = getResources().getString(R.string.str_text_1);
        strings[1] = getResources().getString(R.string.str_text_2);
        strings[2] = getResources().getString(R.string.str_text_3);
    }

    private int count = 0;

    private void initView() {

        lipVerticalView.setOnMargueeListener(new VerticalMarqueeView.OnMargueeListener() {
            @Override
            public void onRollOver() {
                startRoll();
            }
        });
    }

    /**
     * 开始播放
     */
    private void startRoll() {
        Log.i("滚动事件", "startRoll");
        if (strings.length - 1 > count++) {
            lipVerticalView.setText(strings[count]);
            lipVerticalView.startScroll(true, 5);
        } else {
            count = 0;
            lipVerticalView.setText(strings[count]);
            lipVerticalView.startScroll(true, 5);
        }
    }

    /**
     * 停止播放
     */
    private void playNext() {
        lipVerticalView.setText("");
        lipVerticalView.stopScroll();
        startRoll();
    }

    /**
     * 暂停播放
     */
    private void pauseRoll() {
        Log.i("滚动事件", "pauseRoll");
        lipVerticalView.pauseScroll();
    }

    /**
     * 恢复播放
     */
    private void restartRoll() {
        Log.i("滚动事件", "restartRoll");
        lipVerticalView.restartRoll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseRoll();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        restartRoll();
    }

    @OnClick({R.id.tv_start_roll, R.id.tv_next, R.id.tv_restart_roll, R.id.tv_pause_roll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start_roll:
                handler.sendEmptyMessageDelayed(START_ROLL, 100);
                break;
            case R.id.tv_next:
                handler.sendEmptyMessageDelayed(NEXT, 100);
                break;
            case R.id.tv_restart_roll:
                handler.sendEmptyMessageDelayed(RESTART_ROLL, 100);
                break;
            case R.id.tv_pause_roll:
                handler.sendEmptyMessageDelayed(PAUSE_ROLL, 100);
                break;
        }
    }
}
