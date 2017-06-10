package com.example.logonrm.iot;

import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by logonrm on 10/06/2017.
 */

public interface LedApi {

    @POST("/led/ligar")
    public Call<ResponseAPI> ligarLed();

    @POST("/led/desligar")
    public Call<ResponseAPI> desligarLed();
}
