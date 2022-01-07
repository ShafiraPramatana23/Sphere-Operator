package com.example.sphere;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sphere.ui.profile.MyReportActivity;
import com.example.sphere.ui.profile.model.MyReportList;
import com.example.sphere.util.DateFormatter;
import com.example.sphere.util.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailComplainActivity extends AppCompatActivity {

    private String token = "";
    private String id = "";
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_complain);

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        type = sharedPreferences.getString("type", "");

        System.out.println( "type userr" + type);

        RelativeLayout btnAssigned = findViewById(R.id.btnAssign);
        RelativeLayout btnSolving = findViewById(R.id.btnSolving);
        LinearLayout llDetailSolving = findViewById(R.id.llDetailSolving);

        if(type.equals("admin")){
            btnAssigned.setVisibility(View.VISIBLE);
            btnSolving.setVisibility(View.GONE);
        }else if(type.equals("teknisi")){
            btnAssigned.setVisibility(View.GONE);
            btnSolving.setVisibility(View.VISIBLE);
        } else if(type.equals("user")){
            llDetailSolving.setVisibility(View.GONE);
        }

//        Intent intent = getIntent();
//        id = intent.getStringExtra("id");

        Integer id = 4;

        getDataList(4);

        ImageView ivBack = findViewById(R.id.ivBacks);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
                        System.out.println("ars " + arrRes.length());
                        for (int i = 0; i < arrRes.length(); i++) {
                            System.out.println("hahaha " + arrRes.length());
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

                            System.out.println("hihihi " + arrRes.length());
                            System.out.println("solving " + obj.get("solving"));

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

//                            JSONObject objSolving = obj.getJSONObject(solving);
//                            JSONObject objUser = obj.getJSONObject(user);

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

    public String convertDate(String dt) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat fmt2 = new SimpleDateFormat("dd MMM yyyy HH:ss", new Locale("ID"));
        try {
            Date date = fmt.parse(dt);
            return fmt2.format(date);
        } catch(ParseException pe) {
            return "Date";
        }
    }
}