package com.prince.sirius_fr.data;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prince.sirius_fr.App;
import com.prince.sirius_fr.utilities.SessionManager;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit=null;
    private static final String BASE_URL = SessionManager.getInstance(App.getContext()).getKeyIpAddress();
 

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).addConverterFactory(ScalarsConverterFactory.create()).build();
        }
        return retrofit;
    }
}
