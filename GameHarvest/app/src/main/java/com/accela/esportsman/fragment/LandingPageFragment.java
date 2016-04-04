package com.accela.esportsman.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accela.framework.AMApplication;
import com.accela.framework.model.AddressModel;
import com.accela.framework.util.AMUtils;
import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.activity.BaseActivity;
import com.accela.esportsman.data.AccountManager;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.slidemenu.SlideMenu;
import com.accela.esportsman.utils.ActivityUtils;
import com.accela.mobile.AMLogger;
import com.accela.record.model.ContactModel;

import java.util.Observable;
import java.util.Observer;

public class LandingPageFragment extends Fragment implements Observer {

	SlideMenu slideMenu;
	DataManager dataManager = AppContext.getDataManager();
    View contentView;
	public LandingPageFragment() {

	}

	public void setSlideMenu(SlideMenu slideMenu) {
		this.slideMenu = slideMenu;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		AMLogger.logInfo("ProjectFragment.onActivityCreated()");
	}

    @Override
    public void onDestroyView() {
        contentView = null;
        dataManager.deleteObserver(this);
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AMLogger.logInfo("ProjectFragment.onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
    	AMLogger.logInfo("ProjectFragment.onCreateView()");
    	//create progress dialog
    	/*pDialog = new ProgressDialog(getActivity());
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage(getResources().getString(
					R.string.loading_wait_message));
		pDialog.setCanceledOnTouchOutside(true);
		pDialog.setCancelable(true);*/
    	
    	// Create content view.
        contentView = inflater.inflate(R.layout.fragment_landing, container, false);
		View viewMenu =  contentView.findViewById(R.id.viewMenu);
        viewMenu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				slideMenu.open(false, true);
			}
		});

		View viewNewReport = contentView.findViewById(R.id.viewNewReport);
		viewNewReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createNewReport();
			}
		});

		View viewReports = contentView.findViewById(R.id.viewReports);
		viewReports.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                vewReports();
			}
		});

		View viewLicenses = contentView.findViewById(R.id.viewLicenses);
        viewLicenses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				viewLicenses();
			}
		});

        dataManager.addObserver(this);
        dataManager.downloadAllLicenseAndReport();
        dataManager.getProfile();
		update(null, null);
		return contentView;
    }

	protected void createNewReport() {
		if (!AMUtils.isNetworkConnected(AMApplication.mContext) || !AccountManager.isOnlineMode) {
			((BaseActivity)getActivity()).showAlertMessage(getResources().getString(R.string.offline_warning_msg));
			return;
		}
		ActivityUtils.startSelectTagActivity(getActivity());

	}

	protected void vewReports() {
		if (!AMUtils.isNetworkConnected(AMApplication.mContext)  || !AccountManager.isOnlineMode) {
			((BaseActivity)getActivity()).showAlertMessage(getResources().getString(R.string.offline_warning_msg));
			return;
		}
		if (AppContext.getDataManager().getReports()!=null && AppContext.getDataManager().getReports().size()==0){
			((BaseActivity)getActivity()).showAlertMessage(getResources().getString(R.string.no_reports));
			return;
		}
		ActivityUtils.startReportsListActivity(getActivity());
	}

	protected void viewLicenses() {
		if (AppContext.getDataManager().getLicenses()!=null && AppContext.getDataManager().getLicenses().size()==0){
			((BaseActivity)getActivity()).showAlertMessage(getResources().getString(R.string.no_license));
			return;
		}
		ActivityUtils.startLicenseListActivity(getActivity());
	}


    @Override
    public void update(Observable observable, Object o) {
        TextView textReportsNum = (TextView) contentView.findViewById(R.id.textReportsNum);
        textReportsNum.setText(String.format("%d", dataManager.getReports().size()));
        TextView textLicensesNum = (TextView) contentView.findViewById(R.id.textLicensesNum);
        textLicensesNum.setText(String.format("%d", dataManager.getLicenses().size()));

        ContactModel contactModel = dataManager.getProfile();
        if(contactModel!=null) {
            TextView textUserName = (TextView) contentView.findViewById(R.id.textUserName);
            textUserName.setText(contactModel.getFirstName() + " " + contactModel.getLastName());
            TextView textUserCity = (TextView) contentView.findViewById(R.id.textUserCity);
			AddressModel address =  dataManager.getActiveMailingAddress();
			if(address!=null && address.getCity()!=null) {
				textUserCity.setText("- " + address.getCity() + " -");
			} else {
				textUserCity.setText("");
			}
        }


    }
}