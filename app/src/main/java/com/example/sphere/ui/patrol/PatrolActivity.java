package com.example.sphere.ui.patrol;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.sphere.MainActivity;
import com.example.sphere.R;
import com.example.sphere.ui.patrol.adapter.PatrolAdapter;
import com.example.sphere.ui.patrol.model.Patrol;
import com.example.sphere.util.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PatrolActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout llEmpty;

    private PatrolAdapter adapter;
    private ArrayList<Patrol> list;
    private String token = "";
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol);

        list = new ArrayList<Patrol>();

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        ImageView ivBack = findViewById(R.id.ivBack);
        recyclerView = findViewById(R.id.rv);
        llEmpty = findViewById(R.id.llEmpty);

        adapter = new PatrolAdapter(list, "");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PatrolActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        getDataList();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getDataList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
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

                            list.add(new Patrol(id, rivers_id, userId, status, description, latitude, longitude, photo, task_date, created_at, name, false));
                        }

                        adapter.notifyDataSetChanged();

                        if (arrRes.length() == 0) {
                            llEmpty.setVisibility(View.VISIBLE);
                        } else {
                            llEmpty.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        System.out.println("ERROR CUY !!!"+e.toString());
                        e.printStackTrace();
                        llEmpty.setVisibility(View.VISIBLE);

                    }
                    progressDialog.dismiss();
                }, error -> {
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                System.out.println("bods " + body);
                Toast.makeText(PatrolActivity.this,
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
                new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getmInstance(PatrolActivity.this).
                addToRequestQueue(request);
    }

    @Override
    public void onBackPressed() {
        if (type != null && type.equals("lapor")) {
            Intent m = new Intent(this, MainActivity.class);
            m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(m);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}