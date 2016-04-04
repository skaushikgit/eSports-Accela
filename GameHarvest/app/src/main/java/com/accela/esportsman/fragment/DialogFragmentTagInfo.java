package com.accela.esportsman.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accela.esportsman.R;
import com.accela.esportsman.utils.Utils;
import com.accela.record.model.RecordModel;


/**
 * Created by jzhong on 8/20/15.
 */
public class DialogFragmentTagInfo extends DialogFragmentBase {

    RecordModel model;


    public void setRecord(RecordModel model) {
        this.model = model;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dialog_tag_info, container, false);
        View viewBack = view.findViewById(R.id.viewBack);
        viewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragmentTagInfo.this.dismiss();
            }
        });


        TextView textTagName = (TextView) view.findViewById(R.id.textTagName);
        textTagName.setText(model.getName());
        TextView textTagId = (TextView) view.findViewById(R.id.textTagId);
        textTagId.setText(Utils.formatDocNumber(model.getCustomId()));
        TextView textTagInfoDes = (TextView) view.findViewById(R.id.textTagInfoDes);
        String s = getString(R.string.tag_info_details);
        textTagInfoDes.setMovementMethod(LinkMovementMethod.getInstance());
        textTagInfoDes.setText(Html.fromHtml(s));

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
    }


}
