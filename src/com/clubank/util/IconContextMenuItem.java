package com.clubank.util;

import android.graphics.drawable.Drawable;

public class IconContextMenuItem {

	public int resIdText;
	public int resIdImage;
	public Drawable drawable;
	public int actionType;

	public IconContextMenuItem(int resIdText, int resIdImage, int actionType) {
		this.resIdText = resIdText;
		this.resIdImage = resIdImage;
		this.actionType = actionType;
	}

	public IconContextMenuItem(int resIdText, Drawable drawable, int actionType) {
		this.resIdText = resIdText;
		this.drawable = drawable;
		this.actionType = actionType;
	}

}
