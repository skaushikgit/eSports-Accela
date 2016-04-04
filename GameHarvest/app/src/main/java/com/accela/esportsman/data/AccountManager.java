package com.accela.esportsman.data;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.accela.framework.AMApplication;
import com.accela.framework.AMBaseModel;
import com.accela.framework.action.ContactAction;
import com.accela.framework.model.AddressModel;
import com.accela.framework.model.RefContactModel;
import com.accela.framework.model.SearchContactRequest;
import com.accela.framework.model.V3pResponseWrap;
import com.accela.framework.persistence.AMAsyncEntityActionDelegate;
import com.accela.framework.persistence.AMAsyncEntityListActionDelegate;
import com.accela.framework.persistence.AMClientRequest;
import com.accela.framework.persistence.AMDataIncrementalResponse;
import com.accela.framework.persistence.AMDataResponse;
import com.accela.framework.persistence.AMMobilityPersistence;
import com.accela.framework.persistence.AMStrategy;
import com.accela.framework.persistence.request.AMGet;
import com.accela.framework.persistence.request.AMPost;
import com.accela.framework.persistence.request.AMPut;
import com.accela.framework.serializer.AMModelSerializer;
import com.accela.framework.service.CorelibManager;
import com.accela.framework.util.AMUtils;
import com.accela.esportsman.activity.LoginActivity;
import com.accela.esportsman.utils.Utils;
import com.accela.mobile.AMError;
import com.accela.mobile.AMLogger;
import com.accela.mobile.AMSessionDelegate;
import com.accela.mobile.AccelaMobile;
import com.accela.mobile.http.RequestParams;
import com.accela.record.model.RecordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jzhong on 9/16/15.
 */
public class AccountManager {
    public final static String API_HOST = "https://apis.accela.com";
    public final static String AUTH_HOST = "https://auth.accela.com";
    public  static AccelaMobile.Environment ENVIRONMENT = AccelaMobile.Environment.PROD;//default environment
    public static final String AGENCY = "DEC";

    //The App (agency app) for register new user
    public final static String APP_ID_REGISTER = "635834832175861037";
    public final static String APP_SECRET_REGISTER = "127402fb4ed441d89c6dad6304bfb684";
    public final static String[] APP_SCOPES_REGISTER = new String[] { "records", "run_emse_script", "create_partial_record",
            "update_record_customforms", "finalize_record", "contacts"};

    //The App (citizen app) for login
    public final static String APP_ID = "635821081269921095";
    public final static String APP_SECRET = "22f916c4a4ea451092cd19902532bb20";
    public final static String[] APP_SCOPES = new String[] { "records", "users", "create_partial_record",
            "update_record_customforms", "finalize_record", "get_citizenaccess_user_contacts", "get_settings_drilldown",
            "contacts","get_contacts_customforms", "batch_request", "get_record"};

    public static boolean isOnlineMode = true;

    private final static String TAG = "RegisterManager";
    private final Context mContext;
    AccelaMobile accelaMobile;
    private String mUserName;
    private boolean isCreatingUser = false;

    public interface AccountActionListener {
        public void onActionDone(int errorCode, String message, Object data);
    }

    private AccountActionListener actionListener;

    //error code
    public final static int REGISTER_ERROR_LOGIN_REGISTER_SERVER     = 1;
    public final static int REGISTER_ERROR_FIND_MY_INFO     = 2;
    public final static int REGISTER_SUCCESS_FIND_MY_INFO     = 3;
    public final static int REGISTER_SUCCESS_CREATE_NEW_ACCOUNT = 4;
    public final static int REGISTER_ERROR_CREATE_NEW_ACCOUNT = 5;
    public final static int REGISTER_SUCCESS_CLAIM_ACCOUNT = 6;
    public final static int REGISTER_ERROR_CLAIM_ACCOUNT = 7;
    public final static int LOGIN_SUCCESS = 8;
    public final static int LOGIN_ERROR = 9;

    //The info user input for register new account
    private String idDEC;
    private String driverLicense;
    private Date   dateOfBirth;

    private String email;
    private String userName;
    private String password;

    private String question;
    private String answer;

