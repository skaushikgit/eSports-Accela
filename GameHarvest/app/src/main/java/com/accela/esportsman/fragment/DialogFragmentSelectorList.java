package com.accela.esportsman.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.accela.esportsman.R;
import com.accela.esportsman.view.SelectorView;


/**
 * Created by jzhong on 9/1/15.
 */
public class DialogFragmentSelectorList extends android.support.v4.app.DialogFragment {

    String[] selectorList;
    int selectedIndex;
    SelectorListAdapter adapter;
    protected SelectorView.OnSelectItemListener onSelectItemListener;
    String title;

    public void setSelectorList(String[] list, int defaultSelected) {
        selectorList = list;
        selectedIndex = defaultSelected;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setOnSelectItemListener(SelectorView.OnSelectItemListener l) {
        onSelectItemListener = l;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.SelectorListDialogAnimation;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getDialog().getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width =displayMetrics.widthPixels;
            int height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setLayout(width, height);
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dialog_selector_list, container, false);
        if(adapter == null) {
            adapter = new SelectorListAdapter();
        }
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        TextView textTitle = (TextView) view.findViewById(R.id.textTitle);
        textTitle.setText(title);
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
    }



    protected class SelectorListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return selectorList != null ? selectorList.length:0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.list_radio_item, null);
            }
            //set command.
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(selectorList[position]);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            if(selectedIndex == position) {
                imageView.setImageResource(R.mipmap.radio_on);
            } else {
                imageView.setImageResource(R.mipmap.radio_off);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedIndex = position;
                    adapter.notifyDataSetChanged();
                    if(onSelectItemListener!=null) {
                        onSelectItemListener.OnSelectItem(selectorList[position], position);
                    }
                    dismiss();
                }
            });
            return view;
        }


    }

}
