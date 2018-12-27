package com.clubank.util;

import android.app.Dialog;
import android.app.TimePickerDialog;

import com.clubank.consumer.BaseActivity;


public class MyTimeDialog {

	private TimePickerDialog.OnTimeSetListener OnTimeSetListener;
	private BaseActivity ba;

	public MyTimeDialog(BaseActivity ba) {
		this.ba = ba;
	}

	public void setOnTimeSetListener(
			TimePickerDialog.OnTimeSetListener onTimeSetListener) {
		this.OnTimeSetListener = onTimeSetListener;
	}

	public void show(int hour, int minute) {
		Dialog d = new TimePickerDialog(ba, OnTimeSetListener, hour, minute,
				true);
		d.show();
	}

}
