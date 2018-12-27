package com.clubank.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.clubank.consumer.BaseActivity;
import com.clubank.pos.R;
import com.clubank.domain.C;

public class UI {

	private static Display screen;

	public static void showError(Context context, int resId) {
		showError(context, context.getText(resId));
	}

	@SuppressWarnings("deprecation")
	public static void showError(Context context, CharSequence msg) {
		final Dialog d = new Dialog(context, R.style.CustomDialog);
		d.setContentView(R.layout.dialog_message);
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		WindowManager.LayoutParams lp = d.getWindow().getAttributes();
		lp.width = display.getWidth();
		TextView title = (TextView) d.findViewById(R.id.title);
		title.setText(R.string.msg_error);
		TextView message = (TextView) d.findViewById(R.id.message);
		message.setText(msg);
		Button b = (Button) d.findViewById(R.id.btn_ok);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				d.dismiss();
			}
		});
		d.show();
	}

	public static int getId(Context context, String resCode, String type) {
		int resId = context.getResources().getIdentifier(resCode, type,
				context.getPackageName());
		return resId;
	}

	public static View getView(Activity a, String resCode, String type) {
		return a.findViewById(getId(a, resCode, type));
	}

	public static CharSequence getText(Context context, String resCode) {
		int resId = getId(context, resCode, "string");
		if (resId == 0) {
			return null;
		}
		return context.getText(resId);
	}

	public static void showInfo(Context context, int resId) {
		showInfo(context, resId, 0);
	}

	public static void showInfo(Context context, int resId, int type) {
		showInfo(context, context.getText(resId), type, null);
	}

	public static void showInfo(Context context, CharSequence msg, int type) {
		showInfo(context, msg, type, null);
	}

	@SuppressWarnings("deprecation")
	public static void showInfo(Context context, CharSequence msg,
			final int type, final Object tag) {
		final BaseActivity a = (BaseActivity) context;
		final Dialog d = new Dialog(context, R.style.CustomDialog);
		d.setContentView(R.layout.dialog_message);
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		WindowManager.LayoutParams lp = d.getWindow().getAttributes();
		lp.width = display.getWidth();

		TextView title = (TextView) d.findViewById(R.id.title);
		title.setText(R.string.msg_info);
		TextView message = (TextView) d.findViewById(R.id.message);
		message.setText(msg);
		Button btn = (Button) d.findViewById(R.id.btn_ok);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				d.dismiss();
				a.processDialogOK(type, tag);
			}
		});
		d.show();
	}

	@SuppressWarnings("deprecation")
	public static void showOKCancel(BaseActivity context, final int type,
			CharSequence message, CharSequence title, final Object tag) {
		final BaseActivity a = (BaseActivity) context;
		final Dialog d = new Dialog(context, R.style.CustomDialog);
		d.setContentView(R.layout.dialog_message);
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		WindowManager.LayoutParams lp = d.getWindow().getAttributes();
		lp.width = display.getWidth();

		TextView tv1 = (TextView) d.findViewById(R.id.title);
		tv1.setText(R.string.lbl_confirm);
		TextView tv2 = (TextView) d.findViewById(R.id.message);
		tv2.setText(message);
		Button btn1 = (Button) d.findViewById(R.id.btn_ok);
		btn1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				d.dismiss();
				a.processDialogOK(type, tag);
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
	}

	public static void showOKCancel(BaseActivity context, int type,
			int msgResId, int titleResId) {
		showOKCancel(context, type, msgResId, titleResId, null);
	}

	public static void showOKCancel(BaseActivity context, int type,
			int msgResId, int titleResId, Object tag) {
		showOKCancel(context, type, context.getText(msgResId),
				context.getText(titleResId), tag);
	}

	/**
	 * �����Դ�Ż�ȡͼ������ֽ�����
	 * 
	 * @param context
	 * @param resId
	 *            ��Դ��
	 * @return
	 */
	public static byte[] getBytes(Context context, int resId) {
		Resources resources = context.getResources();
		Bitmap bmp = BitmapFactory.decodeResource(resources, resId);
		return getBytes(bmp);
	}

	/**
	 * ���ͼ��Bitmap����ֽ�����??
	 * 
	 * @param bmp
	 * @return
	 */
	public static byte[] getBytes(Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		/* ע��˴�������JPEG��ʽ�����ѹ���ʲ���ʹ�õ�����ݾ�����С */
		bmp.compress(Bitmap.CompressFormat.JPEG, C.compress, stream);
		byte[] b = stream.toByteArray();
		return b;
	}

	public static void takePicture(Activity a, String file, int requestType) {
		Uri uri = Uri.fromFile(new File(file));
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		a.startActivityForResult(intent, requestType);
	}

	public static void choosePicture(Activity a, int requestType) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		a.startActivityForResult(intent, requestType);
	}

	public static void cropPicture(final Activity a, final int requestType,
			final Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		List<ResolveInfo> list = a.getPackageManager().queryIntentActivities(
				intent, 0);
		int size = list.size();
		if (size == 0) {
			Toast.makeText(a, "Can not find image crop app", Toast.LENGTH_SHORT)
					.show();
			return;
		} else {
			intent.setData(uri);
			intent.putExtra("outputX", 350);
			intent.putExtra("outputY", 350);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);
			Intent i = new Intent(intent);
			ResolveInfo res = list.get(0);
			i.setComponent(new ComponentName(res.activityInfo.packageName,
					res.activityInfo.name));
			a.startActivityForResult(i, requestType);
		}
	}

	public static int getPixel(Context context, float dps) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dps * scale + 0.5f);
	}

	public static String getEText(Activity a, String resCode) {
		int id = getId(a, resCode, "id");

		View v = a.findViewById(id);
		CharSequence c = "";
		if (v instanceof TextView) {
			c = ((TextView) v).getText();
		} else if (v instanceof EditText) {
			c = ((EditText) v).getText();
		}
		return c.toString();
	}

	public static void setEText(Activity a, String resCode, CharSequence value) {
		int id = getId(a, resCode, "id");
		if (id == 0 || value == null) {
			return;
		}
		View v = a.findViewById(id);
		if (v instanceof TextView) {
			((TextView) v).setText(value.toString().trim());
		} else if (v instanceof EditText) {
			((EditText) v).setText(value.toString().trim());
		}
	}

	public static int toPixel(Context context, float dp) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	/**
	 * * This method converts device specific pixels to device independent
	 * pixels. * * @param px A value in px (pixels) unit. Which we need to
	 * convert into db * @param context Context to get resources and device
	 * specific display metrics * @return A float value to represent db
	 * equivalent to px value
	 */
	public static int toDp(Context context, float px) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return (int) dp;
	}

	public static Bitmap getBitmapFromURL(String url) {
		InputStream is = UI.getFromURL(url); // ͨ��url�õ�ͼƬ
		if (is == null) {
			return null;
		}
		return BitmapFactory.decodeStream(is);
	}

	private static InputStream getFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3 * 1000);
			conn.setReadTimeout(60 * 1000);
			conn.setDoInput(true);
			conn.connect();
			return conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String setServerType(int type) {
		if (type == C.SERVER_INTRANET) {
			C.imageUrlMember = C.IMAGE_URL_MEMBER_INTRANET;
			C.baseConfUrl = C.BASE_CONF_URL_INTRANET;
			C.baseUrl = C.BASE_URL_INTRANET;
		} else if (type == C.SERVER_INTERNET) {
			C.imageUrlMember = C.IMAGE_URL_MEMBER_INTERNET;
			C.baseConfUrl = C.BASE_CONF_URL_INTERNET;
			C.baseUrl = C.BASE_URL_INTERNET;
		}
		C.wsUrl = C.baseUrl + "POSService.asmx";
		C.wsConfUrl = C.baseConfUrl + "posServer/PosService.asmx";
		return C.wsUrl;
	}

	public static boolean validateEmail(String email) {
		return email != null
				&& (!email.equals(""))
				&& email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,5})$");
	}

	public static boolean validatePhoneAndFax(String in) {
		// ("^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?$");
		// 第1组：区号，以0开头，只允许输入0-9的数字，长度为2-3位，后面连接-号.
		// 第2组：固话，以2-9开头，只允许输入0-9的数字，长度为6-7位。
		// 第3组：分机号，前面连接-号,以0-9开头，长度为1-4位。
		return in != null && (!in.equals(""))
				&& in.matches("^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,15})");
	}

	public static boolean validateMobile(String no) {
		return no != null && no.startsWith("1") && no.length() == 11;
	}

	/**
	 * ����ʺ���Ļ��BMP����ֹ�ڴ����
	 * 
	 * @param resId
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap getBitmap(Context context, int resId) {
		if (screen == null) {
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			screen = wm.getDefaultDisplay();
		}
		BitmapFactory.Options opt = new BitmapFactory.Options();
		// ���isjustdecodebounds����Ҫ
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), resId, opt);

		// ��ȡ�����ͼƬ��ԭʼ��Ⱥ͸߶�
		int picWidth = opt.outWidth;
		int picHeight = opt.outHeight;

		// ��ȡ���Ŀ�Ⱥ͸߶�
		int screenWidth = screen.getWidth();
		int screenHeight = screen.getHeight();

		// isSampleSize�Ǳ�ʾ��ͼƬ�����ų̶ȣ�����ֵΪ2ͼƬ�Ŀ�Ⱥ͸߶ȶ���Ϊ��ǰ��1/2
		opt.inSampleSize = 1;
		// ������Ĵ�С��ͼƬ��С��������ű���
		if (picWidth > picHeight) {
			if (picWidth > screenWidth)
				opt.inSampleSize = picWidth / screenWidth;
		} else {
			if (picHeight > screenHeight)

				opt.inSampleSize = picHeight / screenHeight;
		}

		// �������������һ�������صģ����������˵�bitmap
		opt.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
				resId, opt);
		return bmp;
	}

	public static Bitmap resizeBitmap(Bitmap bitmap, int largeSize) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int h1 = largeSize, w1 = largeSize;
		if (w > h) { // 找出最长的那边
			h1 = (int) (h * 1.0000 / w * largeSize);// 计算另一边的长度
		} else {
			w1 = (int) (w * 1.0000 / h * largeSize);
		}
		Bitmap resultBitmap = Bitmap.createScaledBitmap(bitmap, w1, h1, true);
		return resultBitmap;
	}

	public static String handleString(String s) {
		String handleExpre = "</?[^<]+>\\s*|\t|\r|&nbsp;";
		return s.replaceAll(handleExpre, "");
	}

	// 判断网络是否连接 方式1
	public static boolean isConnected(Context con) {
		ConnectivityManager conMan = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMan != null) {
			NetworkInfo[] info = conMan.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	// 判断网络是否连接 方式2
	public static boolean isOpenNetwork(Context con) {
		ConnectivityManager connManager = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

}
