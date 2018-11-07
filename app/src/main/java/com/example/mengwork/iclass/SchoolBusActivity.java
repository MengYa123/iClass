package com.example.mengwork.iclass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SchoolBusActivity extends AppCompatActivity {

    private Spinner start_spinner,end_spinner;
    private Button left,right;
    private TextView title;
    private ListView time_show;

    private String[] start_str = {"华西","江安","望江"};
    private String[] end_str = {"江安","望江","华西"};
    private String start_addr = "江安";
    private  String end_addr = "望江";

    private String[] jtw = {"文星花园、江安花园7:10到江安点7:20","文星花园7：40江安花园7：50","文星花园、江安花园8:10到江安点:8:20","江安点8:40","★江安点9:00","★10:15行政楼",
                            "★11:20行政楼","★12:10行政楼","★13:00行政楼","★13:25行政楼","★14:00行政楼","★14:55行政楼","★15:45行政楼","★16:35行政楼","★17:10行政楼",
                            "★17:30行政楼","★17:50行政楼","★18:30一基楼","★18:50一基楼","★19:00一基楼","★20:00一基楼","★21:20一基楼","★22:15一基楼","★22:30一基楼"};

    private String[] jth = {"江安点★8:20","江安点★8:40","江安点★9:00","★10:15行政楼","★12:10行政楼","★13:00行政楼","★14:55行政楼","★15:45行政楼","★16:35行政楼",
                            "★17:20行政楼","★17:50行政楼","★18:30一基楼","★18:50一基楼","★19:40一基楼","★21:20一基楼","★22:15一基楼（江安-华西-望江）"};
    private String[] htj = {"★7:15","★7:40","★9:10","10:00","★11:10","12:10","★13:00","★13:55","★14:50","★15:40","★16:50","★17:20","17:50(至文星花园)","★18:15","★19:30"};
    private String[] wtj = {"★7:10","★7:20","★7:30","7:40","★7:50","★8:10","★8:20","★9:00","★10:10","10:50","★11:10","★12:10","12:30","★13:00","★13:55",
                            "14:30","★15:00","15:30","★15:45","★16:50","★17:20","★18:00（至文星花园）","★18:15","★18:50（至文星花园）","★19:30（至文星花园）","★20:30",
                            "★21:10","★21:40","★22:30"};
    private String[] temp_data = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_bus);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);
        title = findViewById(R.id.title_text);
        start_spinner = findViewById(R.id.start_spinner);
        end_spinner = findViewById(R.id.end_spinner);
        time_show = findViewById(R.id.time_show);
        title.setText("校车时刻查询");
        right.setVisibility(View.GONE);
        left.setText("返回");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchoolBusActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        start_spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,start_str));
        start_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                start_addr = start_str[position];
                if (!start_addr.equals(end_addr)){
                    if (start_addr.equals("华西")&&end_addr.equals("江安")){
                        temp_data = htj;
                    }else if(start_addr.equals("望江")&&end_addr.equals("江安")){
                        temp_data = wtj;
                    }else if(start_addr.equals("江安")&&end_addr.equals("华西")){
                        temp_data = jth;
                    }else if(start_addr.equals("江安")&&end_addr.equals("望江")){
                        temp_data = jtw;
                    }else {
                        temp_data = new String[]{};
                    }
                    time_show.setAdapter(new ArrayAdapter<String>(SchoolBusActivity.this,android.R.layout.simple_list_item_1,temp_data));
                }else {
                    temp_data = new String[]{};
                    time_show.setAdapter(new ArrayAdapter<String>(SchoolBusActivity.this,android.R.layout.simple_list_item_1,temp_data));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        end_spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,end_str));
        end_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                end_addr = end_str[position];
                if (!start_addr.equals(end_addr)){
                    if (start_addr.equals("华西")&&end_addr.equals("江安")){
                        temp_data = htj;
                    }else if(start_addr.equals("望江")&&end_addr.equals("江安")){
                        temp_data = wtj;
                    }else if(start_addr.equals("江安")&&end_addr.equals("华西")){
                        temp_data = jth;
                    }else if(start_addr.equals("江安")&&end_addr.equals("望江")){
                        temp_data = jtw;
                    }else {
                        temp_data = new String[]{};
                    }
                    time_show.setAdapter(new ArrayAdapter<String>(SchoolBusActivity.this,android.R.layout.simple_list_item_1,temp_data));
                }else {
                    temp_data = new String[]{};
                    time_show.setAdapter(new ArrayAdapter<String>(SchoolBusActivity.this,android.R.layout.simple_list_item_1,temp_data));
                    Toast.makeText(SchoolBusActivity.this, "出发校区和到达校区不能相同！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
