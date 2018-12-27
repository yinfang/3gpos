package com.clubank.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class ListDialog {
	private Context context;
	private OnSelectedListener onSelectedListener;

	public ListDialog(Context context) {
		this.context = context;
	}

	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;
	}

	public void show(final View view, int resIdTitle, String[] captions) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		if (resIdTitle > 0) {
			b.setTitle(resIdTitle);
		}
		b.setItems(captions, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (onSelectedListener != null) {
					onSelectedListener.onSelected(view, which);
				}
				dialog.dismiss();
			}
		});
		Dialog dialog = b.create();
		dialog.show();
	}

	public interface OnSelectedListener {
		void onSelected(View view, int index);
	}
}
