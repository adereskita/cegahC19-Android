package org.com.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.com.application.Adapter.RecyclerRiwayatAdapter;
import org.com.application.Adapter.RecyclerViewAdapter;
import org.com.application.Model.PostModel;
import org.com.application.Model.UsersCovidModel;
import org.com.application.SessionManager.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String ipaddressLaravel = "10.0.2.2:8000";
    private static final String URL_GET_COVID_CASE = "http://"+ipaddressLaravel+"/api/user/covid?";
    private static final String URL_DELETE_COVID_ID = "http://"+ipaddressLaravel+"/delete/covid?";
    private static final String URL_GET_USER = "http://"+ipaddressLaravel+"/api/auth/user";
    public static final String URL_BASE_STORAGE = "http://"+ipaddressLaravel+"/storage/";

    private static String ACCESS_TOKEN;

    Button btn_logout, btnHapus_riwayat, btnEdit_riwayat;
    BottomNavigationView bottomNavigation;
    TextView tv_nama, tv_nik, tv_email, tv_no_gejala, tv_tanggal_gejala, tv_riwayat_gejala, data_riwayat_saat_kosong;
    ArrayList<UsersCovidModel> user = new ArrayList<UsersCovidModel>();

    ArrayList<PostModel> data;
    RecyclerRiwayatAdapter adapter;
    RecyclerView recyclerViewGejala;

    private int ID;

    SessionManager session;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        ID = mPreferences.getInt(SessionManager.KEY_ID, 0);

        // Session class instance
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // token
        ACCESS_TOKEN = user.get(SessionManager.KEY_TOKEN);
        // email
        String email = user.get(SessionManager.KEY_EMAIL);

//        btnEdit_riwayat = findViewById(R.id.edit_riwayat);
        btnHapus_riwayat = findViewById(R.id.hapus_riwayat);
        bottomNavigation = findViewById(R.id.bottomNavigationView);
        btn_logout = findViewById(R.id.exit_button);

        tv_nama = findViewById(R.id.nama_tv);
        tv_nik = findViewById(R.id.nik_tv);
        tv_email = findViewById(R.id.email_tv);
        tv_no_gejala = findViewById(R.id.no_riwayat);
        tv_tanggal_gejala = findViewById(R.id.tanggal_riwayat);
        tv_riwayat_gejala = findViewById(R.id.gejala_riwayat);

        data_riwayat_saat_kosong = findViewById(R.id.tv_data_riwayat_saat_kosong);

        recyclerViewGejala = findViewById(R.id.rv_riwayat);

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//        recyclerViewGejala.setLayoutManager(linearLayoutManager);


        //Put riwayatGejala to RecyclerView
        recyclerViewGejala.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                // force height of viewHolder here, this will override layout_height from xml
                return super.checkLayoutParams(lp);
//                lp.height = getHeight() / 2;
//                return true;
            }
        });

        //exit button
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logoutUser();
                finish();
            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_menu:
                        System.out.println("Home");
                        Intent intent2 = new Intent(ProfileActivity.this,HomeActivity.class);
                        startActivity(intent2);
                        return true;
                    case R.id.klinik_menu:
                        //TODO: ganti ke intent
                        System.out.println("klinik");
                        Intent intent = new Intent(ProfileActivity.this,MapsActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.profile_menu:
                        //TODO: ganti ke intent
                        Toast.makeText(getApplicationContext(),"Kamu sudah di Halaman Profile",Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });

        System.out.println(ACCESS_TOKEN);

        LoadUser();
        LoadUsersCovid();
    }

    public void DeleteCovids(int id){
        data = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DELETE_COVID_ID+"id="+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        requestQueue.add(stringRequest);

        adapter.notifyItemRemoved(data.size());
        adapter.notifyDataSetChanged();
    }

    private void LoadUsersCovid(){
        data = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_COVID_CASE+"id_user="+ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        if (object != null) {
                            UsersCovidModel item = new UsersCovidModel(
                                    object.getString("gejala"),
                                    object.getString("created_at")
                            );
                            user.add(item);
                            data_riwayat_saat_kosong.setVisibility(View.INVISIBLE);
                        } else {
                            data_riwayat_saat_kosong.setVisibility(View.VISIBLE);
                        }
                    }
                    adapter = new RecyclerRiwayatAdapter(user,
                            getApplicationContext());
                    recyclerViewGejala.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
                public String getBodyContentType() {
                return "application/json; charset=utf-8";
                }

                //This is for Headers If You Needed
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", "Bearer " + ACCESS_TOKEN);
                    return headers;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
    }


    private void LoadUser(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_GET_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject obj = jsonArray.getJSONObject(i);

                                if (obj != null) {
                                    tv_nama.setText(obj.getString("name"));
                                    tv_email.setText(obj.getString("email"));
                                    tv_nik.setText(obj.getString("nik"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Bearer " + ACCESS_TOKEN);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}