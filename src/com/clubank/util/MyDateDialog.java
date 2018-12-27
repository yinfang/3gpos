package com.clubank.util;

import android.app.DatePickerDialog;
import android.app.Dialog;

import com.clubank.consumer.BaseActivity;

public class MyDateDialog {

	private DatePickerDialog.OnDateSetListener onDateSetListener;
	private BaseActivity ba;

	public MyDateDialog(BaseActivity ba) {
		this.ba = ba;
	}

	public void setOnDateSetListener(
			DatePickerDialog.OnDateSetListener onDateSetListener) {
		this.onDateSetListener = onDateSetListener;
	}

	public void show(int year, int month, int day) {
		Dialog d = new DatePickerDialog(ba, onDateSetListener, year, month, day);
		d.show();
	}

}
