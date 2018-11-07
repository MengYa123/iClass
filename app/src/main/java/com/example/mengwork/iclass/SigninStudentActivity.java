package com.example.mengwork.iclass;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SigninStudentActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static public final String ID_TEACHER = "Iclass_";

    static public final String TAG = "SigninStudentActivity";

    private TextView tv_tip;
    private TextView title;
    private Button left,right;
    private ListView lv_teachers;
    private UserDeviceAdapter arrayAdapter;

    private User student;

    private ArrayList<UserDevice> userDevices;

    private final UUID MY_UUID = UUID.fromString("f3e6be6f-e14c-4b94-8656-6fed149dc8f3");
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothDevice selectDevice;
    private BluetoothSocket studentSocket;
    private OutputStream os;

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    userDevices.clear();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String name = device.getName();
                    String address = device.getAddress();
                    if (address == null)
                        return;
                    if(name == null)
                        return;
                    if(name.length()>7){

                        if(name.substring(0,7).equals(SigninStudentActivity.ID_TEACHER)){

                            for(int i = 0;i<userDevices.size();i++){
                                if(userDevices.get(i).getUserInfo().equals(name)){
                                    return;
                                }
                            }

                            userDevices.add(new UserDevice(name, address));
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(SigninStudentActivity.this, "搜索结束", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_student);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        title = findViewById(R.id.title_text);
        title.setText("签到学生端");
        left = findViewById(R.id.title_left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        right = findViewById(R.id.title_right);
        right.setVisibility(View.GONE);
        tv_tip = (TextView) findViewById(R.id.tvTip);
        lv_teachers = (ListView) findViewById(R.id.lvTeachers);
        findViewById(R.id.btnResearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });

        student = (User) getIntent().getSerializableExtra("stu");
        userDevices = new ArrayList<UserDevice>();



        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bondedDevice: bondedDevices){
            String name = bondedDevice.getName();
            String address = bondedDevice.getAddress();

            if (address == null)
                continue;
            if(name == null)
                continue;
            if(name.length()>7){
                Log.i(TAG, name);
                Log.i(TAG, name.substring(0, 7));
                Log.i(TAG, address);

                boolean b = true;
                if(name.substring(0,7).equals(SigninStudentActivity.ID_TEACHER)){
                    for(int i = 0;i<userDevices.size();i++){
                        if(userDevices.get(i).getUserInfo().equals(name)){
                            b = false;
                        }
                    }
                    if (b)
                        userDevices.add(new UserDevice(name, address));



                }
            }
        }


        arrayAdapter = new UserDeviceAdapter(SigninStudentActivity.this, R.layout.item_userdevice, userDevices);
        lv_teachers.setAdapter(arrayAdapter);
        lv_teachers.setOnItemClickListener(this);





        setTip("选择你要签到的老师");




        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(bluetoothReceiver, intentFilter);
        intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, intentFilter);
        intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver, intentFilter);

        startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cancelDiscovery();
        unregisterReceiver(bluetoothReceiver);

        bluetoothAdapter.disable();
    }

    private void setTip(final String tip) {
        tv_tip.setText(tip);
    }
    private boolean enableBlueTooth(){
        if (bluetoothAdapter == null){
            Toast.makeText(SigninStudentActivity.this,"该设备不支持蓝牙，无法签到", Toast.LENGTH_LONG);
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
            Toast.makeText(SigninStudentActivity.this, "开启蓝牙才可完成签到", Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }
    private void cancelDiscovery() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }
    private void startDiscovery() {
        cancelDiscovery();
        bluetoothAdapter.startDiscovery();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        setTip("正在签到...");
        UserDevice userDevice = userDevices.get(position);
        String address = userDevice.getBluetoothAddress();

        cancelDiscovery();

        if(selectDevice == null){
            selectDevice = bluetoothAdapter.getRemoteDevice(address);
        }

        try{
            if (studentSocket == null) {
                studentSocket = selectDevice.createRfcommSocketToServiceRecord(MY_UUID);
            }

            //10秒内尝试10次连接，若还未成功则返回
            for(int i = 0; !studentSocket.isConnected() && i<10; i++){
                studentSocket.connect();
                Thread.sleep(1000);
            }
            os = studentSocket.getOutputStream();

            if (os != null) {
                String text = student.getId();
                os.write(text.getBytes("UTF-8"));
            }
            setTip("签到完成，已可退出");

        }catch (IOException e){
            e.printStackTrace();
            setTip("签到失败, 请重试");
        }catch (InterruptedException e){
            e.printStackTrace();
            setTip("签到失败, 请重试");
        }


    }

    private class UserDeviceAdapter extends ArrayAdapter{

        private final int resourceId;
        public UserDeviceAdapter(Context context, int TextViewResourceId, List<UserDevice> objects){
            super(context, TextViewResourceId, objects);
            this.resourceId = TextViewResourceId;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            UserDevice userDevice = (UserDevice) getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            TextView tv_userInfo = (TextView) view.findViewById(R.id.tvUserInfo);
            tv_userInfo.setText(userDevice.getUserInfo());
            return view;
        }


    }
}
