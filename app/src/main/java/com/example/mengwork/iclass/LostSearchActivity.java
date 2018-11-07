package com.example.mengwork.iclass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LostSearchActivity extends AppCompatActivity {
    private Button right,left,search;
    private TextView title;
    private ListView lost_item_search;
    private String[] addr_data = {"一教","综合楼","二基楼","一基楼","体育馆","图书馆","文科楼","建环楼","西园食堂","东园食堂","校外"};
    private List<LostItem> item_list = new ArrayList<LostItem>();
    private LostItem item;
    private EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_search);
        right = findViewById(R.id.title_right);
        left = findViewById(R.id.title_left);
        title = findViewById(R.id.title_text);
        search = findViewById(R.id.search);
        et = findViewById(R.id.search_name);
        lost_item_search = findViewById(R.id.lost_item_search);
        title.setText("失物搜索");
        right.setVisibility(View.GONE);

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LostSearchActivity.this.finish();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString().trim();
                MyThread my = new MyThread(str);
                my.start();
            }
        });
    }
    private class MyThread extends Thread{
        private String str;
        MyThread(String str){
            this.str = str;
        }

        @Override
        public void run() {
            String params = "getlostfromstr_" + str;
            Socket socket;
            try {
                socket = new Socket("119.23.231.17",6666);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write(params);
                pw.flush();
                pw.close();

                socket = new Socket("119.23.231.17",6666);
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
                        LostAdapter adapter = new LostAdapter(LostSearchActivity.this,R.layout.lost_item,item_list);
                        lost_item_search.setAdapter(adapter);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
