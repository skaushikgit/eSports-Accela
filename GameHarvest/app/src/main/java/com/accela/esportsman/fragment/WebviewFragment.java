package com.accela.esportsman.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.accela.esportsman.R;
import com.accela.esportsman.slidemenu.SlideMenu;
import com.accela.mobile.AMLogger;


public class WebviewFragment extends Fragment  {

	SlideMenu slideMenu;
	WebView webView;
	String url;

	public WebviewFragment() {}

	public void setSlideMenu(SlideMenu slideMenu) {
		this.slideMenu = slideMenu;
	}

    public void setFragmentUrl(String url) {
        this.url = url;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		AMLogger.logInfo("ProjectFragment.onActivityCreated()");
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
        View contentView = inflater.inflate(R.layout.fragment_webview, container, false);
		View viewMenu =  contentView.findViewById(R.id.viewMenu);
		webView = (WebView) contentView.findViewById(R.id.webview);
		viewMenu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				slideMenu.open(false, true);
			}
		});

		setWebView();
        return contentView;
    }

	private void setWebView() {
		webView.loadUrl(url);
		WebSettings webSettings = webView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
		webView.setWebViewClient(new MyWebViewClient());
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (Uri.parse(url).getHost().equals(url)) {
				// This is my web site, so do not override; let my WebView load the page
				return false;
			}
			// Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			return true;
		}
	}

}