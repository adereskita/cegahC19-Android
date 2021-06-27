package org.com.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class RegisterActivity extends AppCompatActivity {

    private static final String ipaddressLaravel = "10.0.2.2:8000";
    private static final String URL_POST_LOGIN = "http://"+ipaddressLaravel+"/api/auth/login";

    private static String token;

    ProgressBar progressBar;
    EditText edt_name, edt_email, edt_nik, edt_password, edt_password_sec;
    Button btn_signup;
    String name, email, password, password2, nik;
    TextView tv_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialize
        progressBar = findViewById(R.id.progressBar);
        tv_login = findViewById(R.id.tv_login);
        edt_name = findViewById(R.id.edt_nama_reg);
        edt_email = findViewById(R.id.edt_email_reg);
        edt_password = findViewById(R.id.edt_password_reg);
        edt_password_sec = findViewById(R.id.edt_password_reg_conf);
        edt_nik = findViewById(R.id.edt_nik_reg);
        btn_signup = findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);

                startActivity(i);
                finish();
            }
        });
    }

    private void signUp(){
        nik = edt_nik.getText().toString().trim();
        name = edt_name.getText().toString().trim();
        email = edt_email.getText().toString().trim();
        password = edt_password.getText().toString().trim();
        password2 = edt_password_sec.getText().toString().trim();

        if (password.equals(password2)){
            Retrofit retrofit = RetrofitClient.getRetrofitInstance();

            InterfaceAPI apiClient = retrofit.create(InterfaceAPI.class);

            UserModel user = new UserModel(name,email,password2,nik);
            Call<UserModel> call = apiClient.signup(user);

            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, retrofit2.Response<UserModel> response) {
                    if (response.isSuccessful()) {
                        try {
                            token = response.body().getAccess_token();

                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            i.putExtra("ACCESS_TOKEN_EXTRA",token);
                            startActivity(i);

                            Toast.makeText(RegisterActivity.this, "Berhasil Registrasi, silahkan login terlebih dahulu", Toast.LENGTH_SHORT).show();
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "Password tidak sama.", Toast.LENGTH_SHORT).show();
        }
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create());
//        Retrofit retrofit = builder.build();

    }
}
