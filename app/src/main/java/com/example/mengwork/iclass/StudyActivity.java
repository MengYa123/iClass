package com.example.mengwork.iclass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class StudyActivity extends AppCompatActivity {

    private TimePicker time;
    private Button left,right,start;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        text = findViewById(R.id.title_text);
        start = findViewById(R.id.start_time);
        time = findViewById(R.id.time);
        time.setIs24HourView(true);
        time.setHour(0);
        time.setMinute(0);
        right.setVisibility(View.GONE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudyActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        text.setText("我要专心");

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = time.getHour();
                int minute = time.getMinute();
                Intent intent = new Intent(StudyActivity.this,LockActivity.class);
                intent.putExtra("hour",hour);
                intent.putExtra("minute",minute);
                startActivity(intent);
            }
        });

    }
}
