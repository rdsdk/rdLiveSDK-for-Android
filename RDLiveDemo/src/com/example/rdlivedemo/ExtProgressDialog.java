package com.example.rdlivedemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author abreal
 * 
 */
public class ExtProgressDialog extends Dialog {

	private TextView m_tvMessage;
	private ImageView m_pwProgress;
	private String m_strMessage;

	public ExtProgressDialog(Context context) {
		super(context, R.style.dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.progress_dialog_live, null);
		m_tvMessage = (TextView) view.findViewById(R.id.tvMessage);
		m_pwProgress = (ImageView) view.findViewById(R.id.pbProgress);
		setMessage(m_strMessage);
		setContentView(view);

		super.onCreate(savedInstanceState);
		LayoutParams lp = getWindow().getAttributes();
		lp.width=LayoutParams.MATCH_PARENT;
		lp.height=LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.CENTER;
		this.onWindowAttributesChanged(lp);

	}

	int[] drawables;

	private void initStep() {
		drawables = new int[] { R.drawable.loading_0, R.drawable.loading_1,
				R.drawable.loading_2, R.drawable.loading_3 };
		mhandler.sendEmptyMessage(15);

	}

	@Override
	protected void onStart() {
		super.onStart();
		initStep();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mhandler.removeCallbacks(runnable);

	}

	private int step = 0;
	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {

			m_pwProgress.setImageResource(drawables[step]);

			step++;
			if (step >= drawables.length) {
				step = 0;
			}
			mhandler.postDelayed(runnable, 500);

		};
	};

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {

			mhandler.sendEmptyMessage(15);

		}
	};

	public void setMessage(String strMessage) {
		m_strMessage = strMessage;
		if (null != m_tvMessage) {
			m_tvMessage.setText(strMessage);
			m_tvMessage.setVisibility(TextUtils.isEmpty(strMessage) ? View.GONE
					: View.VISIBLE);
		}
	}

}
