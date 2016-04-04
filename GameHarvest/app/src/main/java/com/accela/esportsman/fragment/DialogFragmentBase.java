package com.accela.esportsman.fragment;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;

import com.accela.esportsman.R;

/**
 * Created by jzhong on 8/20/15.
 */
public class DialogFragmentBase extends DialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            float density = getResources().getDisplayMetrics().density;
            int dialogWidth = (int) (getResources().getDisplayMetrics().widthPixels -
                     getResources().getDimension(R.dimen.activity_horizontal_margin));
            int dialogHeight = (int) (getResources().getDisplayMetrics().heightPixels - 2* 60 * density);
            dialog.getWindow().setLayout(dialogWidth, dialogHeight);
        }
    }
}
