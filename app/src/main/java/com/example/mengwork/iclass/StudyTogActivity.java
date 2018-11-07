package com.example.mengwork.iclass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StudyTogActivity extends AppCompatActivity {

    private MapView mapView;
    private LocationManager locationManager;
    private boolean isFirstLocate = true;
    private BaiduMap baiduMap;
    private String provider;
    private Button left;
    private Button right;
    private TextView text;
    private String id;
    private SharedPreferences sp;
    private ArrayList<userLocation> list;
    private userLocation user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_study_tog);
        list = new ArrayList<>();
        getLocation get = new getLocation();
        get.start();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE}, 2);
        }

        sp = getSharedPreferences("info",MODE_PRIVATE);
        id = sp.getString("id","");

        text = findViewById(R.id.title_text);
        left = findViewById(R.id.title_left);
        right = findViewById(R.id.title_right);

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudyTogActivity.this.finish();
                Intent intent = new Intent(StudyTogActivity.this,MainActivity.class);
                startActivity(intent);
                StudyTogActivity.this.finish();
            }
        });
        text.setText("一起去自习");
        right.setText("我的位置");
        mapView = findViewById(R.id.map);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        addOtherPosition();
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                String id_show = bundle.getString("id");

                sp = getSharedPreferences("mes",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("from_id",id);
                editor.putString("to_id",id_show);
                editor.apply();

                Intent intent = new Intent(StudyTogActivity.this,MesActivity.class);
                startActivity(intent);
                return true;
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locationListener);
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)){
            provider = LocationManager.GPS_PROVIDER;
        }else if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
        }else {
            Toast.makeText(this, "No location provider to use!", Toast.LENGTH_SHORT).show();
            return;
        }
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOtherPosition();
                if (ActivityCompat.checkSelfPermission(StudyTogActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(StudyTogActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(StudyTogActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(provider);
                while(location  == null){
                    locationManager.requestLocationUpdates("gps", 60000, 1, locationListener);
                }
                navigatorTo(location);
                double latitude,longitude;
                double z = Math.sqrt(location.getLongitude()*location.getLongitude()+location.getLatitude()*location.getLatitude()) + 0.00002 *Math.sin(location.getLatitude()*Math.PI) ;
                double temp =Math.atan2(location.getLatitude(), location.getLongitude())  + 0.000003 * Math.cos(location.getLongitude()*Math.PI);
                longitude = z * Math.cos(temp) + 0.0065;
                latitude = z * Math.sin(temp) + 0.006;
                MyThread my = new MyThread(id,longitude,latitude);
                my.start();
                navigatorTo(location);
            }
        });

        Location location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locationListener);
        if (location != null){
            navigatorTo(location);
        }

    }

    private void navigatorTo(Location location) {
        double latitude,longitude;
        double z = Math.sqrt(location.getLongitude()*location.getLongitude()+location.getLatitude()*location.getLatitude()) + 0.00002 *Math.sin(location.getLatitude()*Math.PI) ;
        double temp =Math.atan2(location.getLatitude(), location.getLongitude())  + 0.000003 * Math.cos(location.getLongitude()*Math.PI);
        longitude = z * Math.cos(temp) + 0.0065;
        latitude = z * Math.sin(temp) + 0.006;
        if (isFirstLocate){

            LatLng ll = new LatLng(latitude,longitude);
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
        }
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(latitude);
        builder.longitude(longitude);
        MyLocationData data = builder.build();
        baiduMap.setMyLocationData(data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        if (locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (ActivityCompat.checkSelfPermission(StudyTogActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(StudyTogActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(StudyTogActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates("gps", 60000, 1, locationListener);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            navigatorTo(location);
            addOtherPosition();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private void addOtherPosition(){
        OverlayOptions options = null;
        Marker marker = null;
        for (int i = 0;i < list.size();i++){
             LatLng ll = new LatLng(list.get(i).getLatitude(),list.get(i).getLongitude());
             View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.marker, null);
             TextView tv = view.findViewById(R.id.marker_name);
             tv.setText(list.get(i).getName());
             BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(getViewBitmap(view));
             options = new MarkerOptions()
                    .position(ll)
                    .zIndex(9)
                    .icon(markerIcon)
                    .draggable(true);
             marker = (Marker) baiduMap.addOverlay(options);
            Bundle mBundle = new Bundle();
            mBundle.putString("id", list.get(i).id);
            marker.setExtraInfo(mBundle);
        }
    }
    private Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0, addViewContent.getMeasuredWidth(), addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }
    private class getLocation extends Thread{
        @Override
        public void run() {
            String params = "getAllLocation";
            Socket socket;
            try {
                socket = new Socket("119.23.231.17",6666);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write(params);
                pw.flush();
                pw.close();
                socket.close();
                socket = new Socket("119.23.231.17",6666);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String data = br.readLine();
                String iid;
                String name;
                br.close();
                socket.close();
                double longitude;
                double latitude;

                StringTokenizer st = new StringTokenizer(data,"_");
                while(!(iid = st.nextToken()).equals("ok")){
                    name = st.nextToken();
                    longitude = Double.parseDouble(st.nextToken());
                    latitude = Double.parseDouble(st.nextToken());
                    user = new userLocation(iid,name,longitude,latitude);
                    list.add(user);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private class userLocation{
        private String id;
        private String name;
        private double latitude;
        private double longitude;

        userLocation(String id,String name,double longitude,double latitude){
            this.id = id;
            this.name = name;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
    private class MyThread extends Thread{
        private String id;
        private double longitude;
        private double latitude;
        MyThread(String id,double longitude,double latitude){
            this.id = id;
            this.longitude = longitude;
            this.latitude = latitude;
        }
        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket("119.23.231.17", 6666);
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream out = null;
            try {
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter pw = new PrintWriter(out);
            String params = "pushgpsloc_" + id + "_" + longitude + "_" + latitude;
            pw.write(params);
            pw.flush();
            pw.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
