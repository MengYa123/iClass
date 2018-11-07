package com.example.mengwork.iclass;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private List<Msg> msgList = new ArrayList<>();
    private EditText input_Text;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private Socket socket;
    private Button left,right;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        title = findViewById(R.id.title_text);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                startActivity(intent);
                ChatActivity.this.finish();
            }
        });


        title.setText("课下讨论");
        right.setText("群组");

        send=(Button)findViewById(R.id.btn) ;
        input_Text=(EditText)findViewById(R.id.et);
        adapter = new MsgAdapter(this);
        msgRecyclerView=(RecyclerView)findViewById(R.id.rv);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String inputText =input_Text.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OutputStream outputStream = socket.getOutputStream();
                            outputStream.write((socket.getLocalPort() + "//" + inputText).getBytes("utf-8"));
                            outputStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                input_Text.getText().clear();
            }
        });


        final  Handler handler=new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                int localPort = socket.getLocalPort();
                String[] split = ((String) msg.obj).split("//");
                if (split[0].equals(localPort + "")) {
                    Msg bean = new Msg(split[1],0);
                    msgList.add(bean);
                } else {
                    Msg bean = new Msg(split[1],1);
                    msgList.add(bean);
                }
                adapter.setData((ArrayList<Msg>) msgList);
                msgRecyclerView.setAdapter(adapter);
                LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
                msgRecyclerView.setLayoutManager(manager);
            }

        };



        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket=new Socket("119.23.231.17",16666);
                    InputStream inputStream=socket.getInputStream();
                    int length;
                    byte[] buff=new byte[1024];
                    while((length=inputStream.read(buff))!=0){
                        String text=new String(buff,0,length);
                        Message msg=Message.obtain();
                        msg.obj=text;
                        handler.sendMessage(msg);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
