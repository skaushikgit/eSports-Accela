package com.accela.esportsman.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.CustomFormManager;
import com.accela.esportsman.data.ReportForm;
import com.accela.esportsman.utils.ActivityUtils;
import com.accela.esportsman.utils.Utils;
import com.accela.esportsman.view.SelectorView;
import com.accela.esportsman.view.SelectorView.OnSelectItemListener;
import com.accela.mobile.AMLogger;
import com.accela.record.model.RecordModel;

import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;


public class HarvestLocationFragment extends ReportFormFragmentBase implements Observer, MapViewFragment.MapViewFragmentReadyListener {

    SelectorView countyView;
    SelectorView townView;
    SelectorView wmuView;
    String[] dataArray = null;
    private Hashtable<String, String> locationTable = new Hashtable<String, String>();
    private static List<String> localCountyList;
    private static List<String> localTownList;
    private static List<String> localWMUList;

    MapViewFragment mapViewFragment;
    CustomFormManager customFormManager = AppContext.getCustomFormManager();

    public static HarvestLocationFragment newInstance(RecordModel tag, ReportForm reportForm) {
        HarvestLocationFragment fragment = new HarvestLocationFragment();
        fragment.tag = tag;
        fragment.reportForm = reportForm;
        return fragment;
    }

    public HarvestLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AMLogger.logInfo("HarvestLocationFragment.onActivityCreated()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AMLogger.logInfo("HarvestLocationFragment.onCreate()");
        //Launch Overlay tutorial per APPGAMEHAR-396, always show
        //if (Utils.isFirstRun(this.getActivity())) {
        ActivityUtils.startOverlayTutorialActivity(getActivity());
        //}
    }

    @Override
    public void onDestroyView() {
        customFormManager.deleteObserver(this);
        super.onDestroyView();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_harvest_location, container, false);
        customFormManager.addObserver(this);
        countyView = (SelectorView) contentView.findViewById(R.id.countyViewId);
        townView = (SelectorView) contentView.findViewById(R.id.townViewId);
        wmuView = (SelectorView) contentView.findViewById(R.id.wmuViewId);

        countyView.setSelectorTitle(getResources().getString(R.string.county));
        townView.setSelectorTitle(getResources().getString(R.string.town));
        wmuView.setSelectorTitle(getResources().getString(R.string.wmu));

        countyView.setSelectorTextStyle(true);
        townView.setSelectorTextStyle(false);
        wmuView.setSelectorTextStyle(false);

        countyView.clearSelectorList();
        townView.clearSelectorList();
        wmuView.clearSelectorList();

        townView.disableClickListener();
        wmuView.disableClickListener();

        localCountyList = customFormManager.getCountyList();
        if (localCountyList.size() > 0) {
            countyView.setSelectorList(Utils.convertListToArray(localCountyList, dataArray), -1);
            countyView.setSelectorText(reportForm.county);
        } else {
            customFormManager.getRecordTypeCustomformAsync();
        }

        localTownList = customFormManager.getTownList();
        if (localTownList.size() > 0) {
            dataArray = null;
            townView.setSelectorList(Utils.convertListToArray(localTownList, dataArray), -1);
            townView.setSelectorText(reportForm.town);
            if (reportForm.town != null) {
                townView.setSelectorTextStyle(true);
                townView.enableClickListener();
            } else {
                townView.setSelectorTextStyle(false);
                townView.disableClickListener();
            }
        }

        localWMUList = customFormManager.getWmuList();
        if (localWMUList.size() > 0) {
            dataArray = null;
            wmuView.setSelectorList(Utils.convertListToArray(localWMUList, dataArray), -1);
            wmuView.setSelectorText(reportForm.wmu);
            if (reportForm.wmu != null) {
                wmuView.setSelectorTextStyle(true);
                wmuView.enableClickListener();
            } else {
                wmuView.setSelectorTextStyle(false);
                wmuView.disableClickListener();
            }
        }

        countyView.setOnSelectItemListener(new OnSelectItemListener() {
            @Override
            public void OnSelectItem(String item, int position) {
                String townDrillId = "" + customFormManager.getTownDrillId();
                List<String> localList = customFormManager.getTownListLocally(item);
                if (localList != null) {
                    customFormManager.setTownList(localList);
                    localTownList = localList;
                    autoPopulateData(localTownList);
                } else {
                    customFormManager.getDrillDownTownList(item, townDrillId);
                }
                selectLocation("COUNTY", item);
                countyView.setSelectorTextStyle(true);
                townView.setSelectorTextStyle(true);
                townView.enableClickListener();
                wmuView.setSelectorTextStyle(false);
                wmuView.disableClickListener();
                wmuView.setSelectorIndex(-1);
                wmuView.clearSelectorList();
                reportForm.county = item;
                notfityFormListener(reportForm.isHarvestLocationFormFilled());
            }
        });

