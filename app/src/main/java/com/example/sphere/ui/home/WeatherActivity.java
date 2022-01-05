package com.example.sphere.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.sphere.R;
import com.example.sphere.ui.home.adapter.DailyAdapter;
import com.example.sphere.ui.home.adapter.HourlyAdapter;
import com.example.sphere.ui.home.model.Daily;
import com.example.sphere.ui.home.model.Hourly;
import com.example.sphere.util.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class WeatherActivity extends AppCompatActivity {

    RecyclerView rvHourly, rvDaily;
    TextView tvSuhu, tvSuhuDesc, tvHumidity, tvPressure, tvWind, tvUvIndex;
    ImageView iv;

    HourlyAdapter hourlyAdapter;
    DailyAdapter dailyAdapter;

    private ArrayList<Hourly> hourlyList;
    private ArrayList<Daily> dailyList;

    SharedPreferences sharedPreferences;

    String longitude = "";
    String latitude = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        rvHourly = findViewById(R.id.rvHourly);
        rvDaily = findViewById(R.id.rvDaily);
        tvSuhu = findViewById(R.id.tvSuhu);
        tvSuhuDesc = findViewById(R.id.tvSuhuDesc);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWind);
        tvPressure = findViewById(R.id.tvPressure);
        tvUvIndex = findViewById(R.id.tvUvIndex);
        iv = findViewById(R.id.iv);
        RelativeLayout rlBack = findViewById(R.id.rlBack);

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        longitude = sharedPreferences.getString("longitude", "");
        latitude = sharedPreferences.getString("latitude", "");

        hourlyList = new ArrayList<>();
        dailyList = new ArrayList<>();

        rlBack.bringToFront();

        hourlyAdapter = new HourlyAdapter(this, hourlyList);
        rvHourly.setLayoutManager(new GridLayoutManager(this, 4));
        rvHourly.setAdapter(hourlyAdapter);

        dailyAdapter = new DailyAdapter(this, dailyList);
        rvDaily.setLayoutManager(new LinearLayoutManager(this));
        rvDaily.setAdapter(dailyAdapter);

        getCurrentWeather();
        checkTime();

        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherActivity.super.onBackPressed();
            }
        });
    }

    private void checkTime() {
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentHour < 18) {
            Glide.with(this)
                    .load(R.drawable.bg_day)
                    .centerCrop()
                    .into(iv);
        } else {
            Glide.with(this)
                    .load(R.drawable.bg_night)
                    .centerCrop()
                    .into(iv);
        }
    }

    private void getCurrentWeather() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&exclude=minutely&units=metric&appid=" + getString(R.string.open_weather_id);
        System.out.println("URL weather nyaaa: " + uRl);
        StringRequest request = new StringRequest(Request.Method.GET,
                uRl,
                (String response) -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject obj = jsonObject.getJSONObject("current");
                        String temp = obj.getString("temp");
                        String humidity = obj.getString("humidity");
                        String windSpeed = obj.getString("wind_speed");
                        String pressure = obj.getString("pressure");
                        String uv = obj.getString("uvi");
                        JSONArray arr = obj.getJSONArray("weather");
                        JSONObject objWeather = arr.getJSONObject(0);
                        String desc = objWeather.getString("main");

                        tvSuhu.setText(temp);
                        tvSuhuDesc.setText(desc);
                        tvWind.setText(windSpeed + " m/s");
                        tvHumidity.setText(humidity + " %");
                        tvPressure.setText(pressure + " hPa");
                        tvUvIndex.setText(uv);

                        JSONArray arrHourly = jsonObject.getJSONArray("hourly");
                        for (int i = 0; i < 4; i++) {
                            JSONObject objHourly = arrHourly.getJSONObject(i);
                            String date = objHourly.getString("dt");
                            String tempHour = objHourly.getString("temp");
                            JSONArray arrHour = obj.getJSONArray("weather");
                            JSONObject objHourWeather = arrHour.getJSONObject(0);
                            String id = objHourWeather.getString("id");

                            hourlyList.add(new Hourly(
                                    date,
                                    tempHour,
                                    id
                            ));
                        }

                        JSONArray arrDaily = jsonObject.getJSONArray("daily");
                        for (int i = 0; i < 3; i++) {
                            JSONObject objDaily = arrDaily.getJSONObject(i);
                            String date = objDaily.getString("dt");
                            JSONObject objTemp = objDaily.getJSONObject("temp");
                            String tempDaily = objTemp.getString("day");
                            JSONArray arrDaiy = objDaily.getJSONArray("weather");
                            JSONObject objDailyWeather = arrDaiy.getJSONObject(0);
                            String id = objDailyWeather.getString("id");

                            dailyList.add(new Daily(
                                    date,
                                    tempDaily,
                                    id
                            ));
                        }

                        hourlyAdapter.notifyDataSetChanged();
                        dailyAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("OMG: " + e.toString());
                    }
                    progressDialog.dismiss();
                }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
        request.setRetryPolicy(
                new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getmInstance(this).addToRequestQueue(request);
    }
}