    //accountRecord - the record model returned by server in the proccess of create new account
    private RecordModel accountRecord;


    public AccountManager(Context context, boolean forRegister) {
        this.mContext = context;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Utils.getEnvironment(settings.getString("pref_environment", ""));
        if(forRegister) {
            CorelibManager.getInstance().initialize(APP_ID_REGISTER, APP_SECRET_REGISTER, API_HOST,
                    AUTH_HOST, ENVIRONMENT, APP_SCOPES_REGISTER);
        } else {
            CorelibManager.getInstance().initialize(APP_ID, APP_SECRET, API_HOST,
                    AUTH_HOST, ENVIRONMENT, APP_SCOPES);
        }
        accelaMobile = AccelaMobile.getInstance();
        //logout last session
        accelaMobile.getAuthorizationManager().logout();
    }

    public void setRegisterListener(AccountActionListener l) {
        actionListener = l;
    }

    private void notifyListener(int errorCode, String message, Object data) {
        if(actionListener!=null) {
            actionListener.onActionDone(errorCode, message, data);
        }
    }

    private boolean isReigsterTokenValid() {
        return accelaMobile.isSessionValid();

    }

    //Motheds:  Login and find my info

    private  AMSessionDelegate registerSessionDelegate = new AMSessionDelegate() {
        @Override
        public void amDidCancelLogin() {
            AMLogger.logInfo("Cancel login.");
        }

        @Override
        public void amDidLogin() {
            AMLogger.logInfo("Login succeed.");
            AMLogger.logInfo("access token:" + accelaMobile.getAuthorizationManager().getAccessToken());
            findMyInfo(driverLicense, idDEC, dateOfBirth);
        }

        @Override
        public void amDidLoginFailure(AMError error) {
            AMLogger.logError(error.toString());
            notifyListener(REGISTER_ERROR_LOGIN_REGISTER_SERVER, null, null);
        }

        @Override
        public void amDidLogout() {
            AMLogger.logInfo("Logout succeed.");
        }

        @Override
        public void amDidSessionInvalid(AMError error) {
            AMLogger.logError(error.toString());
        }
    };

    private void getRegisterToken() {
        accelaMobile.getAuthorizationManager().setSessionDelegate(registerSessionDelegate);
        accelaMobile.getAuthorizationManager().authenticate(AGENCY, "harvest", "report",
                ENVIRONMENT, APP_SCOPES_REGISTER, null);

    }

    // Find my info by user driver license or DEC ID and date of birth
    AMAsyncEntityListActionDelegate<RefContactModel> contactActionDelegate = new  AMAsyncEntityListActionDelegate<RefContactModel>() {

        @Override
        public void onCompleted(AMDataIncrementalResponse<RefContactModel> response) {
            //verify date of birth
            Calendar birth = Calendar.getInstance();
            birth.setTimeInMillis(dateOfBirth.getTime());
            int year = birth.get(Calendar.YEAR);
            int day = birth.get(Calendar.DAY_OF_YEAR);
            boolean match = false;
            RefContactModel model = null;
            for(RefContactModel contactModel: response.getResult()) {
                Date date = contactModel.getBirthDate();
                if(date!=null) {
                    birth.setTimeInMillis(date.getTime());
                    if(birth.get(Calendar.YEAR) == year && birth.get(Calendar.DAY_OF_YEAR) == day) {
                        match = true;
                        model = contactModel;
                        if(idDEC==null) {
                            idDEC = contactModel.getId();
                        }

                        if(driverLicense==null) {
                            driverLicense = contactModel.getDriverLicenseNumber();
                        }
                        break;
                    }
                }
            }
            if(match) {
                notifyListener(REGISTER_SUCCESS_FIND_MY_INFO, null, model);
            } else {
                notifyListener(REGISTER_ERROR_FIND_MY_INFO, null, null);
            }

        }

        @Override
        public void onFailure(Throwable error) {
            //
            notifyListener(REGISTER_ERROR_FIND_MY_INFO, null, null);
        }
    };

    private AddressModel activeMailingAddress;

