package pl.jawegiel.mvpappwithlocation.presenter;

import java.util.ArrayList;
import java.util.List;

import pl.jawegiel.mvpappwithlocation.model.RestData;
import pl.jawegiel.mvpappwithlocation.view.ViewMvpLocation;

public class PresenterTextView {

    private static final int BEGINNING_OF_LAT_FROM_GPGGA = 130;
    private static final int END_OF_LAT_FROM_GPGGA = 145;
    private static final int BEGINNING_OF_LON_FROM_GPGGA = 146;
    private static final int END_OF_LON_FROM_GPGGA = 162;

    private RestData restData;
    private ViewMvpLocation viewMvpLocation;

    public PresenterTextView(ViewMvpLocation viewMvpSimpleTextView) {
        this.restData = new RestData();
        this.viewMvpLocation = viewMvpSimpleTextView;
    }

    public void updateLocation() {
        viewMvpLocation.configureProgressDialog();
        viewMvpLocation.showProgressDialog();
        restData.getRestDataListRetrofit(viewMvpLocation);
    }

    public List<String> getCoordsInNmeaFormat(String gpgga) {
        List<String> coordinates = new ArrayList<>();
        coordinates.add(gpgga.substring(BEGINNING_OF_LAT_FROM_GPGGA, END_OF_LAT_FROM_GPGGA));
        coordinates.add(gpgga.substring(BEGINNING_OF_LON_FROM_GPGGA, END_OF_LON_FROM_GPGGA));
        return coordinates;
    }

    public List<Double> getCoordsFromNmeaFormat(String latInNmeaFormat, String longInNmeaFormat) {
        List<Double> receiverCoords = new ArrayList<>();
        int firstPartOfLat = Integer.parseInt(latInNmeaFormat.substring(0,2));
        double secondPartOfLat = Double.parseDouble(latInNmeaFormat.substring(3, 7));
        receiverCoords.add(firstPartOfLat + (secondPartOfLat/60));

        int firstPartOfLon = Integer.parseInt(longInNmeaFormat.substring(2,4));
        double secondPartOfLon = Double.parseDouble(longInNmeaFormat.substring(5, 9));
        receiverCoords.add(firstPartOfLon + (secondPartOfLon/60));
        return receiverCoords;
    }
}
