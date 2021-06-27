package org.com.application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.com.application.Adapter.RecyclerViewAdapter;
import org.com.application.Model.PostModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ArticleActivity extends AppCompatActivity {

    private static final String ipaddressLaravel = "10.0.2.2:8000"; //berdasarkan emulator masing2
    private static final String URL_GET_POST_DETAIL = "http://"+ipaddressLaravel+"/api/post?";

    TextView tv_body;
    String id_post;
    PostModel data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel);

        Intent i = getIntent();
        id_post = i.getStringExtra("EXTRA_ID_POST");
        tv_body = findViewById(R.id.tv_body);

        getDetails();
    }


    private void getDetails(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_POST_DETAIL+"id="+id_post,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject obj = jsonArray.getJSONObject(i);

                                if (obj != null) {
//                                            obj.getString("id").trim();
//                                            obj.getString("category_id");
//                                            obj.getString("title");
                                            tv_body.setText(obj.getString("body"));
//                                            obj.getString("created_at");
//                                            obj.getString("image");
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
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
