package com.accela.esportsman.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.data.GeocoderService;
import com.accela.esportsman.data.ReportForm;
import com.accela.esportsman.utils.Utils;
import com.accela.esportsman.view.ElasticScrollView;
import com.accela.esportsman.view.OptionsView;
import com.accela.esportsman.view.SelectorView;
import com.accela.mobile.http.volley.Legacy.TextUtils;
import com.accela.record.model.RecordModel;

import java.util.List;


public class HarvestSpeciesFragment extends ReportFormFragmentBase {

    View contentView;
    ElasticScrollView scrollViewContainer;
    View form;
    EditText textAddress;
    EditText textContact;
    boolean isPhoneNumberValid;

    public static HarvestSpeciesFragment newInstance(RecordModel tag, ReportForm reportForm) {
        HarvestSpeciesFragment fragment = new HarvestSpeciesFragment();
        fragment.reportForm = reportForm;
        fragment.tag = tag;
        return fragment;
    }

    public HarvestSpeciesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_harvest_species, container, false);
        scrollViewContainer = (ElasticScrollView) contentView.findViewById(R.id.scrollViewContainer);
        createReportForm(inflater);
        return contentView;
    }

    private void createReportForm(LayoutInflater inflater) {

        int tagInt = DataManager.getAnimalTagInt(tag);

        ImageView imageTag = (ImageView) contentView.findViewById(R.id.imageTag);
        TextView textTagName = (TextView) contentView.findViewById(R.id.textTagName);

        textTagName.setText(tag.getName());
        //test: tagInt = DataManager.ANIMAL_TAG_BEAR;

        if (tagInt == DataManager.ANIMAL_TAG_BEAR) {
            imageTag.setImageResource(R.mipmap.icon_bear_tag);
            form = inflater.inflate(R.layout.report_form_species_bear, null);
            scrollViewContainer.addView(form);

            textAddress = (EditText) form.findViewById(R.id.editTextAddress);
            textContact = (EditText) form.findViewById(R.id.editTextPhone);

            textAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final String location = textAddress.getText().toString();
                        if (!TextUtils.isEmpty(location)) {
                            GeocoderService.getGeoLocationByAddressAsync(location, new GeocoderService.GeocoderDelegate() {
                                @Override
                                public void onComplete(boolean successful, float latitude, float longitude) {
                                    if (successful) {
                                        reportForm.address = location;
                                        notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                                    }else {
                                        Toast.makeText(getActivity(), "Invalid Address", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            });

            textContact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (reportForm.contactPhone != null) {
                            if (reportForm.contactPhone.length() != 10) {
                                Toast.makeText(getActivity(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

            textContact.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String RegEx = "[^\\d]";
                    String phoneNumber = s.toString().replaceAll(RegEx, "");

                    reportForm.contactPhone = phoneNumber;
                    if (phoneNumber.length() == 10) {
                        if (Utils.isValidPhoneNumber(phoneNumber)) {
//                            reportForm.contactPhone = phoneNumber;
                            notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                        }
                    }
                }
            });


            //sex
            OptionsView optionsSex = (OptionsView) form.findViewById(R.id.optionsSex);
            optionsSex.setSelectorText(reportForm.sex);
            optionsSex.setOnSelectItemListener(new OptionsView.OnSelectItemListener() {
                @Override
                public void OnSelectItem(String itemValue, int position) {
                    reportForm.sex = itemValue;
                    notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                }
            });

            //age
            OptionsView optionsAge = (OptionsView) form.findViewById(R.id.optionsAge);
            optionsAge.setSelectorText(reportForm.age);
            optionsAge.setOnSelectItemListener(new OptionsView.OnSelectItemListener() {
                @Override
                public void OnSelectItem(String itemValue, int position) {
                    reportForm.age = itemValue;
                    notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                }
            });

            //examinationCounty
            SelectorView selectorCounty = (SelectorView) form.findViewById(R.id.selectorCounty);
            List<String> countyList = AppContext.getCustomFormManager().getCountyList();
            String[] list = new String[countyList.size()];
            countyList.toArray(list);
            selectorCounty.setSelectorList(list, -1);
            selectorCounty.setSelectorText(reportForm.examinationCounty);
            selectorCounty.setOnSelectItemListener(new SelectorView.OnSelectItemListener() {
                @Override
                public void OnSelectItem(String item, int position) {
                    reportForm.examinationCounty = item;
                    notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                }
            });


        } else if (tagInt == DataManager.ANIMAL_TAG_SPRING_TURKEY
                || tagInt == DataManager.ANIMAL_TAG_FALL_TURKEY) {
            imageTag.setImageResource(R.mipmap.icon_turkey_tag);
            form = inflater.inflate(R.layout.report_form_species_turkey, null);
            scrollViewContainer.addView(form);
            //weight
            SelectorView selectorWeight = (SelectorView) form.findViewById(R.id.selectorWeight);
            String[] weights = new String[25];
            for (int i = 0; i < weights.length; i++) {
                if (i == weights.length - 1) {
                    weights[i] = String.format("%d+", i + 1);
                } else {
                    weights[i] = String.format("%d", i + 1);
                }
            }
            selectorWeight.setSelectorList(weights, -1);
            selectorWeight.setSelectorText(reportForm.weight);
            selectorWeight.setOnSelectItemListener(new SelectorView.OnSelectItemListener() {
                @Override
                public void OnSelectItem(String item, int position) {
                    reportForm.weight = item;
                    notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                }
            });

            //leg saved?
            OptionsView optionsLegSaved = (OptionsView) form.findViewById(R.id.optionsLegSaved);

            if (tagInt == DataManager.ANIMAL_TAG_SPRING_TURKEY) {
                optionsLegSaved.setVisibility(View.GONE);
                form.findViewById(R.id.seperatorLegSaved).setVisibility(View.GONE);
            } else {
                optionsLegSaved.setSelectorText(reportForm.legSaved);
                optionsLegSaved.setOnSelectItemListener(new OptionsView.OnSelectItemListener() {
                    @Override
                    public void OnSelectItem(String itemValue, int position) {
                        reportForm.legSaved = itemValue;
                        notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                    }
                });
            }

            //spur length
            OptionsView optionsSpurLength = (OptionsView) form.findViewById(R.id.optionsSpurLength);
            optionsSpurLength.setSelectorText(reportForm.spurLength);
            optionsSpurLength.setOnSelectItemListener(new OptionsView.OnSelectItemListener() {
                @Override
                public void OnSelectItem(String itemValue, int position) {
                    reportForm.spurLength = itemValue;
                    notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                }
            });

            //beard length
            OptionsView optionsBeardLength = (OptionsView) form.findViewById(R.id.optionsBeardLength);
            optionsBeardLength.setSelectorText(reportForm.beardLength);
            optionsBeardLength.setOnSelectItemListener(new OptionsView.OnSelectItemListener() {
                @Override
                public void OnSelectItem(String itemValue, int position) {
                    reportForm.beardLength = itemValue;
                    notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                }
            });

        } else if (tagInt == DataManager.ANIMAL_TAG_DEER) {
            imageTag.setImageResource(R.mipmap.icon_deer_tag);
            form = inflater.inflate(R.layout.report_form_species_deer, null);
            scrollViewContainer.addView(form);

            //sex
            OptionsView optionsSex = (OptionsView) form.findViewById(R.id.optionsSex);
            optionsSex.setSelectorText(reportForm.sex);
            optionsSex.setOnSelectItemListener(new OptionsView.OnSelectItemListener() {
                @Override
                public void OnSelectItem(String itemValue, int position) {
                    reportForm.sex = itemValue;
                    notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                }
            });
            // Left Antler Points
            SelectorView selectorLAP = (SelectorView) form.findViewById(R.id.selectorLAP);
            String[] laps = new String[12];
            for (int i = 0; i < laps.length - 1; i++) {
                if (i == laps.length - 2) {
                    laps[i] = String.format("%d+", i-1);
                } else {
                    laps[i] = String.format("%d", i);
                }
            }
            laps[laps.length - 1] = getString(R.string.unknown);
            selectorLAP.setSelectorList(laps, -1);
            selectorLAP.setSelectorText(reportForm.leftAntlerPoints);

            selectorLAP.setOnSelectItemListener(new SelectorView.OnSelectItemListener() {
                @Override
                public void OnSelectItem(String item, int position) {
                    reportForm.leftAntlerPoints = item;
                    notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                }
            });


            // Right Antler Points
            SelectorView selectorRAP = (SelectorView) form.findViewById(R.id.selectorRAP);
            String[] raps = new String[12];
            for (int i = 0; i < raps.length - 1; i++) {
                if (i == raps.length - 2) {
                    raps[i] = String.format("%d+", i-1);
                } else {
                    raps[i] = String.format("%d", i);
                }
            }
            raps[raps.length - 1] = getString(R.string.unknown);
            selectorRAP.setSelectorList(raps, -1);
            selectorRAP.setSelectorText(reportForm.rightAntlerPoints);
            selectorRAP.setOnSelectItemListener(new SelectorView.OnSelectItemListener() {
                @Override
                public void OnSelectItem(String item, int position) {
                    reportForm.rightAntlerPoints = item;
                    notfityFormListener(reportForm.isHarvestSpeciesFormFilled());
                }
            });
        }
    }

}
