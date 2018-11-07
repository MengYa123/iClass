package com.example.mengwork.iclass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText et_2,et_3,et_5,et_6;
    private Button title_left,title_right,btn_register;
    private TextView title_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        et_2 = findViewById(R.id.et_2);
        et_5 = findViewById(R.id.et_5);
        et_6 = findViewById(R.id.et_6);
        title_left = findViewById(R.id.title_left);
        title_right = findViewById(R.id.title_right);
        title_text = findViewById(R.id.title_text);
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_text.setText("注册页面");
        title_right.setText("登陆");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.btn_register:
                String stu_id = et_2.getText().toString().trim();
                String pass = et_5.getText().toString().trim();
                String pass_double = et_6.getText().toString().trim();

                if (pass.equals(pass_double)){
                    Toast.makeText(RegisterActivity.this,"用户名："+stu_id+"注册成功",Toast.LENGTH_SHORT).show();
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    User user = new User(stu_id,"","","",pass,0,0);

                    MyThread thread1 = new MyThread(stu_id,pass);
                    thread1.start();

                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    intent.putExtra("username",stu_id);
                    intent.putExtra("pass",pass);
                    intent.putExtra("isAvaiable",true);
                    startActivity(intent);
                }else {
                    Toast.makeText(RegisterActivity.this, "您两次所输入的密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.title_left:
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.title_right:
                Intent intent1 = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent1);
                break;
        }
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
            String params = "register_"+ id + "_" + pass;
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
    }
}
