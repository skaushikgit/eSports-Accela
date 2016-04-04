package com.accela.esportsman.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accela.esportsman.R;
import com.accela.esportsman.activity.BaseActivity;
import com.accela.esportsman.fragment.DialogFragmentSelectorList;

/**
 * Created by jzhong on 9/1/15.
 */

public class SelectorView extends FrameLayout {

    String[] selectorList;
    int selectedIndex;
    TextView textValue;
    TextView textTitle;
    String title;
    View selectorView;
    OnClickListener onClickListener;
    protected OnSelectItemListener onSelectItemListenerOutside;
    protected OnSelectItemListener onSelectItemListenerInteral = new OnSelectItemListener() {
        @Override
        public void OnSelectItem(String item, int position) {
            textValue.setText(item);
            selectedIndex = position;
            if(onSelectItemListenerOutside!=null) {
                onSelectItemListenerOutside.OnSelectItem(item, position);
            }
        }
    };

    public interface OnSelectItemListener {
        public void OnSelectItem(String item, int position);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String getSelectorText() {
        return textValue.getText().toString();
    }

    public int setSelectorText(String value) {
        //reset select index and find it by text
        selectedIndex = -1;

        for(int i=0; i<selectorList.length ; i++) {
            if(value!= null && value.equals(selectorList[i])) {
                selectedIndex = i;
                break;
            }
        }
        setSelectorIndex(selectedIndex);
        return selectedIndex;
    }

    public void setSelectorIndex(int index) {
        if(index < 0 || index >= selectorList.length) {
            selectedIndex = -1;
            textValue.setText(R.string.select);
        } else {
            selectedIndex = index;
            textValue.setText(selectorList[index]);
        }
    }

    public void setSelectorTextStyle(boolean isActive) {
        if (isActive) {
            textValue.setTextColor(getResources().getColor(R.color.black));
        } else {
            textValue.setTextColor(getResources().getColor(R.color.grey));
        }
    }


    public SelectorView(Context context) {
        super(context);
        init(null);
    }

    public SelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public SelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void setSelectorList(String[] list, int defaultSelected) {
        selectorList = list;
        selectedIndex = defaultSelected;
        if(textValue!=null) {
            if (list.length==1) {
                textValue.setText(list[0]);
            }else if(defaultSelected >=0 && defaultSelected < list.length) {
                textValue.setText(list[defaultSelected]);
            } else {
                textValue.setText(R.string.select);
            }
        }

    }

    public void clearSelectorList() {
        selectorList = new String[0];
    }

    public void setSelectorTitle(String text) {
        title = text;
        if(textTitle!=null) {
            textTitle.setText(title);
        }
    }

    public void enableClickListener() {
        selectorView.setOnClickListener(onClickListener);
    }

    public void disableClickListener() {
        selectorView.setOnClickListener(null);
    }

    public void setOnSelectItemListener(OnSelectItemListener l) {
        onSelectItemListenerOutside = l;
    }

    private void init(AttributeSet attrs) {

        //get Strings array from xml.
        TypedArray ta = null;
        if(attrs!=null)
            ta = getContext().obtainStyledAttributes(attrs, R.styleable.SelectorView);

        if (ta != null) {
            CharSequence[] strings = ta.getTextArray(R.styleable.SelectorView_stringArray);
            if(strings!=null && strings.length>0) {
                selectorList = new String[strings.length];
                for(int i=0; i< strings.length; i++) {
                    selectorList[i] = strings[i].toString();
                }
            }
            CharSequence s = ta.getText(R.styleable.SelectorView_title);
            if(s!=null) {
                title = s.toString();
            }
            ta.recycle();
        }

        //add view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        selectorView = (View) inflater.inflate(R.layout.selector_view, null);
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectorList();
            }
        };
        selectorView.setOnClickListener(onClickListener);
        textValue = (TextView) selectorView.findViewById(R.id.textValue);
        textTitle = (TextView) selectorView.findViewById(R.id.textTitle);
        setSelectorTitle(title);
        addView(selectorView);
    }

    protected void showSelectorList() {
       Context context = getContext();
        if(context instanceof BaseActivity && selectorList!=null && selectorList.length>0) {
            FragmentManager fragmentManager = ((BaseActivity) context).getSupportFragmentManager();
            DialogFragmentSelectorList dialogFragmentSelectorList = new DialogFragmentSelectorList();
            dialogFragmentSelectorList.setSelectorList(selectorList, selectedIndex);
            dialogFragmentSelectorList.setOnSelectItemListener(onSelectItemListenerInteral);
            dialogFragmentSelectorList.setTitle(textTitle.getText().toString());
            dialogFragmentSelectorList.show( fragmentManager, "DialogFragmentSelectorList");
        }else{
            Toast.makeText(context, getResources().getString(R.string.no_location), Toast.LENGTH_SHORT).show();
        }

    }


}
