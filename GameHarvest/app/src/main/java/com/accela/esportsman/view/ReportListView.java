package com.accela.esportsman.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.utils.Utils;
import com.accela.record.model.RecordModel;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by skaushik on 10/2/15.
 */
public class ReportListView extends ElasticListView implements Observer{
    DataManager dataManager = AppContext.getDataManager();
    ReportListViewAdapter adapter;
    List<RecordModel> reportList;
    reportListOnClickListener listener;
    final int offset = 0;
    final int limit = 25;
    TextView reportNumText;
    TextView reportUpdateTimeText;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;
    SwipeRefreshLayout swipeRefreshLayout;

    public ReportListView(Context context) {
        super(context);
    }

    public ReportListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReportListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public interface reportListOnClickListener {
        public void reportListOnClick(RecordModel model);
    }

    public void setReportListClickListener(reportListOnClickListener l) {
        listener = l;
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout){
        this.swipeRefreshLayout =swipeRefreshLayout;
    }

    public void initReportlist(TextView timeText) {
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        timeFormat = new SimpleDateFormat("hh:mm a");
        dataManager.addObserver(this);
        reportList = dataManager.getReports();
        Collections.sort(reportList, new ReportComparator());
        this.reportUpdateTimeText = timeText;
        this.setDivider(null);
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.report_list_header, null);
        reportNumText = (TextView) headerView.findViewById(R.id.reportNumId);
        this.addHeaderView(headerView);
        this.setDividerHeight(0);
        adapter = new ReportListViewAdapter();
        this.setAdapter(adapter);
        this.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    listener.reportListOnClick(reportList.get(position - 1));
            }
        });
        updateHeader();
    }


    private void updateHeader() {
        if (reportList!=null&&reportNumText!=null) {
            reportNumText.setText(String.valueOf(reportList.size()));
            long lastUpdateTime = dataManager.getLastUpdateTime();
            if (lastUpdateTime>0) {
                Date date = new Date();
                date.setTime(lastUpdateTime);
                reportUpdateTimeText.setText("Updated " + dateFormat.format(date)
                        + " at " + timeFormat.format(date));
            }
        }
        if(swipeRefreshLayout!=null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    private class ViewHolder {
        TextView textTagName;
        TextView textDate;
        TextView textStatus;
    }

    private class ReportListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return reportList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.basic_report_view, null);
                viewHolder = new ViewHolder();
                viewHolder.textTagName = (TextView)convertView.findViewById(R.id.textTagName);
                viewHolder.textDate = (TextView)convertView.findViewById(R.id.textDate);
                viewHolder.textStatus = (TextView)convertView.findViewById(R.id.textStatus);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            populateReportData(viewHolder, position);
            return convertView;
        }
    }

    private void populateReportData(ViewHolder viewHolder, int pos) {
        RecordModel tagModel = reportList.get(pos);
        if (tagModel != null) {
            viewHolder.textTagName.setText(tagModel.getName() != null ? tagModel.getName() : "");
            viewHolder.textStatus.setText(tagModel.getStatus_text() != null ? tagModel.getStatus_text() : "");
            RecordModel recordModel = dataManager.getGameHavestRecordByTagId(tagModel.getCustomId());

            if (recordModel != null) {
                viewHolder.textDate.setText(Utils.getDate(recordModel.getOpenedDate(), "MM/dd/yyyy"));
            } else {
                viewHolder.textDate.setText("");
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        reportList = dataManager.getReports();
        Collections.sort(reportList, new ReportComparator());
        adapter.notifyDataSetChanged();
        updateHeader();
    }

    private class ReportComparator implements Comparator<RecordModel>{

        @Override
        public int compare(RecordModel lhs, RecordModel rhs) {
            RecordModel recordModelLeft = dataManager.getGameHavestRecordByTagId(lhs.getCustomId());
            RecordModel recordModelRight = dataManager.getGameHavestRecordByTagId(rhs.getCustomId());
            if(recordModelLeft !=null && recordModelRight !=null) {
                return recordModelRight.getOpenedDate().compareTo(recordModelLeft.getOpenedDate());
            } else if(recordModelLeft !=null) {
                return -1;
            } else if(recordModelRight != null) {
                return 1;
            } else {
                return rhs.getOpenedDate().compareTo(lhs.getOpenedDate());
            }
        }
    }
}


