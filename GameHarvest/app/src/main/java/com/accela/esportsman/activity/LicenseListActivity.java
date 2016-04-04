package com.accela.esportsman.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.utils.ActivityUtils;
import com.accela.esportsman.view.LicenseListView;
import com.accela.esportsman.view.LicenseListView.licenseListOnClickListener;
import com.accela.record.model.RecordModel;

public class LicenseListActivity extends BaseActivity implements licenseListOnClickListener{
    DataManager dataManager = AppContext.getDataManager();
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_list);
        dataManager.getCitizenContact();
        LicenseListView licenseListView = (LicenseListView) findViewById(R.id.licenseListViewId);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshId);
        TextView licenseTimeText = (TextView) findViewById(R.id.licenseUpdateTimeId);
        ImageView viewClose = (ImageView) findViewById(R.id.viewClose);
        viewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        licenseListView.setLicenseListClickListener(this);
        licenseListView.initLicenseList(licenseTimeText);
        licenseListView.setSwipeRefreshLayout(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataManager.refreshLicenseAndReport();
            }
        });
    }

    @Override
    public void licenseListOnClick(RecordModel model) {
        ActivityUtils.startLicenseDetailActivity(this, model);
    }
}
