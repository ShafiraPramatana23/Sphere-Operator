package com.example.sphere.ui.home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.sphere.MainActivity;
import com.example.sphere.R;
import com.example.sphere.ui.complain.DetailComplainActivity;
import com.example.sphere.ui.complain.FormTeknisiActivity;
import com.example.sphere.ui.patrol.PatrolActivity;
import com.example.sphere.ui.patrol.adapter.PatrolAdapter;
import com.example.sphere.ui.patrol.model.Patrol;
import com.example.sphere.ui.scan.ScanActivity;
import com.example.sphere.util.MySingleton;
import com.scwang.wave.MultiWaveHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    SharedPreferences sharedPreferences;
    String longitude = "";
    String latitude = "";
    String address = "";
    String token = "";
    String type = "";
    Boolean isRefresh = false;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    TextView tvAddress, tvDate, tvTime, tvSuhu, tvSuhuDesc, tvHumidity, tvPressure,
            tvWind, tvUvIndex, tvHeight, tvStatus, tvRiverName, tvAllPatroli, tvRole;
    LinearLayout llLocation, llPatroli;
    ImageView iv;
    MultiWaveHeader waveHeader;
    RecyclerView rvPatroli;
    SwipeRefreshLayout refreshLayout;

    private PatrolAdapter adapterPatrol;
    private ArrayList<Patrol> listPatrol = new ArrayList<>();

    @SuppressLint("ResourceType")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        longitude = sharedPreferences.getString("longitude", "");
        latitude = sharedPreferences.getString("latitude", "");
        token = sharedPreferences.getString("token", "");
        type = sharedPreferences.getString("type", "");

        String name = sharedPreferences.getString("name", "");

        CardView cvWeather = root.findViewById(R.id.cvWeather);
        CardView cvRiver = root.findViewById(R.id.cvRiver);
        TextView tvName = root.findViewById(R.id.tvName);
        ImageView ivNotif = root.findViewById(R.id.ivNotif);
        llLocation = root.findViewById(R.id.llLocation);
        tvAddress = root.findViewById(R.id.tvAddress);
        tvDate = root.findViewById(R.id.tvDate);
        tvTime = root.findViewById(R.id.tvTime);
        tvSuhu = root.findViewById(R.id.tvSuhu);
        tvSuhuDesc = root.findViewById(R.id.tvSuhuDesc);
        tvHumidity = root.findViewById(R.id.tvHumidity);
        tvWind = root.findViewById(R.id.tvWind);
        tvPressure = root.findViewById(R.id.tvPressure);
        tvUvIndex = root.findViewById(R.id.tvUvIndex);
        tvHeight = root.findViewById(R.id.tvHeight);
        tvStatus = root.findViewById(R.id.tvStatus);
        tvRiverName = root.findViewById(R.id.tvRiverName);
        tvAllPatroli = root.findViewById(R.id.tvAllPatroli);
        tvRole = root.findViewById(R.id.tvRole);

        iv = root.findViewById(R.id.iv);
        rvPatroli = root.findViewById(R.id.rvPatrol);
        llPatroli = root.findViewById(R.id.llPatroli);
        waveHeader = root.findViewById(R.id.waveHeader);
        waveHeader.start();

        tvName.setText(name);

        formatCalendar();
        setAdapterPatrol();
        checkRole();

        getRiverById();

        refreshLayout = root.findViewById(R.id.swipe_to_refresh_layout);
