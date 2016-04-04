package com.accela.esportsman.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.accela.esportsman.R;
import com.accela.esportsman.data.GeocoderService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    Marker marker;
    protected MapViewFragmentReadyListener mapViewFragmentReadyListner;

    public MapViewFragment() {
        // Required empty public constructor
    }

    public interface MapViewFragmentReadyListener {
        public void mapViewFragmentReady();
    }

   public void setMapViewFragmentReadyListner(MapViewFragmentReadyListener l) {
       mapViewFragmentReadyListner = l;
   }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.fragment_map_view, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapViewFragment.this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (mapViewFragmentReadyListner != null) {
            mapViewFragmentReadyListner.mapViewFragmentReady();
        }
    }

    public void setAddress(String location, final int zoomLevel) {
        if (location == null) {
            return;
        }
        GeocoderService.getGeoLocationByAddressAsync(location, new GeocoderService.GeocoderDelegate() {
            @Override
            public void onComplete(boolean successful, float latitude, float longitude) {
                if (successful) {
                    addMarkerToMap(latitude, longitude, zoomLevel);
                }
            }
        });
    }

    private void addMarkerToMap(float latitude, float longitude, int zoomLevel) {
        if (marker != null){
            marker.remove();
        }
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        MarkerOptions options = new MarkerOptions();
        final LatLng latLng = new LatLng(latitude, longitude);
        options.position(latLng);
        marker = map.addMarker(options);
        builder.include(latLng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
        map.animateCamera(update, 500, null);
    }


}
