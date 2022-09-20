package com.example.POI.Fragments;

import com.example.POI.Notifications.MyResponse;
import com.example.POI.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAfE_VPIM:APA91bF6TcXJCbIM4UD3CyjV6de0wxlWMl-aexRIvmpbfexBHeGIA8GpGq81f8_5L3oD9gtwE6Dc93udFcTVZooYj0JuhoUmb9_DvP7hXvVTv5O75UoJOyGriUFevriz-W_pNW6xE5ZE"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
