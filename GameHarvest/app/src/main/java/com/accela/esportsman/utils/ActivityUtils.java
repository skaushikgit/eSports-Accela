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
package com.accela.esportsman.utils;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import com.accela.esportsman.R;
import com.accela.esportsman.activity.FacebookIntegration;
import com.accela.esportsman.activity.HarvestReportFormActivity;
import com.accela.esportsman.activity.LandingActivity;
import com.accela.esportsman.activity.LicenseDetailActivity;
import com.accela.esportsman.activity.LicenseListActivity;
import com.accela.esportsman.activity.OverlayTutorialActivity;
import com.accela.esportsman.activity.ReportDetailActivity;
import com.accela.esportsman.activity.ReportsListActivity;
import com.accela.esportsman.activity.SelectTagActivity;
import com.accela.record.model.RecordModel;

public class ActivityUtils {

	public static void lockActivityOrientation(Activity activity) {
		int currentOrientation = activity.getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}
		else {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		}
	}
	
	public static void setActivityPortrait(Activity activity) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
	}

	public static void startLandingActivity(Activity activity) {
		Intent intent = new Intent(activity, LandingActivity.class);
		activity.startActivity(intent);
	}

	public static void startSelectTagActivity(Activity activity) {
		Intent intent = new Intent(activity, SelectTagActivity.class);
		activity.startActivity(intent);
	}

	public static void startOverlayTutorialActivity(Activity activity) {
		Intent intent = new Intent(activity, OverlayTutorialActivity.class);
		activity.startActivity(intent);
	}

	public static void startReportsListActivity(Activity activity) {
		Intent intent = new Intent(activity, ReportsListActivity.class);
		activity.startActivity(intent);
	}

	public static void startReportDetailActivity(Activity activity, RecordModel model) {
		Intent intent = new Intent(activity, ReportDetailActivity.class);
		intent.putExtra("record_model", model);
		activity.startActivity(intent);
	}

	public static void startLicenseListActivity(Activity activity) {
		Intent intent = new Intent(activity, LicenseListActivity.class);
		activity.startActivity(intent);
	}

	public static void startLicenseDetailActivity(Activity activity, RecordModel model){
		Intent intent = new Intent(activity, LicenseDetailActivity.class);
		intent.putExtra("record_model", model);
		activity.startActivity(intent);
	}

	public static void startHarvestReportFormActivity(Activity activity, RecordModel tag) {
		Intent intent = new Intent(activity, HarvestReportFormActivity.class);
        intent.putExtra("tag", tag);
		activity.startActivity(intent);
	}

	public static void startActivity(Activity from, Class to){
		Intent intent = new Intent(from, to);
		from.overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_fade_out);
		from.startActivity(intent);
	}


	public static void goBackLandingPage(Activity from){
		Intent intent = new Intent(from, LandingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		from.startActivity(intent);
		from.finish();
	}

}
