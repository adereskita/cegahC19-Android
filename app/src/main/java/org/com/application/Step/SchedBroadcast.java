package org.com.application.Step;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.com.application.API.InterfaceAPI;
import org.com.application.API.RetrofitClient;
import org.com.application.HomeActivity;
import org.com.application.LoginActivity;
import org.com.application.Model.StepModel;
import org.com.application.Model.UserModel;
import org.com.application.RegisterActivity;
import org.com.application.SessionManager.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.prefs.Preferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SchedBroadcast extends BroadcastReceiver {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private int step;

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        step = mPreferences.getInt(StepActivity.EXTRA_STEP, 0);
        uploadStep();

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().remove(StepActivity.EXTRA_STEP).apply();

    }

    private void uploadStep() {

        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd", new Locale("id", "ID"));
        Calendar currentCal = Calendar.getInstance();
        String currentdate=dateFormat.format(currentCal.getTime());

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();

        InterfaceAPI apiClient = retrofit.create(InterfaceAPI.class);

        int id = mPreferences.getInt(SessionManager.KEY_ID, 0);

        StepModel stepModel = new StepModel(id, currentdate, step);
        Call<StepModel> call = apiClient.stepPOST(stepModel);

        call.enqueue(new Callback<StepModel>() {
            @Override
            public void onResponse(Call<StepModel> call, retrofit2.Response<StepModel> response) {
                if (response.isSuccessful()) {
                    try {
                        System.out.println("Berhasil input step.");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("Gagal input step." +response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<StepModel> call, Throwable t) {
                System.out.println(t.getMessage());

            }
        });
    }
}
