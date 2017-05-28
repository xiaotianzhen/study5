package com.qianwang.study5;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.qianwang.study5.Custom.FlickerProgressBar;

public class MainActivity extends AppCompatActivity implements Runnable {
    private FlickerProgressBar flickerpb;
    private Thread loadThread;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                int num = (int) msg.obj;
                flickerpb.setProgress(num);
                if (num == 100) {
                    flickerpb.finishLoad();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flickerpb = (FlickerProgressBar) findViewById(R.id.flickerpb);

        flickerpb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flickerpb.toggle();
                if (flickerpb.isStop()) {
                    loadThread.interrupt();
                } else {
                    loading();
                }
            }
        });

    }

    public void reLoad() {
        loadThread.interrupt();
        flickerpb.reset();
        loading();
    }

    public void loading() {

        loadThread = new Thread(this);
        loadThread.start();
    }

    @Override
    public void run() {

        while (!loadThread.isInterrupted()) {
            try {
                int progress = (int) flickerpb.getProgress();
                Message msg = Message.obtain();
                progress += 1;
                msg.what = 1;
                msg.obj = progress;
                mHandler.sendMessage(msg);
                if (progress == 100) {
                    break;
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
