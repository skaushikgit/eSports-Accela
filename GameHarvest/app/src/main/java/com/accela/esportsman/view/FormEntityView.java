package com.accela.esportsman.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.accela.esportsman.R;

/**
 * Created by skaushik on 9/14/15.
 */
public class FormEntityView extends FrameLayout {
    String sKey;
    String sValue;
    TextView tvKey;
    TextView tvValue;

    public FormEntityView(Context context) {
        super(context);
        init(null);
    }

    public FormEntityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FormEntityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = null;
        if (attrs != null) {
            ta = getContext().obtainStyledAttributes(attrs, R.styleable.FormEntityView);
        }

        if (ta != null) {
            CharSequence key = ta.getText(R.styleable.FormEntityView_entitykey);
            CharSequence value = ta.getText(R.styleable.FormEntityView_entityValue);
            if (key != null)
                sKey = key.toString();
            if (value != null)
                sValue = value.toString();
            ta.recycle();
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = (View) inflater.inflate(R.layout.form_entity_view, null);
        tvKey = (TextView) view.findViewById(R.id.keyId);
        tvValue = (TextView) view.findViewById(R.id.valueId);
        addView(view);
    }

    public void setEntityKey(String key) {
        tvKey.setText(key);
    }

    public void setEntityValue(String value) {
        tvValue.setText(value);
    }
}
