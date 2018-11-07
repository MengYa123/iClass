package com.example.mengwork.iclass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MesActivity extends AppCompatActivity {

    private EditText et_mes;
    private Button left,right;
    private TextView title;
    private SharedPreferences sp;
    private String from_id;
    private String to_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes);
        Intent intent = new Intent();
        sp = getSharedPreferences("mes",MODE_PRIVATE);
        from_id = sp.getString("from_id","");
        to_id = sp.getString("to_id","");
        et_mes = findViewById(R.id.mes);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        title = findViewById(R.id.title_text);
        title.setText("约自习吧(ง •_•)ง");
        right.setText("发送");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MesActivity.this.finish();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mes = et_mes.getText().toString().trim();
                MyThread my = new MyThread(from_id,to_id,mes);
                my.start();
                Toast.makeText(MesActivity.this, "消息已发送！", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private class MyThread extends Thread{

        private String from;
        private String to;
        private String mes;
        MyThread(String from,String to,String mes){
            this.from = from;
            this.to = to;
            this.mes = mes;
        }
        @Override
        public void run() {
            try {
                Socket socket = new Socket("119.23.231.17",6666);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                String params = "uploadmes_" + from + "_" +  to + "_" + mes;
                pw.write(params);
                pw.flush();
                pw.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
