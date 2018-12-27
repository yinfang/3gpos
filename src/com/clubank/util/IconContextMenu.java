package com.clubank.util;

import java.util.ArrayList;
import java.util.List;

import com.clubank.pos.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IconContextMenu {

	private IconMenuAdapter ima;
	private Activity context;
	private IconContextMenuOnClickListener clickHandler;
	public Object tag;

	public IconContextMenu(Activity parent, Object tag) {
		this.context = parent;
		this.tag = tag;
		ima = new IconMenuAdapter(context, R.layout.menu_item,
				new ArrayList<IconContextMenuItem>());
	}

	public void add(int textResourceId, int imageResourceId, int actionType) {
		ima.add(new IconContextMenuItem(textResourceId, imageResourceId,
				actionType));
	}

	public void add(int textResourceId, Drawable drawable, int actionType) {
		ima.add(new IconContextMenuItem(textResourceId, drawable, actionType));
	}

	public void setOnClickListener(IconContextMenuOnClickListener listener) {
		clickHandler = listener;
	}

	public Dialog createMenu(String menuItitle) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(menuItitle);
		builder.setAdapter(ima, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				IconContextMenuItem item = (IconContextMenuItem) ima.getItem(i);

				if (clickHandler != null) {
					clickHandler.onClick(item.actionType);
				}
			}
		});
		// builder.setInverseBackgroundForced(true);
		AlertDialog dialog = builder.create();
		return dialog;
	}

	protected class IconMenuAdapter extends ArrayAdapter<IconContextMenuItem> {
		private Context context = null;
		private int resId;

		public IconMenuAdapter(Context context, int resId,
				List<IconContextMenuItem> data) {
			super(context, resId, data);
			this.context = context;
			this.resId = resId;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(resId, null);
			}
			TextView tv = (TextView) v.findViewById(R.id.menu_name);
			ImageView iv = (ImageView) v.findViewById(R.id.imageView1);
			IconContextMenuItem o = getItem(position);
			tv.setText(context.getText(o.resIdText));
			if (o.drawable != null) {
				iv.setImageDrawable(o.drawable);
			} else if (o.resIdImage > 0) {
				iv.setImageResource(o.resIdImage);
			}
			return v;
		}
	}
}