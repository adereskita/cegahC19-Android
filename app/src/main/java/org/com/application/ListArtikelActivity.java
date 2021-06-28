package org.com.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.com.application.Adapter.RecyclerArticleAdapter;
import org.com.application.Adapter.RecyclerViewAdapter;
import org.com.application.Model.PostModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ListArtikelActivity extends AppCompatActivity {

    private static final String ipaddressLaravel = "192.168.100.30:8000";
//    private static final String ipaddressLaravel = "10.0.2.2:8000"; //berdasarkan emulator masing2
    private static final String URL_GET_POSTS = "http://"+ipaddressLaravel+"/api/posts";
    public static final String URL_BASE_STORAGE = "http://"+ipaddressLaravel+"/storage/";



    ArrayList<PostModel> data;
    RecyclerArticleAdapter adapterlist;
    RecyclerView recyclerView;

    TextView tv_list_body, tv_list_tittle, tv_tanggal;
    ImageView iv_list_post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_artikel);

        Intent i = getIntent();

        recyclerView = findViewById(R.id.recycler_artikel);
        tv_list_body = findViewById(R.id.tv_body);
        tv_list_tittle = findViewById(R.id.tv_judul);
        tv_tanggal = findViewById(R.id.tv_tanggal);
        iv_list_post = findViewById(R.id.iv_image_artikel);

//        Date c = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
//        String formattedDate = df.format(c);
//
//        tv_tanggal.setText(formattedDate);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                // force height of viewHolder here, this will override layout_height from xml
                return super.checkLayoutParams(lp);
//                lp.height = getHeight() / 2;
//                return true;
            }
        });


        getDetails();
    }

    private void getDetails() {
        data = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_GET_POSTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                if (obj != null) {
                                    PostModel item = new PostModel(
                                            obj.getString("id").trim(),
                                            obj.getString("category_id"),
                                            obj.getString("title"),
                                            obj.getString("body"),
                                            obj.getString("created_at"),
                                            obj.getString("image")
                                    );
                                    data.add(item);
                                }
                            }
                            adapterlist = new RecyclerArticleAdapter(data,
                                    getApplicationContext());
                            recyclerView.setAdapter(adapterlist);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}