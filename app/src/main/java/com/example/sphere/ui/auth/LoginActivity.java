package com.example.sphere.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.sphere.MainActivity;
import com.example.sphere.R;
import com.example.sphere.util.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextView email, password, register;
    ImageView showPassLogin;
    Button btnLogin;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        register = findViewById(R.id.txtRegister);
        btnLogin = findViewById(R.id.btnLogin);
        showPassLogin = findViewById(R.id.icPassLogin);
        sharedPreferences = getSharedPreferences("UserInfo",
                Context.MODE_PRIVATE);

        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(m);
            }
        });

        showPassLogin.setOnClickListener(new View.OnClickListener() {
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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tex_email = Objects
                        .requireNonNull(email.getText()).toString();
                String tex_password = Objects
                        .requireNonNull(password.getText()).toString();
                if (TextUtils.isEmpty(tex_email) || TextUtils.isEmpty(tex_password)) {
                    Toast.makeText(LoginActivity.this,
                            "Pastikan Semua Data Sudah Terisi !", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                    Toast.makeText(LoginActivity.this,
                            "Format Email Tidak Valid !", Toast.LENGTH_SHORT).show();
                } else {
                    login(email.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    private void login(String email, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(
                LoginActivity.this);
        progressDialog.setTitle("Masuk Akun Anda....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/auth/login";
        StringRequest request = new StringRequest(Request.Method.POST,
                uRl,
                (String response) -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("isError")){
                            Toast.makeText(LoginActivity.this,
                                    jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                        } else{
                            JSONObject userObj = jsonObject.getJSONObject("user");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", jsonObject.getString("token"));
                            editor.putString("userId", userObj.getString("id"));
                            editor.putString("name", userObj.getString("name"));
                            editor.putString("type", userObj.getString("type"));
                            editor.apply();
                            startActivity(new Intent(LoginActivity
                                    .this, MainActivity.class));
//                            startActivity(new Intent(LoginActivity.this, DetailComplainActivity.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }, error -> {
            Toast.makeText(LoginActivity.this,
                    error.toString(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("email", email);
                param.put("password", password);
                return param;
            }
        };
        request.setRetryPolicy(
                new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getmInstance(LoginActivity.this).
                addToRequestQueue(request);
    }
}