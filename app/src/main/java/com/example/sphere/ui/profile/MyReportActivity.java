package com.example.sphere.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.sphere.AlertActivity;
import com.example.sphere.MainActivity;
import com.example.sphere.R;
import com.example.sphere.ui.auth.EditPasswordActivity;
import com.example.sphere.ui.home.WaterLevelActivity;
import com.example.sphere.ui.home.model.Notif;
import com.example.sphere.ui.profile.adapter.MyReportAdapter;
import com.example.sphere.ui.profile.model.MyReportList;
import com.example.sphere.util.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyReportAdapter adapter;
    private ArrayList<MyReportList> list;
    private String token = "";
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report);

        list = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        ImageView ivBack = findViewById(R.id.ivBack);
        recyclerView = findViewById(R.id.rv);

        adapter = new MyReportAdapter(list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyReportActivity.this);
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
        String uRl = "https://sphere-apps.herokuapp.com/api/report";

        StringRequest request = new StringRequest(Request.Method.GET,
                uRl,
                (String response) -> {
                    try {
                        JSONArray arrRes = new JSONArray(response);
                        for (int i = 0; i < arrRes.length(); i++) {
                            JSONObject obj = arrRes.getJSONObject(i);
                            String id = obj.getString("id");
                            String userId = obj.getString("user_id");
                            String title = obj.getString("title");
                            String desc = obj.getString("description");
                            String category = obj.getString("category");
                            String address = obj.getString("address");
                            String image = obj.getString("image");
                            String date = obj.getString("created_at");
                            String latitude = obj.getString("latitude");
                            String longitude = obj.getString("longitude");

                            list.add(new MyReportList(id, userId, title, desc, category, address, image, date, latitude, longitude, false));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }, error -> {
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                System.out.println("bods " + body);
                Toast.makeText(MyReportActivity.this,
                        body, Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                // exception
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

        MySingleton.getmInstance(MyReportActivity.this).
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