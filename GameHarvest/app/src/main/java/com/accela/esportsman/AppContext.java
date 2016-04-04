package com.accela.esportsman;

import com.accela.framework.AMApplication;
import com.accela.esportsman.data.CustomFormManager;
import com.accela.esportsman.data.DataManager;

/**
 * Created by eyang on 8/18/15.
 */
public class AppContext extends AMApplication{

    private static DataManager dataManager = null;
    private static CustomFormManager customFormManager = null;

    public static DataManager getDataManager(){
        if (dataManager==null)
            dataManager = new DataManager();
        return dataManager;
    }

    public static CustomFormManager getCustomFormManager(){
        if(customFormManager==null)
            customFormManager = new CustomFormManager();
        return customFormManager;
    }

    public static void resetBackendData(){
        dataManager = null;
        customFormManager = null;
    }
}
