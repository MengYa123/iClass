package com.example.mengwork.iclass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements TextView.OnClickListener,OnBannerListener{

    static public String TAG = "MainActivity";

    private ImageButton btn_1,btn_2,btn_3,btn_4,btn_5,btn_7;
    private  Button title_left,title_right;
    private Banner banner;
    private ArrayList<Integer> list_path;
    private ArrayList<String> list_title;
    private TextView title_text;
    public ResideMenu resideMenu;
    private ResideMenuItem item1;
    private ResideMenuItem item2;
    private ResideMenuItem item3;
    private String id;
    private String pass;
    private Button mine,here;
    private SharedPreferences sp;
    private String name;
    private int ver;

    private BluetoothAdapter bluetoothAdapter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_banner();
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.main_slide_bg);
        resideMenu.attachToActivity(this);
        sp = getSharedPreferences("info",MODE_PRIVATE);
        id = sp.getString("id","");
        pass = sp.getString("pass","");
        name = sp.getString("name","");
        ver = sp.getInt("ver",0);


        Log.i(TAG,ver+"");


        String[] str = {"","系统设置","退出账号"};
        int[] icon = {R.drawable.menu,R.drawable.setting,R.drawable.exit};
        if (id.equals("")){
            str[0] = "登陆/注册";
        }else {
            str[0] = name;
            icon[2] = R.drawable.user;
        }
        item1 = new ResideMenuItem(this,icon[0],str[0]);
        item2 = new ResideMenuItem(this,icon[1],str[1]);
        item3 = new ResideMenuItem(this,icon[2],str[2]);

        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        resideMenu.addMenuItem(item1);
        resideMenu.addMenuItem(item2);
        resideMenu.addMenuItem(item3);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        init();

    }

    private void init_banner() {
        banner = findViewById(R.id.banner);
        list_path = new ArrayList<>();
        list_title = new ArrayList<>();
        list_path.add(R.drawable.circle_bg);
        list_path.add(R.drawable.iv1);
        list_path.add(R.drawable.iv2);
        list_title.add("欢迎使用i课堂");
        list_title.add("智慧课堂");
        list_title.add("课堂互动");
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setImageLoader(new MyLoader());

        banner.setImages(list_path);
        banner.setBannerAnimation(Transformer.Default);
        banner.setBannerTitles(list_title);
        banner.setDelayTime(3000);
        banner.isAutoPlay(true);
        banner.setIndicatorGravity(BannerConfig.CENTER).setOnBannerListener(this).start();
    }
    @Override
    public void OnBannerClick(int position) {
        Toast.makeText(this, "欢迎使用i课堂", Toast.LENGTH_SHORT).show();
    }
    private class MyLoader extends ImageLoader{

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    public void init(){

        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        btn_7 = findViewById(R.id.btn_7);
        title_left = findViewById(R.id.title_left);
        title_right = findViewById(R.id.title_right);
        title_text = findViewById(R.id.title_text);
        mine = findViewById(R.id.mine);
        here = findViewById(R.id.here);
        mine.setOnClickListener(this);
        here.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_right.setText("菜单");
        if (!id.equals("")){
            title_text.setText("欢迎您！" + id);
        }else {
            title_text.setText("你好，请先登陆！");
        }

        title_left.setVisibility(View.GONE);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }
    @Override
    public void onClick(View v) {
        Intent intent;
        if (v == item1){
            if (id.equals("")){
                intent = new Intent(MainActivity.this,LoginActivity.class);
                MainActivity.this.finish();
            }else{
                intent = new Intent(MainActivity.this,UserInfoActivity.class);
                intent.putExtra("id",id);
            }
            startActivity(intent);

        }else if (v == item2){

            Intent intent1 = new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent1);
            MainActivity.this.finish();
        }else if (v == item3){
            if(!id.equals("")){
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("id","");
                editor.putString("pass","");
                editor.apply();
                Intent intent1 = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent1);
                this.finish();

            }else {
                Toast.makeText(MainActivity.this,"对不起，您当前未登录，请登陆后重试！",Toast.LENGTH_SHORT).show();
                return;
            }

        }
        int id = v.getId();
        switch (id){
            case R.id.btn_1:
                switch (ver){
                    case 1:
                        Intent intent12 = new Intent(MainActivity.this,PPTDetailActivity.class);
                        startActivity(intent12);
                        break;
                    case 2:
                        Intent intent11 = new Intent(Intent.ACTION_GET_CONTENT);
                        intent11.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                        intent11.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent11,1);
                        break;
                }

                break;
            case R.id.btn_2:
                Intent intent1 = new Intent(MainActivity.this,StudyTogActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_3:
                Intent intent3 = new Intent(MainActivity.this,SchoolBusActivity.class);
                startActivity(intent3);
                break;
            case R.id.btn_4:
                Intent intent2 = new Intent(MainActivity.this,LostActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_5:
                Intent intent5 = new Intent(MainActivity.this,StudyActivity.class);
                startActivity(intent5);
                break;
            case R.id.btn_7:
                Intent intent4 = new Intent(MainActivity.this,ChatActivity.class);
                startActivity(intent4);
                break;
            case R.id.title_left:
                break;
            case R.id.title_right:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                break;
            case R.id.mine:
                Intent intent10 = new Intent(MainActivity.this,MineActivity.class);
                startActivity(intent10);
                break;
            case R.id.here:

                if (!enableBlueTooth()) {
                    return;
                }
                switch (ver){
                        case 1:
                            User tea = new User(this.id, name, "F", "17381573749", "12345678", 0, 0);
                            Intent intentSignin = new Intent(MainActivity.this, SigninTeacherActivity.class);
                            intentSignin.putExtra("tea", tea);
                            startActivity(intentSignin);
                            break;
                        case 2:
                            User stu = new User(this.id, name, "F", "17381573749", "12345678", 0, 0);
                            Intent intentSignin1 = new Intent(MainActivity.this, SigninStudentActivity.class);
                            intentSignin1.putExtra("stu", stu);
                            startActivity(intentSignin1);
                            break;
                    }


                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            Intent intent11 = new Intent(MainActivity.this,PPTChooseActivity.class);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("path",uri.getPath().toString());
            editor.apply();
            startActivity(intent11);
        }else {
            Toast.makeText(this,"您取消了文件选择！",Toast.LENGTH_SHORT).show();
        }
    }
    private boolean enableBlueTooth(){
        if (bluetoothAdapter == null){
            Toast.makeText(MainActivity.this,"该设备不支持蓝牙，无法签到", Toast.LENGTH_LONG);
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
            Toast.makeText(MainActivity.this, "开启蓝牙才可进行签到", Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }
}
