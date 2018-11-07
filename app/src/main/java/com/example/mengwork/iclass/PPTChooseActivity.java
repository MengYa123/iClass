package com.example.mengwork.iclass;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PPTChooseActivity extends AppCompatActivity {

    private Button left,right,choose;
    private TextView title;
    private EditText et_pptname;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pptchoose);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        title = findViewById(R.id.title_text);
        et_pptname = findViewById(R.id.et_pptname);
        choose = findViewById(R.id.choose);
        sp = getSharedPreferences("info",MODE_PRIVATE);
        String path = sp.getString("path","");

        String path_path = path.split("/")[7];
        et_pptname.setText(path_path);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PPTChooseActivity.this.finish();
            }
        });
        title.setText("请选择PPT");
        right.setVisibility(View.GONE);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (ActivityCompat.checkSelfPermission(PPTChooseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PPTChooseActivity.this, perms, 10086);
                } else {
                    String inputText=et_pptname.getText().toString();
                    Intent intent = new Intent(PPTChooseActivity.this, PPTShowActivity.class);//如果context上下文不是Activity的话， 需要添加下面这个flag
                    //添加要传递的数据
                    Bundle bundle = new Bundle();
                    bundle.putString("name", inputText);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }
}
