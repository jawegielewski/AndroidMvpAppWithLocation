package pl.jawegiel.mvpappwithlocation.utility;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Api {

    private static final String URL = "http://213.73.2.250:5018/";

    public static ApiInterface getClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(ApiInterface.class);
    }
}