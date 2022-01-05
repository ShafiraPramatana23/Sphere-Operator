package com.example.sphere.ui.auth;

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

import androidx.appcompat.app.AppCompatActivity;

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

public class EditPasswordActivity extends AppCompatActivity {

    TextView oldpass, newpass, confirmpass;
    ImageView showPassEditPass1, showPassEditPass2, showPassEditPass3, ivBack;
    Button btnEditPass;
    SharedPreferences sharedPreferences;

    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        oldpass = findViewById(R.id.oldPassword);
        newpass = findViewById(R.id.newPassword);
        confirmpass = findViewById(R.id.confirmPassword);
        btnEditPass = findViewById(R.id.btnEditPassword);
        showPassEditPass1 = findViewById(R.id.icPassEdit1);
        showPassEditPass2 = findViewById(R.id.icPassEdit2);
        showPassEditPass3 = findViewById(R.id.icPassEdit3);
        ivBack = findViewById(R.id.ivBack);

        sharedPreferences = getSharedPreferences("UserInfo",
                Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        oldpass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        newpass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        confirmpass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditPasswordActivity.super.onBackPressed();
            }
        });

        showPassEditPass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer inputType1 = oldpass.getInputType();

                if (inputType1 == InputType.TYPE_CLASS_TEXT) {
                    oldpass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                } else {
                    oldpass.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        showPassEditPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer inputType2 = newpass.getInputType();

                if (inputType2 == InputType.TYPE_CLASS_TEXT) {
                    newpass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                } else {
                    newpass.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        showPassEditPass3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer inputType3 = confirmpass.getInputType();

                if (inputType3 == InputType.TYPE_CLASS_TEXT) {
                    confirmpass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                } else {
                    confirmpass.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        btnEditPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tex_password1 = Objects
                        .requireNonNull(oldpass.getText()).toString();
                String tex_password2 = Objects
                        .requireNonNull(newpass.getText()).toString();
                String tex_password3 = Objects
                        .requireNonNull(confirmpass.getText()).toString();
                if (TextUtils.isEmpty(tex_password1) || TextUtils.isEmpty(tex_password2) || TextUtils.isEmpty(tex_password3)) {
                    Toast.makeText(EditPasswordActivity.this,
                            "Pastikan Semua Data Sudah Terisi !", Toast.LENGTH_SHORT).show();
                } else if ((newpass.getText().toString().length() < 8) || (confirmpass.getText().toString().length() < 8)) {
                    Toast.makeText(EditPasswordActivity.this,
                            "Kata Sandi Minimal 8 Karakter !", Toast.LENGTH_SHORT).show();
                } else if (!newpass.getText().toString().equals(confirmpass.getText().toString())) {
                    Toast.makeText(EditPasswordActivity.this,
                            "Password dan Konfirmasi Password Tidak Sama !", Toast.LENGTH_SHORT).show();
                } else {
                    editPassword(oldpass.getText().toString(), newpass.getText().toString(), confirmpass.getText().toString());
                }
            }
        });
    }

    private void editPassword(String oldpass, String newpass, String confirmpass) {
        final ProgressDialog progressDialog = new ProgressDialog(
                EditPasswordActivity.this);
        progressDialog.setTitle("Tunggu....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/auth/edit-password";

        StringRequest request = new StringRequest(Request.Method.PUT,
                uRl,
                (String response) -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("isError")) {
                            Toast.makeText(EditPasswordActivity.this,
                                    jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent m = new Intent(EditPasswordActivity.this, AlertActivity.class);
                            m.putExtra("menu", "password");
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
                Toast.makeText(EditPasswordActivity.this,
                        body, Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                // exception
            }

            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("password_old", oldpass);
                param.put("password", newpass);
                param.put("password_confirmation", confirmpass);
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

        MySingleton.getmInstance(EditPasswordActivity.this).
                addToRequestQueue(request);
    }
}