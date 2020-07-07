package pl.jawegiel.mvpappwithlocation.model;

import android.util.Log;

import pl.jawegiel.mvpappwithlocation.utility.Api;
import pl.jawegiel.mvpappwithlocation.view.ViewMvpLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestData {

    public void getRestDataListRetrofit(final ViewMvpLocation viewMvpLocation) {
        Call<String> result = Api.getClient().getString();
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                viewMvpLocation.dismissProgressDialogWithSuccess(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                viewMvpLocation.dismissProgressDialogWithError(call.request().toString() + " " + t.toString());
                Log.e("tag",t.toString());
            }
        });
    }
}