package com.example.sphere.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.sphere.R;
import com.example.sphere.ui.home.adapter.RiverSpinnerAdapter;
import com.example.sphere.ui.home.model.River;
import com.example.sphere.util.MySingleton;
import com.scwang.wave.MultiWaveHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WaterLevelActivity extends AppCompatActivity {

    private Spinner spin;
    private MultiWaveHeader waveHeader;
    TextView tvHeight, tvStatus;

    RiverSpinnerAdapter adapter;

    private String token = "";

    private ArrayList<River> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_level);

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        spin = findViewById(R.id.spinner);
        ImageView ivBack = findViewById(R.id.ivBack);
        tvHeight = findViewById(R.id.tvHeight);
        tvStatus = findViewById(R.id.tvStatus);
        waveHeader = findViewById(R.id.waveHeader);
        waveHeader.start();

        adapter = new RiverSpinnerAdapter(this, R.layout.item_spinner, list);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("poosisis: "+list.get(position).getId());
                getRiverById(list.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WaterLevelActivity.super.onBackPressed();
            }
        });

        getRiverList();
    }

    private void getRiverList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/river/list";
        System.out.println("URL weather nyaaa: " + uRl);
        StringRequest request = new StringRequest(Request.Method.GET,
                uRl,
                (String response) -> {
                    try {
                        JSONArray arrRes = new JSONArray(response);
                        for (int i = 0; i < arrRes.length(); i++) {
                            JSONObject obj = arrRes.getJSONObject(i);
                            String id = obj.getString("id");
                            String name = obj.getString("name");
                            String date = obj.getString("created_at");

                            list.add(new River(
                                    id, name, date
                            ));

                            if (i == 0) {
                                getRiverById(id);
                            }
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("OMG: " + e.toString());
                    }
                    progressDialog.dismiss();
                }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
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

        MySingleton.getmInstance(this).addToRequestQueue(request);
    }

    private void getRiverById(String id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/river/" + id;
        System.out.println("URL weather nyaaa: " + uRl);
        StringRequest request = new StringRequest(Request.Method.GET,
                uRl,
                (String response) -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String height = obj.getString("height");
                        String status = obj.getString("status");

                        tvHeight.setText(height + " cm");
                        tvStatus.setText("("+status+")");

                        if (status.equals("Aman")) {
                            waveHeader.setProgress(0.25F);
                            waveHeader.setStartColor(ContextCompat.getColor(this, R.color.green_main));
                            waveHeader.setCloseColor(ContextCompat.getColor(this, R.color.green_light));
                        } else if (status.equals("Siaga")) {
                            waveHeader.setProgress(0.5F);
                            waveHeader.setStartColor(ContextCompat.getColor(this, R.color.orange_dark));
                            waveHeader.setCloseColor(ContextCompat.getColor(this, R.color.orange_light));
                        } else if (status.equals("Bahaya") || status.equals("danger")) {
                            waveHeader.setProgress(1F);
                            waveHeader.setStartColor(ContextCompat.getColor(this, R.color.red_dark));
                            waveHeader.setCloseColor(ContextCompat.getColor(this, R.color.red_light));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("OMG: " + e.toString());
                    }
                    progressDialog.dismiss();
                }, error -> {

            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
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

        MySingleton.getmInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onPause() {
        super.onPause();
        waveHeader.stop();
    }
}