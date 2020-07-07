package pl.jawegiel.mvpappwithlocation.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.jawegiel.mvpappwithlocation.R;
import pl.jawegiel.mvpappwithlocation.presenter.PresenterTextView;
import pl.jawegiel.mvpappwithlocation.view.ViewMvpLocation;

public class Util implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_LOCATION = 1;
    public static final int LAT = 0;
    public static final int LON = 1;
    private static final String FILE_NAME = "geotronics_app_log.txt";

    private boolean stopHandler = false;
    private List<Double> myCoords = new ArrayList<>();
    private Activity activity;
    private LocationManager locationManager;
    private PresenterTextView presenterTextView;

    public void setStopHandler(boolean stopHandler) {
        this.stopHandler = stopHandler;
    }

    public Util(Activity activity) {
        this.activity = activity;
        presenterTextView = new PresenterTextView((ViewMvpLocation) activity);
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void buildGoogleApiClient() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    public boolean checkPermission() {
        return (ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    public void logData(List<Double> receiversCoords) {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    presenterTextView.updateLocation();
                    StringBuilder log_line = logLine(myCoords, receiversCoords);
                    writeToFile(log_line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!stopHandler)
                    handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    public Toast createToast(String string) {
        return Toast.makeText(activity, string, Toast.LENGTH_LONG);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(lat1 * Math.PI / 180.0) * Math.sin(lat2 * Math.PI / 180.0) + Math.cos(lat1 * Math.PI / 180.0) * Math.cos(lat2 * Math.PI / 180.0) * Math.cos(theta * Math.PI / 180.0);
        dist = Math.acos(dist);
        dist = dist * 180.0 / Math.PI;
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    public void getMyCoords(Location location) {
        myCoords.add(location.getLatitude());
        myCoords.add(location.getLongitude());
    }

    public List<Double> getMyLocation() {
        if (checkPermission())
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                getMyCoords(locationGPS);
                createToast(getStr(R.string.my_lat) + " " + myCoords.get(LAT) + ", " + getStr(R.string.my_lon) + " " + myCoords.get(LON) + ".").show();
            } else {
                createToast(getStr(R.string.wait_to_find_location)).show();
            }
        }
        return myCoords;
    }

    public String getStr(int id) {
        return activity.getResources().getString(id);
    }

    public StringBuilder logLine(List<Double> myCoords, List<Double> receoversCoords) {
        StringBuilder log_line = new StringBuilder();
        log_line.append("[" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + "], ");
        log_line.append("[" + myCoords.get(LAT) + ", " + myCoords.get(LON) + "], ");
        log_line.append("[" + receoversCoords.get(LAT) + ", " + receoversCoords.get(LON) + "], ");
        log_line.append("[" + distance(myCoords.get(LAT), myCoords.get(LON), receoversCoords.get(LAT), receoversCoords.get(LON)) + "]");
        return log_line;
    }

    public void writeToFile(StringBuilder log_line) throws IOException {
        FileOutputStream fos = activity.openFileOutput(FILE_NAME, Context.MODE_APPEND);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        osw.write(log_line.toString() + "\r\n");
        osw.flush();
        osw.close();
    }
}
