package com.accela.esportsman.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.accela.esportsman.AppConstant;
import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.fragment.LandingPageFragment;
import com.accela.esportsman.fragment.WebviewFragment;
import com.accela.esportsman.slidemenu.SlideMenu;
import com.accela.esportsman.view.ElasticListView;
import com.accela.record.model.ContactModel;

import java.util.Calendar;
import java.util.Date;

public class LandingActivity extends BaseActivity {
    SlideMenuListView slideMenuListView;
    SlideMenu slideMenu;
    SlideMenuAdapter adapterSlideMenu;
    TextView textProfileName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        slideMenu = new SlideMenu(this);
        setContentView(slideMenu);

        // Setup the content view
        FrameLayout contentView = new FrameLayout(this);
        contentView.setId(R.id.contentViewId);
        slideMenu.addView(contentView, new SlideMenu.LayoutParams(
                SlideMenu.LayoutParams.MATCH_PARENT, SlideMenu.LayoutParams.MATCH_PARENT,
                SlideMenu.LayoutParams.ROLE_CONTENT));


        int slideMenuWidth = (int) (getResources().getDisplayMetrics().density * 275);

        // Setup the primary menu
        slideMenuListView = new SlideMenuListView(this);
        slideMenu.addView(slideMenuListView, new SlideMenu.LayoutParams(slideMenuWidth,
                SlideMenu.LayoutParams.MATCH_PARENT, SlideMenu.LayoutParams.ROLE_PRIMARY_MENU));
        slideMenuListView.setBackgroundColor(getResources().getColor(R.color.black));
        showLandingPageFragment();

        slideMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onSelectMenuItem(i);
            }
        });

        slideMenu.setOnSlideStateChangeListener(new SlideMenu.OnSlideStateChangeListener() {
            @Override
            public void onSlideStateChange(int slideState) {
                if (textProfileName != null && textProfileName.getText().equals("")) {
                    //update profile
                    DataManager dataManager = AppContext.getDataManager();
                    ContactModel contactModel = dataManager.getProfile();
                    if (contactModel != null) {
                        textProfileName.setText(contactModel.getFirstName() + " " + contactModel.getLastName());
                    }
                }
            }

            @Override
            public void onSlideOffsetChange(float offsetPercent) {

            }
        });
        loadTagValidDates();
    }

    private void loadTagValidDates(){
        String[] dateArray = getResources().getStringArray(R.array.valid_dates_tag);
        int year = Calendar.getInstance().get(Calendar.YEAR)-1900;
        AppConstant.bearStart = new Date(year, Integer.valueOf(dateArray[0].split("/")[0])-1, Integer.valueOf(dateArray[0].split("/")[1]));
        AppConstant.bearEnd = new Date(year, Integer.valueOf(dateArray[1].split("/")[0])-1, Integer.valueOf(dateArray[1].split("/")[1]));
        AppConstant.deerStart = new Date(year, Integer.valueOf(dateArray[2].split("/")[0])-1, Integer.valueOf(dateArray[2].split("/")[1]));
        AppConstant.deerEnd = new Date(year, Integer.valueOf(dateArray[3].split("/")[0])-1, Integer.valueOf(dateArray[3].split("/")[1]));
        AppConstant.fallTurkeyStart = new Date(year, Integer.valueOf(dateArray[4].split("/")[0])-1, Integer.valueOf(dateArray[4].split("/")[1]));
        AppConstant.fallTurkeyEnd = new Date(year, Integer.valueOf(dateArray[5].split("/")[0])-1, Integer.valueOf(dateArray[5].split("/")[1]));
        AppConstant.springTurkeyStart = new Date(year, Integer.valueOf(dateArray[6].split("/")[0])-1, Integer.valueOf(dateArray[6].split("/")[1]));
        AppConstant.springTurkeyEnd = new Date(year, Integer.valueOf(dateArray[7].split("/")[0])-1, Integer.valueOf(dateArray[7].split("/")[1]));
    }



    protected void showLandingPageFragment() {
        String tag = "landingPageFragment";
        if(this.getSupportFragmentManager().findFragmentByTag(tag)!=null) {
            return;
        }

        LandingPageFragment landingPageFragment = new LandingPageFragment();
        landingPageFragment.setSlideMenu(slideMenu);
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.contentViewId, landingPageFragment, tag);
        ft.commit();
    }

    protected void showWebView(String url) {
        String tag = "webviewFragment";
//        WebviewFragment webviewFragment = (WebviewFragment) this.getSupportFragmentManager().findFragmentByTag(tag);
//        if(webviewFragment!=null) {
//            //TODO: call navigate to url.
////            return;
//        }
        //add webview fragment here
        WebviewFragment webviewFragment = new WebviewFragment();
        webviewFragment.setSlideMenu(slideMenu);
        webviewFragment.setFragmentUrl(url);
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentViewId, webviewFragment, tag);
        //TODO: call navigate to url.
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        LandingPageFragment landingPageFragment = (LandingPageFragment) this.getSupportFragmentManager().findFragmentByTag("landingPageFragment");
        if (landingPageFragment != null && landingPageFragment.isVisible()) {
            showLogoutConfirmMessage();
        }

        WebviewFragment webviewFragment = (WebviewFragment) this.getSupportFragmentManager().findFragmentByTag("webviewFragment");
        if (webviewFragment != null && webviewFragment.isVisible()) {
            Intent intent = new Intent(this, LandingActivity.class);
            startActivity(intent);
        }

    }


    protected class SlideMenuListView extends ElasticListView{

        public SlideMenuListView(Context context) {
            super(context);

            adapterSlideMenu = new SlideMenuAdapter();
            setAdapter(adapterSlideMenu);
        }

    }

    int MENU_ITEMS[][] = {
            {0, 0},
            {0, R.string.HelpTitle},
            {0, R.string.RegulationTitle},
            {0, R.string.ApplicationTitle},
            {0, R.string.ContactECO},
            {0, R.string.FAQTitle},
            {R.mipmap.menu_user, R.string.logout},
            {R.mipmap.icon_feedback, R.string.send_feedback}
    };

    protected void onSelectMenuItem(int position) {

        switch(position) {
            case 0:
                //profile
                slideMenu.close(true);
                showLandingPageFragment();
                break;
            case 1:
                //eSportsman information
                slideMenu.close(true);
                showWebView("http://www.dec.ny.gov/outdoor/8316.html");
                break;
            case 2:
                //eSportsman regulation
                slideMenu.close(true);
                showWebView("http://www.dec.ny.gov/outdoor/28182.html");
                break;
            case 3:
                //application url
                slideMenu.close(true);
                showWebView("http://www.dec.ny.gov/permits/6094.html");
                break;
            case 4:
                //contact ECO url
                slideMenu.close(true);
                showWebView("http://www.dec.ny.gov/about/50303.html");
                break;
            case 5:
                //FAQ url
                slideMenu.close(true);
                showWebView("http://www.dec.ny.gov/outdoor/8310.html");
                break;
            case 6:
                //log out
                showLogoutConfirmMessage();
                break;
            case 7:
                //feedback
                sendFeedBack();
                slideMenu.close(true);
                break;
            case 8:
                //feedback
                showSettings();
                slideMenu.close(true);
                break;
        }
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void sendFeedBack() {
        String to = getString(R.string.support_email_to) ;
        String subject = getString(R.string.support_email_subject);
        String body = getString(R.string.support_email_body);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
    }

    protected class SlideMenuAdapter extends BaseAdapter {
        private int VIEW_TYPE_PROFILE = 0;
        private int VIEW_TYPE_MENU_ITEM = 1;
        @Override
        public int getCount() {
            return MENU_ITEMS.length;
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
        public View getView(int position, View view, ViewGroup viewGroup) {
            if(view == null) {
                if(position == 0) {
                    view = LayoutInflater.from(LandingActivity.this).inflate(R.layout.slide_menu_item_profile, null);
                } else {
                    view = LayoutInflater.from(LandingActivity.this).inflate(R.layout.slide_menu_item_button, null);
                }
            }

            if(position == 0) {
                //set Profile photo
                textProfileName = (TextView) view.findViewById(R.id.textProfileName);
                DataManager dataManager = AppContext.getDataManager();
                ContactModel contactModel = dataManager.getProfile();
                if(contactModel!=null) {
                    textProfileName.setText(contactModel.getFirstName() + " " + contactModel.getLastName());
                }
            } else {
                //set command.
                TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText(MENU_ITEMS[position][1]);

                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                if(MENU_ITEMS[position][0]>0) {
                    imageView.setImageResource(MENU_ITEMS[position][0]);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }

            }

            return view;
        }

        @Override
        public int getItemViewType(int position) {
            if(position==0) {
                return VIEW_TYPE_PROFILE;
            } else {
                return VIEW_TYPE_MENU_ITEM;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }
    }


}
