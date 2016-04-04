package com.accela.esportsman.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.AccountManager;
import com.accela.esportsman.data.CitizenContactModel;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.fragment.FormEntityCollectionView;
import com.accela.record.model.RecordModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by skaushik on 9/17/15.
 */
public class LicenseListView extends ElasticListView implements Observer {
    DataManager dataManager = AppContext.getDataManager();
    List<RecordModel> tags;
    List<RecordModel> licenseList;
    LicenseListViewAdapter adapter;
    licenseListOnClickListener listener;
    String[] licenseValueArray = licenseValueArray = new String[2];
    CitizenContactModel citizenContactModel;
    SwipeRefreshLayout swipeRefreshLayout;
    private TextView licenseNumText;
    private TextView licenseUpdateTimeText;
    private Context mContext;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;

    public interface licenseListOnClickListener {
        public void licenseListOnClick(RecordModel model);
    }

    public void setLicenseListClickListener(licenseListOnClickListener l) {
        listener = l;
    }

    public LicenseListView(Context context) {
        super(context);
        mContext = context;
    }

    public LicenseListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public LicenseListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout){
        this.swipeRefreshLayout =swipeRefreshLayout;
    }

    @Override
    protected void onDetachedFromWindow() {
        dataManager.deleteObserver(this);
        super.onDetachedFromWindow();
    }

    public void initLicenseList(TextView licenceTime) {
        dataManager.addObserver(this);
        licenseList = new ArrayList<RecordModel>(dataManager.getLicenses());
        this.setDivider(null);
        this.setDividerHeight(0);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View listHead = inflater.inflate(R.layout.license_list_header, null);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        timeFormat = new SimpleDateFormat("hh:mm a");
        licenseNumText = (TextView) listHead.findViewById(R.id.licenseNumId);
        licenseUpdateTimeText = licenceTime;
        this.addHeaderView(listHead);
        adapter = new LicenseListViewAdapter();
        this.setAdapter(adapter);
        this.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 1)
                    return;
                listener.licenseListOnClick(licenseList.get(position - 1));
            }
        });
        updateHeader();
    }

    private void updateHeader() {
        if (licenseList!=null&&licenseNumText!=null) {
            licenseNumText.setText(String.valueOf(licenseList.size()));
        }
        long lastUpdateTime = dataManager.getLicenseLastUpdateTime();
        if (lastUpdateTime>0) {
            Date date = new Date();
            date.setTime(lastUpdateTime);
            licenseUpdateTimeText.setText("Updated " + dateFormat.format(date)
                 + " at " + timeFormat.format(date));
        }
    }


    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof DataManager && data instanceof Integer) {
            int flag = (Integer) data;
            switch (flag) {
                case 0:
                    citizenContactModel = dataManager.getCitizenContact();
                    adapter.notifyDataSetChanged();
                    break;
                case DataManager.DATAMANAGER_UPDATE_FINISH:
                    swipeRefreshLayout.setRefreshing(false);
            }
            licenseList = new ArrayList<RecordModel>(dataManager.getLicenses());
            adapter.notifyDataSetChanged();
            updateHeader();
        }
    }

    private class ViewHolder {
        ImageView imageTag;
        TextView textTagName;
        FormEntityCollectionView entityContainer;
    }

    private class LicenseListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return licenseList.size();
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.basic_license_view, null);
                viewHolder = new ViewHolder();
                viewHolder.imageTag = (ImageView) convertView.findViewById(R.id.imageTag);
                viewHolder.textTagName = (TextView) convertView.findViewById(R.id.textTagName);
                viewHolder.entityContainer = (FormEntityCollectionView) convertView.findViewById(R.id.entityContainer);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            popluateLicenseData(viewHolder, position);
            return convertView;
        }
    }

    private void popluateLicenseData(ViewHolder viewHolder, int pos) {
        if (licenseList.size() == 0) {
            return;
        }
        if (citizenContactModel != null && citizenContactModel.getId()!=null) {
            licenseValueArray[0] = citizenContactModel.getId() != null? citizenContactModel.getId(): "";
        }else{
            licenseValueArray[0] = AccountManager.MY_DEC_ID;
        }
        licenseValueArray[1] = licenseList.get(pos).getType_subType() != null ? licenseList.get(pos).getType_subType() : "";

        viewHolder.textTagName.setText(licenseList.get(pos).getName()!=null ? licenseList.get(pos).getName() : "");
        viewHolder.entityContainer.populateEntityCollectionView(getResources().getStringArray(R.array.license_key_array),
                licenseValueArray, true);
    }
}
