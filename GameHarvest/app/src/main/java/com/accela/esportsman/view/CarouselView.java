package com.accela.esportsman.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jzhong on 8/19/15.
 */
public class CarouselView extends ViewPager {

    public static abstract class CarouselViewAdapter extends PagerAdapter {
        public abstract View getViewByPosition(int position);
    }

    View cur, next, prev, nextnext;
    private int lastPage = 0;
    private boolean swipedLeft=false;

    private static float minAlpha=0.6f;
    private static float maxAlpha=1f;
    private static float minDegree=50.0f;
    private final static float BIG_SCALE = 1.0f;
    private final static float SMALL_SCALE = 0.75f;
    private final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    CarouselViewAdapter adapter;

    public CarouselView(Context context) {
        super(context);
        initView();
    }

    public CarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if(!(adapter instanceof CarouselViewAdapter)) {
            throw new RuntimeException("Please set subclass of CarouselViewAdapter");
        }
        this.adapter = (CarouselViewAdapter) adapter;
        super.setAdapter(adapter);
    }

    protected void initView() {
        this.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*
                 * to get finger swipe direction
                 */
                if (lastPage <= position) {
                    swipedLeft = true;
                } else if (lastPage > position) {
                    swipedLeft = false;
                }
                lastPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        this.setOffscreenPageLimit(3);
        int pageMargin = (int) (getResources().getDisplayMetrics().widthPixels * 0.45);
        this.setPageMargin( - pageMargin);
        this.setClipToPadding(false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int offsetPixels) {

        if (positionOffset >= 0f && positionOffset <= 1f)
        {
            positionOffset=positionOffset*positionOffset;

            cur = adapter.getViewByPosition(position);
            next = adapter.getViewByPosition(position + 1);
            prev = adapter.getViewByPosition(position - 1);
            nextnext=adapter.getViewByPosition(position +2);


            if(nextnext!=null)
            {
                nextnext.setAlpha(minAlpha);
                nextnext.setRotationY( -minDegree);
            }
            if(cur!=null)
            {
                cur.setAlpha(maxAlpha-0.5f*positionOffset);

                float scale = BIG_SCALE
                        - DIFF_SCALE * positionOffset;
                cur.setScaleX(scale);
                cur.setScaleY(scale);
                cur.setRotationY(0);
            }

            if(next!=null)
            {
                next.setAlpha(minAlpha+0.5f*positionOffset);

                float scale = SMALL_SCALE
                        + DIFF_SCALE * positionOffset;
                next.setScaleX(scale);
                next.setScaleY(scale);
                next.setRotationY(-minDegree);
            }
            if(prev!=null)
            {
                prev.setAlpha(minAlpha+0.5f*positionOffset);
                prev.setRotationY(minDegree);
            }


			//To animate it properly we must understand swipe direction this code adjusts the rotation according to direction.

            if(swipedLeft)
            {
                if(next!=null)
                    next.setRotationY( -minDegree+minDegree*positionOffset);
                if(cur!=null)
                    cur.setRotationY( 0+minDegree*positionOffset);
            }
            else
            {
                if(next!=null)
                    next.setRotationY(-minDegree+minDegree*positionOffset);
                if(cur!=null)
                {
                    cur.setRotationY(0+minDegree*positionOffset);
                }
            }
        }
        if(positionOffset>=1f && cur != null)
        {
            cur.setAlpha( maxAlpha);
        }

        super.onPageScrolled(position, positionOffset, offsetPixels);
    }



}
