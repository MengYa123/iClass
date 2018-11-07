package com.example.mengwork.iclass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MesDetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private SharedPreferences sp;
    private ArrayList<Mes> list;
    private Mes mmm;
    private String id;
    private ListView list_mes;
    private TextView title;
    private Button left,right;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_detail);
        sp = getSharedPreferences("info",MODE_PRIVATE);
        id = sp.getString("id","");
        list = new ArrayList<>();
        list_mes = findViewById(R.id.list_mes);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        title = findViewById(R.id.title_text);
        title.setText("我收到的消息");
        right.setVisibility(View.GONE);
        list_mes.setOnItemClickListener(this);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MesDetailActivity.this.finish();
            }
        });
        MyThread my = new MyThread(id);
        my.start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String from_id = list.get(position).getFrom_id();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("reply_to_id",from_id);
        editor.apply();
        Intent intent = new Intent(MesDetailActivity.this,ReplyMesActivity.class);
        startActivity(intent);
    }

    class MyThread extends Thread{
        private String iid;
        MyThread(String id){
            this.iid = id;
        }
        @Override
        public void run() {
            try {
                Socket socket;
                socket = new Socket("119.23.231.17",6666);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                String params = "getmesdetail_" + id;
                pw.write(params);
                pw.flush();
                pw.close();
                socket.close();
                socket = new Socket("119.23.231.17",6666);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String data = br.readLine();
                br.close();
                socket.close();
                String from_id,mes;
                StringTokenizer tk = new StringTokenizer(data,"_");
                while (!(from_id = tk.nextToken()).equals("ok")){
                    mes = tk.nextToken();
                    mmm = new Mes(from_id,mes);
                    list.add(mmm);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MesAdapter adapter = new MesAdapter(MesDetailActivity.this,R.layout.mes_detail,list);
                        list_mes.setAdapter(adapter);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
