package com.accela.esportsman.fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.activity.BaseActivity;
import com.accela.esportsman.activity.ReportSumitCompleteActivity;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.data.ReportForm;
import com.accela.esportsman.data.ReportManager;
import com.accela.record.model.RecordModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HarvestConfirmFragment extends ReportFormFragmentBase {
    FormEntityCollectionView formEntityCollectionView;
    String[] confirmFormArray;
    Button submitButton;
    ImageView imageTag;
    TextView textTagName;
    ReportManager reportManager;
    private static int tagIng;
    DataManager dataManager = AppContext.getDataManager();

    public static HarvestConfirmFragment newInstance(RecordModel tag, ReportForm reportForm) {
        HarvestConfirmFragment fragment = new HarvestConfirmFragment();
        fragment.reportForm = reportForm;
        fragment.tag = tag;
        return fragment;
    }


    public HarvestConfirmFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_harvest_confirm, container, false);

        imageTag = (ImageView) contentView.findViewById(R.id.imageTag);
        textTagName = (TextView) contentView.findViewById(R.id.textTagName);
        textTagName.setText(tag.getName());

        tagIng = DataManager.getAnimalTagInt(tag);
        formEntityCollectionView = (FormEntityCollectionView) contentView.findViewById(R.id.entityContainer);
        submitButton = (Button) contentView.findViewById(R.id.submitReportButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tagIng == 3 || tagIng == 4) {
                    if (reportForm.isTurkeyReadyForSubmit()) {
                        showSubmitAgreement();
                    }
                } else if (reportForm.isReadyForSubmit()) {
                    showSubmitAgreement();
                } else {
                    Toast.makeText(getActivity(), "Please go back and complete the form", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return contentView;
    }

    private void showSubmitAgreement() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name).setMessage(
                        R.string.report_submit_agreement);
        alertDialog.setNegativeButton(R.string.disagree, null);
        alertDialog.setPositiveButton(R.string.agree,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        submitReport();
                    }
                });
        AlertDialog dialog = alertDialog.show();
        Resources resources = dialog.getContext().getResources();
        int color = getResources().getColor(R.color.button_blue); // your color here

        int alertTitleId = resources.getIdentifier("alertTitle", "id", "android");
        TextView alertTitle = (TextView) dialog.getWindow().getDecorView().findViewById(alertTitleId);
        alertTitle.setTextColor(color); // change title text color

