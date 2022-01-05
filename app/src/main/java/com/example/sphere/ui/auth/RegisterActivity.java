package com.example.sphere.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.sphere.AlertActivity;
import com.example.sphere.MainActivity;
import com.example.sphere.R;
import com.example.sphere.util.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    TextView name, email, password, password_conf;
    RelativeLayout btnRegister;
    ImageView showPassReg, showPassConfReg, ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Daftar");
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.title_action_bar);

        name = findViewById(R.id.etName);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        password_conf = findViewById(R.id.etPasswordConfirmation);
        btnRegister = findViewById(R.id.btnRegister);

        showPassReg = findViewById(R.id.icPassReg);
        showPassConfReg = findViewById(R.id.icPassConfReg);
        ivBack = findViewById(R.id.ivBack);

        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        password_conf.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.super.onBackPressed();
            }
        });

        showPassReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer inputType = password.getInputType();
                if (inputType == InputType.TYPE_CLASS_TEXT) {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        showPassConfReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer inputType = password_conf.getInputType();
                if (inputType == InputType.TYPE_CLASS_TEXT) {
                    password_conf.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                } else {
                    password_conf.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tex_name = Objects
                        .requireNonNull(name.getText()).toString();
                String tex_email = Objects
                        .requireNonNull(email.getText()).toString();
                String tex_password = Objects
                        .requireNonNull(password.getText()).toString();
                String tex_passwordconf = Objects
                        .requireNonNull(password_conf.getText()).toString();
                if (TextUtils.isEmpty(tex_name) || TextUtils.isEmpty(tex_email) || TextUtils.isEmpty(tex_password) || TextUtils.isEmpty(tex_passwordconf)) {
                    Toast.makeText(RegisterActivity.this,
                            "Pastikan Semua Data Sudah Terisi !", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                    Toast.makeText(RegisterActivity.this,
                            "Format Email Tidak Valid !", Toast.LENGTH_SHORT).show();
                } else if ((password.getText().toString().length() < 8) || (password_conf.getText().toString().length() < 8)) {
                    Toast.makeText(RegisterActivity.this,
                            "Kata Sandi Minimal 8 Karakter !", Toast.LENGTH_SHORT).show();
                } else if (!password.getText().toString().equals(password_conf.getText().toString())) {
                    Toast.makeText(RegisterActivity.this,
                            "Konfirmasi Kata Sandi Tidak Sama !", Toast.LENGTH_SHORT).show();
                } else {
                    register(name.getText().toString(), email.getText().toString(), password.getText().toString(), password_conf.getText().toString());
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent m = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(m);
        finish();
        return true;
    }

    private void register(String name, String email, String password, String password_conf) {
        final ProgressDialog progressDialog = new ProgressDialog(
                RegisterActivity.this);
        progressDialog.setTitle("Mendaftarkan Akun Anda....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/auth/register";
        StringRequest request = new StringRequest(Request.Method.POST,
                uRl,
                (String response) -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("isError")) {
                            Toast.makeText(RegisterActivity.this,
                                    jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent m = new Intent(RegisterActivity.this, AlertActivity.class);
                            m.putExtra("menu", "register");
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
                Toast.makeText(RegisterActivity.this,
                        body, Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                // exception
            }

            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("name", name);
                param.put("email", email);
                param.put("password", password);
                param.put("password_confirmation", password_conf);
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                return params;
            }
        };
        request.setRetryPolicy(
                new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getmInstance(RegisterActivity.this).
                addToRequestQueue(request);
    }
}