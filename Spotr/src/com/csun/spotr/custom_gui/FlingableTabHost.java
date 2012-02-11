package com.csun.spotr.custom_gui;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;

public class FlingableTabHost extends TabHost {
    GestureDetector gestureDetector;

	public FlingableTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);

		setBackgroundColor(android.R.color.transparent);
		
		final int minScaledFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
		gestureDetector = new GestureDetector(
				new GestureDetector.SimpleOnGestureListener() {
					private int constrain(int amount, int low, int high) {
				        return amount < low ? low : (amount > high ? high : amount);
					}
					
					@Override
					public boolean onFling(
							final MotionEvent e1,
							final MotionEvent e2, 
							final float velocityX,
							final float velocityY) {

						final int tabCount = getTabWidget().getTabCount();
						final int currentTab = getCurrentTab();
						Log.e("FlingableTabHost", "velocityX: " + velocityX + " velocityY: " + velocityY + "minScaledFlingVelocity: " + minScaledFlingVelocity);
						if (Math.abs(velocityX) > minScaledFlingVelocity
								&& Math.abs(velocityY) < minScaledFlingVelocity * 10) {

							final boolean right = velocityX < 0;
							final int newTab = constrain(currentTab
									+ (right ? 1 : -1), 0, tabCount - 1);
							if (newTab != currentTab) {
								// Somewhat hacky, depends on current
								// implementation of TabHost:
								// http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;
								// f=core/java/android/widget/TabHost.java
								final View currentView = getCurrentView();
								setCurrentTab(newTab);
								final View newView = getCurrentView();

								newView.startAnimation(right ? rightInAnimation() : leftInAnimation());
								currentView.startAnimation(right ? leftOutAnimation() : rightOutAnimation());
							}
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				}
		);
	}
	
    @Override
	public boolean onInterceptTouchEvent(final MotionEvent ev) {
		if (gestureDetector.onTouchEvent(ev))
			return true;
		return super.onInterceptTouchEvent(ev);
	}
		
	private Animation rightInAnimation() {
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}
	
	private Animation rightOutAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	private Animation leftInAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	private Animation leftOutAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}
}
