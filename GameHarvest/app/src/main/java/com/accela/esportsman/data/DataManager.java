package com.accela.esportsman.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.accela.framework.AMApplication;
import com.accela.framework.model.ASIFieldValueModel;
import com.accela.framework.model.AddressModel;
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
import com.accela.framework.serializer.AMModelSerializer;
import com.accela.framework.serializer.ASIValueSerializer;
import com.accela.framework.util.AMUtils;
import com.accela.esportsman.AppConstant;
import com.accela.esportsman.AppContext;
import com.accela.esportsman.utils.Utils;
import com.accela.mobile.AMBatchResponse;
import com.accela.mobile.AMBatchSession;
import com.accela.mobile.AMError;
import com.accela.mobile.AMLogger;
import com.accela.mobile.AMRequestSender;
import com.accela.mobile.AccelaMobile;
import com.accela.mobile.http.RequestParams;
import com.accela.record.action.RecordAction;
import com.accela.record.model.ContactModel;
import com.accela.record.model.RecordModel;
import com.accela.sqlite.framework.util.SQLUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

/**
 * Created by jzhong on 8/24/15.
 */
public class DataManager extends Observable {
    private static String licenseFileName = "license-list";
    private String citizenFileName = "citizenContact";
    private String customFormFileName = "customFormContacts";


    public static final int ANIMAL_TAG_BEAR = 1;
    public static final int ANIMAL_TAG_DEER = 2;
    public static final int ANIMAL_TAG_SPRING_TURKEY = 3;
    public static final int ANIMAL_TAG_FALL_TURKEY = 4;
    public static final int ANIMAL_TAG_UNKNOWN = 10;
    private static final long TAG_EXPIRE_DATE_BUFFER = 30l * 24 * 3600 * 1000;
    CitizenContactModel citizenContactModel;
    private boolean isRefresh = false;

    enum DataStatus {
        DATA_NOT_READY,
        DATA_DOWNLOADING,
        DATA_DOWNLOADED,
    }

    public final static int DATAMANAGER_UPDATE_FINISH = 1;

    private final static String TAG = "DataManager";
    List<RecordModel> allTags = new ArrayList<RecordModel>();
    List<RecordModel> tags = new ArrayList<RecordModel>();
    List<RecordModel> reports = new ArrayList<RecordModel>();
    List<RecordModel> licenses = new ArrayList<RecordModel>();
    List<RecordModel> gameHarvests = new ArrayList<RecordModel>();

    private Hashtable<String, List<ASIFieldValueModel>> reportASITable = new Hashtable<>();
    private Hashtable<String, RecordModel> recordDetailTable = new Hashtable<>();
    private Hashtable<String, List<ASIFieldValueModel>> gameHarvestASITable = new Hashtable<>();
    List<ASIFieldValueModel> customFormContactModelList = new ArrayList<ASIFieldValueModel>();

    public ContactModel userContact;

    //download variables
    DataStatus dataStatus = DataStatus.DATA_NOT_READY;
    DataStatus contactStatus = DataStatus.DATA_NOT_READY;
    int offset = 0;
    int limit = 25;
    int startIndex = 0;
    int endIndex = 25;
    int indexCounter = 0;
    //    int limit = 50;
    String[] fields = {"result.address", "result", "result.id"};

    long lastUpdateTime;

    public static int getAnimalTagInt(RecordModel tag) {
        if (tag.getType_text().toUpperCase().contains("TURKEY")) {
            if (tag.getType_text().toUpperCase().contains("SPRING")) {
                return ANIMAL_TAG_SPRING_TURKEY;
            } else {
                return ANIMAL_TAG_FALL_TURKEY;
            }

        } else if (tag.getType_text().toUpperCase().contains("DEER") ||
                tag.getType_text().toUpperCase().contains("MUZZ")) {
            return ANIMAL_TAG_DEER;
        } else if (tag.getType_text().toUpperCase().contains("BEAR")) {
            return ANIMAL_TAG_BEAR;
        } else {
            return ANIMAL_TAG_UNKNOWN;
        }
    }


    public List<RecordModel> getAllTags() {
        return allTags;
    }

    public List<RecordModel> getTags() {
        return tags;
    }

