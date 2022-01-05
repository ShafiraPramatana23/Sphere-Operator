package com.example.sphere.ui.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.sphere.AlertActivity;
import com.example.sphere.R;
import com.example.sphere.util.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    TextView txtUsername;
    EditText fullname, email, telepon;
    ImageView ivBack;
    Button btnEditProfile;
    SharedPreferences sharedPreferences;

    String token = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_layout);

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        String name = sharedPreferences.getString("name", "");

        Intent intent = getIntent();
        String emailEdit = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        if (phone == null || phone.equals("null")){
            phone = "";
        }

        txtUsername = findViewById(R.id.txtUsername);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        telepon = findViewById(R.id.telepon);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        ivBack = findViewById(R.id.ivBack);
        fullname.setText(name);
        email.setText(emailEdit);
        telepon.setText(phone);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { EditProfileActivity.super.onBackPressed();
            }
        });
        txtUsername.setText(name);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tex_password1 = Objects
                        .requireNonNull(fullname.getText()).toString();
                String tex_password2 = Objects
                        .requireNonNull(email.getText()).toString();
                String tex_password3 = Objects
                        .requireNonNull(telepon.getText()).toString();
                if (TextUtils.isEmpty(tex_password1) || TextUtils.isEmpty(tex_password2) || TextUtils.isEmpty(tex_password3)) {
                    Toast.makeText(EditProfileActivity.this,
                            "Pastikan Semua Data Sudah Terisi !", Toast.LENGTH_SHORT).show();
                } else {
                    editProfile(fullname.getText().toString(), email.getText().toString(), telepon.getText().toString());
                }
            }
        });

    }

    private void editProfile(String fullname, String email, String telepon) {
        final ProgressDialog progressDialog = new ProgressDialog(
                EditProfileActivity.this);
        progressDialog.setTitle("Tunggu....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/auth/edit-profile";

        StringRequest request = new StringRequest(Request.Method.PUT,
                uRl,
                (String response) -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("isError")) {
                            Toast.makeText(EditProfileActivity.this,
                                    jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent m = new Intent(EditProfileActivity.this, AlertActivity.class);
                            m.putExtra("menu", "profile");
                            startActivity(m);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                    finish();
                }, error -> {
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                System.out.println("bods " + body);
                Toast.makeText(EditProfileActivity.this,
                        body, Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                // exception
            }

            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("name", fullname);
                param.put("email", email);
                param.put("phone", telepon);
                return param;
            }

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

        MySingleton.getmInstance(EditProfileActivity.this).
                addToRequestQueue(request);
    }
}
