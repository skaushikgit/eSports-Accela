package com.accela.esportsman.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.CustomFormManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class LocationListView extends FrameLayout implements Observer{
    ElasticListView listView;
    LocationListViewAdapter locationListViewAdapter;
    OnSelectLocationItemListener onSelectLocationItemListener;
    private static String locationType = null;
    CustomFormManager customFormManager = AppContext.getCustomFormManager();
    List<String> locationList = new ArrayList<String>();


    public interface OnSelectLocationItemListener {
        public void onSelectItem(String value);
    }

    public LocationListView(Context context) {
        super(context);
        init();
    }

    public LocationListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LocationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        customFormManager.addObserver(this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        customFormManager.addObserver(this);
        super.onDetachedFromWindow();
    }

    private void init() {
        locationListViewAdapter = new LocationListViewAdapter();
        listView = new ElasticListView(getContext());
        listView.setMaxOverScrollDistance(0, 80);
        //Remove the horizontal line
//        ColorDrawable black = new ColorDrawable(getResources().getColor(R.color.black));
//        listView.setDivider(black);
//        listView.setDividerHeight(1);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View Header = (View) inflater.inflate(R.layout.location_list_view_header, null);
        listView.addHeaderView(Header);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(listView, lp);
        listView.setAdapter(locationListViewAdapter);
    }


    public void setOnSelectLocationItemListener(OnSelectLocationItemListener l) {
        onSelectLocationItemListener = l;
    }


    public void updateListViewByData(List<String> data) {
        locationList.clear();
        locationList.addAll(data);
        if (locationListViewAdapter != null) {
            locationListViewAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void update(Observable observable, Object data) {
        int flag = (Integer)data;
        switch (flag) {
            case 0:
                updateListViewByData(customFormManager.getCountyList());
                break;
            case 1:
                updateListViewByData(customFormManager.getTownList());
                break;
            case 2:
                updateListViewByData(customFormManager.getWmuList());
                break;
        }

    }

    public void setLocationtype(String type) {
        locationType = type;
    }

    private class LocationListViewAdapter extends BaseAdapter {
        Integer selectedPosition = 0;


        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_list_item,null);
                viewHolder.radioGroup = (RadioGroup) convertView.findViewById(R.id.radioGroup);
                viewHolder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        for (int i=0; i<group.getChildCount(); i++) {
                            RadioButton btn = (RadioButton) group.getChildAt(i);
                            int id = group.getId();
                            if (btn.getId() == checkedId) {
                                String value = btn.getText().toString();
                                onSelectLocationItemListener.onSelectItem(value);
                                Toast.makeText(getContext(), value , Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                });
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            setLocationListViewItems(viewHolder.radioGroup);
            return convertView;
        }
    }

    private void setLocationListViewItems(RadioGroup radioGroup) {
        RadioButton[] mbutton = null;
        mbutton = new RadioButton[locationList.size()];
        for (int i=0; i<locationList.size(); i++) {
            mbutton[i] = new RadioButton(getContext());
            mbutton[i].setTextColor(getResources().getColor(R.color.black));
            Typeface face = Typeface.createFromAsset(getContext().getAssets(), "Monotype - New Clarendon MT Bold.ttf");
            mbutton[i].setTypeface(null, face.getStyle());
            mbutton[i].setTextSize(20);
            mbutton[i].setText(locationList.get(i));
            mbutton[i].setPadding(25, 16, 16, 16);
            radioGroup.addView(mbutton[i]);
        }

    }

    private class ViewHolder {
       RadioGroup radioGroup;
    }


}
