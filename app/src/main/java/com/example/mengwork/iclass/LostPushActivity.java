package com.example.mengwork.iclass;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class LostPushActivity extends AppCompatActivity {

    private String[] addr_data = {"一教","综合楼","二基楼","一基楼","体育馆","图书馆","文科楼","建环楼","西园食堂","东园食堂","校外"};
    private Spinner lost_addr;
    private EditText et_name,et_lost,et_contract;
    private Button push,left,right;
    private ImageView lost_image;
    private TextView title;
    private int select_addr_index;
    private Uri imageUri;
    private String id;
    private String name;
    private SharedPreferences sp;
    private String path = "";
    private String lost_name;
    private String contract;
    private String base64;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_push);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        sp = getSharedPreferences("info", MODE_PRIVATE);
        id = sp.getString("id","");
        name = sp.getString("name","");
        lost_image = findViewById(R.id.lost_image);
        lost_addr = findViewById(R.id.lost_addr);
        et_name = findViewById(R.id.pick_name);
        et_name.setText(name);
        et_lost = findViewById(R.id.lost_name);
        et_contract = findViewById(R.id.lost_contract);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        title = findViewById(R.id.title_text);
        title.setText("信息发布");
        left.setText("返回");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(LostPushActivity.this,LostActivity.class);
                startActivity(intent);
            }
        });
        right.setText("发布");
        push = findViewById(R.id.push_lost);
        lost_addr.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,addr_data));
        lost_addr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_addr_index = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                path = getExternalFilesDir("image/") + id + "_" + System.currentTimeMillis() + "_" + ".jpg";
                File image = new File(path);
                imageUri = Uri.fromFile(image);

                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,1);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lost_name = et_lost.getText().toString().trim();
                contract = et_contract.getText().toString().trim();
                if (!lost_name.equals("")&&!contract.equals("")&&!name.equals("")){
                    MyThread my = new MyThread(name,select_addr_index,lost_name,contract,path);
                    my.start();
                    Toast.makeText(LostPushActivity.this,"发布失物招领信息成功！",Toast.LENGTH_SHORT).show();
                    LostPushActivity.this.finish();
                }else{
                    Toast.makeText(LostPushActivity.this,"信息越详细，失物找到小主人的几率更大哦！请您补充一下信息哦！",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        lost_image.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
    private class MyThread extends Thread{

        private String name;
        private int addr;
        private String lost_name;
        private String img_path;
        private String contract;
        public MyThread(String name,int addr,String lost_name,String contract,String img_path){
            this.name = name;
            this.addr = addr;
            this.lost_name = lost_name;
            this.img_path = img_path;
            this.contract = contract;
        }


        @Override
        public void run() {
            File img = new File(path);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            ByteArrayOutputStream out_byte = null;
            out_byte = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out_byte);
            try {
                out_byte.flush();
                out_byte.close();
                byte[] imageBytes = out_byte.toByteArray();
                base64 = Base64.encodeToString(imageBytes,Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }


            Socket socket = null;
            try {
                socket = new Socket("119.23.231.17",6666);
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream out = null;
            try {
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter pw = new PrintWriter(out,true);
            String params = "lost_"+ name + "_" + select_addr_index + "_" + lost_name + "_" + contract + "_" + "base64" + "\nok";

            /**
             * 测试socket传输的数据完整性，经检测数据完整
             */
//            final int len = base64.length();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(LostPushActivity.this,"" + len, Toast.LENGTH_SHORT).show();
//                }
//            });


            pw.write(params);
            pw.flush();
            pw.close();
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
