package com.accela.esportsman.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accela.esportsman.AppContext;
import com.accela.esportsman.R;
import com.accela.esportsman.data.DataManager;
import com.accela.esportsman.data.ReportForm;
import com.accela.esportsman.fragment.FormEntityCollectionView;
import com.accela.esportsman.utils.ActivityUtils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportSumitCompleteActivity extends BaseActivity {
    private ReportForm reportForm;
    private FormEntityCollectionView formEntityCollectionView;
    final int SELECT_IMAGE = 1;
    final int TAKE_IMAGE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_submit_complete);
        Intent intent = getIntent();
        reportForm = (ReportForm) intent.getSerializableExtra("reportForm");
        ImageView viewClose = (ImageView) findViewById(R.id.viewClose);
        viewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.goBackLandingPage(ReportSumitCompleteActivity.this);
            }
        });
        TextView textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.harvest_report_submitted);

        Button shareButton = (Button) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Button newReportButton = (Button) findViewById(R.id.newReport);
        newReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewReport();
            }
        });

        Button reportListButton = (Button) findViewById(R.id.reportList);
        reportListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vewReports();
            }
        });

        Button licenseListButton = (Button) findViewById(R.id.licenceList);
        licenseListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewLicenses();
            }
        });

        formEntityCollectionView = (FormEntityCollectionView) findViewById(R.id.entityContainer);
        if (reportForm != null)
        populateReportFormView();
    }

    protected void createNewReport() {
        ActivityUtils.startSelectTagActivity(this);
    }

    protected void vewReports() {
        ActivityUtils.startReportsListActivity(this);
    }

    protected void viewLicenses() {
        ActivityUtils.startLicenseListActivity(this);
    }


    private void selectImage() {
        CharSequence colors[] = new CharSequence[] {"Take New", "Select Existing"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_image);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch (which) {
                    case 0:
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, TAKE_IMAGE);
                        break;
                    case 1:
                        selectImageFromGallary();
                        break;
                }

            }
        });
        builder.show();
    }

    private void selectImageFromGallary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    launchSocialMediaIntent(data.getData());
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(requestCode == TAKE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    launchSocialMediaIntent(data.getData());
                }
            }else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchSocialMediaIntent(Uri uri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    @Override
    public void onBackPressed(){
        // do something here and don't write super.onBackPressed()
        ActivityUtils.goBackLandingPage(ReportSumitCompleteActivity.this);

    }

    public void populateReportFormView() {
        DataManager dataManager = AppContext.getDataManager();
        ImageView imageTag = (ImageView) findViewById(R.id.imageTag);
        TextView textTagName = (TextView) findViewById(R.id.textTagName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date today = new Date();
        today.setTime(System.currentTimeMillis());
        formEntityCollectionView.addEntity(getString(R.string.submitted), dateFormat.format(today));
        formEntityCollectionView.setEntiryColor(getString(R.string.submitted), R.color.text_green);
        formEntityCollectionView.addEntity(getString(R.string.dec_id), dataManager.userContact.getId());

        if (reportForm.tagInt == DataManager.ANIMAL_TAG_DEER) {
            imageTag.setImageResource(R.mipmap.icon_deer_tag);
            textTagName.setText(R.string.deer_report);
            populateDeerForm(getResources().getStringArray(R.array.confirm_harvest_report_array_deer));
        }
        if (reportForm.tagInt == DataManager.ANIMAL_TAG_FALL_TURKEY ) {
            imageTag.setImageResource(R.mipmap.icon_turkey_tag);
            textTagName.setText(R.string.turkey_report);
            populateFallTurkeyForm(getResources().getStringArray(R.array.confirm_harvest_report_array_fall_turkey));
        }
        if (reportForm.tagInt == DataManager.ANIMAL_TAG_SPRING_TURKEY) {
            imageTag.setImageResource(R.mipmap.icon_turkey_tag);
            textTagName.setText(R.string.turkey_report);
            populateTurkeyForm(getResources().getStringArray(R.array.confirm_harvest_report_array_turkey));
        }
        if(reportForm.tagInt == DataManager.ANIMAL_TAG_BEAR) {
            imageTag.setImageResource(R.mipmap.icon_bear_tag);
            textTagName.setText(R.string.bear_report);
            populateBearForm(getResources().getStringArray(R.array.confirm_harvest_report_array_bear));
        }
    }

    private void populateDeerForm(String[] keyArray) {
        String [] formArray = new String[keyArray.length];
        for (int i =0; i<keyArray.length; i++) {
            switch (i) {
                case 0:
                    formArray[0] = reportForm.county;
                    break;
                case 1:
                    formArray[1] = reportForm.town;
                    break;
                case 2:
                    formArray[2] = reportForm.wmu;
                    break;
                case 3:
                    formArray[3] = reportForm.dateOfKill;
                    break;
                case 4:
                    formArray[4] = reportForm.season;
                    break;
                case 5:
                    formArray[5] = reportForm.methodUsed;
                    break;
                case 6:
                    formArray[6] = reportForm.sex;
                    break;
                case 7:
                    formArray[7] = reportForm.leftAntlerPoints;
                    break;
                case 8:
                    formArray[8] = reportForm.rightAntlerPoints;
                    break;
                default:
                    break;

            }
        }
        formEntityCollectionView.populateEntityCollectionView(keyArray, formArray, false);
    }

    private void populateFallTurkeyForm(String[] keyArray) {
        String [] formArray = new String[keyArray.length];
        for (int i =0; i<keyArray.length; i++) {
            switch (i) {
                case 0:
                    formArray[0] = reportForm.county;
                    break;
                case 1:
                    formArray[1] = reportForm.town;
                    break;
                case 2:
                    formArray[2] = reportForm.wmu;
                    break;
                case 3:
                    formArray[3] = reportForm.dateOfKill;
                    break;
                case 4:
                    formArray[4] = reportForm.weight;
                    break;
                case 5:
                    formArray[5] = reportForm.legSaved;
                    break;
                case 6:
                    formArray[6] = reportForm.beardLength;
                    break;
                case 7:
                    formArray[7] = reportForm.spurLength;
                    break;
                default:
                    break;

            }
        }
        formEntityCollectionView.populateEntityCollectionView(keyArray, formArray, false);
    }


    private void populateTurkeyForm(String[] keyArray) {
        String [] formArray = new String[keyArray.length];
        for (int i =0; i<keyArray.length; i++) {
            switch (i) {
                case 0:
                    formArray[0] = reportForm.county;
                    break;
                case 1:
                    formArray[1] = reportForm.town;
                    break;
                case 2:
                    formArray[2] = reportForm.wmu;
                    break;
                case 3:
                    formArray[3] = reportForm.dateOfKill;
                    break;
                case 4:
                    formArray[4] = reportForm.weight;
                    break;
                case 5:
                    formArray[5] = reportForm.beardLength;
                    break;
                case 6:
                    formArray[6] = reportForm.spurLength;
                    break;
                default:
                    break;

            }
        }
        formEntityCollectionView.populateEntityCollectionView(keyArray, formArray, false);

    }

    private void populateBearForm(String[] keyArray) {
        String [] formArray = new String[keyArray.length];
        for (int i =0; i<keyArray.length; i++) {
            switch (i) {
                case 0:
                    formArray[0] = reportForm.county;
                    break;
                case 1:
                    formArray[1] = reportForm.town;
                    break;
                case 2:
                    formArray[2] = reportForm.wmu;
                    break;
                case 3:
                    formArray[3] = reportForm.dateOfKill;
                    break;
                case 4:
                    formArray[4] = reportForm.season;
                    break;
                case 5:
                    formArray[5] = reportForm.methodUsed;
                    break;
                case 6:
                    formArray[6] = reportForm.sex;
                    break;
                case 7:
                    formArray[7] = reportForm.age;
                    break;
                case 8:
                    formArray[8] = reportForm.examinationCounty;
                    break;
                case 9:
                    formArray[9] = reportForm.address;
                    break;
                case 10:
                    formArray[10] = reportForm.contactPhone;
                    break;
                default:
                    break;
            }
        }
        formEntityCollectionView.populateEntityCollectionView(keyArray, formArray, false);
    }

}
