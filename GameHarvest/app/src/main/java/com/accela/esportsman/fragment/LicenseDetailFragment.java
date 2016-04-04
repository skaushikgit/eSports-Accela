package com.accela.esportsman.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.accela.framework.model.ASIFieldValueModel;
import com.accela.framework.util.AMUtils;
import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.CitizenContactModel;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.utils.Utils;
import com.accela.record.model.RecordModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class LicenseDetailFragment extends Fragment implements Observer{
    private static final String HEIGHT_FEET = "Height";
    private static final String HEGHT_INCHES = "Height - inches";
    private static final String EYE_COLOR = "Eye Color";
    DataManager dataManager = AppContext.getDataManager();
    RecordModel recordModel;
    private static int tagIng;
    ImageView imageTag;
    TextView textTagName;
    FormEntityCollectionView collectionView;
    String[] valueArray;
    CitizenContactModel citizenContactModel;
    List<ASIFieldValueModel> customFormContactModelList = new ArrayList<ASIFieldValueModel>();
    View contentView;
    String[] dateTokens = new String[4];

    public static LicenseDetailFragment newInstance(RecordModel model) {
        LicenseDetailFragment fragment = new LicenseDetailFragment();
        fragment.recordModel = model;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_license_detail, container, false);
        imageTag = (ImageView) contentView.findViewById(R.id.imageTag);
        textTagName = (TextView) contentView.findViewById(R.id.textTagName);
        collectionView = (FormEntityCollectionView) contentView.findViewById(R.id.entityContainer);
        tagIng = DataManager.getAnimalTagInt(recordModel);
        dataManager.addObserver(this);
        citizenContactModel = dataManager.getCitizenContact();
        if (AMUtils.isNetworkConnected(getActivity()))
            customFormContactModelList = dataManager.getCustomFormForContacts();

        if (citizenContactModel != null && customFormContactModelList != null) {
            populateLicenseDetailForm();
        }
        return contentView;
    }


    @Override
    public void onDestroyView() {
        contentView = null;
        dataManager.deleteObserver(this);
        super.onDestroyView();
    }

    private void populateLicenseDetailForm() {
        int arrLength = getResources().getStringArray(R.array.license_detail_array).length;
        if (recordModel != null && citizenContactModel != null && customFormContactModelList !=null) {
            //Setup Animal Image and Name
            textTagName.setText(recordModel.getName());
            imageTag.setImageResource(R.mipmap.logo_nys2);

            //Fill up licence detail form
            valueArray = new String[arrLength];
            for (int i=0; i<arrLength; i++) {
                switch (i) {
                    case 0:
                        valueArray[0] = citizenContactModel.getId();
                        break;
                    case 1:
                        valueArray[1] = recordModel.getType_subType();
                        break;
                    case 2:
                        valueArray[2] = recordModel.getType_alias();
                        break;
                    case 3:
                        StringBuilder builder = new StringBuilder();
                        if (citizenContactModel.getFirstName() != null) {
                            builder.append(citizenContactModel.getFirstName());
                            builder.append(" ");
                        }
                        if (citizenContactModel.getMiddleName() != null) {
                            builder.append(citizenContactModel.getMiddleName());
                            builder.append(" ");
                        }
                        if (citizenContactModel.getLastName() != null) {
                            builder.append(citizenContactModel.getLastName());
                        }
                        valueArray[3] = builder.toString();
                        break;
                    case 4:
                        valueArray[4] =  Utils.getAddressFullLine(dataManager.getActiveMailingAddress());
                        break;
                    case 5:
                        valueArray[5] =  Utils.getDate(citizenContactModel.getBirthDate(), "MM/dd/yyyy");
                        break;
                    case 6:
                        valueArray[6] = citizenContactModel.getGender_text();
                        break;
                    case 7:
                        valueArray[7] = dataManager.getCustomFormContactField(EYE_COLOR);
                        break;
                    case 8:
                        valueArray[8] = dataManager.getCustomFormContactField(HEIGHT_FEET)+"' "+dataManager.getCustomFormContactField(HEGHT_INCHES)+"\"";
                        break;
                    case 9:
                        valueArray[9] = Utils.getDate(recordModel.getOpenedDate(), "MM/dd/yyyy");
                        break;
                    case 10:
                        dateTokens = Utils.getDate(dataManager.getStatusDateByRecordId(recordModel.getId()), "MMMM dd yyyy hh:mm:ss").split(" ");
                        if (dateTokens.length == 4)
                        valueArray[10] = dateTokens[3];
                        break;
                    case 11:
                        valueArray[11] = Utils.getDate(recordModel.getOpenedDate(), "MM/dd/yyyy")+" - "+ Utils.getDate(recordModel.getRenewalInfo_expirationDate(), "MM/dd/yyyy");
                        break;
                }
            }
            collectionView.populateEntityCollectionView(getResources().getStringArray(R.array.license_detail_array),
                    valueArray, true);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data==null || !(data instanceof Integer))
            return;
        int flag = (Integer) data;
        switch (flag) {
            case 0:
                if (AMUtils.isNetworkConnected(getActivity()))
                    citizenContactModel = dataManager.getCitizenContact();
                populateLicenseDetailForm();
                break;
            case 1:
                if (AMUtils.isNetworkConnected(getActivity()))
                    customFormContactModelList = dataManager.getCustomFormForContacts();
                populateLicenseDetailForm();
                break;
        }

    }
}