    public AddressModel getActiveMailingAddress(String id) {
        if (activeMailingAddress != null) {
            return activeMailingAddress;
        }

        AMStrategy strategy = null;
        if (strategy == null) {
            strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        }
        HashMap<String, Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<AddressModel, V3pResponseWrap<List<AddressModel>>> amMobilityPersistence = new AMMobilityPersistence<AddressModel, V3pResponseWrap<List<AddressModel>>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<List<AddressModel>>());
        AMClientRequest action = new AMGet("/v4/contacts/" + id + "/addresses"); //&expand=customForms
        amMobilityPersistence.setEntityType(new AddressModel());
        amMobilityPersistence.processRequestAsync(null, new AMAsyncEntityListActionDelegate<AddressModel>() {

            @Override
            public void onCompleted(AMDataIncrementalResponse<AddressModel> response) {

                for (AddressModel addressModel : response.getResult()) {
                    //get active mailing address
                    String type = addressModel.getType_value();
                    String status = addressModel.getStatus_value();
                    if ("Mailing".equals(type) && "A".equals(status)) {
                        activeMailingAddress = addressModel;
                    } else if (activeMailingAddress == null && "Home".equals(type)) {
                        //anyway, set an address
                        activeMailingAddress = addressModel;
                    }
                }
                notifyListener(REGISTER_SUCCESS_FIND_MY_INFO, null, activeMailingAddress);
            }

            @Override
            public void onFailure(Throwable error) {
                notifyListener(REGISTER_ERROR_FIND_MY_INFO, null, null);
            }
        }, action, strategy, modelSerializer);

