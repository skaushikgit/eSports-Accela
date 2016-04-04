package com.accela.esportsman.data;

import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.accela.esportsman.AppContext;
import com.accela.mobile.AMLogger;

import java.io.IOException;
import java.util.List;

/**
 * Created by skaushik on 9/1/15.
 */
public class GeocoderService {

    public interface GeocoderDelegate {
        void onComplete(boolean successful, float latitude, float longitude);
    }

    public static void getGeoLocationByAddressAsync(String location, GeocoderDelegate delegate) {
        if (location == null || delegate == null)
            return;
        reverseGeoTask(location, delegate);
    }

    private static void reverseGeoTask(final String location, final GeocoderDelegate delegate) {
        new AsyncTask<String, Integer, PointF>() {

            @Override
            protected PointF doInBackground(String... params) {
                Geocoder geocoder = new Geocoder(AppContext.mContext);
                try {
                    List<Address> list = geocoder.getFromLocationName(location, 1);
                    if (list.size() > 0) {
                        Address address = list.get(0);
                        PointF coordinate = new PointF();
                        coordinate.x = (float) address.getLatitude();
                        coordinate.y = (float) address.getLongitude();
                        AMLogger.logInfo("Address geo: (%f,  %f)", coordinate.x, coordinate.y);
                        return coordinate;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AMLogger.logInfo("Failed to get Address geo: "+location);
                return null;
            }

            @Override
            protected void onPostExecute(PointF coordinate) {
                if (coordinate != null) {
                    delegate.onComplete(true, coordinate.x, coordinate.y);
                } else {
                    delegate.onComplete(false, 0, 0);
                }
            }
        }.execute(location);
    }
}
