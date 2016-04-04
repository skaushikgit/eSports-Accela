package com.accela.esportsman.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accela.esportsman.R;

/**
 * Created by jzhong on 9/1/15.
 */

public class OptionsView extends FrameLayout {

    String[] selectorList;
    int selectedIndex = -1;
    View[] optionItemView;
    TextView textTitle;
    LinearLayout optionsContainer;
    String title;
    String value;
    protected OnSelectItemListener onSelectItemListenerOutside;
    protected OnSelectItemListener onSelectItemListenerInteral = new OnSelectItemListener() {
        @Override
        public void OnSelectItem(String itemValue, int position) {
            updateSelectOption(position);
            if(onSelectItemListenerOutside!=null) {
                onSelectItemListenerOutside.OnSelectItem(itemValue, position);
            }
        }
    };

    public interface OnSelectItemListener {
        public void OnSelectItem(String itemValue, int position);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String getSelectorText() {
        return value;
    }

    public OptionsView(Context context) {
        super(context);
        init(null);
    }

    public OptionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public OptionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void setSelectorList(String[] list, int defaultSelected) {
        selectorList = list;
        selectedIndex = defaultSelected;
        if(defaultSelected >=0 && defaultSelected < list.length) {
            value = list[defaultSelected];
        } else {
            value = null;
        }
        showOptionsList();
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
        } else {
            selectedIndex = index;
        }
        updateSelectOption(selectedIndex);
    }

    public void setSelectorTitle(String text) {
        title = text;
        if(textTitle!=null) {
            textTitle.setText(title);
        }
    }

    public void setOnSelectItemListener(OnSelectItemListener l) {
        onSelectItemListenerOutside = l;
    }

    private void init(AttributeSet attrs) {

        //get Strings array from xml.
        TypedArray ta = null;
        if(attrs!=null)
            ta = getContext().obtainStyledAttributes(attrs, R.styleable.OptionsView);

        if (ta != null) {
            CharSequence[] strings = ta.getTextArray(R.styleable.OptionsView_optionsArray);
            if(strings!=null && strings.length>0) {
                selectorList = new String[strings.length];
                for(int i=0; i< strings.length; i++) {
                    selectorList[i] = strings[i].toString();
                }
            }
            CharSequence s = ta.getText(R.styleable.OptionsView_title);
            if(s!=null) {
                title = s.toString();
            }
            ta.recycle();
        }

        //add view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = (View) inflater.inflate(R.layout.options_view, null);
        textTitle = (TextView) view.findViewById(R.id.textTitle);
        optionsContainer = (LinearLayout) view.findViewById(R.id.optionContainer);
        setSelectorTitle(title);
        addView(view);
        showOptionsList();
    }

    protected void showOptionsList() {
        optionsContainer.removeAllViews();
        optionItemView = new View[selectorList.length];
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for(int i=0; i< selectorList.length; i++) {
            View view = (View) inflater.inflate(R.layout.list_radio_item, null);
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(selectorList[i]);
            optionItemView[i] = view;
            final int pos = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    onSelectItemListenerInteral.OnSelectItem(selectorList[pos], pos);
                }
            });
            if(i==selectorList.length-1) {
                View divider = view.findViewById(R.id.divider);
                divider.setVisibility(View.GONE);
            }
            optionsContainer.addView(view);
        }
        updateSelectOption(selectedIndex);
    }

    protected void updateSelectOption(int selected) {
        selectedIndex = selected;
        if(optionItemView==null) {
            return;
        }

        for(int i=0; i< selectorList.length; i++) {
            ImageView imageV = (ImageView) optionItemView[i].findViewById(R.id.imageView);
            if(i==selectedIndex) {
                imageV.setImageResource(R.mipmap.radio_on);
            } else {
                imageV.setImageResource(R.mipmap.radio_off);
            }
        }
    }

}