    public List<RecordModel> getLicenses() {
        return licenses;
    }

    public List<RecordModel> getReports() {
        return reports;
    }

    public List<RecordModel> getGameHarvests() {
        return gameHarvests;
    }

    public DataStatus getDataStatus() {
        return dataStatus;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void downloadAllLicenseAndReport() {
        if (dataStatus != DataStatus.DATA_NOT_READY) {
            return;
        }
        if (AMUtils.isNetworkConnected(AMApplication.mContext)) {
            downloadLicenseAndReport();
        } else {
            getLocalLicense();
        }
    }

    public void refreshLicenseAndReport() {
        if (AMUtils.isNetworkConnected(AMApplication.mContext) && AccountManager.isOnlineMode && dataStatus == DataStatus.DATA_DOWNLOADED) {
            licenses.clear();
            tags.clear();
            reports.clear();
            gameHarvests.clear();
            gameHarvestASITable.clear();
            reportASITable.clear();
            isRefresh = true;
            dataStatus = DataStatus.DATA_NOT_READY;
            offset = 0;
            downloadLicenseAndReport();
        } else {
            setChanged();
            notifyObservers(DATAMANAGER_UPDATE_FINISH);
        }
    }

    private void downloadLicenseAndReport() {
        dataStatus = DataStatus.DATA_DOWNLOADING;
        AMStrategy strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);

        HashMap<String, Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        context.put("offset", offset);
        context.put("limit", limit);
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<RecordModel, V3pResponseWrap<List<RecordModel>>> amMobilityPersistence = new AMMobilityPersistence<RecordModel, V3pResponseWrap<List<RecordModel>>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<List<RecordModel>>());
        AMClientRequest action = new AMGet("/v4/records/mine?module=Licenses&offset={offset}&limit={limit}"); //&expand=customForms&expand=customForms

        action.addUrlParam("offset", offset, true);
        action.addUrlParam("limit", limit, true);

