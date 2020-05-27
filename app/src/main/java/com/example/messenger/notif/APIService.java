package com.example.messenger.notif;

import com.example.messenger.notif.MyResponse;
import com.example.messenger.notif.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAN7aVNTA:APA91bG3iFegL1J8ohmDC5izvQjdbsQFAG7qpvSeUHma2YoRCL3f5HcA7xBvkEFCdgvn8j_T8tJdmtLmWhtl-r7q0zh2CMjSuyQy1W4KBVlTK_JGsGMDBol22kjwKo3yOpUSErMqmT9b"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
