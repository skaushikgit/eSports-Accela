package com.accela.esportsman.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.accela.esportsman.R;
import com.accela.esportsman.data.AccountManager;
import com.accela.esportsman.utils.ActivityUtils;
import com.accela.mobile.AccelaMobile;


public class LoginActivity extends BaseActivity {
    static final String LOGIN_PREFS_NAME = "login_prefs_name";
    static final String USER_NAME = "login_user_name";
    static final String REGISTER_KEY = "isFromRegister";

    private EditText editTextUser;
    private EditText editTextPassword;
    private View forgotPasswordView;
    private AccountManager accountManager;
    private int count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextUser = (EditText) findViewById(R.id.userId);
        editTextPassword = (EditText) findViewById(R.id.passwordId);
        forgotPasswordView = (View) findViewById(R.id.forgotPwd);
        forgotPasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lunchWebPortal();
            }
        });
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String password = intent.getStringExtra("password");

        Button button = (Button) findViewById(R.id.login_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = editTextUser.getText().toString();
                String password = editTextPassword.getText().toString();
                showProgressDialog(getString(R.string.logging_in), false);
                accountManager.login(userName, password);
            }
        });
        TextView registerText = (TextView) findViewById(R.id.registerNewUserId);
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(REGISTER_KEY, true);
                LoginActivity.this.startActivity(intent);
                finish();
            }
        });
        accountManager = new AccountManager(this, false);
        accountManager.setRegisterListener(accountActionListener);
        if (userName != null && password != null) {
            editTextUser.setText(userName);
            editTextPassword.setText(password);
            showProgressDialog(getString(R.string.logging_in), false);
            accountManager.login(userName, password);
        } else {
            SharedPreferences prefs = getSharedPreferences(LOGIN_PREFS_NAME, MODE_PRIVATE);
            String prefs_name = prefs.getString(USER_NAME, null);
            if (prefs_name != null) {
                editTextUser.setText(prefs_name);
            }
        }
        final View view = findViewById(R.id.loginRelativeId);
        this.registerForContextMenu(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if (count == 3) {
                    view.showContextMenu();
                    count = 0;
                }
            }
        });

        handleKeyboard(view, 50, view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_environment, menu);
        if (AccountManager.ENVIRONMENT == AccelaMobile.Environment.PROD) {
            menu.getItem(0).setChecked(true);
        } else if (AccountManager.ENVIRONMENT == AccelaMobile.Environment.DEV) {
            menu.getItem(1).setChecked(true);
        } else if (AccountManager.ENVIRONMENT == AccelaMobile.Environment.TEST) {
            menu.getItem(2).setChecked(true);
        } else if (AccountManager.ENVIRONMENT == AccelaMobile.Environment.STAGE) {
            menu.getItem(3).setChecked(true);
        } else if (AccountManager.ENVIRONMENT == AccelaMobile.Environment.CONFIG) {
            menu.getItem(4).setChecked(true);
        } else if (AccountManager.ENVIRONMENT == AccelaMobile.Environment.SUPP) {
            menu.getItem(5).setChecked(true);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterViewCompat.AdapterContextMenuInfo info = (AdapterViewCompat.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.prodId:
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.PROD;
                return true;
            case R.id.devId:
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.DEV;
                return true;
            case R.id.configId:
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.CONFIG;
                return true;
            case R.id.stageId:
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.STAGE;
                return true;
            case R.id.suppId:
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.SUPP;
                return true;
            case R.id.testId:
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.TEST;
                return true;
            default:
                return false;
        }
    }


//    private void showPopup() {
//        PopupMenu popup = new PopupMenu(this, forgotPasswordView);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.menu_environment, popup.getMenu());
//        popup.setOnMenuItemClickListener(this);
//        popup.show();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        count = 0;
    }

    private void lunchWebPortal() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://decals.dec.ny.gov/DECALSCitizenWeb/citizenlogin.htm"));
        startActivity(browserIntent);
    }

    AccountManager.AccountActionListener accountActionListener = new AccountManager.AccountActionListener() {
        @Override
        public void onActionDone(int errorCode, String message, Object data) {
            switch (errorCode) {
                case AccountManager.LOGIN_SUCCESS:
                    closeProgressDialog();
                    SharedPreferences.Editor editor = getSharedPreferences(LOGIN_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.clear();
                    editor.putString(USER_NAME, editTextUser.getText().toString());
                    editor.commit();
                    ActivityUtils.startActivity(LoginActivity.this, LandingActivity.class);
                    break;
                case AccountManager.LOGIN_ERROR:
                    closeProgressDialog();
                    showAlertMessage(getString(R.string.login_failed));
                    break;
            }
        }
    };
}
