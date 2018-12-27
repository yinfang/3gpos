package com.clubank.consumer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import com.clubank.domain.C;
import com.clubank.pos.R;

public class SplashActivity extends BaseActivity {

	private Thread mSplashThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		mSplashThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						wait(2500);
					}
				} catch (InterruptedException ex) {
				}
				C.wsUrl = settings.getString("wsUrl", C.wsUrl);				
				Intent it = new Intent(getApplicationContext(),
						LoginActivity.class);
				it.putExtra("title", getString(R.string.lbl_SystemLogin));
				it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(it);
				finish();
			}
		};
		mSplashThread.start();
	}

	/**
	 * Processes splash screen touch events
	 */
	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		if (evt.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (mSplashThread) {
				mSplashThread.notifyAll();
			}
		}
		return true;
	}
}