        Log.d(TAG, action.getPath());
        amMobilityPersistence.setEntityType(new RecordModel());
        amMobilityPersistence.processRequestAsync(null, entityListActionDelegate, action, strategy, modelSerializer);
    }

    AMAsyncEntityListActionDelegate entityListActionDelegate = new AMAsyncEntityListActionDelegate<RecordModel>() {
        @Override
        public void onCompleted(AMDataIncrementalResponse<RecordModel> response) {

            for (RecordModel model : response.getResult()) {
                if ("Licenses".equals(model.getModule())) {

                    String type = model.getType_type() != null ? model.getType_type() : "";
                    String typeValue = model.getType_value() != null ? model.getType_value() : "";
                    String subType = model.getType_subType() != null ? model.getType_subType() : "";
                    String statusType = model.getStatusType() != null ? model.getStatusType() : "";
                    String statusValue = (model.getStatus_value() != null) ? model.getStatus_value() : "";

                    if (type.equals("Tag") && subType.equals("Hunting")) {
                        // License/Tag
                        if (statusValue.equals("Active")) {
                            addTag(model);
                        }
                        if (statusValue.equals("Reported")) {
                            addReport(model);
                        }

                        allTags.add(model);
                        //Create another list to store all the tags
                    } else if ((type.equals("Annual") || type.equals("Lifetime")) && (subType.equals("Hunting") || subType.equals("Fishing") || subType.equals("Trapping")) && statusValue.equals("Active")) {
                        // License/Annual or License/Lifetime
                        addLicense(model);
                    } else if (typeValue.equals("Licenses/Other/Sportsmen/Game Harvest") && statusValue.equals("Approved")) {
                        // Reports can also have type 'Licenses/Other/Sportsmen/Game Harvest' with Status type approved
                        addGameHarvests(model);
//                        addReport(model);
                    } else {
                        Log.d(TAG, "Not a license, report or tag");
                    }
                }
                Log.d(TAG, "id:" + model.getId());
            }
            offset += response.getResult().size();
            if (response.hasMoreItems()) {
                downloadLicenseAndReport();
                setChanged();
                notifyObservers();
            } else {
                lastUpdateTime = System.currentTimeMillis();
                dataStatus = DataStatus.DATA_DOWNLOADED;
                saveLicense();
                downloadHavestCustomFields();
                downloadRecordDetail();
                setChanged();
                notifyObservers(DATAMANAGER_UPDATE_FINISH);
            }
        }

        @Override
        public void onFailure(Throwable error) {
            dataStatus = DataStatus.DATA_NOT_READY;
            setChanged();
            notifyObservers(DATAMANAGER_UPDATE_FINISH);
        }
    };


    HashMap<String, Integer> tagCategory;

    private void buildTagCategory() {
        tagCategory = new HashMap<String, Integer>();
        String[] category = {"Bear", "Either Sex", "Deer", "DMP Deer", "Antlerless", "Spring Turkey", "Fall Turkey"};
        for (int i = 0; i < category.length; i++) {
            tagCategory.put(category[i], 1);
        }

    }

    private void addGameHarvests(RecordModel recordModel) {
        gameHarvests.add(recordModel);
    }

    private void addTag(RecordModel recordModel) {
        //type.category IN ("Bear", "Either Sex", "Deer", "DMP Deer", "Anterless", "Spring Turkey", "Fall Turkey")
        //status.text IN ("Active", "Expired")
        if (tagCategory == null) {
            buildTagCategory();
        }

        Date date = new Date();
        if (recordModel.getType_category() != null) {
            if (recordModel.getType_category().equals("Bear")) {
                if (date.compareTo(AppConstant.bearStart) < 0 && date.compareTo(AppConstant.bearEnd) > 0) {//not in bear range
                    AMLogger.logError(recordModel.getType_category() + "Not in the range");
                    return;
                }
            }
            if (recordModel.getType_category().equals("Deer") || recordModel.getType_category().equals("DMP Deer")) {
                if (date.compareTo(AppConstant.deerStart) < 0 && date.compareTo(AppConstant.deerEnd) > 0) { //not in deer range
                    AMLogger.logError(recordModel.getType_category() + "Not in the range");
                    return;
                }
            }
            if (recordModel.getType_category().equals("Spring Turkey")) {
                if (date.compareTo(AppConstant.springTurkeyStart) < 0 || date.compareTo(AppConstant.springTurkeyEnd) > 0) {
                    AMLogger.logError(recordModel.getType_category() + "Not in the range");
                    return;
                }
            }
            if (recordModel.getType_category().equals("Fall Turkey")) {
                if (date.compareTo(AppConstant.fallTurkeyStart) < 0 || date.compareTo(AppConstant.fallTurkeyEnd) > 0) { //not in deer range
                    AMLogger.logError(recordModel.getType_category() + "Not in the range");
                    return;
                }
            }
        }

        Date expirationDate = recordModel.getRenewalInfo_expirationDate();

        if (expirationDate == null || (System.currentTimeMillis() - expirationDate.getTime()) > TAG_EXPIRE_DATE_BUFFER) {
            //skip the expired tag if expiration date is NULL or expiration date is greater than or equal to today - 30
            Log.d(TAG, "The expiration date:" + recordModel.getRenewalInfo_expirationDate().toString());
            return;
        }

        if (recordModel.getStatus_text().equals("Active") || recordModel.getStatus_text().equals("Expired")) {
            AMLogger.logInfo("The type category: %s", recordModel.getType_category() + " status: " + recordModel.getStatus_text());
            if (tagCategory.get(recordModel.getType_category()) != null) {
                tags.add(recordModel);
            } else {
                AMLogger.logInfo("This category is not a tag: %s", recordModel.getType_category());
            }
        } else {
            AMLogger.logInfo("This tag not a a tag (status: %s)", recordModel.getStatus_text());
        }
    }

    private void addReport(RecordModel recordModel) {
        reports.add(recordModel);
    }

    private void addLicense(RecordModel recordModel) {
        licenses.add(recordModel);
    }

    private void saveLicense() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<RecordModel>>() {
        }.getType();
        String json = gson.toJson(licenses, type);
        Utils.saveData(licenseFileName, json);
        //save last update time for license, because it can be local or remote
        SharedPreferences sp = AppContext.mContext.getSharedPreferences("App", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("License_LUT", lastUpdateTime);
        editor.commit();
    }

    public long getLicenseLastUpdateTime() {
        SharedPreferences sp = AppContext.mContext.getSharedPreferences("App", Context.MODE_PRIVATE);
        return sp.getLong("License_LUT", 0);
    }

    private void getLocalLicense() {
        String content = Utils.getLocalData(licenseFileName);
        if (content == null || content.length() == 0)
            return;
        Gson gson = new Gson();
        Type type = new TypeToken<List<RecordModel>>() {
        }.getType();
        List<RecordModel> list = gson.fromJson(content.toString(), type);
        if (list != null && list.size() > 0) {
            licenses.clear();
            licenses = list;
            setChanged();
            notifyObservers(DATAMANAGER_UPDATE_FINISH);
        }
    }


    // method to get profile
    public ContactModel getProfile() {
        if (userContact != null) {
            return userContact;
        }

        if (contactStatus == DataStatus.DATA_DOWNLOADING) {
            return null;
        }

        //https://apis.accela.com/v4/citizenaccess/contacts?expand=contact,customForms
        AMStrategy strategy = null;
        if (strategy == null) {
            strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        }
        HashMap<String, Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<ContactModel, V3pResponseWrap<List<ContactModel>>> amMobilityPersistence = new AMMobilityPersistence<ContactModel, V3pResponseWrap<List<ContactModel>>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<List<ContactModel>>());
        AMClientRequest action = new AMGet("/v4/citizenaccess/contacts?expand=contact"); //&expand=customForms

        amMobilityPersistence.setEntityType(new ContactModel());
        amMobilityPersistence.processRequestAsync(null, entityActionContactDelegate, action, strategy, modelSerializer);

        return null;
    }


    AMAsyncEntityListActionDelegate entityActionContactDelegate = new AMAsyncEntityListActionDelegate<ContactModel>() {

        @Override
        public void onCompleted(AMDataIncrementalResponse<ContactModel> response) {

            if (response.getResult().size() > 0) {
                userContact = response.getResult().get(0);
                setChanged();
                notifyObservers();
            }
            contactStatus = DataStatus.DATA_DOWNLOADED;


        }

        @Override
        public void onFailure(Throwable error) {
            contactStatus = DataStatus.DATA_NOT_READY;
        }
    };

    //get Active mailing address
    DataStatus activeMailingAddressStatus = DataStatus.DATA_NOT_READY;
    AddressModel activeMailingAddress;

    public AddressModel getActiveMailingAddress() {
        if (activeMailingAddress != null || activeMailingAddressStatus == DataStatus.DATA_DOWNLOADED) {
            return activeMailingAddress;
        }

        if (userContact == null || activeMailingAddressStatus == DataStatus.DATA_DOWNLOADING) {
            return null;
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
        AMClientRequest action = new AMGet("/v4/contacts/" + userContact.getId() + "/addresses"); //&expand=customForms
        amMobilityPersistence.setEntityType(new AddressModel());
        activeMailingAddressStatus = DataStatus.DATA_DOWNLOADING;
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
                activeMailingAddressStatus = DataStatus.DATA_DOWNLOADED;
                setChanged();
                notifyObservers();
            }

            @Override
            public void onFailure(Throwable error) {
                activeMailingAddressStatus = DataStatus.DATA_NOT_READY;
            }
        }, action, strategy, modelSerializer);

        return null;
    }


    public CitizenContactModel getCitizenContact() {
        if (citizenContactModel != null) {
            return citizenContactModel;
        }
        if (!AMUtils.isNetworkConnected(AMApplication.mContext))
            return getLocalCitizenContact();

        if (contactStatus == DataStatus.DATA_DOWNLOADING) {
            return null;
        }

        String languageCode = String.format("%s_%s", Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        AMStrategy strategy = null;
        if (strategy == null) {
            strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        }
        HashMap<String, Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        context.put("lang", languageCode);
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<CitizenContactModel, V3pResponseWrap<List<CitizenContactModel>>> amMobilityPersistence = new AMMobilityPersistence<CitizenContactModel, V3pResponseWrap<List<CitizenContactModel>>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<List<CitizenContactModel>>());
        AMClientRequest action = new AMGet("/v4/citizenaccess/contacts?lang={lang}");
        action.addUrlParam("lang", languageCode, true);
        Log.d(TAG, action.getPath());
        amMobilityPersistence.setEntityType(new CitizenContactModel());
        amMobilityPersistence.processRequestAsync(null, citizenContactActionDelegate, action, strategy, modelSerializer);
        return null;
    }

    AMAsyncEntityListActionDelegate<CitizenContactModel> citizenContactActionDelegate = new AMAsyncEntityListActionDelegate<CitizenContactModel>() {

        @Override
        public void onCompleted(AMDataIncrementalResponse<CitizenContactModel> response) {
            if (response == null || response.getResult() == null || response.getResult().size() == 0)
                return;
            if (response.getResult().size() > 0) {
                citizenContactModel = response.getResult().get(0);
            }
            contactStatus = DataStatus.DATA_DOWNLOADED;
            setChanged();
            notifyObservers(0);
            SharedPreferences settings = AMApplication.mContext.getSharedPreferences(AccountManager.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(AccountManager.DEC_ID_KEY, citizenContactModel.getId());
            AccountManager.MY_DEC_ID = citizenContactModel.getId();
            editor.apply();
            saveCitizenContact();
            getCustomFormForContacts();
        }

        @Override
        public void onFailure(Throwable error) {
            contactStatus = DataStatus.DATA_DOWNLOADING;
            setChanged();
            notifyObservers(0);
        }
    };


    private void saveCitizenContact() {
        Gson gson = new Gson();
        Type type = new TypeToken<CitizenContactModel>() {
        }.getType();
        String json = gson.toJson(citizenContactModel, type);
        Utils.saveData(citizenFileName, json);
    }

    private CitizenContactModel getLocalCitizenContact() {
        String content = Utils.getLocalData(citizenFileName);
        if (content == null || content.length() == 0)
            return citizenContactModel;
        Gson gson = new Gson();
        Type type = new TypeToken<CitizenContactModel>() {
        }.getType();
        citizenContactModel = gson.fromJson(content.toString(), type);
        setChanged();
        notifyObservers(DATAMANAGER_UPDATE_FINISH);
        return citizenContactModel;
    }

    public String getCustomFormContactField(String value) {
        List<ASIFieldValueModel> customeFormList = getCustomFormForContacts();
        for (ASIFieldValueModel model : customeFormList) {
            if (value.equals(model.getFieldName())) {
                return model.getFieldValue();
            }
        }
        return null;
    }

    public List<ASIFieldValueModel> getCustomFormForContacts() {
        if (customFormContactModelList != null && customFormContactModelList.size() > 0) {
            return customFormContactModelList;
        }
        if (!AMUtils.isNetworkConnected(AMApplication.mContext)) {
            return getLocalCustomFormContacts();
        }
        String id = "";
        CitizenContactModel citizenContactModel = getCitizenContact();
        if (citizenContactModel != null) {
            id = citizenContactModel.getId();
        }
        String languageCode = String.format("%s_%s", Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());

        AMStrategy strategy = null;
        if (strategy == null) {
            strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        }
        HashMap<String, Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");

        if (!TextUtils.isEmpty(id)) {
            strWhere.append(" and entityId = '" + SQLUtils.escape(id) + "'");
            context.put("entityId", id);
        }
        context.put("lang", languageCode);

        strategy.setAccidentalCondition(strWhere.toString());
        ASIValueSerializer modelSerializer = new ASIValueSerializer(context);
        AMMobilityPersistence<ASIFieldValueModel, V3pResponseWrap<List<ASIFieldValueModel>>> amMobilityPersistence = new AMMobilityPersistence<ASIFieldValueModel, V3pResponseWrap<List<ASIFieldValueModel>>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<List<ASIFieldValueModel>>());
        AMClientRequest action = new AMGet("/v4/contacts/{id}/customForms?lang={lang}");
        action.addUrlParam("id", id, true);
        action.addUrlParam("lang", languageCode, true);
        amMobilityPersistence.setEntityType(new ASIFieldValueModel());
        amMobilityPersistence.processRequestAsync(null, customFormForContactsDelegate, action, strategy, modelSerializer);
        contactStatus = DataStatus.DATA_DOWNLOADING;
        return null;
    }

    AMAsyncEntityListActionDelegate<ASIFieldValueModel> customFormForContactsDelegate = new AMAsyncEntityListActionDelegate<ASIFieldValueModel>() {

        @Override
        public void onCompleted(AMDataIncrementalResponse<ASIFieldValueModel> response) {
            if (response.getResult().size() > 0) {
                for (ASIFieldValueModel model : response.getResult()) {
                    if (model != null)
                        customFormContactModelList.add(model);
                }
            }
            contactStatus = DataStatus.DATA_DOWNLOADED;
            setChanged();
            notifyObservers(1);
            savecustomFormForContacts();
        }

        @Override
        public void onFailure(Throwable error) {
            contactStatus = DataStatus.DATA_NOT_READY;
            setChanged();
            notifyObservers(1);
        }
    };


    private String getASIDetailItem(List<ASIFieldValueModel> list, String key) {
        if (list.size() > 0) {
            for (ASIFieldValueModel model : list) {
                if (key.equals(model.getFieldName())) {
                    return model.getFieldValue();
                }
            }
        }
        return null;
    }

    public RecordModel getGameHavestRecordByTagId(String id) {
        if (id == null)
            return null;
        //   Log.d("ReportDetails", "get havest record for tag: " + id);
        int i = 0;
        for (RecordModel model : gameHarvests) {
            List<ASIFieldValueModel> list = gameHarvestASITable.get(model.getId());
            i++;
            if (list != null) {
                //map the game harvest and tag
                String tagId = getASIDetailItem(list, "TAG ID to Report On");
                //         Log.d("ReportDetails", "TAG ID to Report On: " + tagId + " #" +  model.getId() + " no." +i );
                if (id.equals(tagId)) {
                    //           Log.d("ReportDetails", "Match!!!==========");
                    return model;
                }
            } else {
                //       Log.d("ReportDetails", "Empty ASI:" + model.getId());
            }
        }
        return null;
    }

    //download all ASI for game havest
    public void downloadHavestCustomFields() {
        int limit = 25;
        int offset = gameHarvestASITable.size();

        AMStrategy strategy = null;
        if (strategy == null) {
            strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        }
        AccelaMobile accelaMobile = AccelaMobile.getInstance();
        AMRequestSender requestSender = accelaMobile.getRequestSender();
        AMBatchSession session = requestSender.batchBegin();
        RecordAction recordAction = new RecordAction();
        int requestCount = 0;
        for (int i = offset; i < offset + limit; i++) {
            if (gameHarvests.size() <= i) {
                break;
            }
            RecordModel model = gameHarvests.get(i);
            GameHavestASIActionDelegate requestDelegate = new GameHavestASIActionDelegate();
            requestDelegate.recordId = model.getId();
            recordAction.getRecordCustomFieldsAsync(session, requestDelegate, strategy, model.getId());
            requestCount++;
        }

        if (requestCount <= 0) {
            return;
        }

        requestSender.batchCommit(session, null,
                new AMBatchResponse.AMBatchRequestDelegate() {
                    @Override
                    public void onSuccessful() {
                        AMLogger.logInfo("batchCommitd successful");
                        if (gameHarvests.size() > gameHarvestASITable.size()) {
                            downloadHavestCustomFields();
                        }
                        setChanged();
                        notifyObservers();
                    }

                    @Override
                    public void onFailed(AMError paramAMError) {
                        setChanged();
                        notifyObservers();
                    }
                });
    }


    private class GameHavestASIActionDelegate extends AMAsyncEntityListActionDelegate<ASIFieldValueModel> {
        String recordId;

        @Override
        public void onCompleted(AMDataIncrementalResponse<ASIFieldValueModel> response) {
            if (response.getResult().size() > 0) {
                List<ASIFieldValueModel> asiFieldValueModelList = new ArrayList<ASIFieldValueModel>();
                for (ASIFieldValueModel model : response.getResult()) {
                    asiFieldValueModelList.add(model);
                    gameHarvestASITable.put(recordId, asiFieldValueModelList);
                }
            }
            setChanged();
            notifyObservers();
        }

        @Override
        public void onFailure(Throwable error) {
            setChanged();
            notifyObservers();
        }
    }

    ;

    //Get ASI for tag
    public List<ASIFieldValueModel> getReportCustomFields(RecordModel tag) {
        return reportASITable.get(tag.getId());
    }

    public void downloadReportCustomFields(RecordModel tag) {
        AMStrategy strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        AccelaMobile accelaMobile = AccelaMobile.getInstance();
        AMRequestSender requestSender = accelaMobile.getRequestSender();
        RecordAction recordAction = new RecordAction();
        ReportActionDelegate requestDelegate = new ReportActionDelegate();
        requestDelegate.recordId = tag.getId();
        recordAction.getRecordCustomFieldsAsync(null, requestDelegate, strategy, tag.getId());
    }

    private class ReportActionDelegate extends AMAsyncEntityListActionDelegate<ASIFieldValueModel> {
        String recordId;

        @Override
        public void onCompleted(AMDataIncrementalResponse<ASIFieldValueModel> response) {
            if (response.getResult().size() > 0) {
                List<ASIFieldValueModel> asiFieldValueModelList = new ArrayList<ASIFieldValueModel>();
                for (ASIFieldValueModel model : response.getResult()) {
                    asiFieldValueModelList.add(model);
                    reportASITable.put(recordId, asiFieldValueModelList);
                }
            }
            setChanged();
            notifyObservers();
        }

        @Override
        public void onFailure(Throwable error) {
            setChanged();
            notifyObservers();
        }
    }

    ;


    //Method for contact list.
    private void savecustomFormForContacts() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ASIFieldValueModel>>() {
        }.getType();
        String json = gson.toJson(customFormContactModelList, type);
        Utils.saveData(customFormFileName, json);
    }

    private List<ASIFieldValueModel> getLocalCustomFormContacts() {
        String content = Utils.getLocalData(customFormFileName);
        if (content == null || content.length() == 0)
            return customFormContactModelList;
        Gson gson = new Gson();
        Type type = new TypeToken<List<ASIFieldValueModel>>() {
        }.getType();

        List<ASIFieldValueModel> list = gson.fromJson(content.toString(), type);
        if (list != null && list.size() > 0) {
            customFormContactModelList = list;
            setChanged();
            notifyObservers(DATAMANAGER_UPDATE_FINISH);
        }
        return customFormContactModelList;
    }

    private void downloadRecordDetail() {
        StringBuilder builder = new StringBuilder();
        List<RecordModel> licenses = getLicenses();
        if (licenses.size() > 0) {
            for (int i = 0; i < licenses.size(); i++) {
                RecordModel model = licenses.get(i);
                builder.append(model.getId());
                if (i != licenses.size() - 1)
                    builder.append(",");
            }
            getRecordDetailByRecordId(builder.toString());
        }

    }

    public void getRecordDetailByRecordId(String recordIds) {
        AMStrategy strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        HashMap<String, Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<RecordModel, V3pResponseWrap<RecordModel>> amMobilityPersistence
                = new AMMobilityPersistence<RecordModel, V3pResponseWrap<RecordModel>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<RecordModel>());
        AMClientRequest action = new AMGet("/v4/records/{ids}?fields=statusDate");
        action.addUrlParam("ids", recordIds, true);

        String json = "";// "\"" + newReport.getId() + "\"";
        RequestParams requestParams = new RequestParams(json);
        action.setBody(requestParams);
        amMobilityPersistence.setEntityType(new RecordModel());
        amMobilityPersistence.processRequestAsync(null, recordDetailDelegate, action, strategy, modelSerializer);
    }

    AMAsyncEntityListActionDelegate<RecordModel> recordDetailDelegate = new AMAsyncEntityListActionDelegate<RecordModel>() {
        @Override
        public void onCompleted(AMDataIncrementalResponse<RecordModel> response) {
            if (response.getResult().size() > 0) {
                for (RecordModel model: response.getResult()) {
                    recordDetailTable.put(model.getId(), model);
                }
                setChanged();
                notifyObservers();
            }
        }

        @Override
        public void onFailure(Throwable error) {
            setChanged();
            notifyObservers();
        }
    };

    public Date getStatusDateByRecordId(String recordId) {
        if (recordDetailTable.size() > 0) {
            RecordModel model = recordDetailTable.get(recordId);
            if (model != null) {
                return model.getStatusDate();
            }
        }
        return null;
    }

}