//        int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
//        View titleDivider = dialog.getWindow().getDecorView().findViewById(titleDividerId);
//        titleDivider.setBackgroundColor(color); // change divider color

        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);

        TextView titleView = (TextView)dialog.findViewById(getActivity().getResources().getIdentifier("alertTitle", "id", "android"));
        if (titleView != null) {
            titleView.setGravity(Gravity.CENTER);
        }

        Button agreeButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        agreeButton.setTextColor(color);
        Button disagreeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        disagreeButton.setTextColor(color);

    }

    private void submitReport() {
        if(reportManager==null) {
            reportManager = new ReportManager();
            reportManager.setActionListener(new ReportManager.ReportActionListener() {
                @Override
                public void onActionDone(int errorCode, String message, Object data) {
                    ((BaseActivity) getActivity()).closeProgressDialog();
                    switch (errorCode) {
                        case ReportManager.SUBMIT_REPORT_ERROR:
                            Toast.makeText(getActivity(), getString(R.string.submit_report_failed), Toast.LENGTH_SHORT).show();
                            break;
                        case ReportManager.SUBMIT_REPORT_SUCCESSFUL:
                            onSumbitReportSuccess();
                            break;
                    }
                }
            });
        }
        ((BaseActivity) getActivity()).showProgressDialog(getString(R.string.submiting_report), false);
        reportManager.submitNewReport(reportForm);
    }

    private void removeTag(RecordModel recordModel){
        List<RecordModel> list = AppContext.getDataManager().getTags();
        if (list==null)
            return;
        for (int i=0; i<list.size(); i++){
            if (list.get(i).getId()!=null && recordModel.getId()!=null && list.get(i).getId().equals(recordModel.getId())){
                list.remove(i);
                return;
            }
        }

    }

    private void onSumbitReportSuccess() {
        dataManager.refreshLicenseAndReport();
        removeTag(tag);
        Intent intent = new Intent(getActivity(), ReportSumitCompleteActivity.class);
        intent.putExtra("reportForm", reportForm);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    public void populateConfirmFormValues() {
        if (tagIng == DataManager.ANIMAL_TAG_DEER) {
            imageTag.setImageResource(R.mipmap.icon_deer_tag);
            populateDeerForm(getResources().getStringArray(R.array.confirm_harvest_report_array_deer));
        }
        if (tagIng == DataManager.ANIMAL_TAG_FALL_TURKEY) {
            imageTag.setImageResource(R.mipmap.icon_turkey_tag);
            populateFallTurkeyForm(getResources().getStringArray(R.array.confirm_harvest_report_array_fall_turkey));
        }
        if (tagIng == DataManager.ANIMAL_TAG_SPRING_TURKEY) {
            imageTag.setImageResource(R.mipmap.icon_turkey_tag);
            populateTurkeyForm(getResources().getStringArray(R.array.confirm_harvest_report_array_turkey));
        }
        if(tagIng == DataManager.ANIMAL_TAG_BEAR) {
            imageTag.setImageResource(R.mipmap.icon_bear_tag);
            populateBearForm(getResources().getStringArray(R.array.confirm_harvest_report_array_bear));
        }
    }

    private void populateDeerForm(String[] keyArray) {
        int keyArrayLength = keyArray.length;
        confirmFormArray = null;
        confirmFormArray = new String[keyArrayLength];
        for (int i=0; i<keyArrayLength; i++) {
            switch (i) {
                case 0:
                    confirmFormArray[0] = reportForm.county;
                    break;
                case 1:
                    confirmFormArray[1] = reportForm.town;
                    break;
                case 2:
                    confirmFormArray[2] = reportForm.wmu;
                    break;
                case 3:
                    confirmFormArray[3] = reportForm.dateOfKill;
                    break;
                case 4:
                    confirmFormArray[4] = reportForm.season;
                    break;
                case 5:
                    confirmFormArray[5] = reportForm.methodUsed;
                    break;
                case 6:
                    confirmFormArray[6] = reportForm.sex;
                    break;
                case 7:
                    confirmFormArray[7] = reportForm.leftAntlerPoints;
                    break;
                case 8:
                    confirmFormArray[8] = reportForm.rightAntlerPoints;
                    break;
                default:
                    break;

            }
        }
        displayForm(keyArray);
    }

    private void populateFallTurkeyForm(String[] keyArray) {
        int keyArrayLength = keyArray.length;
        confirmFormArray = null;
        confirmFormArray = new String[keyArrayLength];
        for (int i=0; i<keyArrayLength; i++) {
            switch (i) {
                case 0:
                    confirmFormArray[0] = reportForm.county;
                    break;
                case 1:
                    confirmFormArray[1] = reportForm.town;
                    break;
                case 2:
                    confirmFormArray[2] = reportForm.wmu;
                    break;
                case 3:
                    confirmFormArray[3] = reportForm.dateOfKill;
                    break;
                case 4:
                    confirmFormArray[4] = reportForm.weight;
                    break;
                case 5:
                    confirmFormArray[5] = reportForm.legSaved;
                    break;
                case 6:
                    confirmFormArray[6] = reportForm.beardLength;
                    break;
                case 7:
                    confirmFormArray[7] = reportForm.spurLength;
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
        for (int i=0; i<keyArrayLength; i++) {
            switch (i) {
                case 0:
                    confirmFormArray[0] = reportForm.county;
                    break;
                case 1:
                    confirmFormArray[1] = reportForm.town;
                    break;
                case 2:
                    confirmFormArray[2] = reportForm.wmu;
                    break;
                case 3:
                    confirmFormArray[3] = reportForm.dateOfKill;
                    break;
                case 4:
                    confirmFormArray[4] = reportForm.weight;
                    break;
                case 5:
                    confirmFormArray[5] = reportForm.beardLength;
                    break;
                case 6:
                    confirmFormArray[6] = reportForm.spurLength;
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
        for (int i=0; i<keyArrayLength; i++) {
            switch (i) {
                case 0:
                    confirmFormArray[0] = reportForm.county;
                    break;
                case 1:
                    confirmFormArray[1] = reportForm.town;
                    break;
                case 2:
                    confirmFormArray[2] = reportForm.wmu;
                    break;
                case 3:
                    confirmFormArray[3] = reportForm.dateOfKill;
                    break;
                case 4:
                    confirmFormArray[4] = reportForm.season;
                    break;
                case 5:
                    confirmFormArray[5] = reportForm.methodUsed;
                    break;
                case 6:
                    confirmFormArray[6] = reportForm.sex;
                    break;
                case 7:
                    confirmFormArray[7] = reportForm.age;
                    break;
                case 8:
                    confirmFormArray[8] = reportForm.examinationCounty;
                    break;
                case 9:
                    confirmFormArray[9] = reportForm.address;
                    break;
                case 10:
                    confirmFormArray[10] = reportForm.contactPhone;
                    break;
                default:
                    break;

            }
        }
        displayForm(keyArray);
    }


    private void displayForm(String [] keyArray) {

        formEntityCollectionView.populateEntityCollectionView(keyArray, confirmFormArray, true);

    }
}
