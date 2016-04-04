package com.accela.esportsman.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.fragment.DialogFragmentTagInfo;
import com.accela.esportsman.utils.ActivityUtils;
import com.accela.esportsman.utils.Utils;
import com.accela.esportsman.view.CarouselView;
import com.accela.record.model.RecordModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SelectTagActivity extends BaseActivity implements Observer {


    CarouselView carouselView;
    DataManager dataManager = AppContext.getDataManager();
    List<RecordModel> listTag = new ArrayList<RecordModel>();
    CarouselPageAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tag);
        setActionBarTitle(R.string.select_a_tag);

        //text info
        TextView textViewInfo = (TextView) findViewById(R.id.textViewInfo);
        String s = getString(R.string.select_tag_info);
        textViewInfo.setText(Html.fromHtml(s));

        //carousel view
        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setAdapter(adapter = new CarouselPageAdapter());

        View viewClose = findViewById(R.id.viewClose);

        viewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dataManager.addObserver(this);
        update(null, null);
    }

    @Override
    protected void onResume(){
        super.onResume();
        listTag.clear();
        listTag.addAll(dataManager.getTags());
        carouselView.setAdapter(adapter = new CarouselPageAdapter());
//        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        dataManager.deleteObserver(this);
        super.onDestroy();
    }

    protected void viewTagInfo(RecordModel tagModel) {
        DialogFragmentTagInfo dialogFragmentTagInfo = new DialogFragmentTagInfo();
        dialogFragmentTagInfo.setRecord(tagModel);
        dialogFragmentTagInfo.show(getSupportFragmentManager(), "DialogFragmentTagInfo");
    }

    protected void selectTag(RecordModel tagModel) {
        ActivityUtils.startHarvestReportFormActivity(this, tagModel);
    }

    @Override
    public void update(Observable observable, Object o) {
        listTag.clear();
        listTag.addAll(dataManager.getTags());
        adapter.notifyDataSetChanged();
    }

    private class CarouselPageAdapter extends CarouselView.CarouselViewAdapter {

        @Override
        public View getViewByPosition(int position) {
            String tag = String.format("item_%d", position);
            return carouselView.findViewWithTag(tag);
        }

        @Override
        public int getCount() {
            return listTag.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            String tag = String.format("item_%d", position);
            View view = carouselView.findViewWithTag(tag);
            if(view == null) {
                view = LayoutInflater.from(SelectTagActivity.this).inflate(R.layout.page_item_select_tag, null);
                container.addView(view);
                view.setTag(tag);

                //set tag name and info
                final RecordModel model = listTag.get(position);
                TextView textTagName = (TextView) view.findViewById(R.id.textTagName);
                textTagName.setText(model.getName());
                TextView textTagInfo = (TextView) view.findViewById(R.id.textTagInfo);
                textTagInfo.setText(Utils.formatDocNumber(model.getCustomId()));

                //set Icon
                ImageView imageTag = (ImageView) view.findViewById(R.id.imageTag);
                int tagInt = DataManager.getAnimalTagInt(model);
                if(tagInt == DataManager.ANIMAL_TAG_FALL_TURKEY ||
                        tagInt == DataManager.ANIMAL_TAG_SPRING_TURKEY) {
                    imageTag.setImageResource(R.mipmap.icon_turkey_tag);
                } else if(tagInt == DataManager.ANIMAL_TAG_DEER) {
                    imageTag.setImageResource(R.mipmap.icon_deer_tag);
                } else if(tagInt == DataManager.ANIMAL_TAG_BEAR) {
                    imageTag.setImageResource(R.mipmap.icon_bear_tag);
                } else {
                    imageTag.setImageResource(R.mipmap.photo_regular_deer);
                }

                //info button
                View viewInfo = view.findViewById(R.id.viewInfo);
                viewInfo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        viewTagInfo(model);
                    }
                });;

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectTag(model);
                    }
                });

            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

        @Override
        public int getItemPosition(Object object) {
            // must add this. force refresh when call notifyDataSetChanged
            return POSITION_NONE;
        }

        @Override
        public float getPageWidth(int position) {
            return 1f;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }


}
