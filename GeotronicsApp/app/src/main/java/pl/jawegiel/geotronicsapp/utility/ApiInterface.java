package pl.jawegiel.mvpappwithlocation.utility;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("/")
    Call<String> getString();
}