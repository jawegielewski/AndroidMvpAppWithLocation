package pl.jawegiel.mvpappwithlocation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.jawegiel.mvpappwithlocation.presenter.PresenterNavigationView;
import pl.jawegiel.mvpappwithlocation.presenter.PresenterTextView;
import pl.jawegiel.mvpappwithlocation.utility.Util;
import pl.jawegiel.mvpappwithlocation.view.ViewMvpNavigationView;
import pl.jawegiel.mvpappwithlocation.view.ViewMvpLocation;

import static pl.jawegiel.mvpappwithlocation.utility.Util.LAT;
import static pl.jawegiel.mvpappwithlocation.utility.Util.LON;

public class MainActivity extends AppCompatActivity implements ViewMvpLocation, ViewMvpNavigationView, OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 1;
    private static final String UNIT = "km";
    private static final String PART_OF_ERROR = "Unexpected status line";

    private double distance = 0;
    private List<Double> receiversCoords = new ArrayList<>(), myCoords = new ArrayList<>();
    private List<String> coordsInNmeaFormat = new ArrayList<>();
    private ActionBarDrawerToggle barDrawerToggle;
    private Button buttonDistance, buttonGetMyPosition, buttonGetReceiverPosition, buttonSetLine, buttonStartLogging, buttonStopLogging;
    private DrawerLayout drawerLayout;
    private GoogleMap googleMap;
    private NavigationView navigationView;
    private PresenterTextView presenterTextView;
    private PresenterNavigationView presenterNavigationView;
    private ProgressDialog progressDialog;
    private SupportMapFragment mapFragment;
    private Util util;
    private boolean notLoggingFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        mapFragment.getMapAsync(this);

        drawerLayout.addDrawerListener(barDrawerToggle);
        barDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.map:
                    presenterNavigationView.setMapItem();
                    break;
                case R.id.exit:
                    presenterNavigationView.setExitItem();
                    break;
                default:
                    return true;
            }
            return true;
        });

        buttonGetMyPosition.setOnClickListener(v -> {
            myCoords = util.getMyLocation();

            if (coordsInNmeaFormat.size() > 0 && myCoords.size() > 0) {
                buttonSetLine.setEnabled(true);
                buttonStartLogging.setEnabled(true);
            }
        });

        buttonGetReceiverPosition.setOnClickListener(v -> {
            presenterTextView.updateLocation();
            notLoggingFlag = true;
        });

        buttonSetLine.setOnClickListener(v -> googleMap.addPolyline(new PolylineOptions()
                .add(new LatLng(myCoords.get(LAT), myCoords.get(LON)), new LatLng(receiversCoords.get(LAT), receiversCoords.get(LON)))
                .color(Color.GREEN)));

        buttonDistance.setOnClickListener(v -> {
            if (myCoords.size() > 0 && coordsInNmeaFormat.size() > 0) {
                distance = Util.distance(myCoords.get(LAT), myCoords.get(LON), receiversCoords.get(LAT), receiversCoords.get(LON));
            }
            util.createToast(distance + UNIT).show();
        });

        buttonStartLogging.setOnClickListener(v -> {
            util.setStopHandler(false);
            util.logData(receiversCoords);
            notLoggingFlag = false;
        });

        buttonStopLogging.setOnClickListener(v -> util.setStopHandler(true));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (barDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void configureProgressDialog() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage(util.getStr(R.string.getting_receivers_coords));
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialogWithSuccess(final String response) {
        progressDialog.dismiss();
    }

    @Override
    public void dismissProgressDialogWithError(final String errorMessage) {
        runOnUiThread(() -> {
            progressDialog.dismiss();
            if (errorMessage.contains(PART_OF_ERROR)) {
                getLonLatFromNmeaFormat(errorMessage);
                createToastAndSetButtonsEnabledIfConditionsMet();
                googleMap.addMarker(new MarkerOptions().position(new LatLng(receiversCoords.get(LAT), receiversCoords.get(LON))).title(util.getStr(R.string.receiver)));
            }
            else
                util.createToast(util.getStr(R.string.error_occured) + " " + errorMessage).show();
        });
    }

    @Override
    public void setMapItem() {
        util.createToast(util.getStr(R.string.map)).show();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void setExitItem() {
        System.exit(0);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        configureMaps(googleMap);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            util.buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }
    }

    public void configureMaps(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.googleMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    public void createNewObjects() {
        util = new Util(this);
        presenterTextView = new PresenterTextView(this);
        presenterNavigationView = new PresenterNavigationView(this);
        progressDialog = new ProgressDialog(MainActivity.this);
        barDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
    }

    public void createToastAndSetButtonsEnabledIfConditionsMet() {
        if (notLoggingFlag)
            util.createToast(util.getStr(R.string.receivers_latitude) + receiversCoords.get(LAT) + ", " + util.getStr(R.string.receivers_longitude) + receiversCoords.get(LON) + ".").show();
        if (myCoords.size() > 1)
            if (myCoords.get(LAT) != 0 && myCoords.get(LON) != 0) {
                buttonSetLine.setEnabled(true);
                buttonStartLogging.setEnabled(true);
            }
    }

    public void findViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nv);
        buttonGetMyPosition = findViewById(R.id.buttonGetMyPosition);
        buttonGetReceiverPosition = findViewById(R.id.buttonGetReceiverPosition);
        buttonSetLine = findViewById(R.id.buttonSetLine);
        buttonDistance = findViewById(R.id.buttonDistance);
        buttonStartLogging = findViewById(R.id.buttonStartLoggingData);
        buttonStopLogging = findViewById(R.id.buttonStopLoggingData);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }

    public void getLonLatFromNmeaFormat(String errorMessage) {
        coordsInNmeaFormat = presenterTextView.getCoordsInNmeaFormat(errorMessage);
        receiversCoords = presenterTextView.getCoordsFromNmeaFormat(coordsInNmeaFormat.get(LAT), coordsInNmeaFormat.get(LON));
    }

    public void initialize() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        findViews();
        createNewObjects();
    }
}
