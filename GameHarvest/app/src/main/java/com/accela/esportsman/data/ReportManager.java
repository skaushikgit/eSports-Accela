package com.accela.esportsman.data;


import com.accela.framework.AMBaseModel;
import com.accela.framework.model.V3pResponseWrap;
import com.accela.framework.persistence.AMAsyncEntityActionDelegate;
import com.accela.framework.persistence.AMAsyncEntityListActionDelegate;
import com.accela.framework.persistence.AMClientRequest;
import com.accela.framework.persistence.AMDataIncrementalResponse;
import com.accela.framework.persistence.AMDataResponse;
import com.accela.framework.persistence.AMMobilityPersistence;
import com.accela.framework.persistence.AMStrategy;
import com.accela.framework.persistence.request.AMPost;
import com.accela.framework.persistence.request.AMPut;
import com.accela.framework.serializer.AMModelSerializer;
import com.accela.mobile.AMError;
import com.accela.mobile.AMLogger;
import com.accela.mobile.AMRequestDelegate;
import com.accela.mobile.AccelaMobile;
import com.accela.mobile.http.RequestParams;
import com.accela.record.model.RecordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by jzhong on 9/24/15.
 */
public class ReportManager {


    private final static String TAG = "ReportManager";

    public interface ReportActionListener {
        public void onActionDone(int errorCode, String message, Object data);
    }

    private ReportActionListener actionListener;

    //error code
    public final static int SUBMIT_REPORT_SUCCESSFUL     = 1;
    public final static int SUBMIT_REPORT_ERROR         = 2;

    //The info user input for register new account
    ReportForm reportForm;
    RecordModel newReport;
    //the contacts json array for create new report
    JSONArray contacts;
    public void setActionListener(ReportActionListener l) {
        actionListener = l;
    }

    private void notifyListener(int errorCode, String message, Object data) {
        if(actionListener!=null) {
            actionListener.onActionDone(errorCode, message, data);
        }
    }

    public void submitNewReport(ReportForm form) {
        reportForm = form;
        getContacts();
    }

    /*
    The procedure to create a new report:
    0. get contacts
    1. initialize a record - initializeReportRecord()
    2. create costom form - addASItoReportRecord()
    3. finalize the record - finalizeReportRecord()
     */

    AMRequestDelegate contactRequestDelegate = new AMRequestDelegate() {
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(JSONObject content) {
            try {
                if (content != null) {
                    contacts = content.getJSONArray("result");
                }
                if(contacts!=null) {
                    for(int i=0; i<contacts.length(); i++) {
                        JSONObject jsonObject = contacts.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        jsonObject.put("referenceContactId", id);
                        jsonObject.remove("status");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(contacts!=null) {
                initializeReportRecord();
            } else {
                notifyListener(SUBMIT_REPORT_ERROR, null, null);
            }
        }

        @Override
        public void onFailure(AMError error) {
            notifyListener(SUBMIT_REPORT_ERROR, null, null);
        }
    };

    private void getContacts() {
        AccelaMobile accelaMobile = AccelaMobile.getInstance();
        accelaMobile.getRequestSender().sendRequest("/v4/citizenaccess/contacts", null, null, contactRequestDelegate);

    }


    AMAsyncEntityActionDelegate<RecordModel> initializeRecordDelegate = new AMAsyncEntityActionDelegate<RecordModel>()
    {
        @Override
        public void onCompleted(AMDataResponse<RecordModel> response) {
            newReport = response.getResult();
            if(newReport!=null) {
                AMLogger.logInfo("initilaize report successfully");
                addASItoReportRecord();
            } else {
                AMLogger.logInfo("initilaize report, but can't get record");
                notifyListener(SUBMIT_REPORT_ERROR, null, null);
            }
        }

        @Override
        public void onFailure(Throwable error) {
            AMLogger.logInfo("initilaize report error");
            notifyListener(SUBMIT_REPORT_ERROR, null, null);
        }
    };

    private void initializeReportRecord() {
        newReport = null;
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
            type.put("id", "Licenses-Other-Sportsmen-Game.cHarvest");
            jsonObject.put("type", type);
            jsonObject.put("contacts", contacts);
        } catch (JSONException e) {
            AMLogger.logError(e.toString());
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
            if(success==4) {
                //submit 2 ASI, all should be successful.
                AMLogger.logInfo("Create report ASI Done");
                finalizeReportRecord();
            } else {
                AMLogger.logInfo("Create report ASI error!, not all asi created successful: " + success);
                notifyListener(SUBMIT_REPORT_ERROR, null, null);
            }
        }

        @Override
        public void onFailure(Throwable error) {
            AMLogger.logInfo("Create report ASI error!");
            notifyListener(SUBMIT_REPORT_ERROR, null, null);
        }
    };

    private void addASItoReportRecord() {
        AMStrategy strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        HashMap<String,Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<ASIResponseModel, V3pResponseWrap<ASIResponseModel>> amMobilityPersistence
                = new AMMobilityPersistence<ASIResponseModel, V3pResponseWrap<ASIResponseModel>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<ASIResponseModel>());
        AMClientRequest action = new AMPut("/v4/records/{recordId}/customForms");
        action.addUrlParam("recordId", newReport.getId(), true);
        JSONArray jsonPost = reportForm.buildJSONForSubmit(getDateOfBirth());
        String json = jsonPost.toString();
        RequestParams requestParams = new RequestParams(json);
        action.setBody(requestParams);
        amMobilityPersistence.setEntityType(new ASIResponseModel());
        amMobilityPersistence.processRequestAsync(null, asiRecordDelegate, action, strategy, modelSerializer);
    }

    private String getDateOfBirth(){
        String dateOfBirth = "";
        try {
            if(contacts!=null) {
                for(int i=0; i<contacts.length(); i++) {
                    JSONObject jsonObject = contacts.getJSONObject(i);
                    dateOfBirth = jsonObject.getString("birthDate");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dateOfBirth;
    }

    AMAsyncEntityActionDelegate<RecordModel> finalizeRecordDelegate = new AMAsyncEntityActionDelegate<RecordModel>()
    {
        @Override
        public void onCompleted(AMDataResponse<RecordModel> response) {
            newReport = response.getResult();
            if(newReport!=null) {
                notifyListener(SUBMIT_REPORT_SUCCESSFUL, null, null);
            } else {
                notifyListener(SUBMIT_REPORT_ERROR, null, null);
            }
        }

        @Override
        public void onFailure(Throwable error) {
            notifyListener(SUBMIT_REPORT_ERROR, null, null);
        }
    };

    private void finalizeReportRecord() {
        AMStrategy strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        HashMap<String,Object> context = new HashMap<String, Object>();
        StringBuilder strWhere = new StringBuilder("1 =1 ");
        strategy.setAccidentalCondition(strWhere.toString());

        AMModelSerializer modelSerializer = new AMModelSerializer(context);
        AMMobilityPersistence<RecordModel, V3pResponseWrap<RecordModel>> amMobilityPersistence
                = new AMMobilityPersistence<RecordModel, V3pResponseWrap<RecordModel>>();
        amMobilityPersistence.setResponseWrap(new V3pResponseWrap<RecordModel>());
        AMClientRequest action = new AMPost("/v4/records/{recordId}/finalize");
        action.addUrlParam("recordId", newReport.getId(), true);

        String json = "";// "\"" + newReport.getId() + "\"";
        RequestParams requestParams = new RequestParams(json);
        action.setBody(requestParams);
        amMobilityPersistence.setEntityType(new RecordModel());
        amMobilityPersistence.processRequestAsync(null, finalizeRecordDelegate, action, strategy, modelSerializer);
    }

}
