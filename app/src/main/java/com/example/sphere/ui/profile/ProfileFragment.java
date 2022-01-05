package com.example.sphere.ui.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.sphere.MainActivity;
import com.example.sphere.R;
import com.example.sphere.ui.auth.EditPasswordActivity;
import com.example.sphere.ui.auth.EditProfileActivity;
import com.example.sphere.ui.auth.LoginActivity;
import com.example.sphere.ui.home.model.River;
import com.example.sphere.ui.lapor.LaporFragment;
import com.example.sphere.util.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {
    TextView txtUsername;
    LinearLayout LLmyakun, LLlaporan, LLsandi, LLKeluar, ivBack;
    SharedPreferences sharedPreferences;
    private String token = "";
    private String name = "";
    private String email = "";
    private String phone = "";

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
            View root = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        txtUsername = root.findViewById(R.id.txtUsername);
        LLmyakun = root.findViewById(R.id.LLmyakun);
        LLlaporan = root.findViewById(R.id.LLlaporan);
        LLsandi = root.findViewById(R.id.LLsandi);
        LLKeluar = root.findViewById(R.id.LLKeluar);

        LLmyakun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Akunsaya = new Intent(getContext(), EditProfileActivity.class);
                Akunsaya.putExtra("name", name);
                Akunsaya.putExtra("email", email);
                Akunsaya.putExtra("phone", phone);
                startActivity(Akunsaya);
            }
        });

        LLlaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Laporansaya = new Intent(getContext(), MyReportActivity.class);
                startActivity(Laporansaya);
            }
        });

        LLsandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Ubahsandi = new Intent(getContext(), EditPasswordActivity.class);
                startActivity(Ubahsandi);
            }
        });

        LLKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        getProfile();
        return root;

    }
    private void getProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Tunggu ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/auth/profile";
        System.out.println("URL weather nyaaa: " + uRl);
        StringRequest request = new StringRequest(Request.Method.GET,
                uRl,
                (String response) -> {
                    try {

                            JSONObject obj = new JSONObject(response);
                            name = obj.getString("name");
                            email = obj.getString("email");
                            phone = obj.getString("phone");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name",name);
                        txtUsername.setText(name);
                        editor.apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("OMG: " + e.toString());
                    }
                    progressDialog.dismiss();
                }, error -> {

            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
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

        MySingleton.getmInstance(getContext()).addToRequestQueue(request);
    }
    private void logout() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Tunggu ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String uRl = "https://sphere-apps.herokuapp.com/api/auth/logout";
        System.out.println("URL weather nyaaa: " + uRl);
        StringRequest request = new StringRequest(Request.Method.POST,
                uRl,
                (String response) -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("isError")){
                            Toast.makeText(getContext(),
                                    jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                        } else{
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            getActivity().finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("OMG: " + e.toString());
                    }
                    progressDialog.dismiss();
                }, error -> {

            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
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

        MySingleton.getmInstance(getContext()).addToRequestQueue(request);
    }

    @Override
    public void onResume() {
        super.onResume();
        getProfile();
    }
}