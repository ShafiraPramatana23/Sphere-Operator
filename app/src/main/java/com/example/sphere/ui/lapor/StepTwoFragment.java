package com.example.sphere.ui.lapor;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sphere.AlertActivity;
import com.example.sphere.BuildConfig;
import com.example.sphere.R;
import com.example.sphere.ui.auth.RegisterActivity;
import com.example.sphere.ui.lapor.adapter.SpinnerAdapter;
import com.example.sphere.ui.lapor.model.LaporData;
import com.example.sphere.ui.profile.MyReportActivity;
import com.example.sphere.util.ImageUtils;
import com.example.sphere.util.MySingleton;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StepTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepTwoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    LaporData data = null;

    private String token = "";
    private String longitude = "";
    private String latitude = "";
    private String address = "";

    private static final int CAMERA_REQUEST = 1888;
    private Uri uri = null;
    private File file = null;

    EditText etLokasi;
    MapView mapView;
    ImageView iv;
    LinearLayout llNoteUpload, llPhoto;
    TextView tvChange;

    public StepTwoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepOneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StepTwoFragment newInstance(String param1, String param2) {
        StepTwoFragment fragment = new StepTwoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Mapbox.getInstance(getContext().getApplicationContext(), getString(R.string.mapbox_access_token));

        View view = inflater.inflate(R.layout.fragment_step_two, container, false);


        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        longitude = sharedPreferences.getString("longitude", "");
        latitude = sharedPreferences.getString("latitude", "");

        getLocationAdress();

        RelativeLayout btnDone = view.findViewById(R.id.btnDone);
        RelativeLayout btnPrev = view.findViewById(R.id.btnPrev);
        llNoteUpload = view.findViewById(R.id.llNoteUpload);
        llPhoto = view.findViewById(R.id.llPhoto);
        etLokasi = view.findViewById(R.id.etLokasi);
        mapView = view.findViewById(R.id.map1);
        iv = view.findViewById(R.id.iv);
        tvChange = view.findViewById(R.id.tvChange);

        if (savedInstanceState != null) {
            mapView.onCreate(savedInstanceState);
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        MarkerOptions options = new MarkerOptions();
                        options.title("Current Position");
                        options.position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
                        mapboxMap.addMarker(options);

                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                                .zoom(10)
                                .tilt(20)
                                .build();
                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);
                    }
                });
            }
        });

        llNoteUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getContext(),
                        Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            getActivity(),
                            Manifest.permission.CAMERA
                    )
                    ) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
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

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file == null) {
                    Toast.makeText(getContext(), "Upload foto terlebih dahulu!", Toast.LENGTH_SHORT).show();
                } else {
                    data = StepOneFragment.getInstance().getFormData();
                    System.out.println("UHUY title: " + data.getTitle());
                    sendLapor();
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaporFragment.getInstance().prevStep();
            }
        });

        return view;
    }

    private void getLocationAdress() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Tunggu ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://api.mapbox.com/geocoding/v5/mapbox.places/" + longitude + "," + latitude + ".json?types=poi&access_token=" + getString(R.string.mapbox_access_token);
        System.out.println("URL nyaaa: " + uRl);
        StringRequest request = new StringRequest(Request.Method.GET,
                uRl,
                (String response) -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("features");
                        JSONObject obj = jsonArray.getJSONObject(0);
                        address = obj.getString("place_name");
                        etLokasi.setText(address);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }, error -> {
            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
        request.setRetryPolicy(
                new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getmInstance(getContext()).addToRequestQueue(request);
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
            Toast.makeText(getContext(), f.getPath(), Toast.LENGTH_LONG).show();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File testMediaFile = new File(f, "lapor" + timeStamp + ".png");
        testMediaFile.createNewFile();

        file = new File(testMediaFile.getPath());

        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", file);
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
            Glide.with(getContext())
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(iv);

            llNoteUpload.setVisibility(View.GONE);
            llPhoto.setVisibility(View.VISIBLE);
        }
    }

    private void sendLapor() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Mengirim laporan Anda ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/report/posting";
        StringRequest request = new StringRequest(Request.Method.POST,
                uRl,
                (String response) -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("isError")) {
                            Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent m = new Intent(getContext(), AlertActivity.class);
                            m.putExtra("menu", "report");
                            startActivity(m);
//                            getActivity().finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }, error -> {
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                System.out.println("bods " + body);
                Toast.makeText(getContext(), body, Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                // exception
            }

            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("title", data.getTitle());
                param.put("category", data.getCategory());
                param.put("latitude", latitude);
                param.put("longitude", longitude);
                param.put("address", address);
                param.put("description", data.getDesc());
                param.put("image", file.getPath());
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
                new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getmInstance(getContext()).addToRequestQueue(request);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}