package com.accela.esportsman.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.CustomFormManager;
import com.accela.esportsman.view.LocationListView;
import com.accela.esportsman.view.LocationListView.OnSelectLocationItemListener;

import java.util.List;

public class LocationListFragment extends Fragment {

    OnSelectLocationItemListener onSelectLocationItemListener;
    LocationListView locationListView;
    private static String locationType = null;
    private static String selectedValue = null;
    CustomFormManager customFormManager = AppContext.getCustomFormManager();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_location_list, container, false);
        locationListView = (LocationListView) contentView.findViewById(R.id.locationListViewId);
        if (locationType != null) {
            locationListView.setLocationtype(locationType);
        }

        if (locationType.equals("County")) {
            List<String> lList = customFormManager.getCountyList();
            if (lList.size() > 0) {
                locationListView.updateListViewByData(lList);
            } else {
                customFormManager.getRecordTypeCustomformAsync();
            }

        }

        if (locationType.equals("town")) {
            String townDrillId = "" + customFormManager.getTownDrillId();
            customFormManager.getDrillDownTownList(selectedValue, townDrillId);
        }

        if (locationType.equals("wmu")) {
            String wmuDrillId = "" + customFormManager.getWmuDrillId();
            customFormManager.getDrillDownTownList(selectedValue, wmuDrillId);
        }

        locationListView.setOnSelectLocationItemListener(onSelectLocationItemListener);
        return contentView;
    }

    public void setSelectLocationItemListener(OnSelectLocationItemListener l) {
        onSelectLocationItemListener = l;
        if (locationListView != null) {
            locationListView.setOnSelectLocationItemListener(onSelectLocationItemListener);
        }
    }

    public void setLocationType(String type, String value) {
        locationType = type;
        selectedValue = value;
    }
}
