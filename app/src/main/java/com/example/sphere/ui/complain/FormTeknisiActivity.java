package com.example.sphere.ui.complain;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sphere.AlertActivity;
import com.example.sphere.BuildConfig;
import com.example.sphere.R;
import com.example.sphere.ui.lapor.adapter.SpinnerAdapter;
import com.example.sphere.util.ImageUtils;
import com.example.sphere.util.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FormTeknisiActivity extends AppCompatActivity {

    String[] status = {"Bersih", "Tidak Bersih"};

    private Spinner spin;
    private EditText etDesc;
    ImageView iv;
    LinearLayout llNoteUpload, llPhoto;
    TextView tvChange;

    private String token = "";
    private String longitude = "";
    private String latitude = "";
    private String id = "";

    private static final int CAMERA_REQUEST = 1888;
    private Uri uri = null;
    private File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_teknisi);

        RelativeLayout btnSubmit = findViewById(R.id.btnSubmit);
        llNoteUpload = findViewById(R.id.llNoteUpload);
        llPhoto = findViewById(R.id.llPhoto);
        iv = findViewById(R.id.iv);
        tvChange = findViewById(R.id.tvChange);

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        longitude = sharedPreferences.getString("longitude", "");
        latitude = sharedPreferences.getString("latitude", "");

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
//        id = "1";

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSolving();
            }
        });

        llNoteUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            getParent(),
                            Manifest.permission.CAMERA
                    )
                    ) {
                        ActivityCompat.requestPermissions(FormTeknisiActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    } else {
                        ActivityCompat.requestPermissions(FormTeknisiActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(FormTeknisiActivity.this,
                                new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                100);
                    } else {
                        try {
                            takePicture();
                        } catch (IOException e) {
                            System.out.println("aawww salah : " + e);
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    takePicture();
                } catch (IOException e) {
                    System.out.println("aawww salah : " + e);
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendSolving() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mengirim perubahan data ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/report/solved/"+id;
        StringRequest request = new StringRequest(Request.Method.POST,
                uRl,
                (String response) -> {
                    System.out.println("alhamdulilah");
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("isError")) {
                            Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent m = new Intent(this, AlertActivity.class);
                            m.putExtra("menu", "solving");
                            startActivity(m);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("error anjay form solving : "+e.toString());
                    }
                    progressDialog.dismiss();
                }, error -> {
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                System.out.println("bods " + body);
                Toast.makeText(this, body, Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                // exception
                System.out.println("error form solving : "+e.toString());
            }

            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("latitude", latitude);
                param.put("longitude", longitude);
                param.put("photo", file.getPath());
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

    private void takePicture() throws IOException {
        File f = new File(Environment.getExternalStorageDirectory() + File.separator +
                Environment.DIRECTORY_PICTURES + File.separator + "sphere");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Files.createDirectory(Paths.get(f.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            f.mkdir();
            f.mkdirs();
            Toast.makeText(this, f.getPath(), Toast.LENGTH_LONG).show();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File testMediaFile = new File(f, "patrol" + timeStamp + ".png");
        testMediaFile.createNewFile();

        file = new File(testMediaFile.getPath());

        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            new ImageUtils().saveBitmapToFile(file);
            Glide.with(this)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(iv);

            llNoteUpload.setVisibility(View.GONE);
            llPhoto.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                try {
                    takePicture();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}