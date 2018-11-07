package com.example.mengwork.iclass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText et_1,et_2;
    private Button btn_login,left,right;
    private TextView tv_text;
    String Exists = "NO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        tv_text = findViewById(R.id.title_text);
        et_1 = findViewById(R.id.et_1);
        et_2 = findViewById(R.id.et_2);
        btn_login = findViewById(R.id.btn_login);
        tv_text.setText("登陆");
        right.setText("注册");
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.title_right:
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.title_left:
                Intent intent1 = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_login:
                String stu_id = et_1.getText().toString().trim();
                String pass = et_2.getText().toString().trim();
                vertifyAccount(stu_id,pass);
                break;
        }
    }
    public void vertifyAccount(String id,String inputPass){
        MyThread login_thread = new MyThread(id,inputPass);
        login_thread.start();
    }
    private class MyThread extends Thread{
        private String id;
        private String pass;
        public MyThread(String id,String pass){
            this.id = id;
            this.pass = pass;
        }

        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket("119.23.231.17",6666);
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
            String params = "login_"+ id + "_" + pass;
            pw.write(params);
            pw.flush();
            pw.close();
            try {
                socket = new Socket("119.23.231.17",6666);
                InputStream in = socket.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input_status = br.readLine().trim();
                br.close();
                Exists = input_status;
                if (Exists.equals("YES")){
                    SharedPreferences sp = getSharedPreferences("info",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("id",id);
                    editor.putString("pass",pass);
                    editor.apply();
                    Intent intent2 = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent2);
                }else if (Exists.equals("NO")){
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this, "对不起，你输入的用户名或密码有误，请重新输入！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*
        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket("172.25.201.2",6666);
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
            String params = "login_"+ id + "_" + pass;
            pw.write(params);

            pw.flush();
            try {
                socket.shutdownOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pw.close();
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
    }
}
