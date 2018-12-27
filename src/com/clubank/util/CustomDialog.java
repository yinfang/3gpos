package com.clubank.util;

import com.clubank.pos.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialog {
	private Context context;
	private OKProcessor processor;
	private Initializer initializer;

	public CustomDialog(Context context) {
		this.context = context;
	}

	public void setOKProcessor(OKProcessor processor) {
		this.processor = processor;
	}

	public void setInitializer(Initializer initializer) {
		this.initializer = initializer;
	}

	public interface OKProcessor {
		void process(Dialog dialog);
	}

	public interface Initializer {
		void init(View view);
	}

	@SuppressWarnings("deprecation")
	private Dialog getDialog() {
		final Dialog d = new Dialog(context, R.style.CustomDialog);
		d.setContentView(R.layout.custom_dialog);
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		WindowManager.LayoutParams lp = d.getWindow().getAttributes();
		lp.width = display.getWidth();
		return d;
	}

	public Dialog show(CharSequence title, int resView, int resIdIcon) {

		LayoutInflater factory = LayoutInflater.from(context);
		final View v = factory.inflate(resView, null);
		final Dialog d = getDialog();
		if (resIdIcon > 0) {
			ImageView icon = (ImageView) d.findViewById(R.id.imageView1);
			icon.setImageResource(resIdIcon);
		}
		TextView tv = (TextView) d.findViewById(R.id.title);
		tv.setText(title);
		LinearLayout body = (LinearLayout) d.findViewById(R.id.body);
		body.addView(v);
		if (initializer != null) {
			initializer.init(v);
		}
		Button btn = (Button) d.findViewById(R.id.btn_ok);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (processor != null) {
					processor.process(d);
					d.dismiss();
				}
			}
		});
		Button btn2 = (Button) d.findViewById(R.id.btn_cancel);
		btn2.setVisibility(View.VISIBLE);
		btn2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				d.dismiss();
			}
		});

		d.show();
		return d;
	}
}
