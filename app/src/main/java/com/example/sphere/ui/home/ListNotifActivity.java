package com.example.sphere.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.sphere.R;
import com.example.sphere.ui.home.adapter.ListNotifAdapter;
import com.example.sphere.ui.home.model.Notif;
import com.example.sphere.ui.profile.MyReportActivity;
import com.example.sphere.ui.profile.adapter.MyReportAdapter;
import com.example.sphere.ui.profile.model.MyReportList;
import com.example.sphere.util.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListNotifActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListNotifAdapter adapter;
    private ArrayList<Notif> list;

    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notif);

        list = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        System.out.println("hei token: "+token);

        ImageView ivBack = findViewById(R.id.ivBack);
        recyclerView = findViewById(R.id.rv);

        adapter = new ListNotifAdapter(list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        getDataList();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListNotifActivity.super.onBackPressed();
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
        String uRl = "https://sphere-apps.herokuapp.com/api/notif";
        System.out.println("URL notif nyaaa: " + uRl);
        StringRequest request = new StringRequest(Request.Method.GET,
                uRl,
                (String response) -> {
                    try {
                        JSONArray arrRes = new JSONArray(response);
                        for (int i = 0; i < arrRes.length(); i++) {
                            JSONObject obj = arrRes.getJSONObject(i);
                            String id = obj.getString("id");
                            String title = obj.getString("status");
                            String message = obj.getString("message");
                            String date = obj.getString("created_at");

                            list.add(new Notif(id, date, message, title));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("OMG: " + e.toString());
                    }
                    progressDialog.dismiss();
                }, error -> {
            System.out.println("OMG: " + error.getMessage());
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
}