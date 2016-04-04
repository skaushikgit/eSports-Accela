package com.accela.esportsman.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.accela.esportsman.R;
import com.accela.esportsman.fragment.LicenseDetailFragment;
import com.accela.record.model.RecordModel;

public class LicenseDetailActivity extends BaseActivity {

    RecordModel recordModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_detail);
        Intent intent = getIntent();
        recordModel = (RecordModel) intent.getSerializableExtra("record_model");

        LicenseDetailFragment fragment = LicenseDetailFragment.newInstance(recordModel);

        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.add(R.id.licenseDetailContainer, fragment, null);
        ft.commit();
    }

}
