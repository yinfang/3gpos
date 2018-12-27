package com.clubank.util;

import com.clubank.pos.R;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

public class WaitingDialog extends Dialog {

	public WaitingDialog(Context context) {
		super(context, R.style.WaitingDialog);
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.width = UI.getPixel(context, 50);
		lp.height = UI.getPixel(context, 50);
		addContentView(new ProgressBar(context), lp);
	}
}
