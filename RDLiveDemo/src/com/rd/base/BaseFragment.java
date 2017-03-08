package com.rd.base;

import android.app.Fragment;
import android.os.Bundle;

public class BaseFragment extends Fragment {
	protected String TAG = BaseFragment.class.toString();
	protected boolean isRunning = true;

	public BaseFragment() {
		super();
		isRunning = true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isRunning = true;

	}

	@Override
	public void onStart() {
		super.onStart();
		isRunning = true;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	/**
	 * 响应返回键
	 * 
	 * @return 1 :可以继续向上返回;其他code:拦截返回键功能
	 */
	protected int onBackPressed() {
		getFragmentManager().popBackStack();
		return 1;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		isRunning = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
