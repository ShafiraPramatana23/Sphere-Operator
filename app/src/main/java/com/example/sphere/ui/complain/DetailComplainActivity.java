package com.example.sphere.ui.complain;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sphere.AlertActivity;
import com.example.sphere.R;
import com.example.sphere.ui.complain.adapter.TeknisiSpinnerAdapter;
import com.example.sphere.ui.complain.model.Teknisi;
import com.example.sphere.util.DateFormatter;
import com.example.sphere.util.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailComplainActivity extends AppCompatActivity {

    private String token = "";
    private String id = "";
    private String type = "";
    private String selectedTeknisi = "";

    TeknisiSpinnerAdapter adapterSpinTeknisi;

    private ArrayList<Teknisi> listTeknisi = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_complain);

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        type = sharedPreferences.getString("type", "");

        RelativeLayout btnAssigned = findViewById(R.id.btnAssign);
        RelativeLayout btnSolving = findViewById(R.id.btnSolving);
        LinearLayout llDetailSolving = findViewById(R.id.llDetailSolving);

        getTeknisi();

        if(type.equals("admin")){
            btnAssigned.setVisibility(View.VISIBLE);
            btnSolving.setVisibility(View.GONE);
        }else if(type.equals("teknisi")){
            btnAssigned.setVisibility(View.GONE);
            btnSolving.setVisibility(View.VISIBLE);
        } else if(type.equals("user")){
            llDetailSolving.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        getDataList(Integer.valueOf(id));

        ImageView ivBack = findViewById(R.id.ivBacks);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAssigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAssignTask();
            }
        });

        btnSolving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(DetailComplainActivity.this, FormTeknisiActivity.class);
                m.putExtra("id", id);
                startActivity(m);
            }
        });
    }

    private void dialogAssignTask() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_assign_task);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout rlSubmit = dialog.findViewById(R.id.btnSubmit);
        Spinner spin = dialog.findViewById(R.id.spinner);

        adapterSpinTeknisi = new TeknisiSpinnerAdapter(this, R.layout.item_spinner, listTeknisi);
        spin.setAdapter(adapterSpinTeknisi);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTeknisi = listTeknisi.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rlSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSendTask();
            }
        });

        dialog.show();
    }

    private void actionSendTask() {
        System.out.println("assign id: "+id);
        System.out.println("assign selected teknis: "+selectedTeknisi);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Meng-assign tugas ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "api/report/assign/"+id;
        StringRequest request = new StringRequest(Request.Method.POST,
                uRl,
                (String response) -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("isError")) {
                            Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent m = new Intent(this, AlertActivity.class);
                            m.putExtra("menu", "assign");
                            startActivity(m);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Gagal meng-assign tugas", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }, error -> {
            Toast.makeText(this, "Gagal meng-assign tugas", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("assigned_id", selectedTeknisi);
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        request.setRetryPolicy(
                new DefaultRetryPolicy(10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getmInstance(this).addToRequestQueue(request);
    }

    private void getDataList(int id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/report/" + id;

        StringRequest request = new StringRequest(Request.Method.GET,
                uRl,
                (String response) -> {
                    try {
                        JSONArray arrRes = new JSONArray(response);
                        for (int i = 0; i < arrRes.length(); i++) {
                            JSONObject obj = arrRes.getJSONObject(i);
                            String idReport = obj.getString("id");
                            String userId = obj.getString("user_id");
                            String title = obj.getString("title");
                            String desc = obj.getString("description");
                            String category = obj.getString("category");
                            String address = obj.getString("address");
                            String image = obj.getString("image");
                            String date = obj.getString("created_at");
                            String latitude = obj.getString("latitude");
                            String longitude = obj.getString("longitude");


                            TextView tvTitle = findViewById(R.id.tvTitle);
                            TextView tvLocation = findViewById(R.id.tvLocation);
                            TextView tvDate = findViewById(R.id.tvDate);
                            TextView tvStatus = findViewById(R.id.statusSolving);
                            LinearLayout llDetail = findViewById(R.id.llDetail);
                            LinearLayout llDetailSolving = findViewById(R.id.llDetailSolving);
                            TextView tvCtg = findViewById(R.id.tvCtg);
                            TextView tvDesc = findViewById(R.id.tvDesc);
                            ImageView ivBack = findViewById(R.id.ivBack);
                            ImageView ivMaps = findViewById(R.id.ivMaps);
                            ImageView ivMapsSolving = findViewById(R.id.ivImageSolving);
                            TextView tvUser = findViewById(R.id.tvPetugas);
                            TextView lblFotoSolv = findViewById(R.id.fotoSolv);
                            TextView lblUser = findViewById(R.id.lblUser);
                            RelativeLayout btnAssigned = findViewById(R.id.btnAssign);
                            RelativeLayout btnSolving = findViewById(R.id.btnSolving);


                            tvTitle.setText(title);
                            tvDate.setText(new DateFormatter().convertDate(date));
                            tvLocation.setText(address);
                            tvCtg.setText(category);
                            tvDesc.setText(desc);

                            Glide.with(this)
                                    .load(image)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(ivMaps);

                            if(obj.get("solving") != null && !obj.get("solving").toString().equals("null")){
                                JSONObject solving = obj.getJSONObject("solving");
                                JSONObject user = obj.getJSONObject("user");
                                ivMapsSolving.setVisibility(View.VISIBLE);
                                Glide.with(this)
                                        .load(solving.getString("photo"))
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(ivMaps);
                                tvUser.setText(user.getString("name"));
                                tvStatus.setText("Selesai");
                            }else{
                                ivMapsSolving.setVisibility(View.GONE);
                                lblFotoSolv.setVisibility(View.GONE);
                                btnAssigned.setVisibility(View.GONE);
                                if(obj.get("user").toString().equals("null")) {
                                    tvStatus.setText("");
                                }
                            }

                            if(type.equals("admin")){
                                btnSolving.setVisibility(View.GONE);
                                if(obj.get("solving") != null && !obj.get("solving").toString().equals("null")) {
                                    btnAssigned.setVisibility(View.GONE);
                                }
                                if(!obj.get("user").toString().equals("null")) {
                                    btnAssigned.setVisibility(View.GONE);
                                }else{
                                    tvUser.setVisibility(View.GONE);
                                    lblUser.setVisibility(View.GONE);
                                    btnAssigned.setVisibility(View.VISIBLE);
                                }
                            }else if(type.equals("teknisi")){
                                btnAssigned.setVisibility(View.GONE);
                                btnSolving.setVisibility(View.VISIBLE);
                                if(obj.get("solving") != null && !obj.get("solving").toString().equals("null")) {
                                    btnSolving.setVisibility(View.GONE);
                                }
                            } else if(type.equals("user")){
                                llDetailSolving.setVisibility(View.GONE);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("tess " + e.toString());
                    }
                    progressDialog.dismiss();
                }, error -> {
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                System.out.println("bods " + body);
                Toast.makeText(DetailComplainActivity.this,
                        body, Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                System.out.println("tessa " + e.toString());
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

        MySingleton.getmInstance(DetailComplainActivity.this).
                addToRequestQueue(request);
    }

    private void getTeknisi() {
        /*final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();*/
        String uRl = "https://sphere-apps.herokuapp.com/api/teknisi";
        StringRequest request = new StringRequest(Request.Method.GET,
                uRl,
                (String response) -> {
                    try {
                        JSONArray arrRes = new JSONArray(response);
                        for (int i = 0; i < arrRes.length(); i++) {
                            JSONObject obj = arrRes.getJSONObject(i);
                            String id = obj.getString("id");
                            String name = obj.getString("name");

                            listTeknisi.add(new Teknisi(id, name));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("tess " + e.toString());
                    }
//                    progressDialog.dismiss();
                }, error -> {
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                System.out.println("bods " + body);
                Toast.makeText(DetailComplainActivity.this, body, Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                System.out.println("tessa " + e.toString());
                // exception
            }
//            progressDialog.dismiss();
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

        MySingleton.getmInstance(DetailComplainActivity.this).
                addToRequestQueue(request);
    }
}