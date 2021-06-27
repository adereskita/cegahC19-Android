package org.com.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.com.application.API.InterfaceAPI;
import org.com.application.API.RetrofitClient;
import org.com.application.Model.UserModel;
import org.com.application.SessionManager.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private static final String ipaddressLaravel = "10.0.2.2:8000";
    private static final String URL_POST_LOGIN = "http://"+ipaddressLaravel+"/api/auth/login";
    private static final String URL_GET_USER = "http://"+ipaddressLaravel+"/api/auth/user";

    private static String token;

    EditText edt_email, edt_password;
    Button btn_login;
    String email,password;
    TextView tv_register;

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_email = findViewById(R.id.edt_email);
        tv_register = findViewById(R.id.tv_register);
        edt_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.login_button);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivity(i);
                finish();
            }
        });
    }

    private void login(){
        email = edt_email.getText().toString().trim();
        password = edt_password.getText().toString().trim();
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create());
//        Retrofit retrofit = builder.build();

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();

        InterfaceAPI apiClient = retrofit.create(InterfaceAPI.class);

        UserModel user = new UserModel(email,password);
//        Call<UserModel> call = apiClient.login("application/json","application/json",user);
        Call<UserModel> call = apiClient.login(user);

        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, retrofit2.Response<UserModel> response) {
                if (response.isSuccessful()) {
                    try {
                        token = response.body().getAccess_token();

                        session.createLoginSession(token, email);

                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        i.putExtra("ACCESS_TOKEN_EXTRA",token);

                        startActivity(i);
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Toast.makeText(LoginActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
