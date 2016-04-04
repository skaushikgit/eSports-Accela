package com.accela.esportsman.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accela.esportsman.R;

/**
 * Created by skaushik on 9/14/15.
 */
public class FormEntityCollectionView extends FrameLayout {
    String[] entityKeyList;
    String[] entityValueList;
    TextView tvKey;
    TextView tvValue;
    LinearLayout entityContainer;

    public FormEntityCollectionView(Context context) {
        super(context);
        init(null);
    }

    public FormEntityCollectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FormEntityCollectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = null;
        if (attrs != null)
            ta = getContext().obtainStyledAttributes(attrs, R.styleable.FormEntityCollectionView);

        if (ta != null) {
            CharSequence[] keyArray = ta.getTextArray(R.styleable.FormEntityCollectionView_entityKeyArray);
            if (keyArray != null && keyArray.length > 0) {
                entityKeyList = new String[keyArray.length];
                for (int i = 0; i < keyArray.length; i++) {
                    entityKeyList[i] = keyArray[i].toString();
                }
            }

            CharSequence[] valueArray = ta.getTextArray(R.styleable.FormEntityCollectionView_entityKeyArray);
            if (valueArray != null && valueArray.length > 0) {
                entityValueList = new String[valueArray.length];
                for (int i = 0; i < valueArray.length; i++) {
                    entityValueList[i] = valueArray[i].toString();
                }
            }
            ta.recycle();
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        entityContainer = (LinearLayout) inflater.inflate(R.layout.form_entity_collection_view, null);
        addView(entityContainer);
        populateEntityCollectionView(entityKeyList, entityValueList, true);
    }

    public void populateEntityCollectionView(String[] keyList, String[] valueList, boolean removeAllView ) {
        if (keyList != null && valueList != null) {
            entityKeyList = keyList;
            entityValueList = valueList;
            if(removeAllView) {
                entityContainer.removeAllViews();
            }
            if (entityKeyList.length == entityValueList.length) {
                for (int i = 0; i < entityKeyList.length; i++) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    View view = (View) inflater.inflate(R.layout.form_entity_view, null);
                    tvKey = (TextView) view.findViewById(R.id.keyId);
                    tvKey.setText(entityKeyList[i]);
                    tvValue = (TextView) view.findViewById(R.id.valueId);
                    tvValue.setText(entityValueList[i]);
                    entityContainer.addView(view);
                    entityContainer.setTag(entityKeyList[i]);
                }
            }
        }
    }

    public void addEntity(String key, String value) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view =  inflater.inflate(R.layout.form_entity_view, null);
        tvKey = (TextView) view.findViewById(R.id.keyId);
        tvKey.setText(key);
        tvValue = (TextView) view.findViewById(R.id.valueId);
        tvValue.setText(value);
        entityContainer.addView(view);
        entityContainer.setTag(key);
    }

    public void setEntiryColor(String key, int color) {
        View view =  entityContainer.findViewWithTag(key);
        if(view!=null) {
            tvKey = (TextView) view.findViewById(R.id.keyId);
            tvKey.setTextColor(color);
        }
    }

    public void emptyCollectionView() {
        if (entityContainer != null) {
            entityContainer.removeAllViews();
        }
    }
}
