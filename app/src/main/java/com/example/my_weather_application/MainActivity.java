package com.example.my_weather_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout home1;
    private ProgressBar loadingbar1;
    private TextView cityname1,temp1,tvcondition1;
    private TextInputEditText editcity1;
    private ImageView idback1,ic_search1,tvicon;
    private RecyclerView Rvweather1;
    private ArrayList<RVWeatherModal> rvWeatherModalArrayList;
    private RVWeatherAdapter rvWeatherAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE=1;
    private String cityname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
        home1 = findViewById(R.id.homeid);
        loadingbar1 = findViewById(R.id.loadingbar);
        cityname1 = findViewById(R.id.cityname);
        temp1 = findViewById(R.id.temp);
        tvcondition1 = findViewById(R.id.tvcondition);
        editcity1 = findViewById(R.id.editcity);
        idback1 = findViewById(R.id.idback);
        ic_search1 = findViewById(R.id.ic_search);
        tvicon = findViewById(R.id.tvicon);
        Rvweather1 = findViewById(R.id.Rvweather);
        rvWeatherModalArrayList = new ArrayList<>();
        rvWeatherAdapter = new RVWeatherAdapter(this,rvWeatherModalArrayList);
        Rvweather1.setAdapter(rvWeatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       if(ActivityCompat.checkSelfPermission(this,  android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

           ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
       }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
       cityname = getCityName(location.getLongitude(), location.getLatitude());
       getWeatherInfo(cityname);
       ic_search1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String city = editcity1.getText().toString();
               if(city.isEmpty()){
                   Toast.makeText(MainActivity.this,"Please enter city name",Toast.LENGTH_SHORT).show();
               }else {
                   cityname1.setText(cityname);
                   getWeatherInfo(city);
               }
           }
       });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permissions granted",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Please Grant the permissions",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityname = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {

            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for(Address adr : addresses){
                if(adr!=null){
                    String city = adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityname = city;
                    }else {
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this,"USER CITY NOT FOUND...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityname;
    }

    private void getWeatherInfo(String cityname){
        String url = "http://api.weatherapi.com/v1/forecast.json?key=f6f43411c12f4e4ea6a144727230107&q="+ cityname +"&days=1&aqi=yes&alerts=yes";

        cityname1.setText(cityname);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingbar1.setVisibility(View.GONE);
                home1.setVisibility(View.VISIBLE);
                rvWeatherModalArrayList.clear();
                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temp1.setText(temperature + "°c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionicon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionicon)).into(tvicon);
                    tvcondition1.setText(condition);
                    if(isDay==1){
                        Picasso.get().load("https://wallpapercave.com/wp/wp11946669.jpg").into(idback1);
                    }else {
                        Picasso.get().load("https://wallpapercave.com/dwp1x/wp5833268.png").into(idback1);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forcast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forcast0.getJSONArray("hour");

                    for(int i=0;i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kmph");
                        rvWeatherModalArrayList.add(new RVWeatherModal(time,temper,img,wind));
                    }
                    rvWeatherAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Please Valid City Name",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}