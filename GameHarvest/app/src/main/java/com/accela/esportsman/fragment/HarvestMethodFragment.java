package com.accela.esportsman.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accela.esportsman.AppConstant;
import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.CustomFormManager;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.data.ReportForm;
import com.accela.esportsman.utils.Utils;
import com.accela.esportsman.view.CalendarView;
import com.accela.esportsman.view.CalendarView.OnSelectDateListener;
import com.accela.esportsman.view.ElasticScrollView;
import com.accela.esportsman.view.OptionsView;
import com.accela.esportsman.view.SelectorView;
import com.accela.record.model.RecordModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class HarvestMethodFragment extends ReportFormFragmentBase implements OnSelectDateListener, Observer{

    View contentView;
    ElasticScrollView scrollViewContainer;
    OptionsView methodUsedOptions;
    SelectorView seasonView;
    CalendarView calenderView;
    TextView tvDateOfKill;
    RecordModel tag;
    List<String> seasonList;
    String[] methodArray = null;
    private static int tagIng;
    CustomFormManager customFormManager = AppContext.getCustomFormManager();
    String[] dateTokens = new String[3];

    public static HarvestMethodFragment newInstance(RecordModel tag, ReportForm reportForm) {
        HarvestMethodFragment fragment = new HarvestMethodFragment();
        fragment.tag = tag;
        fragment.reportForm = reportForm;
        return fragment;
    }

    public HarvestMethodFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_harvest_method, container, false);
        calenderView = (CalendarView) contentView.findViewById(R.id.calendarViewId);
        calenderView.setOnSelectDateListener(this);
        customFormManager.addObserver(this);
        scrollViewContainer = (ElasticScrollView) contentView.findViewById(R.id.scrollViewContainer);
        createReportMethodForm(inflater);
        return contentView;
    }

    private void createReportMethodForm(LayoutInflater inflater) {
        tagIng = DataManager.getAnimalTagInt(tag);
        View form = null;
        form = inflater.inflate(R.layout.report_form_method, null);

        View dateOfKillContainer = (View) form.findViewById(R.id.dateOfKillContainer);

        tvDateOfKill = (TextView) form.findViewById(R.id.dateOfKillId);
        onSelectDate(System.currentTimeMillis());

        seasonView = (SelectorView) form.findViewById(R.id.seasonSelector);
        seasonView.setSelectorTitle("Season");

        methodUsedOptions = (OptionsView) form.findViewById(R.id.methodUsedOptionsId);

        if (tagIng == DataManager.ANIMAL_TAG_DEER) {
            scrollViewContainer.removeAllViews();
            scrollViewContainer.addView(form);
            seasonList = customFormManager.getASIFieldModelOptions(AppConstant.DEER_SEASON);
            populateViews(seasonList);
        } else if (tagIng == DataManager.ANIMAL_TAG_FALL_TURKEY || tagIng == DataManager.ANIMAL_TAG_SPRING_TURKEY) {
            if (dateOfKillContainer.getParent() != null) {
                ((ViewGroup)dateOfKillContainer.getParent()).removeView(dateOfKillContainer);
            }
            scrollViewContainer.removeAllViews();
            scrollViewContainer.addView(dateOfKillContainer);
            notfityFormListener(true);
        }else if (tagIng == DataManager.ANIMAL_TAG_BEAR) {
            scrollViewContainer.removeAllViews();
            scrollViewContainer.addView(form);
            seasonList = customFormManager.getASIFieldModelOptions(AppConstant.BEAR_SEASON);
            populateViews(seasonList);
        }

        seasonView.setOnSelectItemListener(new SelectorView.OnSelectItemListener() {
            @Override
            public void OnSelectItem(String item, int position) {
                setMethodUsedOptions(item);
                reportForm.season = item;
                notfityFormListener(reportForm.isHarvestMethodFormFilled());
            }
        });

        methodUsedOptions.setOnSelectItemListener(new OptionsView.OnSelectItemListener() {
            @Override
            public void OnSelectItem(String itemValue, int position) {
                reportForm.methodUsed = itemValue;
                notfityFormListener(reportForm.isHarvestMethodFormFilled());
            }
        });
    }

    private void populateViews(List<String> seasonList) {
        if (seasonList.size() > 0) {
            seasonView.setSelectorList(Utils.convertListToArray(seasonList), -1);
            seasonView.setSelectorText(reportForm.season);
            seasonView.setSelectorTextStyle(true);
        }
        if (methodArray != null) {
            methodUsedOptions.setSelectorList(methodArray, -1);
            methodUsedOptions.setSelectorText(reportForm.methodUsed);
        }
    }

    private void populateMethodArray(String[] arr) {
        methodArray = new String[arr.length];
        methodArray = arr;
        methodUsedOptions.setSelectorList(methodArray, -1);
    }

    private void setMethodUsedOptions(String item) {
        if (item.equals("Bowhunting")) {
            populateMethodArray(getResources().getStringArray(R.array.bowHunting_method_array));
        }
        if (item.equals("Regular")) {
            populateMethodArray(getResources().getStringArray(R.array.regular_method_array));
        }
        if (item.equals("Muzzleloading")) {
            populateMethodArray(getResources().getStringArray(R.array.muzzleloader_method_array));
        }
        if (item.equals("Youth")) {
            populateMethodArray(getResources().getStringArray(R.array.regular_method_array));
        }
    }

    @Override
    public void onSelectDate(long milliSecondOfDate) {
        dateTokens = null;
        dateTokens = Utils.getDate(milliSecondOfDate, "MMMM dd yyyy").split(" ");

        StringBuilder builder = new StringBuilder();
        for (int i=0; i<dateTokens.length; i++) {
            if (i == 1) {
                builder.append(" ");
                builder.append(dateTokens[i]);
                builder.append(", ");
            } else {
                builder.append(dateTokens[i]);
            }
        }
        tvDateOfKill.setText(builder.toString());
        reportForm.dateOfKill = Utils.getDate(milliSecondOfDate, "MM/dd/yyyy");
        if (tagIng != DataManager.ANIMAL_TAG_FALL_TURKEY || tagIng != DataManager.ANIMAL_TAG_SPRING_TURKEY) {
//            notfityFormListener(false);
            notfityFormListener(reportForm.isHarvestMethodFormFilled());
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            int flag = (Integer) data;
            switch (flag) {
                case 0:
                    if (tagIng == DataManager.ANIMAL_TAG_DEER || tagIng == DataManager.ANIMAL_TAG_FALL_TURKEY || tagIng == DataManager.ANIMAL_TAG_SPRING_TURKEY) {
                        seasonView.setSelectorList(Utils.convertListToArray(customFormManager.getASIFieldModelOptions(AppConstant.DEER_SEASON)), -1);
                    } else if (tagIng == DataManager.ANIMAL_TAG_BEAR) {
                        seasonView.setSelectorList(Utils.convertListToArray(customFormManager.getASIFieldModelOptions(AppConstant.BEAR_SEASON)), -1);
                    }
                    break;
            }
        }
    }
}
