package com.example.mengwork.iclass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class PPTDetailActivity extends AppCompatActivity {

    private List list;
    private Button left,right,refresh;
    private TextView title;
    private ListView list_page;
    private PageAdapter adapter;
    private Page item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pptdetail);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        title = findViewById(R.id.title_text);
        list_page = findViewById(R.id.list_page);
        refresh = findViewById(R.id.btn_refresh);
        list = new ArrayList<Page>();
        Refresh re = new Refresh();
        re.start();
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PPTDetailActivity.this.finish();
            }
        });
        right.setText("重置");
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckOut out = new CheckOut();
                out.start();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Refresh re = new Refresh();
                re.start();
            }
        });
    }

    private class Refresh extends Thread{
        @Override
        public void run() {
            try {
                Socket socket = new Socket("119.23.231.17",6666);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write("refresh");
                pw.flush();
                pw.close();
                socket.close();

                socket = new Socket("119.23.231.17",6666);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String returnData = br.readLine();
                socket.close();
                StringTokenizer st = new StringTokenizer(returnData,"_");
                int pagenum,num;
                String pageNum;
                while(!(pageNum = st.nextToken()).equals("ok")){
                    pagenum = Integer.parseInt(pageNum);
                    num = Integer.parseInt(st.nextToken());
                    item = new Page(pagenum,num);
                    list.add(item);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new PageAdapter(PPTDetailActivity.this,R.layout.page,list);
                        list_page.setAdapter(adapter);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class CheckOut extends Thread{
        @Override
        public void run() {
            try {
                Socket socket = new Socket("119.23.231.17",6666);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write("checkout");
                pw.flush();
                pw.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
