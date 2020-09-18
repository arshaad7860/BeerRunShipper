package com.example.beerrunshipper.Remote;

import com.example.beerrunshipper.Model.FCMResponse;
import com.example.beerrunshipper.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAO9gsGOM:APA91bH8joQ9C7lWGpJbLlT6XwMKE4yL-4gV67jBQGTjoMU1uc2qRnRnIWuwGGUBrj8qyzB3I5aUo0Y_kI-1Wlg63waC9evgIO_POTsqSVY6bFqHygk5i-N94HiBNCHQcg2ixXLDpjEL"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);

}
