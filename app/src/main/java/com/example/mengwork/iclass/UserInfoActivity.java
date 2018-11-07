package com.example.mengwork.iclass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class UserInfoActivity extends AppCompatActivity {

    private EditText et_1,et_2,et_3,et_4,et_5;
    private Button left,right;
    private TextView text;
    private ImageView iv;
    private String id;
    private String name;
    private String sex;
    private String phoneNum;
    private String ver;

    private int switch_state = 0;//定位开关状态

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        sp = getSharedPreferences("info",MODE_PRIVATE);
        editor = sp.edit();
        id = sp.getString("id","2017141463055");
        MyThread my = new MyThread(id);
        my.start();
        init();
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_1.setFocusable(true);
                et_3.setFocusable(true);
                et_4.setFocusable(true);
                right.setText("保存更改");
                left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfoActivity.this.finish();
                        Intent intent = new Intent(UserInfoActivity.this,MainActivity.class);
                    }
                });
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ver = "1";
                        sex = "male";
                        name = et_1.getText().toString().trim();
                        id = et_2.getText().toString().trim();
                        sex = et_3.getText().toString().trim();
                        phoneNum = et_4.getText().toString().trim();
                        String data = "changeInfo_" + id + "_" + name+"_"+sex+"_"+phoneNum+"_"+ver;
                        StoreInfo store = new StoreInfo(data);
                        store.start();
                    }
                });
            }
        });

    }
    public void init(){
        et_1 = findViewById(R.id.et_1);
        et_2 = findViewById(R.id.et_2);
        et_3 = findViewById(R.id.et_3);
        et_4 = findViewById(R.id.et_4);
        et_5 = findViewById(R.id.et_5);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        text = findViewById(R.id.title_text);
        iv = findViewById(R.id.userImage);
        text.setText("个人信息");
        left.setText("返回");
        right.setText("修改");
        Intent intent = new Intent();
        id = intent.getStringExtra("id");
        MyThread my = new MyThread(id);
        et_1.setText(name);
        et_2.setText(id);
        et_3.setText(sex);
        et_4.setText(phoneNum);
        et_5.setText(ver);

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
            String params = "getInfo_" + id;
            pw.write(params);
            pw.flush();
            pw.close();
            try {
                socket = new Socket("119.23.231.17", 6666);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input_status = br.readLine().trim();
                br.close();
                StringTokenizer st = new StringTokenizer(input_status,"_");
                name = st.nextToken();
                sex = st.nextToken();
                phoneNum = st.nextToken();
                ver = st.nextToken();
                switch_state = Integer.parseInt(st.nextToken());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_1.setText(name);
                        editor.putString("name",name);
                        editor.putInt("ver", Integer.parseInt(ver));
                        editor.putInt("switch_state",switch_state);
                        editor.apply();
                        et_2.setText(id);
                        et_3.setText(sex);
                        et_4.setText(phoneNum);
                        et_5.setText(ver);
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
    private class StoreInfo extends Thread{
        private String data;

        public StoreInfo(String data){
            this.data = data;
        }
        @Override
        public void run() {
            try {
                Socket socket = new Socket("119.23.231.17", 6666);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write(data);
                pw.flush();
                pw.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
