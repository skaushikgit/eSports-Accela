package com.accela.esportsman.data;

import com.accela.framework.action.SettingAction;
import com.accela.framework.model.ASIDrillDownChildModel;
import com.accela.framework.model.ASIDrillDownModel;
import com.accela.framework.model.ASIFieldModel;
import com.accela.framework.model.DrillDownModel;
import com.accela.framework.persistence.AMAsyncEntityListActionDelegate;
import com.accela.framework.persistence.AMDataIncrementalResponse;
import com.accela.framework.persistence.AMStrategy;
import com.accela.esportsman.AppConstant;
import com.accela.record.action.RecordAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

/**
 * Created by skaushik on 8/26/15.
 */
public class CustomFormManager extends Observable {
    DataStatus dataStatus = DataStatus.DATA_NOT_READY;
    List<String> countyList = new ArrayList<>();
    List<String> townList = new ArrayList<>();
    List<String> wmuList = new ArrayList<>();
    List<String> optionsList = new ArrayList<>();

    private static Hashtable<String, ASIFieldModel> drillDownTable = new Hashtable<>();
    private static Hashtable<String, List<String>> townListTable = new Hashtable<>();

    int offset = 0;
    int limit = 25;
    private static String currentLocation = null;
    private static long wmuDrillId;
    private static long townDrillId;
    private static final String TOWN_ID = "401";
    private static final String WMU_ID = "402";

    enum DataStatus {
        DATA_NOT_READY,
        DATA_DOWNLOADING,
        DATA_DOWNLOADED,
    }

    public List<String> getCountyList() {
        return countyList;
    }

    public List<String> getTownList() {
        return townList;
    }

    public void setTownList(List<String> list) {
        townList = list;
    }

    public List<String> getWmuList() {
        return wmuList;
    }

    public long getWmuDrillId() {
        return wmuDrillId;
    }

    public long getTownDrillId() {
        return townDrillId;
    }

    public List<String> getASIFieldModelOptions(String fieldName) {
        optionsList.clear();
        ASIFieldModel model = drillDownTable.get(fieldName);
        if (model != null) {
            JSONArray jArray = model.getOptions();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject obj = null;
                try {
                    obj = jArray.getJSONObject(i);
                    if (obj.getString("value") != null)
                        optionsList.add(obj.getString("value"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return optionsList;
    }

    public ASIFieldModel getASIFieldModel(String fieldName) {
        return drillDownTable.get(fieldName);
    }

    public void getRecordTypeCustomformAsync() {
        if (dataStatus==DataStatus.DATA_DOWNLOADING || dataStatus==DataStatus.DATA_DOWNLOADED)
            return;
        dataStatus = DataStatus.DATA_DOWNLOADING;
        AMStrategy strategy = null;
        if (strategy == null) {
            strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        }

        RecordAction recordAction = new RecordAction();
        recordAction.getRecordTypeCustomformAsync(null, entityActionCountyDelegate, strategy, "Licenses-Other-Sportsmen-Game.cHarvest");
    }

    AMAsyncEntityListActionDelegate entityActionCountyDelegate = new AMAsyncEntityListActionDelegate<ASIFieldModel>() {
        @Override
        public void onCompleted(AMDataIncrementalResponse<ASIFieldModel> response) {

            for (ASIFieldModel model : response.getResult()) {

                if (model.getFieldType().equals(AppConstant.DROP_DOWN_LIST)) {
                    if (drillDownTable.get(model.getFieldName()) == null) {
                        drillDownTable.put(model.getFieldName(), model);
                    }
                }


                if (model.getFieldName().equals(AppConstant.COUNTY_OF_KILL)) {
                    //Get townDrillId
                    ASIDrillDownModel drillDown = model.getDrillDown();
                    List<ASIDrillDownChildModel> childModelArray = drillDown.getChildren();
                    for (int j = 0; j < childModelArray.size(); j++) {
                        ASIDrillDownChildModel childModel = childModelArray.get(j);
                        townDrillId = childModel.getDrillId();
                    }

                    //Get County list
                    JSONArray jArray = model.getOptions();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject obj = null;
                        try {
                            obj = jArray.getJSONObject(i);
                            if (obj.getString("value") != null)
                                countyList.add(obj.getString("value"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //Get wmuDrillId
                if (model.getFieldName().equals("Town")) {
                    ASIDrillDownModel drillDown = model.getDrillDown();
                    List<ASIDrillDownChildModel> childModelArray = drillDown.getChildren();
                    for (int j = 0; j < childModelArray.size(); j++) {
                        ASIDrillDownChildModel childModel = childModelArray.get(j);
                        wmuDrillId = childModel.getDrillId();
                    }
                }

            }

            offset += response.getResult().size();
            if (response.hasMoreItems()) {
                getRecordTypeCustomformAsync();
                setChanged();
                notifyObservers(0);
                dataStatus = DataStatus.DATA_DOWNLOADING;
            } else {
                setChanged();
                notifyObservers(0);
                dataStatus = DataStatus.DATA_DOWNLOADED;
            }
        }

        @Override
        public void onFailure(Throwable error) {
            dataStatus = DataStatus.DATA_NOT_READY;
            setChanged();
            notifyObservers();
        }
    };

    public List<String> getTownListLocally(String county) {
        if (townListTable.size() > 0) {
            return townListTable.get(county);
        }
        return null;
    }

    public void getDrillDownTownList(String location, String drillId) {
        if (drillId.equals(TOWN_ID)) {
            currentLocation = location;
        }
        AMStrategy strategy = null;
        if (strategy == null) {
            strategy = new AMStrategy(AMStrategy.AMAccessStrategy.Http);
        }
        String languageCode = String.format("%s_%s", Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        SettingAction settingAction = new SettingAction();
        settingAction.getSettingsDrilldownAsync(null, entityActionTownDelegate, strategy, drillId, languageCode, location);
    }

    AMAsyncEntityListActionDelegate entityActionTownDelegate = new AMAsyncEntityListActionDelegate<DrillDownModel>() {

        @Override
        public void onCompleted(AMDataIncrementalResponse<DrillDownModel> response) {
            if (response==null || response.getResult()==null || response.getResult().size()==0){
                townList.clear();
                wmuList.clear();
                setChanged();
                notifyObservers(1);
                return;
            }
            String drillId = response.getResult().get(0).getDrillId();
            if (drillId.equals(TOWN_ID)) {
                townList.clear();
                wmuList.clear();
            } else if(drillId.equals(WMU_ID)) {
                wmuList.clear();
            }

            if (response.getResult().size() > 0) {
                for (DrillDownModel model : response.getResult()) {
                    if (model.getDrillId().equals(TOWN_ID)) {
                        townList.add(model.getValue());
                    }
                    if (model.getDrillId().equals(WMU_ID)) {
                        wmuList.add(model.getValue());
                    }
                }

                if (!townListTable.contains(currentLocation)) {
                    List<String> newList = new ArrayList<>(townList);
                    townListTable.put(currentLocation, newList);
                }

                dataStatus = DataStatus.DATA_DOWNLOADED;
                if (drillId.equals(TOWN_ID)) {
                    setChanged();
                    notifyObservers(1);
                }else if(drillId.equals(WMU_ID)){
                    setChanged();
                    notifyObservers(2);
                }
            }

        }

        @Override
        public void onFailure(Throwable error) {
            dataStatus = DataStatus.DATA_NOT_READY;
            townList.clear();
            wmuList.clear();
            setChanged();
            notifyObservers(1);
        }
    };
}