        townView.setOnSelectItemListener(new OnSelectItemListener() {
            @Override
            public void OnSelectItem(String item, int position) {
                String wmuDrillId = "" + customFormManager.getWmuDrillId();
                customFormManager.getDrillDownTownList(item, wmuDrillId);
                selectLocation("TOWN", item);
                wmuView.setSelectorTextStyle(true);
                wmuView.enableClickListener();

                reportForm.town = item;
                notfityFormListener(reportForm.isHarvestLocationFormFilled());
            }
        });

        wmuView.setOnSelectItemListener(new OnSelectItemListener() {
            @Override
            public void OnSelectItem(String item, int position) {
                reportForm.wmu = item;
                notfityFormListener(reportForm.isHarvestLocationFormFilled());
            }
        });

        addMapView();
        return contentView;
    }

    @Override
    public void mapViewFragmentReady() {
        if (reportForm != null) {
            if (reportForm.town != null && reportForm.county != null && locationTable != null) {
                if (locationTable.size() > 0) {
                    displayOnMap(locationTable);
                }
            } else {
                mapViewFragment.setAddress("USA", 2);
            }
        }
    }

    private void selectLocation(String location, String item) {
        if (location.equals("COUNTY")) {
            locationTable.clear();
            locationTable.put(location, item);
            displayOnMap(locationTable);
        } else if (location.equals("TOWN")) {
            locationTable.put(location, item);
            displayOnMap(locationTable);
        }
    }

    private void displayOnMap(Hashtable<String, String> hm) {
        StringBuilder builder = new StringBuilder();
        ;
        Set<String> keys = hm.keySet();
        String[] arr = Utils.reverseArray(keys.toArray(new String[keys.size()]));
        for (int i = 0; i < arr.length; i++) {
            builder.append(hm.get(arr[i]));
            builder.append(",");
        }
        if (builder != null) {
            mapViewFragment.setAddress(builder.toString(), 15);
        }
    }

    private void addMapView() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        mapViewFragment = new MapViewFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.mapViewContainer, mapViewFragment);
        ft.commit();
        mapViewFragment.setMapViewFragmentReadyListner(this);
    }

    private void autoPopulateData(List<String> localTownList) {
        if (localTownList != null && localTownList.size() == 1) {
            String wmuDrillId = "" + customFormManager.getWmuDrillId();
            customFormManager.getDrillDownTownList(localTownList.get(0), wmuDrillId);
            selectLocation("TOWN", localTownList.get(0));
            wmuView.setSelectorTextStyle(true);
            wmuView.enableClickListener();
            reportForm.town = localTownList.get(0);
            townView.setSelectorList(Utils.convertListToArray(localTownList, dataArray), 0);
            notfityFormListener(reportForm.isHarvestLocationFormFilled());
        } else {
            townView.setSelectorList(Utils.convertListToArray(localTownList, dataArray), -1);
        }
        localWMUList = customFormManager.getWmuList();
        wmuView.setSelectorList(Utils.convertListToArray(localWMUList, dataArray), -1);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            int flag = (Integer) data;
            switch (flag) {
                case 0:
                    dataArray = null;
                    localCountyList = customFormManager.getCountyList();
                    countyView.setSelectorList(Utils.convertListToArray(localCountyList, dataArray), -1);
                    break;
                case 1:
                    dataArray = null;
                    localTownList = customFormManager.getTownList();
                    autoPopulateData(localTownList);
                    break;
                case 2:
                    dataArray = null;
                    localWMUList = customFormManager.getWmuList();
                    if (localWMUList != null && localWMUList.size() == 1) {
                        reportForm.wmu = localWMUList.get(0);
                        notfityFormListener(reportForm.isHarvestLocationFormFilled());
                        wmuView.setSelectorList(Utils.convertListToArray(localWMUList, dataArray), 0);
                    } else {
                        wmuView.setSelectorList(Utils.convertListToArray(localWMUList, dataArray), -1);
                    }
                    break;
            }
        }
    }

}