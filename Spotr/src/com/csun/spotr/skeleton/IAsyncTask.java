package com.csun.spotr.skeleton;

import android.app.Activity;

public interface IAsyncTask<T> {
	public void attach(T a);
	public void detach();
}
