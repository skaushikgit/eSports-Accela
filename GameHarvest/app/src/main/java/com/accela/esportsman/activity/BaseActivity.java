/** 
  * Copyright 2014 Accela, Inc. 
  * 
  * You are hereby granted a non-exclusive, worldwide, royalty-free license to 
  * use, copy, modify, and distribute this software in source code or binary 
  * form for use in connection with the web services and APIs provided by 
  * Accela. 
  * 
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
  * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
  * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
  * DEALINGS IN THE SOFTWARE. 
  * 
  * 
  * 
  */

/*
 * 
 * 
 *   Created by jzhong on 8/18/15.
 *   Copyright (c) 2015 Accela. All rights reserved.
 *   -----------------------------------------------------------------------------------------------------
 *   
 */

package com.accela.esportsman.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.utils.ActivityUtils;


public class BaseActivity extends ActionBarActivity {

	private TextView textTitle;
	private TextView textSmallTitle1;
	private TextView textSmallTitle2;
	private View  buttonBack;
	private Button  buttonRight;
	private View actionbarView;
	private ProgressDialog progressDialog;
	private View buttonPlus;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	 
		//Lock the orientation
//		ActivityUtils.lockActivityOrientation(this);
		ActivityUtils.setActivityPortrait(this);
		ActionBar actionBar = this.getSupportActionBar(); 
		actionBar.hide();
	}

	public void setActionBarTitle(String text) {
		View viewActionBar = findViewById(R.id.actionBar);
		if(viewActionBar!=null) {
			TextView title = (TextView) viewActionBar.findViewById(R.id.textTitle);
			if(title!=null)
				title.setText(text);
		}
	}

	public void setActionBarTitle(int stringId) {
		setActionBarTitle(getString(stringId));
	}

	public void showProgressDialog( String message,  boolean cancelable) {
		if(progressDialog!=null){
			progressDialog.cancel();
		}
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(message != null ? message: getResources().getString(
				R.string.processing_and_wait));
		progressDialog.setCanceledOnTouchOutside(cancelable);
		progressDialog.setCancelable(cancelable);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				
			}
		});
		progressDialog.show();
	}

	public void closeProgressDialog() {
		if(progressDialog!=null){
			progressDialog.cancel();
			progressDialog = null;
		}
	}

	public void showAlertMessage(String message){
		this.showAlertMessage(null, message);
	}

	protected void showAlertMessage(String title, String message) {
		if (title==null)
			title = getString(R.string.app_name);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this).setTitle(title).setMessage(message);
		alertDialog.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						onAlertMessageDismiss();
					}
				});
		alertDialog.show();

	}

	protected void showAlertHtmlMessage(String message) {
		TextView msg = new TextView(this);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.app_name));
        FrameLayout view = new FrameLayout(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = (int) (getResources().getDisplayMetrics().density * 12);
        lp.setMargins(margin, margin, margin, margin);
        msg.setLayoutParams(lp);
        view.addView(msg, lp);
		alertDialog.setView(view);
		msg.setText(Html.fromHtml(message));
		alertDialog.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						onAlertMessageDismiss();
					}
				});
		alertDialog.show();


	}

	protected void onAlertMessageDismiss() {
		
	}

	protected void handleKeyboard(final View view, final int offset, final int lv, final int tv, final int rv, final int bv){
		final float density = getResources().getDisplayMetrics().density;
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard(view);
			}
		});
		view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				int w = right-left, h=bottom-top;
				int oldw = oldRight-oldLeft, oldh=oldBottom-oldTop;
				if ((w == oldw) && (oldw != 0) && (oldh != 0)) {
					if (h >= oldh) {
						view.setPadding(lv, (int) (tv-offset*density), rv, bv);
					} else {
						view.setPadding(lv, tv, rv, bv);
					}
				}
			}
		});
	}

	private void hideKeyboard(View view){
		InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}


	protected void showLogoutConfirmMessage() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.logout)).setMessage(
						getString(R.string.logout_confirm_message));
		alertDialog.setNegativeButton(R.string.Cancel, null);
		alertDialog.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						logout();
					}
				});
		alertDialog.show();
	}

	public void logout() {
		Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		AppContext.resetBackendData();
	}

	protected void onLogout() {

	}
	
}