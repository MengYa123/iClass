package com.example.mengwork.iclass;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class LockActivity extends AppCompatActivity {

    private Button left, right;
    private TextView tv, time,complete;

    private static int count;
    private String id;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        sp = getSharedPreferences("info",MODE_PRIVATE);
        id = sp.getString("id","");

        complete = findViewById(R.id.complete);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        tv = findViewById(R.id.title_text);
        time = findViewById(R.id.tv_time);
        left.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
        tv.setText("专心倒计时");
        count = 0;
        Intent intent = getIntent();
        int hour = intent.getIntExtra("hour", 0);
        int minute = intent.getIntExtra("minute", 0);
        int seconds = 0;
        MyThread thread = new MyThread(hour,minute,seconds);
        thread.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }

    class MyThread extends Thread {
        private int hour;
        private int minute;
        private int seconds = 0;
        int mhour;
        int mminute;
        MyThread(int hour, int minute, int second) {
            this.hour = hour;
            this.minute = minute;
            this.seconds = second;
            mhour = hour;
            mminute = minute;
        }
        @Override
        public void run() {
            while (hour > 0 || minute > 0 || seconds > 0) {
                LockActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time.setText("" + hour + ":" + minute + ":" + seconds);
                    }
                });
                SystemClock.sleep(1000);
                seconds--;
                if (seconds < 0 && minute > 0) {
                    seconds = 59;
                    minute--;
                    if (minute < 0 && hour > 0) {
                        minute = 59;
                        hour--;
                        if (hour < 0) {
                            hour = 0;
                        }
                    } else if (minute < 0 && hour == 0) {
                        minute = 0;
                    }
                } else if (seconds < 0 && minute == 0) {
                    if (hour > 0){
                        hour--;
                        minute = 59;
                        seconds = 59;
                    }
                }
            }
            try {
                Socket socket = new Socket("119.23.231.17",6666);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                int total = mhour*60 + mminute;
                String params = "increasestudytime_" + id + "_" + total;
                pw.write(params);
                pw.flush();
                pw.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LockActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    complete.setText("恭喜你完成了本次\n" + mhour + "小时" + mminute + "分钟\n的专心挑战！");
                }
            });

            SystemClock.sleep(2000);
            Intent intent = new Intent(LockActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        count++;
        if (count > 2){
            this.finish();
        }
    }


}
