package com.example.mengwork.iclass;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;


public class MineActivity extends AppCompatActivity {

    private Button left,right,main,here;
    private TextView text,study_time;
    private SharedPreferences sp;
    private Button invite;
    private String id;
    private int ver;
    private String name;
    private BluetoothAdapter bluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        sp = getSharedPreferences("info",MODE_PRIVATE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        name = sp.getString("name","");
        id = sp.getString("id","");
        ver = sp.getInt("ver",0);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        text = findViewById(R.id.title_text);
        study_time = findViewById(R.id.study_time);
        invite = findViewById(R.id.invite);
        right.setVisibility(View.GONE);
        left.setVisibility(View.GONE);
        text.setText("我的");
        main = findViewById(R.id.main);
        here = findViewById(R.id.here);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineActivity.this,MesDetailActivity.class);
                startActivity(intent);
            }
        });
        here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!enableBlueTooth()) {
                    return;
                }
                switch (ver){
                    case 1:
                        User tea = new User(id, name, "F", "17381573749", "12345678", 0, 0);
                        Intent intentSignin = new Intent(MineActivity.this, SigninTeacherActivity.class);
                        intentSignin.putExtra("tea", tea);
                        startActivity(intentSignin);
                        break;
                    case 2:
                        User stu = new User(id, name, "F", "17381573749", "12345678", 0, 0);
                        Intent intentSignin1 = new Intent(MineActivity.this, SigninStudentActivity.class);
                        intentSignin1.putExtra("stu", stu);
                        startActivity(intentSignin1);
                        break;
                }
            }
        });

        MyThread my = new MyThread(id);
        my.start();
    }
    private boolean enableBlueTooth(){
        if (bluetoothAdapter == null){
            Toast.makeText(MineActivity.this,"该设备不支持蓝牙，无法签到", Toast.LENGTH_LONG);
            return false;
        }
        if(!bluetoothAdapter.isEnabled()){
            if (bluetoothAdapter.enable()){
                //如果用户选择开启蓝牙，那么等到蓝牙加载完毕再返回
                while(!bluetoothAdapter.isEnabled()){
                }
                return true;
            }
        }
        if (!bluetoothAdapter.isEnabled()){
            Toast.makeText(MineActivity.this, "开启蓝牙才可进行签到", Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }
    private class MyThread extends Thread{

        private String id;
        MyThread(String id){
            this.id = id;
        }
        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket("119.23.231.17",6666);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                String params = "getstudytime_" + id;
                pw.write(params);
                pw.flush();
                pw.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket = new Socket("119.23.231.17", 6666);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input_status = br.readLine().trim();
                br.close();
                StringTokenizer st = new StringTokenizer(input_status,"_");
                id = st.nextToken();
                final int time = Integer.parseInt(st.nextToken());
                final int mesCount = Integer.parseInt(st.nextToken());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       study_time.setText("" + time);
                       invite.setText("" + mesCount);
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


}
