package com.accela.esportsman.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accela.esportsman.R;

import java.util.Calendar;

/**
 * Created by jzhong on 9/1/15.
 */

public class CalendarView extends FrameLayout {


    private Calendar selectDate;
    private Calendar todayDate;

    OnSelectDateListener onSelectDateListener;
    ViewPager viewPager;
    TextView textTitle;
    WeekPageAdapter adapter;

    public interface OnSelectDateListener {
        public void onSelectDate(long milliSecondOfDate);
    }


    public void setSelectDate(long milliSecondOfDate) {
        selectDate.setTimeInMillis(milliSecondOfDate);
        adapter.notifyDataSetChanged();
    }

    public CalendarView(Context context) {
        super(context);
        init(null);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }



    public void setOnSelectDateListener(OnSelectDateListener l) {
        onSelectDateListener = l;
    }

    private void init(AttributeSet attrs) {

        //add view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.calendar_selector_view, null);
        //title
        textTitle = (TextView) view.findViewById(R.id.textTitle);
        //add week header
        LinearLayout weekHeaderContainer = (LinearLayout) view.findViewById(R.id.weekHeaderContainer);
        String[] weekHeader = {
                "S", "M", "T", "W", "T", "F", "S"
        };
        for(int i=0; i< weekHeader.length; i++) {
            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER;
            lp.weight = 1;
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setLayoutParams(lp);
            textView.setText(weekHeader[i]);
            weekHeaderContainer.addView(textView);
        }

        //view pager
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        adapter = new WeekPageAdapter();
        viewPager.setAdapter(adapter);

        //get today calendar
        todayDate = Calendar.getInstance();
        todayDate.setTimeInMillis(System.currentTimeMillis());
        selectDate = Calendar.getInstance();
        selectDate.setTimeInMillis(todayDate.getTimeInMillis());

        //select last view page
        viewPager.setCurrentItem(adapter.getCount()-1);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Calendar tempDate = Calendar.getInstance();
                tempDate.setTimeInMillis(getCalendarPageFirstDayMillis(position));
                //check current select date, if it is in same week of current page, display by select date, otherwise display the month by first day of this week.
                if(tempDate.get(Calendar.YEAR) == selectDate.get(Calendar.YEAR)
                        && tempDate.get(Calendar.WEEK_OF_YEAR) == selectDate.get(Calendar.WEEK_OF_YEAR)) {
                    updateMonthText(selectDate);
                } else {
                    updateMonthText(tempDate);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updateMonthText(selectDate);
        addView(view);
    }


    private void updateMonthText(Calendar date) {
        int[] monthStrings = {
                R.string.Jan,
                R.string.Feb,
                R.string.Mar,
                R.string.Apr,
                R.string.May,
                R.string.Jun,
                R.string.Jul,
                R.string.Aug,
                R.string.Sep,
                R.string.Oct,
                R.string.Nov,
                R.string.Dec
        };

        textTitle.setText(getResources().getString(monthStrings[date.get(Calendar.MONTH)])
                + " " + date.get(Calendar.YEAR));
    }


    long dayMills = 24 * 3600* 1000;
    long weekMillis = 7 * dayMills;
    private long getCalendarPageFirstDayMillis(int position) {
        Calendar firstDayInThisWeek = Calendar.getInstance();
        firstDayInThisWeek.setTimeInMillis(todayDate.getTimeInMillis());
        firstDayInThisWeek.set(Calendar.DAY_OF_WEEK, 1);
        long calendarFirstDayMillis = firstDayInThisWeek.getTimeInMillis() - ((adapter.getCount() - position - 1) * weekMillis);
        return calendarFirstDayMillis;
    }

    private class WeekPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 14; //90 days (7*14 = 98 days allow one week more in case the current day is the first day of week)
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LinearLayout weekContainer = new LinearLayout(getContext());
            weekContainer.setOrientation(LinearLayout.HORIZONTAL);
            float density = getResources().getDisplayMetrics().density;
            Calendar tempDate = Calendar.getInstance();
            long calendarFirstDayMillis = getCalendarPageFirstDayMillis(position);
            for(int i=0; i< 7; i++) {
                FrameLayout dayContainer = new FrameLayout(getContext());
                TextView textView = new TextView(getContext());
                FrameLayout.LayoutParams textLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                textLp.gravity = Gravity.CENTER;
                textLp.setMargins(0, 0, 0, (int) (10 * density));
                textView.setLayoutParams(textLp);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                textView.setGravity(Gravity.CENTER);
                tempDate.setTimeInMillis(calendarFirstDayMillis + i * dayMills);
                if(tempDate.getTimeInMillis() > todayDate.getTimeInMillis()) {
                    textView.setTextColor(getResources().getColor(R.color.mid_gray));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.black));
                }
                textView.setText(String.format("%d", tempDate.get(Calendar.DAY_OF_MONTH)));
                dayContainer.addView(textView);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                lp.gravity = Gravity.CENTER;
                lp.weight = 1;

                if(tempDate.get(Calendar.YEAR) == selectDate.get(Calendar.YEAR)
                        && tempDate.get(Calendar.DAY_OF_YEAR) == selectDate.get(Calendar.DAY_OF_YEAR)) {
                    dayContainer.setBackgroundResource(R.mipmap.icon_date_tab);
                }

                final long millisOfDay = tempDate.getTimeInMillis();
                dayContainer.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(millisOfDay > todayDate.getTimeInMillis()) {
                            //can't select the date after today
                            return;
                        }
                        selectDate.setTimeInMillis(millisOfDay);
                        updateMonthText(selectDate);
                        if(onSelectDateListener!=null) {
                            onSelectDateListener.onSelectDate(millisOfDay);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                weekContainer.addView(dayContainer, lp);
            }
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(weekContainer, lp);
            return weekContainer;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            // must add this. force refresh when call notifyDataSetChanged
            return POSITION_NONE;
        }

        @Override
        public float getPageWidth(int position) {
            return 1f;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
