package io.kustrad.dakaar.Remotes;

import io.kustrad.dakaar.Model.Sender;
import retrofit2.Call;

import io.kustrad.dakaar.Model.MyResponse;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
        {
            "Content-Type:application/json",
            "Authorization:key=AAAA7vEbLZs:APA91bG5YOAwLxv0kGNu5lE_z9P3zjNKmhltfAMzHchEnjZd5T75RnqXk8ixzt3moALgRUTUWgu-976kh6TnLIiLrzNEpzT3DgknQK17zCD3cvG1VFwfNQRyXr_ZUM-Kk1pFylyFdi3M"
        }
    )

    @POST("fcm/send")
    Call<MyResponse>  sendNotification(@Body Sender body);
}