package org.com.application.API;

import org.com.application.Model.CovidModel;
import org.com.application.Model.StepModel;
import org.com.application.Model.UserModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface InterfaceAPI {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("auth/login")
//    Call<UserModel> login(@Header("Accept") String accept,@Header("Content-Type") String content_type,@Body UserModel login);
    Call<UserModel> login(@Body UserModel login);
//    Call<String> login(@Header("Authorization") String authToken);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("auth/signup")
    Call<UserModel> signup(@Body UserModel signup);

    @POST("input/covid")
    Call<CovidModel> covidPost(@Body CovidModel covidPost);

    @GET("auth/user")
    Call<ResponseBody> getUser(@Header("Authorization") String authToken);

    @POST("post/step")
    Call<StepModel> stepPOST(@Body StepModel stepPost);

    @POST("input/covid")
    Call<StepModel> covidPOST(@Body StepModel covidPost);
}
