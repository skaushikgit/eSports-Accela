package com.accela.esportsman.fragment;

import android.support.v4.app.Fragment;

import com.accela.esportsman.data.ReportForm;
import com.accela.record.model.RecordModel;

/**
 * Created by jzhong on 9/9/15.
 */
public class ReportFormFragmentBase extends Fragment {
    protected RecordModel tag;
    protected ReportForm reportForm;
    protected ReportFormFieldChangeListener reportFormFieldChangeListener;
    public interface ReportFormFieldChangeListener {
        public void formFieldChange(ReportFormFragmentBase fragment, boolean isAllFieldsFilled);
    }

    public void setReportFormFieldChangeListener(ReportFormFieldChangeListener l) {
        reportFormFieldChangeListener = l;
    }

    protected void notfityFormListener(boolean isAllFieldsFilled) {
        if(reportFormFieldChangeListener!=null) {
            reportFormFieldChangeListener.formFieldChange(this, isAllFieldsFilled);
        }
    }
}