//        refreshLayout.setColorSchemeResources(
//                ContextCompat.getColor(getContext(), R.color.green_main), ContextCompat.getColor(getContext(), R.color.green_light));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getRiverById();
                checkRole();
            }
        });

        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanActivity.class);
                getActivity().startActivity(intent);
            }
        });

        tvAllPatroli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PatrolActivity.class);
                getActivity().startActivity(intent);
            }
        });

        ivNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListNotifActivity.class);
                getActivity().startActivity(intent);
            }
        });

        cvWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                getActivity().startActivity(intent);
            }
        });

        cvRiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WaterLevelActivity.class);
                getActivity().startActivity(intent);
            }
        });

        int delay = 0; // delay for 0 sec.
        int period = 2000; // repeat every 10 sec.
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                address = ((MainActivity)getActivity()).getAddress();
                latitude = ((MainActivity)getActivity()).getLat();
                longitude = ((MainActivity)getActivity()).getLong();

                System.out.println("oii address: "+address);

                if (!address.isEmpty() && !latitude.isEmpty() && !longitude.isEmpty()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvAddress.setText(address);
                            llLocation.setVisibility(View.VISIBLE);

                            getCurrentWeather(latitude, longitude);
                        }
                    });
                    t.cancel();
                }
            }
        }, delay, period);

        return root;
    }

    private void checkRole() {
        if (type.equals("patroli")) {
            tvRole.setText(" (Patroli)");
            llPatroli.setVisibility(View.VISIBLE);
            getListPatroli();
        } else if (type.equals("teknisi")) {
            tvRole.setText(" (Teknisi)");
            llPatroli.setVisibility(View.GONE);
        } else if (type.equals("admin")) {
            tvRole.setText(" (Admin)");
            llPatroli.setVisibility(View.GONE);
        } else {
            llPatroli.setVisibility(View.GONE);
        }
    }

    private void getListPatroli() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Tunggu ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/river/report/task";

        StringRequest request = new StringRequest(Request.Method.GET,
                uRl,
                (String response) -> {
                    try {
                        JSONArray arrRes = new JSONArray(response);
                        for (int i = 0; i < arrRes.length(); i++) {
                            JSONObject obj = arrRes.getJSONObject(i);
                            String id = obj.getString("id");
                            String rivers_id = obj.getString("rivers_id");
                            String userId = obj.getString("user_id");
                            String status = obj.getString("status");
                            String description = obj.getString("description");
                            String latitude = obj.getString("latitude");
                            String longitude = obj.getString("longitude");
                            String photo = obj.getString("photo");
                            String task_date = obj.getString("task_date");
                            String created_at = obj.getString("created_at");
                            JSONObject river = obj.getJSONObject("river");
                            String name = river.getString("name");

                            if (i < 3) {
                                listPatrol.add(new Patrol(id, rivers_id, userId, status, description, latitude, longitude, photo, task_date, created_at, name, false));
                            }
                        }

                        if (listPatrol.size() == 0) {
                            llPatroli.setVisibility(View.GONE);
                        } else {
                            llPatroli.setVisibility(View.VISIBLE);
                            adapterPatrol.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        System.out.println("ERROR CUY !!!"+e.toString());
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }, error -> {
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                System.out.println("bods " + body);
                Toast.makeText(getContext(),
                        body, Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                System.out.println("ERROR CUY !!!"+e.toString());
            }

            progressDialog.dismiss();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        request.setRetryPolicy(
                new DefaultRetryPolicy(10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getmInstance(getContext()).
                addToRequestQueue(request);
    }

    private void setAdapterPatrol() {
        adapterPatrol = new PatrolAdapter(listPatrol, "home");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPatroli.setLayoutManager(layoutManager);
        rvPatroli.setAdapter(adapterPatrol);
    }

    private void formatCalendar() {
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("ID"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("ID"));
        date = dateFormat.format(calendar.getTime());
        String time = timeFormat.format(calendar.getTime());
        tvDate.setText(date);
        tvTime.setText(time);
    }

    private void getCurrentWeather(String latitude, String longitude) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Tunggu ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://api.openweathermap.org/data/2.5/onecall?lat="+ latitude +"&lon="+ longitude +"&exclude=minutely,hourly,daily&units=metric&appid=" + getString(R.string.open_weather_id);
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
                        String ids = objWeather.getString("id");

                        tvSuhu.setText(temp);
                        tvSuhuDesc.setText(desc);
                        tvWind.setText(windSpeed + " m/s");
                        tvHumidity.setText(humidity + " %");
                        tvPressure.setText(pressure + " hPa");
                        tvUvIndex.setText(uv);

                        int id = Integer.parseInt(ids);
                        if (id >= 200 && id < 300) {
                            iv.setBackgroundResource(R.drawable.ic_thunder);
                        } else if (id >= 500 && id < 600) {
                            iv.setBackgroundResource(R.drawable.ic_rain);
                        } else if (id >= 800) {
                            iv.setBackgroundResource(R.drawable.ic_cloud_2);
                        } else {
                            iv.setBackgroundResource(R.drawable.ic_cloud_2);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("OMG: "+e.toString());
                    }
                    progressDialog.dismiss();
                }, error -> {
            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
        request.setRetryPolicy(
                new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getmInstance(getContext()).addToRequestQueue(request);
    }

    private void getRiverById() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Tunggu ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/river/1";
        System.out.println("URL river id nyaaa: " + uRl);
        StringRequest request = new StringRequest(Request.Method.GET,
                uRl,
                (String response) -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String height = obj.getString("height");
                        String status = obj.getString("status");
                        JSONObject objRiver = obj.getJSONObject("river");
                        String name = objRiver.getString("name");

                        tvRiverName.setText(name);
                        tvHeight.setText(height + " cm");
                        tvStatus.setText("("+status+")");

                        if (status.equals("Aman")) {
                            waveHeader.setProgress(0.25F);
                        } else if (status.equals("Siaga")) {
                            waveHeader.setProgress(0.5F);
                        } else if (status.equals("Bahaya")) {
                            waveHeader.setProgress(1F);
                        }
                        waveHeader.start();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("OMG: " + e.toString());
                    }
                    progressDialog.dismiss();
                }, error -> {
            System.out.println("err river : " + error.toString());
            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        request.setRetryPolicy(
                new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getmInstance(getContext()).addToRequestQueue(request);

        if (isRefresh) {
            refreshLayout.setRefreshing(false);
        }
    }

}