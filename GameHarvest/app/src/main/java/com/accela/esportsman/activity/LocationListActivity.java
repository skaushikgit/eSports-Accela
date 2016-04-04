package com.accela.esportsman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.accela.esportsman.R;
import com.accela.esportsman.fragment.LocationListFragment;
import com.accela.esportsman.view.LocationListView.OnSelectLocationItemListener;


public class LocationListActivity extends BaseActivity implements OnSelectLocationItemListener{

    LocationListFragment locationListFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        Intent intent = getIntent();
        String locationType = intent.getStringExtra("LocationType");
        String SelectedValue = intent.getStringExtra("SelectedValue");
        locationListFragment = new LocationListFragment();
        locationListFragment.setSelectLocationItemListener(this);
        if (locationType != null) {
            locationListFragment.setLocationType(locationType, SelectedValue);
        }

        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.add(R.id.rootContainer, (Fragment) locationListFragment);
        ft.commit();

        FrameLayout leftFrame = (FrameLayout) findViewById(R.id.leftFrame);
        leftFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });

        FrameLayout topFrame = (FrameLayout) findViewById(R.id.topFrame);
        topFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });
    }



    @Override
    public void onSelectItem(String value) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", value);
        setResult(RESULT_OK, returnIntent);
        finish();
        // TODO: 8/25/15 Close activity from right to left 
    }


}
