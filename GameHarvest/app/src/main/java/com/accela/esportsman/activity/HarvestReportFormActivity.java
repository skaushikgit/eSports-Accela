package com.accela.esportsman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.CustomFormManager;
import com.accela.esportsman.data.ReportForm;
import com.accela.esportsman.fragment.HarvestConfirmFragment;
import com.accela.esportsman.fragment.HarvestLocationFragment;
import com.accela.esportsman.fragment.HarvestMethodFragment;
import com.accela.esportsman.fragment.HarvestSpeciesFragment;
import com.accela.esportsman.fragment.ReportFormFragmentBase;
import com.accela.esportsman.utils.Utils;
import com.accela.record.model.RecordModel;

public class HarvestReportFormActivity extends BaseActivity {
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    private TextView textTitle;
    private RecordModel tag;
    private ReportForm reportForm;
    CustomFormManager customFormManager = AppContext.getCustomFormManager();
    private int numOfTabs = 1;
    HarvestConfirmFragment harvestConfirmFragment;

    ImageButton tabLocation;
    ImageButton tabCalender;
    ImageButton tabMethod;
    ImageButton tabTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harvest_report_form);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        customFormManager.getRecordTypeCustomformAsync();
        textTitle = (TextView) findViewById(R.id.textTitle);

        Intent intent = getIntent();
        tag = (RecordModel) intent.getSerializableExtra("tag");
        reportForm = new ReportForm(tag);
        ImageView viewClose = (ImageView) findViewById(R.id.viewClose);
        viewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewPager.setAdapter(adapter = new ViewPageAdapter(getSupportFragmentManager()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setPageTitle(position);
                if (position == 3) {
                    harvestConfirmFragment.populateConfirmFormValues();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLocation = (ImageButton) findViewById(R.id.tabLocation);
        tabCalender = (ImageButton) findViewById(R.id.tabCalender);
        tabMethod = (ImageButton) findViewById(R.id.tabMethod);
        tabTag = (ImageButton) findViewById(R.id.tabTag);

        setEnableButtons(true, tabLocation, R.drawable.tab_location);
        setEnableButtons(false, tabCalender, R.drawable.tab_calendar);
        setEnableButtons(false, tabMethod, R.drawable.tab_method);
        setEnableButtons(false, tabTag, R.drawable.tab_tag);

        tabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0, true);
            }
        });

        tabCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1, true);
            }
        });

        tabMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2, true);
            }
        });

        tabTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(3, true);
            }
        });
    }

    private void setPageTitle(int position) {
        switch (position) {
            case 0:
                textTitle.setText(R.string.harvest_location);
                break;
            case 1:
                textTitle.setText(R.string.harvest_season_method);
                break;
            case 2:
                textTitle.setText(R.string.harvest_species);
                break;
            case 3:
                textTitle.setText(R.string.confirm_harvest_report);
                break;
        }
    }

    private void setNumberofPages(int numberofPages) {
        numOfTabs = numberofPages;
    }

    private boolean isAllTabsEnabled() {
        if (tabLocation.isEnabled() && tabMethod.isEnabled() && tabCalender.isEnabled() && tabTag.isEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    private void setEnableButtons(boolean enabled, ImageButton item, int iconResId) {
        Utils.setImageButtonEnabled(this, enabled, item, iconResId);
    }

    private class ViewPageAdapter extends FragmentPagerAdapter {

        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    HarvestLocationFragment fragment = HarvestLocationFragment.newInstance(tag, reportForm);
                    fragment.setReportFormFieldChangeListener(new ReportFormFragmentBase.ReportFormFieldChangeListener() {
                        @Override
                        public void formFieldChange(ReportFormFragmentBase fragment, boolean isAllFieldsFilled) {
                            if (isAllFieldsFilled) {
                                if (!isAllTabsEnabled()) {
                                    setEnableButtons(true, tabCalender, R.drawable.tab_calendar);
                                    setNumberofPages(2);
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    });
                    return fragment;
                }

                case 1: {
                    HarvestMethodFragment fragment = HarvestMethodFragment.newInstance(tag, reportForm);
                    fragment.setReportFormFieldChangeListener(new ReportFormFragmentBase.ReportFormFieldChangeListener() {
                        @Override
                        public void formFieldChange(ReportFormFragmentBase fragment, boolean isAllFieldsFilled) {
                            if (isAllFieldsFilled) {
                                if (!isAllTabsEnabled()) {
                                    setEnableButtons(true, tabMethod, R.drawable.tab_method);
                                    setNumberofPages(3);
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    });
                    return fragment;
                }

                case 2: {
                    HarvestSpeciesFragment fragment = HarvestSpeciesFragment.newInstance(tag, reportForm);
                    fragment.setReportFormFieldChangeListener(new ReportFormFragmentBase.ReportFormFieldChangeListener() {
                        @Override
                        public void formFieldChange(ReportFormFragmentBase fragment, boolean isAllFieldsFilled) {
                            if (isAllFieldsFilled) {
                                if (!isAllTabsEnabled()) {
                                    setEnableButtons(true, tabTag, R.drawable.tab_tag);
                                    setNumberofPages(4);
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    });
                    return fragment;
                }

                case 3:
                    harvestConfirmFragment = HarvestConfirmFragment.newInstance(tag, reportForm);
                    harvestConfirmFragment.setReportFormFieldChangeListener(new ReportFormFragmentBase.ReportFormFieldChangeListener() {
                        @Override
                        public void formFieldChange(ReportFormFragmentBase fragment, boolean isAllFieldsFilled) {
                            if (isAllFieldsFilled) {

                            }
                        }
                    });
                    return harvestConfirmFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }

}
