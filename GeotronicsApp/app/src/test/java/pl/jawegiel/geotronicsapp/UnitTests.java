package pl.jawegiel.mvpappwithlocation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTests {

    @Test
    public void setLineNotEqualNull() {
        // jesli moja lokalizacja i lokalizacja odbiornika nie są null wtedy dziala

//        FragmentManager fm = MainActivity.getSupportFragmentManager();
//        gmap = ((SupportMapFragment) fm.findFragmentById(com.blindmatchrace.R.id.map)).getMap();
//
//        googleMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(Double.parseDouble(myLatitude), Double.parseDouble(myLongitude)), new LatLng(presenterTextView.getLatFromNmeaFormat(coordinatesInNmeaFormat.get(0)), presenterTextView.getLongFromNmeaFormat(coordinatesInNmeaFormat.get(1))))
//                .color(Color.GREEN));

    }

    @Test
    public void setDistanceNotEqualNull() {
        // jesli moja lokalizacja i lokalizacja odbiornika nie są null wtedy dziala
    }

    @Test
    public void nmeaLatitudeConversionShouldEqual() {
        String nmeaLatitide =  "4916.45";
        int firstPart = Integer.parseInt(nmeaLatitide.substring(0,2));
        double secondPart = Double.parseDouble(nmeaLatitide.substring(3, 6));
        double convertedLatitude = firstPart + (secondPart/60);
        assertEquals(49.10666666666667, convertedLatitude, 1);
    }

    @Test
    public void nmeaLongitudeConversionShouldEqual() {
        String nmeaLatitide =  "12311.12";
        int firstPart = Integer.parseInt(nmeaLatitide.substring(0,3));
        double secondPart = Double.parseDouble(nmeaLatitide.substring(4, 7));
        double convertedLatitude = firstPart + (secondPart/60);
        assertEquals(123.01833333333333, convertedLatitude, 1);
    }
}