        return null;
    }


    public void findMyInfo(String driverLicense, String idDec, Date dateOfBirth) {

        this.driverLicense = driverLicense;
        this.idDEC = idDec;
        this.dateOfBirth = dateOfBirth;
        if(!isReigsterTokenValid()) {
            getRegisterToken();
        } else {
            ContactAction contactAction = new ContactAction();
            SearchContactRequest contactRequest = new SearchContactRequest();
            if(driverLicense!=null) {
                contactRequest.setDriverLicenseNumber(driverLicense);
            } else if(idDec!=null) {
                contactRequest.setId(idDec);
            } else {
                return;
            }
            AMStrategy strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
            contactAction.searchContactsAsync(null, contactActionDelegate, strategy, contactRequest, 0, 10 );
        }
    }

    /*
       Before register user account, need to verify claim Accont - verifyClaimAccount()
     */
    AMAsyncEntityActionDelegate<ClaimAccountResponseModel> claimAccountActionDelegate = new AMAsyncEntityActionDelegate<ClaimAccountResponseModel>()
    {
        @Override
        public void onCompleted(AMDataResponse<ClaimAccountResponseModel> response) {

            ClaimAccountResponseModel model = response.getResult();
            //verify claim account.
            if (model != null && model.isSuccess) {
                AMLogger.logInfo("verify claim account successfully");
                notifyListener(REGISTER_SUCCESS_CLAIM_ACCOUNT, null, null);
            } else {
                AMLogger.logInfo("verify claim account error!!!!");
                notifyListener(REGISTER_ERROR_CLAIM_ACCOUNT, model != null ? model.message : null, null);
            }
        }

        @Override
        public void onFailure(Throwable error) {
            AMLogger.logInfo("verify claim account error");
            notifyListener(REGISTER_ERROR_CLAIM_ACCOUNT, null, null);
        }
    };

    public static class ClaimAccountResponseModel extends AMBaseModel {
        public Boolean isSuccess;
        public String message;
        public void setIsSuccess(Boolean isSuccess) {
            this.isSuccess = isSuccess;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public Boolean getIsSuccess() {
            return this.isSuccess;
        }
        public String getMessage() {
           return this.message;
        }
    }

    public void verifyClaimAccount(String email, String userName, String password) {
        this.email = email;
        this.userName = userName;
        this.password = password;

        AMStrategy strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        HashMap<String,Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<ClaimAccountResponseModel, V3pResponseWrap<ClaimAccountResponseModel>> amMobilityPersistence
                = new AMMobilityPersistence<ClaimAccountResponseModel, V3pResponseWrap<ClaimAccountResponseModel>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<ClaimAccountResponseModel>());
        AMClientRequest action = new AMPost("/v4/scripts/ARA_VERIFY_CLAIM_ACCOUNT");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("spUserName", userName);
            jsonObject.put("showDebug", "N");
            jsonObject.put("spPassword", password);
            jsonObject.put("spEmailAddress", email);
            jsonObject.put("spRetypePassword", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        RequestParams requestParams = new RequestParams(json);
        action.setBody(requestParams);
        amMobilityPersistence.setEntityType(new ClaimAccountResponseModel());
        amMobilityPersistence.processRequestAsync(null, claimAccountActionDelegate, action, strategy, modelSerializer);
    }

    /*

    1. initialize a record - initializeAccountRecord()
    2. create costom form - addASItoAccountRecord()
    3. finalize the record - finalizeAccountRecord()
    4. submit claim account - submitNewAccount()

     */

    AMAsyncEntityActionDelegate<RecordModel> initializeRecordDelegate = new AMAsyncEntityActionDelegate<RecordModel>()
    {
        @Override
        public void onCompleted(AMDataResponse<RecordModel> response) {
            accountRecord = response.getResult();
            if(accountRecord!=null) {
                AMLogger.logInfo("initilaize account successfully");
                addASItoAccountRecord();
            } else {
                AMLogger.logInfo("initilaize account, but can't get record");
                notifyListener(REGISTER_ERROR_CREATE_NEW_ACCOUNT, null, null);
            }
        }

        @Override
        public void onFailure(Throwable error) {
            AMLogger.logInfo("initilaize account error");
            notifyListener(REGISTER_ERROR_CREATE_NEW_ACCOUNT, null, null);
        }
    };

    private void initializeAccountRecord() {
        accountRecord = null;
        AMStrategy strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        HashMap<String,Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<RecordModel, V3pResponseWrap<RecordModel>> amMobilityPersistence
                = new AMMobilityPersistence<RecordModel, V3pResponseWrap<RecordModel>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<RecordModel>());
        AMClientRequest action = new AMPost("/v4/records/initialize");

        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject type = new JSONObject();
            type.put("id", "Licenses-Customer-Registration-Application");
            jsonObject.put("type", type);
            JSONArray contacts = new JSONArray();
            JSONObject contact = new JSONObject();
            contact.put("id", idDEC);
            contacts.put(contact);
            jsonObject.put("contacts", contacts);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        RequestParams requestParams = new RequestParams(json);
        action.setBody(requestParams);
        amMobilityPersistence.setEntityType(new RecordModel());
        amMobilityPersistence.processRequestAsync(null, initializeRecordDelegate, action, strategy, modelSerializer);
    }

    public static class ASIResponseModel extends AMBaseModel {
        public String id;
        public Boolean isSuccess;
        public String message;
        public void setIsSuccess(Boolean isSuccess) {
            this.isSuccess = isSuccess;
        }
        public void setId(String id) {
            this.id = id;
        }

    }

    AMAsyncEntityListActionDelegate<ASIResponseModel> asiRecordDelegate = new AMAsyncEntityListActionDelegate<ASIResponseModel>()
    {

        @Override
        public void onCompleted(AMDataIncrementalResponse<ASIResponseModel> response) {
            int success = 0;
            for(ASIResponseModel asiResponseModel: response.getResult()) {
                if(asiResponseModel.isSuccess) {
                    success++;
                }
            }
            if(success==2) {
                //submit 2 ASI, all should be successful.
                AMLogger.logInfo("Create account ASI Done");
                finalizeAccountRecord();
            } else {
                AMLogger.logInfo("Create account ASI error!, not all asi created successful: " + success);
                notifyListener(REGISTER_ERROR_CREATE_NEW_ACCOUNT, null, null);
            }
        }

        @Override
        public void onFailure(Throwable error) {
            AMLogger.logInfo("Create account ASI error!");
            notifyListener(REGISTER_ERROR_CREATE_NEW_ACCOUNT, null, null);
        }
    };

    private void addASItoAccountRecord() {
        AMStrategy strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        HashMap<String,Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<ASIResponseModel, V3pResponseWrap<ASIResponseModel>> amMobilityPersistence
                = new AMMobilityPersistence<ASIResponseModel, V3pResponseWrap<ASIResponseModel>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<ASIResponseModel>());
        AMClientRequest action = new AMPut("/v4/records/{recordId}/customForms");
        action.addUrlParam("recordId", accountRecord.getId(), true);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        JSONArray jsonPost = new JSONArray();
        try {
            JSONObject asi1 = new JSONObject();
            asi1.put("id", "ASI_REGISTER-ACCOUNT.cCLAIM.cDATA.cCAPTURE");
            asi1.put("Driver's License Number", driverLicense);
            asi1.put("DECALS Customer Number", idDEC);
            asi1.put("Date of Birth", dateFormat.format(dateOfBirth));
            jsonPost.put(asi1);

            JSONObject asi2 = new JSONObject();
            asi2.put("id", "ASI_REGISTER-USER.cREGISTRATION");
            asi2.put("Record Mark", "DEC 2.0");
            asi2.put("Select a Security Question", question);
            asi2.put("Answer", answer);
            asi2.put("Password", password);
            asi2.put("Internal Decid", idDEC);
            asi2.put("Type Password Again", password);
            asi2.put("E-mail Address", email);
            asi2.put("User Name", userName);
            jsonPost.put(asi2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonPost.toString();
        RequestParams requestParams = new RequestParams(json);
        action.setBody(requestParams);
        amMobilityPersistence.setEntityType(new ASIResponseModel());
        amMobilityPersistence.processRequestAsync(null, asiRecordDelegate, action, strategy, modelSerializer);
    }

    AMAsyncEntityActionDelegate<RecordModel> finalizeRecordDelegate = new AMAsyncEntityActionDelegate<RecordModel>()
    {
        @Override
        public void onCompleted(AMDataResponse<RecordModel> response) {
            accountRecord = response.getResult();
            if(accountRecord!=null && isCreatingUser) {
                submitNewAccount();
                isCreatingUser = false; //make sure only call once
            } else {
                notifyListener(REGISTER_ERROR_CREATE_NEW_ACCOUNT, null, null);
            }
        }

        @Override
        public void onFailure(Throwable error) {
            notifyListener(REGISTER_ERROR_CREATE_NEW_ACCOUNT, null, null);
        }
    };

    private void finalizeAccountRecord() {
        AMStrategy strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        HashMap<String,Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<RecordModel, V3pResponseWrap<RecordModel>> amMobilityPersistence
                = new AMMobilityPersistence<RecordModel, V3pResponseWrap<RecordModel>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<RecordModel>());
        AMClientRequest action = new AMPost("/v4/records/{recordId}/finalize");
        action.addUrlParam("recordId", accountRecord.getId(), true);

        String json = "\"" + accountRecord.getId() + "\"";
        RequestParams requestParams = new RequestParams(json);
        action.setBody(requestParams);
        amMobilityPersistence.setEntityType(new RecordModel());
        amMobilityPersistence.processRequestAsync(null, finalizeRecordDelegate, action, strategy, modelSerializer);
    }

    AMAsyncEntityActionDelegate<ClaimAccountResponseModel> submitActionDelegate = new AMAsyncEntityActionDelegate<ClaimAccountResponseModel>()
    {
        @Override
        public void onCompleted(AMDataResponse<ClaimAccountResponseModel> response) {

            ClaimAccountResponseModel model = response.getResult();
            //submit claim account
            if (model != null && model.isSuccess) {
                AMLogger.logInfo("Submit new account successfully");
                notifyListener(REGISTER_SUCCESS_CREATE_NEW_ACCOUNT, null, null);
            } else {
                AMLogger.logInfo("Submit new account error ???");
                notifyListener(REGISTER_ERROR_CREATE_NEW_ACCOUNT, model != null ? model.message : null, null);
            }

        }

        @Override
        public void onFailure(Throwable error) {
            AMLogger.logInfo("Submit new account error");
            notifyListener(REGISTER_ERROR_CREATE_NEW_ACCOUNT, null, null);
        }
    };

    public void submitNewAccount() {

        AMStrategy strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        HashMap<String,Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<ClaimAccountResponseModel, V3pResponseWrap<ClaimAccountResponseModel>> amMobilityPersistence
                = new AMMobilityPersistence<ClaimAccountResponseModel, V3pResponseWrap<ClaimAccountResponseModel>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<ClaimAccountResponseModel>());
        AMClientRequest action = new AMPost("/v4/scripts/ARA_SUBMIT_CLAIM_ACCOUNT");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("showDebug", "N");
            jsonObject.put("ipRecordNumber", accountRecord.getCustomId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        RequestParams requestParams = new RequestParams(json);
        action.setBody(requestParams);
        amMobilityPersistence.setEntityType(new ClaimAccountResponseModel());
        amMobilityPersistence.processRequestAsync(null, submitActionDelegate, action, strategy, modelSerializer);
    }

    public void createNewAccount(String question, String answer) {
        if(question==null || answer == null)
            return;
        this.question = question;
        this.answer = answer;
        this.isCreatingUser = true;
        initializeAccountRecord();
    }

