package com.accela.esportsman.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.accela.framework.model.ASIFieldValueModel;
import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.utils.Utils;
import com.accela.mobile.AMLogger;
import com.accela.record.model.RecordModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportDetailFragment extends Fragment implements Observer {
    FormEntityCollectionView formEntityCollectionView;
    RecordModel recordModel;
    RecordModel tagModel;
    ImageView imageTag;
    TextView textTagName;
    private static int tagInt;
    String[] confirmFormArray;
    DataManager dataManager;
    List<ASIFieldValueModel> reportDetailList;

    public ReportDetailFragment() {
    }

    public static ReportDetailFragment newInstance(RecordModel model) {
        ReportDetailFragment fragment = new ReportDetailFragment();
        fragment.tagModel = model;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_report_detail, container, false);
        imageTag = (ImageView) contentView.findViewById(R.id.imageTag);
        textTagName = (TextView) contentView.findViewById(R.id.textTagName);
        textTagName.setText(tagModel.getName());
        formEntityCollectionView = (FormEntityCollectionView) contentView.findViewById(R.id.entityContainer);
        dataManager = AppContext.getDataManager();
        dataManager.addObserver(this);
        reportDetailList = dataManager.getReportCustomFields (tagModel);
        if(reportDetailList==null) {
            dataManager.downloadReportCustomFields(tagModel);
        }
        populateReportDetailForm();
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AMLogger.logInfo("ReportDetailFragment.onActivityCreated()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AMLogger.logInfo("ReportDetailFragment.onCreate()");
    }

    @Override
    public void onDestroyView() {
        dataManager.deleteObserver(this);
        super.onDestroyView();
    }

    private String getReportDetailItem(List<ASIFieldValueModel> list, String key) {
        for (ASIFieldValueModel model: list) {
            if (key.equals(model.getFieldName())) {
                return model.getFieldValue();
            }
        }
        return "";
    }

    private void populateReportDetailForm() {
        formEntityCollectionView.emptyCollectionView();
        reportDetailList = dataManager.getReportCustomFields (tagModel);
        if (reportDetailList != null) {
            if (reportDetailList.size() > 0) {
                recordModel = dataManager.getGameHavestRecordByTagId(tagModel.getCustomId());

                if (recordModel != null) {
                    formEntityCollectionView.addEntity(getString(R.string.submit_date), Utils.getDate(recordModel.getOpenedDate(), "MM/dd/yyyy"));
                    formEntityCollectionView.addEntity(getString(R.string.conf_id), recordModel.getCustomId());
                }

                formEntityCollectionView.setEntiryColor(getString(R.string.submit_date), R.color.text_green);
                formEntityCollectionView.addEntity(getString(R.string.doc_id), Utils.formatDocNumber(tagModel.getCustomId()));


                tagInt = DataManager.getAnimalTagInt(tagModel);
                if (tagInt == DataManager.ANIMAL_TAG_DEER) {
                    imageTag.setImageResource(R.mipmap.icon_deer_tag);
                    populateDeerForm(getResources().getStringArray(R.array.confirm_harvest_report_array_deer));
                }
                if (tagInt == DataManager.ANIMAL_TAG_FALL_TURKEY || tagInt == DataManager.ANIMAL_TAG_SPRING_TURKEY) {
                    imageTag.setImageResource(R.mipmap.icon_turkey_tag);
                    populateTurkeyForm(getResources().getStringArray(R.array.confirm_harvest_report_array_turkey));
                }
                if (tagInt == DataManager.ANIMAL_TAG_BEAR) {
                    imageTag.setImageResource(R.mipmap.icon_bear_tag);
                    populateBearForm(getResources().getStringArray(R.array.confirm_harvest_report_array_bear));
                }
            }
        }
    }

    private void populateDeerForm(String[] keyArray) {
        int keyArrayLength = keyArray.length;
        confirmFormArray = null;
        confirmFormArray = new String[keyArrayLength];
        for (int i = 0; i < keyArrayLength; i++) {
            switch (i) {
                case 0:
                    confirmFormArray[0] = getReportDetailItem(reportDetailList, "County of Kill");
                    break;
                case 1:
                    confirmFormArray[1] = getReportDetailItem(reportDetailList, "Town");
                    break;
                case 2:
                    confirmFormArray[2] = getReportDetailItem(reportDetailList, "WMU");
                    break;
                case 3:
                    confirmFormArray[3] = Utils.changeDateFormat(getReportDetailItem(reportDetailList, "Date of Kill"));
                    break;
                case 4:
                    confirmFormArray[4] = getReportDetailItem(reportDetailList, "Deer Season");
                    break;
                case 5:
                    confirmFormArray[5] = getReportDetailItem(reportDetailList, "Deer Taken With");
                    break;
                case 6:
                    confirmFormArray[6] = getReportDetailItem(reportDetailList, "Sex");
                    break;
                case 7:
                    confirmFormArray[7] = getReportDetailItem(reportDetailList, "Left Antler Points");
                    break;
                case 8:
                    confirmFormArray[8] = getReportDetailItem(reportDetailList, "Right Antler Points");
                    break;
                default:
                    break;

            }
        }
        displayForm(keyArray);
    }

    private void populateTurkeyForm(String[] keyArray) {
        int keyArrayLength = keyArray.length;
        confirmFormArray = null;
        confirmFormArray = new String[keyArrayLength];
        for (int i = 0; i < keyArrayLength; i++) {
            switch (i) {
                case 0:
                    confirmFormArray[0] = getReportDetailItem(reportDetailList, "County of Kill");
                    break;
                case 1:
                    confirmFormArray[1] = getReportDetailItem(reportDetailList, "Town");
                    break;
                case 2:
                    confirmFormArray[2] = getReportDetailItem(reportDetailList, "WMU");
                    break;
                case 3:
                    confirmFormArray[3] = Utils.changeDateFormat(getReportDetailItem(reportDetailList, "Date of Kill")); //Utils.getDate(tagModel.getOpenedDate(), "MM/dd/yyyy");
                    break;
                case 4:
                    // The key should be "Weight (to the nearest pound)" here. when submit a report, it should be ""Weight (to nearest pound)"
                    // please check APPGAMEHAR-291
                    confirmFormArray[4] = getReportDetailItem(reportDetailList, "Weight (to the nearest pound)");
                    break;
                case 5:
                    confirmFormArray[5] = getReportDetailItem(reportDetailList, "Turkey Beard Length");
                    break;
                case 6:
                    confirmFormArray[6] = getReportDetailItem(reportDetailList, "Turkey Spur Length");
                    break;
                default:
                    break;

            }
        }
        displayForm(keyArray);
    }

    private void populateBearForm(String[] keyArray) {
        int keyArrayLength = keyArray.length;
        confirmFormArray = null;
        confirmFormArray = new String[keyArrayLength];
        for (int i = 0; i < keyArrayLength; i++) {
            switch (i) {
                case 0:
                    confirmFormArray[0] = getReportDetailItem(reportDetailList, "County of Kill");
                    break;
                case 1:
                    confirmFormArray[1] = getReportDetailItem(reportDetailList, "Town");
                    break;
                case 2:
                    confirmFormArray[2] = getReportDetailItem(reportDetailList, "WMU");
                    break;
                case 3:
                    confirmFormArray[3] = Utils.changeDateFormat(getReportDetailItem(reportDetailList, "Date of Kill"));//Utils.getDate(tagModel.getOpenedDate(), "MM/dd/yyyy");
                    break;
                case 4:
                    confirmFormArray[4] = getReportDetailItem(reportDetailList, "Bear Season");
                    break;
                case 5:
                    confirmFormArray[5] = getReportDetailItem(reportDetailList, "Bear Taken With");
                    break;
                case 6:
                    confirmFormArray[6] = getReportDetailItem(reportDetailList, "Sex");
                    break;
                case 7:
                    confirmFormArray[7] = getReportDetailItem(reportDetailList, "Age");
                    break;
                case 8:
                    confirmFormArray[8] = getReportDetailItem(reportDetailList, "County for Examination of Bear");
                    break;
                case 9:
                    confirmFormArray[9] = getReportDetailItem(reportDetailList, "Address for Examination");
                    break;
                case 10:
                    confirmFormArray[10] = getReportDetailItem(reportDetailList, "Contact Phone #");
                    break;
                default:
                    break;
            }
        }
        displayForm(keyArray);
    }

    private void displayForm(String[] keyArray) {
        formEntityCollectionView.populateEntityCollectionView(keyArray, confirmFormArray, false);
    }

    @Override
    public void update(Observable observable, Object data) {
        populateReportDetailForm();
    }
}
