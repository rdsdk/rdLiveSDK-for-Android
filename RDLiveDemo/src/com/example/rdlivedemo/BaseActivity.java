package com.example.rdlivedemo;

import android.app.Activity;

public class BaseActivity extends Activity {

	protected String TAG = getClass().getSimpleName();
	// Activity是否在前台
	protected boolean isRunning = false;

	@Override
	protected void onStart() {
		super.onStart();
		isRunning = true;

	}

	@Override
	protected void onStop() {
		super.onStop();
		isRunning = false;
	}

}