//    class DBInitTask implements Runnable{
//        /**
//         * Starts executing the active part of the class' code. This method is
//         * called when a thread is started that has been created with a class which
//         * implements {@code Runnable}.
//         */
//        @Override
//        public void run() {
//            AppContext.initialize(mContext, mUserName+".db");
//        }
//    }

    /**
     *  Login method and its delegate
     */
    public static final String PREFS_NAME = "GameHarvestPreference";
    public static final String DEC_ID_KEY = "Preference_Key0";
    public static String MY_DEC_ID = "";
    private static final String PASSWORD_KEY = "Preference_Key1";
    private static final String USER_KEY = "Preference_Key2";
    private String securePwd;

    private  AMSessionDelegate loginSessionDelegate = new AMSessionDelegate() {
        @Override
        public void amDidCancelLogin() {
            AMLogger.logInfo("Cancel login.");
        }

        @Override
        public void amDidLogin() {
            AMLogger.logInfo("Login succeed.");
            //AMLogger.logInfo("access token:" + accelaMobile.getAuthorizationManager().getAccessToken());
            notifyListener(LOGIN_SUCCESS, null, null);
            SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PASSWORD_KEY, securePwd);
            editor.putString(USER_KEY, mUserName);
            editor.apply();
        }

        @Override
        public void amDidLoginFailure(AMError error) {
            AMLogger.logError(error.toString());
            notifyListener(LOGIN_ERROR, null, null);
        }

        @Override
        public void amDidLogout() {
            AMLogger.logInfo("Logout succeed.");
        }

        @Override
        public void amDidSessionInvalid(AMError error) {
            AMLogger.logError(error.toString());
        }
    };

    //only call this method after create account successfully
    public void loginAfterRegister(Activity activity) {
        if(userName!= null && password!=null) {
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.putExtra("userName", userName);
            intent.putExtra("password", password);
            activity.startActivity(intent);
        }
    }

    public void login(String userName, String password) {
        logout();
        if (!AMUtils.isNetworkConnected(AMApplication.mContext)) {
            SharedPreferences settings = this.mContext.getSharedPreferences(PREFS_NAME, 0);
            String userStr = settings.getString(USER_KEY, "");
            String passwordStr = settings.getString(PASSWORD_KEY, "");
            MY_DEC_ID = settings.getString(DEC_ID_KEY, "");
            if (userStr.equals(userName) && passwordStr.equals(AMUtils.getMD5(password))){
                notifyListener(LOGIN_SUCCESS, null, null);
                isOnlineMode = false;
            }else {
                notifyListener(LOGIN_ERROR, null, null);
            }

        }else{
            accelaMobile.getAuthorizationManager().setSessionDelegate(loginSessionDelegate);
            Map<String,String> customPostParam = new HashMap<>();
            customPostParam.put("id_provider", "citizen");
            this.mUserName = userName;
            accelaMobile.getAuthorizationManager().authenticate(AGENCY, userName, password,
                    ENVIRONMENT, APP_SCOPES, customPostParam);
            securePwd = AMUtils.getMD5(password);
            isOnlineMode = true;
        }

    }

    public void logout() {
        accelaMobile.getAuthorizationManager().logout();
    }

}
