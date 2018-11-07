package com.example.mengwork.iclass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class SettingActivity extends AppCompatActivity {

    private TextView title;
    private Button left,right;
    private SwitchCompat switch_button;
    private SharedPreferences sp;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = getSharedPreferences("info",MODE_PRIVATE);
        int switch_state = sp.getInt("switch_state",0);
        id = sp.getString("id","");
        title = findViewById(R.id.title_text);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        left.setText("返回");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                startActivity(intent);
                SettingActivity.this.finish();
            }
        });
        right.setVisibility(View.GONE);
        title.setText("系统设置");



        switch_button = findViewById(R.id.switch_loction);

        MyThread my = new MyThread(id);
        my.start();

        switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int check;
                if (isChecked){
                    check = 1;
                }else {
                    check = 0;
                }
                SetState set = new SetState(id,check);
                set.start();
            }
        });
    }

    private class MyThread extends Thread {
        private String id;


        public MyThread(String id) {
            this.id = id;

        }

        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket("119.23.231.17", 6666);
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream out = null;
            try {
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter pw = new PrintWriter(out);
            String params = "getswitchstate_" + id;
            pw.write(params);
            pw.flush();
            pw.close();
            try {
                socket = new Socket("119.23.231.17", 6666);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input_status = br.readLine();
                String name = "";
                br.close();
                StringTokenizer st = new StringTokenizer(input_status,"_");
                final int switch_state;
                name = st.nextToken();
                switch_state = Integer.parseInt(st.nextToken());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (switch_state == 1){
                            switch_button.setChecked(true);
                        }else {
                            switch_button.setChecked(false);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class SetState extends Thread{
        private String id;
        int isChecked;
        SetState(String id,int isChecked){
            this.id = id;
            this.isChecked = isChecked;
        }

        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket("119.23.231.17", 6666);
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream out = null;
            try {
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter pw = new PrintWriter(out);
            String params = "setswitchstate_" + id + "_" + isChecked;
            pw.write(params);
            pw.flush();
            pw.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
