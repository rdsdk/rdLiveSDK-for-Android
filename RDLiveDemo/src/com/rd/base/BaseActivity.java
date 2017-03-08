package com.rd.base;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

	protected boolean isRunning = false;
	protected String TAG = this.toString();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isRunning = true;
	}

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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isRunning = false;
	}

}
