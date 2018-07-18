package com.jinxin.cloudmusic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by XTER on 2017/6/5.
 */
public class DemoGridView extends GridView {
	public DemoGridView(Context context) {
		super(context);
	}

	public DemoGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DemoGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

}
