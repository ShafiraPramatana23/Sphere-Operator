package com.example.sphere.ui.scan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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

public class FormScanActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_form_scan);

        RelativeLayout btnSubmit = findViewById(R.id.btnSubmit);
        spin = findViewById(R.id.spinner);
        etDesc = findViewById(R.id.etDeskripsi);
        llNoteUpload = findViewById(R.id.llNoteUpload);
        llPhoto = findViewById(R.id.llPhoto);
        iv = findViewById(R.id.iv);
        tvChange = findViewById(R.id.tvChange);

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        longitude = sharedPreferences.getString("longitude", "");
        latitude = sharedPreferences.getString("latitude", "");

        setAdapterSpinner();

        Intent intent = getIntent();
//        id = intent.getStringExtra("menu");
        id = "1";

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendPatrol();
                dialogAssignTask();
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
                        ActivityCompat.requestPermissions(FormScanActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    } else {
                        ActivityCompat.requestPermissions(FormScanActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(FormScanActivity.this,
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

        String[] petugas = {"Hayo", "Siapa"};

        SpinnerAdapter aa = new SpinnerAdapter(this, R.layout.item_spinner, petugas);
        spin.setAdapter(aa);

        rlSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();
    }

    private void sendPatrol() {
        int selected = (spin.getSelectedItem().toString().equals("Bersih")) ? 1 : 2;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mengirim Data Sungai ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/river/report/"+id;
        StringRequest request = new StringRequest(Request.Method.POST,
                uRl,
                (String response) -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("isError")) {
                            Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent m = new Intent(this, AlertActivity.class);
                            m.putExtra("menu", "patrol");
                            startActivity(m);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }, error -> {
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                System.out.println("bods " + body);
                Toast.makeText(this, body, Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                // exception
                System.out.println("error form patrol : "+e.toString());
            }

            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("status", String.valueOf(selected));
                param.put("description", etDesc.getText().toString());
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

    private void setAdapterSpinner() {
        SpinnerAdapter aa = new SpinnerAdapter(this, R.layout.item_spinner, status);
        spin.setAdapter(aa);
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