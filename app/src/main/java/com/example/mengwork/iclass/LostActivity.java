package com.example.mengwork.iclass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LostActivity extends AppCompatActivity {
    private String[] addr_data = {"一教","综合楼","二基楼","一基楼","体育馆","图书馆","文科楼","建环楼","西园食堂","东园食堂","校外"};
    private ListView list;
    private Button left,right,lost_push;
    private TextView text;
    private List<LostItem> item_list = new ArrayList<LostItem>();
    private LostItem item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);
        MyThread my = new MyThread();
        my.start();
        list = findViewById(R.id.lost_list);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        lost_push = findViewById(R.id.lost_push);
        text = findViewById(R.id.title_text);
        left.setText("返回");
        right.setText("搜索物品");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LostActivity.this.finish();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostActivity.this,LostSearchActivity.class);
                startActivity(intent);
                LostActivity.this.finish();
            }
        });
        text.setText("失物招领");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        lost_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostActivity.this,LostPushActivity.class);
                startActivity(intent);
            }
        });
    }
    private class MyThread extends Thread {
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
            try {
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter pw = new PrintWriter(out);
            String params = "getlostitem_item";
            pw.write(params);
            pw.flush();
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pw.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket = new Socket("119.23.231.17", 6666);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String name;
                int addr;
                String lost_name;
                String contract;
                String item_data = br.readLine();
                br.close();
                StringTokenizer tk = new StringTokenizer(item_data,"_");

                while(!(name = tk.nextToken()).equals("ok")){
                    addr = Integer.parseInt(tk.nextToken());
                    lost_name = tk.nextToken();
                    contract = tk.nextToken();
                    item = new LostItem(name,lost_name,contract,addr);
                    item_list.add(item);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LostAdapter adapter = new LostAdapter(LostActivity.this,R.layout.lost_item,item_list);
                        list.setAdapter(adapter);
                    }
                });

                br.close();
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

