package com.clubank.util;

import com.clubank.pos.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.View;

public class MListDialog {
	private Context context;
	private OnSelectedListener onSelectedListener;

	public MListDialog(Context context) {
		this.context = context;
	}

	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;
	}

	public void show(final View view, int resIdTitle, String[] captions,
			final boolean[] selected) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		if (resIdTitle > 0) {
			b.setTitle(resIdTitle);
		}
		b.setMultiChoiceItems(captions, selected,
				new OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean val) {
						selected[which] = val;
					}
				});
		b.setPositiveButton(R.string.lbl_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int clicked) {
						switch (clicked) {
						case DialogInterface.BUTTON_POSITIVE:
							if (onSelectedListener != null) {
								onSelectedListener.onSelected(view, selected);
							}
							break;
						}
					}
				}).setNegativeButton(R.string.btn_cancel, null);
		Dialog d = b.create();
		d.show();
	}

	public interface OnSelectedListener {
		void onSelected(View view, boolean[] selected);
	}
}
