package com.example.mengwork.iclass;

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

public class ReplyMesActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private String from_id;
    private String to_id;
    private TextView title;
    private EditText et;
    private Button left,right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_mes);
        sp = getSharedPreferences("info",MODE_PRIVATE);
        from_id = sp.getString("id","");
        to_id = sp.getString("reply_to_id","");
        title = findViewById(R.id.title_text);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        et = findViewById(R.id.mes);
        right.setText("回复");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplyMesActivity.this.finish();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mes = et.getText().toString().trim();
                MyThread my = new MyThread(from_id,to_id,mes);
                my.start();
                Toast.makeText(ReplyMesActivity.this,"已回复",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class MyThread extends Thread{
        private String from_id;
        private String to_id;
        private String mes;
        MyThread(String from_id,String to_id,String mes){
            this.from_id = from_id;
            this.to_id = to_id;
            this.mes = mes;
        }

        @Override
        public void run() {
            try {
                Socket socket = new Socket("119.23.231.17",6666);
                String params = "uploadmes_" + from_id + "_" + to_id + "_" + mes;
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
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
