package com.accela.esportsman.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accela.framework.model.AddressModel;
import com.accela.framework.model.RefContactModel;
import com.accela.esportsman.R;
import com.accela.esportsman.data.AccountManager;
import com.accela.esportsman.utils.Utils;
import com.accela.esportsman.view.ViewPagerEx;

import java.util.Calendar;
import java.util.Date;


public class RegisterActivity extends BaseActivity {

    ViewPagerEx viewPager;
    ViewPageAdapter adapter;
    EditText editDoB;
    AccountManager accountManager;
    Date dateOfBirth;
    Button buttonNext;
    Button buttonRegister;
    Button buttonFindMyInfo;
    EditText editId;
    boolean inputDecID;
    private int minimalIdTextLength = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        View profileContainer = findViewById(R.id.profileContainer);
        profileContainer.setVisibility(View.INVISIBLE);
        viewPager = (ViewPagerEx) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter = new ViewPageAdapter());
        viewPager.setPagingEnabled(false);
        Intent intent = getIntent();

        inputDecID = intent.getBooleanExtra("inputDecID", false);
        viewPager.setOffscreenPageLimit(3);
        View view  = findViewById(R.id.registerLayoutId);
        handleKeyboard(view, 120, view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
    }

    @Override
    public void onStart(){
        super.onStart();
        accountManager = new AccountManager(this, true);
        accountManager.setRegisterListener(accountActionListener);
    }

    AccountManager.AccountActionListener accountActionListener = new AccountManager.AccountActionListener() {
        @Override
        public void onActionDone(int errorCode, String message, Object data) {
            closeProgressDialog();
            switch (errorCode) {
                case AccountManager.REGISTER_SUCCESS_FIND_MY_INFO:
                    //Toast.makeText(RegisterActivity.this, "Found user", Toast.LENGTH_SHORT).show();
                    viewPager.setCurrentItem(1);
                    View profileContainer = findViewById(R.id.profileContainer);
                    if(data!=null && data instanceof RefContactModel) {
                        //set profile
                        RefContactModel contactModel = (RefContactModel)data;
                        TextView textName = (TextView) profileContainer.findViewById(R.id.textUserName);
                        textName.setText(contactModel.getFullName());
                        if (contactModel.getId()!=null)
                                Utils.getAddressFullLine(accountManager.getActiveMailingAddress(contactModel.getId()));
                    }else if(data!=null && data instanceof AddressModel){
                        AddressModel addressModel = (AddressModel)data;
                        TextView textCity = (TextView) profileContainer.findViewById(R.id.textUserCity);
                        textCity.setText(Utils.getAddressFullLine(accountManager.getActiveMailingAddress("")));
                    }

                    View imageBack = findViewById(R.id.imageViewBack);
                    imageBack.setVisibility(View.INVISIBLE);
                    profileContainer.setVisibility(View.VISIBLE);
                    break;
                case AccountManager.REGISTER_ERROR_FIND_MY_INFO:
                    if(inputDecID)
                        showAlertMessage(getString(R.string.user_not_found), message != null ? message : getString(R.string.ensure_dec_license_holder));
                    else
                        showAlertMessage(getString(R.string.user_not_found), message != null ? message : getString(R.string.ensure_drive_license_holder));
                    break;
                case AccountManager.REGISTER_ERROR_LOGIN_REGISTER_SERVER:
                    showAlertMessage(getString(R.string.network_issue));
                    break;
                case AccountManager.REGISTER_SUCCESS_CLAIM_ACCOUNT:
                    viewPager.setCurrentItem(2);
                    break;
                case AccountManager.REGISTER_ERROR_CLAIM_ACCOUNT:
                    if(message!=null) {
                        showAlertMessage(getResources().getString(R.string.user_already_taken));
                    } else {
                        showAlertMessage(getString(R.string.network_issue));
                    }

                    break;
                case AccountManager.REGISTER_SUCCESS_CREATE_NEW_ACCOUNT:
                    Toast.makeText(RegisterActivity.this, getString(R.string.new_account_create_successfully), Toast.LENGTH_SHORT)
                            .show();
                    accountManager.loginAfterRegister(RegisterActivity.this);
                    finish();
                    break;
                case AccountManager.REGISTER_ERROR_CREATE_NEW_ACCOUNT:
                    showAlertMessage(getString(R.string.new_account_create_failed));
                    break;
            }
        }
    };

    @SuppressLint("ValidFragment")
    private class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            if(editDoB!=null)
                editDoB.setText(String.format("%2d/%2d/%4d", month + 1, day, year));
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            if(dateOfBirth==null)
                dateOfBirth = new Date();
            dateOfBirth.setTime(calendar.getTimeInMillis());
            if(editId.length() >= minimalIdTextLength && dateOfBirth!=null) {
                buttonFindMyInfo.setEnabled(true);
            } else {
                buttonFindMyInfo.setEnabled(false);
            }

        }
    }


    private class ViewPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
            View view = null;
            if(position == 0) {
                view = inflater.inflate(R.layout.register_find_my_info, null);
                editId = (EditText) view.findViewById(R.id.editDriverLicense);
                if(inputDecID) {
                    editId.setHint(R.string.dec_id_num);
                } else {
                    editId.setHint(R.string.drive_num);
                }
                editDoB = (EditText) view.findViewById(R.id.editDateBirth);
                editDoB.setInputType(InputType.TYPE_NULL);
                editDoB.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            DatePickerFragment datePickerFragment = new DatePickerFragment();
                            datePickerFragment.show(getSupportFragmentManager(), "DatePickerFragment");
                        }
                        return true;
                    }
                });

                editId.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editId.length() >= minimalIdTextLength && dateOfBirth!=null) {
                            buttonFindMyInfo.setEnabled(true);

                        } else {
                            buttonFindMyInfo.setEnabled(false);
                        }
                    }
                });
                buttonFindMyInfo = (Button) view.findViewById(R.id.buttonFindMyInfo);
                buttonFindMyInfo.setEnabled(false);
                buttonFindMyInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(dateOfBirth==null) {
                            showAlertMessage(getString(R.string.input_valid_dob));
                            return;
                        }
                        if(editId.getText().toString().length() < minimalIdTextLength) {
                            showAlertMessage(getString(R.string.driver_license_number_error));
                            return;
                        }
                        if(inputDecID) {
                            accountManager.findMyInfo(null, editId.getText().toString(), dateOfBirth);
                        } else {
                            accountManager.findMyInfo( editId.getText().toString(),null, dateOfBirth);
                        }
                        showProgressDialog(getString(R.string.processing_and_wait), false);
                    }
                });


            } else if(position == 1) {
                view = inflater.inflate(R.layout.register_input_email, null);
                buttonNext = (Button) view.findViewById(R.id.buttonNext);
                final EditText editEmail = (EditText) view.findViewById(R.id.editEmail);
                final EditText editUserName = (EditText) view.findViewById(R.id.editUserName);
                final EditText editPassword1 = (EditText) view.findViewById(R.id.editPassword1);
                final EditText editPassword2 = (EditText) view.findViewById(R.id.editPassword2);

                buttonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!editPassword1.getText().toString().equals(editPassword2.getText().toString())) {
                            showAlertHtmlMessage(getString(R.string.password_not_same));
                            return;
                        }else if(editEmail.getText()==null || !android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.getText()).matches()){
                            showAlertHtmlMessage(getString(R.string.email_not_valid));
                            return;
                        }
                        accountManager.verifyClaimAccount(editEmail.getText().toString(),
                                editUserName.getText().toString(),
                                editPassword1.getText().toString());
                        showProgressDialog(getString(R.string.processing_and_wait), false);
                    }
                });



            } else if(position == 2) {
                view = inflater.inflate(R.layout.register_security_question, null);
                final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
                final EditText editAnswer = (EditText) view.findViewById(R.id.editAnswer);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterActivity.this,
                        R.array.security_questions, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(-1);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        if (position != 0 && editAnswer.getText().toString().trim().length() > 0) {
                            buttonRegister.setEnabled(true);
                        } else {
                            buttonRegister.setEnabled(false);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                editAnswer.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (spinner.getSelectedItemPosition() != 0 && editAnswer.getText().toString().trim().length() > 0) {
                            buttonRegister.setEnabled(true);
                        } else {
                            buttonRegister.setEnabled(false);
                        }
                    }
                });
                buttonRegister = (Button) view.findViewById(R.id.buttonRegister);
                buttonRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProgressDialog(getString(R.string.processing_and_wait), false);
                        accountManager.createNewAccount(spinner.getSelectedItem().toString(), editAnswer.getText().toString());
                    }
                });

            }

            if(view!=null) {
                container.addView(view);
                return view;
            } else {
                return super.instantiateItem(container, position);
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
            //super.destroyItem(container, position, object);


        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }



}
