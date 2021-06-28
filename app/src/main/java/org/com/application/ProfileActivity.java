package org.com.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import org.com.application.Adapter.RecyclerViewAdapter;
import org.com.application.Model.PostModel;
import org.com.application.Model.UserModel;
import org.com.application.SessionManager.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String ipaddressLaravel = "10.0.0.2:8000";
    private static final String URL_GET_POSTS = "http://"+ipaddressLaravel+"/api/posts";
    private static final String URL_GET_USER = "http://"+ipaddressLaravel+"/api/auth/user";
    public static final String URL_BASE_STORAGE = "http://"+ipaddressLaravel+"/storage/";

    private static String ACCESS_TOKEN;

    Button btn_logout, btnHapus_riwayat, btnEdit_riwayat;
    BottomNavigationView bottomNavigation;
    TextView tv_nama, tv_nik, tv_email, tv_no_gejala, tv_tanggal_gejala, tv_riwayat_gejala;
    RecyclerView recyclerViewGejala;
    ArrayList<UserModel> user = new ArrayList<>();

    ArrayList<PostModel> data;
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    SessionManager session;
    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Session class instance
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // token
        ACCESS_TOKEN = user.get(SessionManager.KEY_TOKEN);
        // email
        String email = user.get(SessionManager.KEY_EMAIL);

        btnEdit_riwayat = findViewById(R.id.edit_riwayat);
        btnHapus_riwayat = findViewById(R.id.hapus_riwayat);
        bottomNavigation = findViewById(R.id.bottomNavigationView);
        btn_logout = findViewById(R.id.exit_button);

        tv_nama = findViewById(R.id.nama_tv);
        tv_nik = findViewById(R.id.nik_tv);
        tv_email = findViewById(R.id.email_tv);
        tv_no_gejala = findViewById(R.id.no_riwayat);
        tv_tanggal_gejala = findViewById(R.id.tanggal_riwayat);
        tv_riwayat_gejala = findViewById(R.id.gejala_riwayat);

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//        recyclerViewGejala.setLayoutManager(linearLayoutManager);

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