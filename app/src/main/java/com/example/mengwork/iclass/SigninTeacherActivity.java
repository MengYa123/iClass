package com.example.mengwork.iclass;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SigninTeacherActivity extends AppCompatActivity implements View.OnClickListener {

    static public final String TAG = "SigninTeacherActivity";

    private List list;
    private TextView title;
    private Button left,right;
    private TextView tv_tip;

    private TextView signinnum;
    private User teacher;
    private LinkedList<String> studentIds;

    private final UUID MY_UUID = UUID.fromString("f3e6be6f-e14c-4b94-8656-6fed149dc8f3");
    private final String SIGNIN = "Signin";
    private BluetoothAdapter bluetoothAdapter;
    private String realBluetoothName;

    private ListView list_already;
    private TeacherThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_teacher);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        list = new ArrayList<SigninItem>();
        signinnum = findViewById(R.id.signinnum);
        signinnum.setText(""+list.size());

        list_already = findViewById(R.id.list_already);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        title = findViewById(R.id.title_text);
        title.setText("签到教师端");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        right.setText("记录");
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tv_tip = (TextView) findViewById(R.id.tvTip);
        findViewById(R.id.btnStartSignin).setOnClickListener(this);
        findViewById(R.id.btnEndSignin).setOnClickListener(this);

        teacher = (User) getIntent().getSerializableExtra("tea");
        studentIds = new LinkedList<String>();

        setTip("点击开始签到开始");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(thread != null){
            thread.cancel();
            thread = null;
        }




        bluetoothAdapter.disable();
    }

    private void setTip(final String tip) {
        tv_tip.setText(tip);
    }
    private boolean enableBlueTooth(){
        if (bluetoothAdapter == null){
            Toast.makeText(SigninTeacherActivity.this,"该设备不支持蓝牙，无法签到", Toast.LENGTH_LONG);
            return false;
        }
        if(!bluetoothAdapter.isEnabled()){
            if (bluetoothAdapter.enable()){
                //如果用户选择开启蓝牙，那么等到蓝牙加载完毕再返回
                while(!bluetoothAdapter.isEnabled()){
                }
                return true;
            }
        }
        if (!bluetoothAdapter.isEnabled()){
            Toast.makeText(SigninTeacherActivity.this, "开启蓝牙才可完成签到", Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 通过msg传递过来的信息，吐司一下收到的信息
            Toast.makeText(SigninTeacherActivity.this, (String) msg.obj + "已签到", Toast.LENGTH_SHORT).show();

            studentIds.add((String) msg.obj);


        }
    };


    private class TeacherThread extends Thread {

        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket;
        private InputStream is;
        private volatile boolean cancelled = false;

        public TeacherThread(){
            try{
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(SIGNIN, MY_UUID);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void cancel(){
            cancelled = true;
        }

        public void run(){
            try{
                while(!cancelled){
                    socket = serverSocket.accept();
                    is = socket.getInputStream();

                    byte[] buffer = new byte[64];
                    int count = 0;

                    while(count != -1){
                        count = is.read(buffer);
                        Message msg = new Message();
                        msg.obj = new String(buffer, 0 ,count, "utf-8");
                        handler.sendMessage(msg);
                    }
                    is.close();
                    socket.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStartSignin:
                if(thread == null){
                    realBluetoothName = bluetoothAdapter.getName();
                    bluetoothAdapter.setName(SigninStudentActivity.ID_TEACHER + teacher.getName() + teacher.getId());

                    thread = new TeacherThread();
                    thread.start();
                }
                setTip("正在签到，点击\"结束签到\"结束");
                break;
            case R.id.btnEndSignin:
                if(thread != null){
                    thread.cancel();

                    bluetoothAdapter.setName(realBluetoothName);
                }

                finish();
                break;
            default:
                break;
        }
    }
}
