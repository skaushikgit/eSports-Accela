package com.accela.esportsman.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.CustomFormManager;
import com.accela.esportsman.utils.ActivityUtils;
import com.accela.esportsman.view.ReportListView;
import com.accela.esportsman.view.ReportListView.reportListOnClickListener;
import com.accela.record.model.RecordModel;


public class ReportsListActivity extends BaseActivity implements reportListOnClickListener {
    CustomFormManager customFormManager;
    TextView reportUpdateText;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form_list);
        ReportListView reportListView = (ReportListView) findViewById(R.id.reportListView);
        reportUpdateText = (TextView) findViewById(R.id.reportUpdateTimeId);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRecordRefreshId);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AppContext.getDataManager().refreshLicenseAndReport();
            }
        });
        reportListView.initReportlist(reportUpdateText);
        reportListView.setReportListClickListener(this);
        reportListView.setSwipeRefreshLayout(swipeRefreshLayout);
        customFormManager = new CustomFormManager();
        ImageView viewClose = (ImageView) findViewById(R.id.viewClose);
        viewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void reportListOnClick(RecordModel model) {
        ActivityUtils.startReportDetailActivity(this, model);
    }
}
