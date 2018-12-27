package com.clubank.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.WrapperListAdapter;

import com.clubank.domain.C;
import com.clubank.domain.Result;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.CustomDialog;
import com.clubank.util.CustomDialog.Initializer;
import com.clubank.util.CustomDialog.OKProcessor;
import com.clubank.util.ImageCache;
import com.clubank.util.ImageFetcher;
import com.clubank.util.ListDialog;
import com.clubank.util.MListDialog;
import com.clubank.util.MyData;
import com.clubank.util.MyDateDialog;
import com.clubank.util.MyRow;
import com.clubank.util.MyTimeDialog;
import com.clubank.util.PostAsyncTask;
import com.clubank.util.UI;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("NewApi")
public class BaseActivity extends FragmentActivity implements OnScrollListener,
		PostAsyncTask {

	public View footerView;
	private int startIndex;
	private Properties properties = new Properties();
	protected ArrayAdapter<MyRow> adapter;
	private ListView listView;
	protected Display screen;
	protected SharedPreferences settings;
	public ImageFetcher mImageFetcher;// 带图片的列表
	protected String[] menus = new String[0];
	private MediaPlayer mediaPlayer = null;
	public boolean playerWhich;

	protected void setAdapter(ListView listView, ArrayAdapter<MyRow> adapter) {
		listView.setAdapter(adapter);
		this.adapter = adapter;
	}

	protected ArrayAdapter<MyRow> getAdapter() {
		return adapter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences(C.APP_ID, MODE_PRIVATE);
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		screen = wm.getDefaultDisplay();
		Resources resources = getResources();
		AssetManager assetManager = resources.getAssets();
		try {
			InputStream inputStream = assetManager.open("config.properties");
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		initImageFetcher();
	}

	protected void openIntent(Context conext, Class<?> cls, CharSequence title) {
		Intent intent = new Intent(this, cls);
		intent.putExtra("title", title.toString().replaceAll("\n", " "));
		startActivity(intent);
	}

	protected void openIntent(Context context, Class<?> cls, int resId) {
		openIntent(context, cls, getText(resId));
	}

	/*
	 * public void home(View view) { Intent intent = new Intent(this,
	 * Main3Activity.class); intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
	 * Intent.FLAG_ACTIVITY_CLEAR_TOP); // 注意本行的FLAG设置 startActivity(intent); }
	 */

	/**
	 * 初始化异步图片装载器，并附带缓存功能
	 */
	protected void initImageFetcher() {
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		final int height = displayMetrics.heightPixels;
		final int width = displayMetrics.widthPixels;

		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				this, C.IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(this, 0.25f); // Set memory cache to
															// 25% of mem class
		final int longest = (height > width ? height : width) / 2;

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(this, longest);
		mImageFetcher.setLoadingImage(R.drawable.icon_empty);
		mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
		mImageFetcher.setImageFadeIn(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Bundle bundle = getIntent().getExtras();
		String title = null;
		if (bundle != null) {
			title = bundle.getString("title");
		}
		if (title != null) {
			setHeaderTitle(title);
		}
		if (mImageFetcher != null) {
			mImageFetcher.setExitTasksEarly(false);
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	protected void setHeaderTitle(CharSequence title) {
		View tv = (View) findViewById(R.id.header_title);
		if (tv != null && title != null && tv instanceof TextView) {
			((TextView) tv).setText(title);
		}
	}

	protected void setHeaderTitle(int resId) {
		setHeaderTitle(getText(resId));
	}

	public void processDialogOK(int type, Object tag) {
	}

	// public void onPostExecute(String action, Result result) {
	// if (result.code != C.RESULT_SUCCESS) {
	// displayFooter(0);
	// }
	// }

	public void onPostExecute(String action, Result result) {
		if (result.code == C.RESULT_SUCCESS && result.obj instanceof MyData) {
			displayFooter(((MyData) result.obj).size());
		}
	}

	protected void setFooter(ListView listView) {
		this.listView = listView;
		LayoutInflater inflater = LayoutInflater.from(this);
		footerView = inflater.inflate(R.layout.data_more, null);
		footerView.setVisibility(View.GONE);
		listView.addFooterView(footerView);
	}

	public void moreData(View view) {
		final TextView footerButton = (TextView) footerView
				.findViewById(R.id.button);
		final LinearLayout footerProgress = (LinearLayout) footerView
				.findViewById(R.id.linearlayout);
		footerButton.setVisibility(View.GONE);
		footerProgress.setVisibility(View.VISIBLE);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				refreshData();
				listView.setSelection(startIndex + 2);
			}
		}, 50);
	}

	protected void loadData() {
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 设置当前屏幕显示的起始index和结束index
		startIndex = firstVisibleItem;
		// this.visibleItemCount = visibleItemCount;

		// if (firstVisibleItem + visibleItemCount >= totalItemCount) {
		// startIndex = totalItemCount - 1;
		// }
	}

	protected void displayFooter(int lastFetched) {
		if (footerView != null) {
			if (lastFetched >= C.cnt) {
				final TextView footerButton = (TextView) footerView
						.findViewById(R.id.button);
				final LinearLayout footerProgress = (LinearLayout) footerView
						.findViewById(R.id.linearlayout);
				footerButton.setVisibility(View.VISIBLE);
				footerProgress.setVisibility(View.GONE);
				footerView.setVisibility(View.VISIBLE);
			} else {
				footerView.setVisibility(View.GONE);
				listView.setFooterDividersEnabled(false);
			}
		}
	}

	protected String getEText(String resCode) {
		return UI.getEText(this, resCode);
	}

	protected void setEText(MyRow row, String resCode) {
		UI.setEText(this, resCode, (String) row.get(resCode));
	}

	protected void setEText(String resCode, CharSequence value) {
		UI.setEText(this, resCode, value);
	}

	public void back(View view) {
		if (validate()) {
			finish();
		}
	}

	protected void setEText(Intent intent, String code) {
		intent.putExtra(code, getEText(code));
	}

	protected void setEText(Bundle bundle, String code) {
		setEText(code, bundle.getString(code));
	}

	@SuppressWarnings("unchecked")
	protected void setScrollListener(ListView listView) {
		adapter = (ArrayAdapter<MyRow>) ((WrapperListAdapter) listView
				.getAdapter()).getWrappedAdapter();
		listView.setOnScrollListener(this);
	}

	protected void setVisibility(String resCode, int visibility) {
		int id = UI.getId(this, resCode, "id");
		View v = findViewById(id);
		if (v != null) {
			v.setVisibility(visibility);
		}
	}

	protected void setVisibility(View view, String resCode, int visibility) {
		int id = UI.getId(this, resCode, "id");
		View v = view.findViewById(id);
		if (v != null) {
			v.setVisibility(visibility);
		}
	}

	protected void setImage(String url, int resId) {
		ImageView iv = (ImageView) findViewById(resId);
		setImage(url, iv);
	}

	protected void setImage(String url, ImageView iv) {
		if (mImageFetcher == null) {
			initImageFetcher();
		}
		mImageFetcher.loadImage(url, iv);
	}

	protected void callPhone(String phone) {
		Intent intent = new Intent(Intent.ACTION_DIAL,
				Uri.parse("tel:" + phone));
		startActivity(intent);
	}

	protected int toggleOption(View view, int containerId) {
		int ret = 0;
		LinearLayout ll = (LinearLayout) findViewById(containerId);
		for (int i = 0; i < ll.getChildCount(); i++) {
			TextView v = (TextView) ll.getChildAt(i);
			int color = getResources().getColor(R.color.my_gray3);
			if (v.equals(view)) {
				v.setBackgroundResource(R.drawable.option_selected);
				color = getResources().getColor(R.color.white);
				ret = i;
			} else {
				v.setBackgroundResource(R.drawable.option_default);
			}
			v.setTextColor(color);
		}
		return ret;
	}

	protected void selectOption(int index, int containerId) {
		LinearLayout ll = (LinearLayout) findViewById(containerId);
		for (int i = 0; i < ll.getChildCount(); i++) {
			TextView v = (TextView) ll.getChildAt(i);
			int color = getResources().getColor(R.color.my_gray3);
			if (i == index) {
				v.setBackgroundResource(R.drawable.option_selected);
				color = getResources().getColor(R.color.white);
			} else {
				v.setBackgroundResource(R.drawable.option_default);
			}
			v.setTextColor(color);
		}
	}

	protected void fillOptions(final int containerId, String[] names,
			int dpWidth, int selectedIndex) {
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout ll = (LinearLayout) findViewById(containerId);
		ll.removeAllViews();
		for (int i = 0; i < names.length; i++) {
			TextView v = (TextView) inflater
					.inflate(R.layout.option_item, null);
			int color = getResources().getColor(R.color.my_gray3);
			v.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					toggleOption(arg0, containerId);
				}
			});
			if (i == selectedIndex) {
				v.setBackgroundResource(R.drawable.option_selected);
				color = getResources().getColor(R.color.white);
			} else {
				color = getResources().getColor(R.color.my_gray3);
				v.setBackgroundResource(R.drawable.option_default);
			}
			v.setTextColor(color);
			v.setText(names[i]);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.width = UI.toPixel(this, dpWidth);
			lp.height = UI.toPixel(this, 35);
			lp.rightMargin = UI.toPixel(this, 8);
			v.setLayoutParams(lp);
			ll.addView(v);
		}
	}

	protected boolean validate() {
		return true;
	}

	protected void logout() {
		clearSetting();
	}

	protected TextView getCell(int i, TableRow row) {
		return getCell(i, row, 0);
	}

	protected TextView getCell(int i, TableRow row, int gravity) {
		int color = getResources().getColor(R.color.light_cyan);
		if (i % 2 == 1) {
			color = getResources().getColor(R.color.gray_eb);
		}
		TextView tv = new TextView(this);
		TableRow.LayoutParams lp = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.MATCH_PARENT);
		lp.rightMargin = 1;
		int g = Gravity.CENTER_VERTICAL;
		if (gravity > 0) {
			g |= gravity;
		}
		lp.height = UI.getPixel(this, 45);
		tv.setGravity(g);
		tv.setLayoutParams(lp);
		tv.setBackgroundColor(color);
		row.addView(tv);
		return tv;
	}

	protected void saveSetting(String name, String value) {
		Editor editor = getSharedPreferences(C.APP_ID, MODE_PRIVATE).edit();
		editor.putString(name, value);
		editor.commit();
	}

	protected void saveSetting(String name, boolean value) {
		Editor editor = getSharedPreferences(C.APP_ID, MODE_PRIVATE).edit();
		editor.putBoolean(name, value);
		editor.commit();
	}

	protected void saveSetting(String name, int value) {
		Editor editor = getSharedPreferences(C.APP_ID, MODE_PRIVATE).edit();
		editor.putInt(name, value);
		editor.commit();
	}

	protected void refreshData() {

	}

	/**
	 * for a common list select dialog
	 * 
	 * @param id
	 * @param index
	 */
	protected void listSelected(View view, int index) {

	}

	protected void mlistSelected(View view, boolean[] selected) {
	}

	/**
	 * 显示普�?列表窗口，必须覆盖listSelected方法实现列表点击事件
	 * 
	 * @param id
	 * @param captions
	 */

	protected void showListDialog(View view, String[] captions) {
		showListDialog(view, 0, captions);
	}

	protected void showListDialog(View view, int resTitleId, String[] captions) {
		ListDialog dialog = new ListDialog(this);
		dialog.setOnSelectedListener(new ListDialog.OnSelectedListener() {
			public void onSelected(View view, int index) {
				listSelected(view, index);
			}
		});
		dialog.show(view, resTitleId, captions);
	}

	protected void showMListDialog(final View view, String[] captions,
			boolean[] selected) {
		showMListDialog(view, 0, captions, selected);
	}

	protected void showMListDialog(final View view, int resTitleId,
			String[] captions, boolean[] selected) {
		MListDialog dialog = new MListDialog(this);
		dialog.setOnSelectedListener(new MListDialog.OnSelectedListener() {
			public void onSelected(View view, boolean[] selected) {
				mlistSelected(view, selected);
			}
		});
		dialog.show(view, resTitleId, captions, selected);
	}

	@SuppressWarnings("deprecation")
	public void showMenu(final View view) {
		if (menus == null || menus.length == 0) {
			return;
		}
		View v = getLayoutInflater().inflate(R.layout.popwindow, null);
		ListView listView1 = (ListView) v.findViewById(R.id.PopWindow_lv);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		final PopupWindow popupWindow = new PopupWindow(v, width / 2, height);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menus.length; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			int imageId = UI.getId(this, menus[i], "drawable");
			CharSequence name = UI.getText(this, menus[i]);
			item.put("image", imageId);
			item.put("name", name);
			data.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.pop_item, new String[] { "image", "name" }, new int[] {
						R.id.image, R.id.name });
		listView1.setAdapter(adapter);
		listView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				menuSelected(view, arg2);
				popupWindow.dismiss();
			}
		});

		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			// (View) view.getParent().getParent()得到父容器，然后将popupWindow放在这个位置。
			popupWindow.showAsDropDown((View) view.getParent().getParent(),
					width / 2, 0);
		}
	}

	protected void menuSelected(View view, int index) {
	}

	/**
	 * 显示通用的日期�?择窗口，并覆�?dateSet方法
	 * 
	 * @param id
	 *            标识号，�?��可传入宿主控件的ID�?
	 * @param year
	 * @param month
	 * @param day
	 */
	protected void showDateDialog(final int id, int year, int month, int day) {
		MyDateDialog d = new MyDateDialog(this);
		// 定义弹出的DatePicker对话框的事件监听器：
		d.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int month, int day) {
				dateSet(id, year, month, day);
			}
		});
		d.show(year, month, day);
	}

	/**
	 * 
	 * @param id
	 * @param date
	 *            2012-01-20
	 */
	protected void showDateDialog(int id, String date) {
		String[] sdate = date.toString().split("-");
		int year = Integer.parseInt(sdate[0]);
		int month = Integer.parseInt(sdate[1]) - 1;
		int day = Integer.parseInt(sdate[2]);
		showDateDialog(id, year, month, day);
	}

	protected void dateSet(int id, int year, int month, int day) {
	}

	protected void showTimeDialog(final int id, int hour, int minute) {
		MyTimeDialog d = new MyTimeDialog(this);
		// 需要定义弹出的DatePicker对话框的事件监听器：
		d.setOnTimeSetListener(new OnTimeSetListener() {
			public void onTimeSet(TimePicker view, int hour, int minute) {
				timeSet(id, hour, minute);
			}
		});
		d.show(hour, minute);
	}

	protected void showTimeDialog(int id, String date) {
		String[] sdate = date.toString().split(":");
		int hour = Integer.parseInt(sdate[0]);
		int minute = Integer.parseInt(sdate[1]);
		showTimeDialog(id, hour, minute);
	}

	protected void timeSet(int id, int hour, int minute) {
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	protected void clearSetting() {
		Editor editor = getSharedPreferences(C.APP_ID, MODE_PRIVATE).edit();
		editor.remove("member");
		editor.remove("memno");
		editor.remove("password");
		editor.remove("playerNo");
		editor.remove("username");
		editor.remove("realName");
		editor.remove("mobileNo");
		editor.remove("rememberPassword");
		editor.remove("clubs");
		// editor.clear();
		editor.commit();
		S.clear();
	}

	/**
	 * after local async work
	 * 
	 * @param id
	 * @param result
	 */
	@Override
	public void afterWork(int id, Result result) {

	}

	public void showCustomDialog(final int type, CharSequence title,
			int layout, int resIcon) {

		CustomDialog cd = new CustomDialog(this);
		cd.setInitializer(new Initializer() {
			public void init(View view) {
				initDialog(type, view);
			}
		});
		cd.setOKProcessor(new OKProcessor() {
			public void process(Dialog ad) {
				finishDialog(type, ad);
			}
		});
		cd.show(title, layout, resIcon);

	}

	protected void initDialog(int type, View view) {

	}

	protected boolean finishDialog(int type, Dialog dialog) {
		return true;
	}

	public void doWork(Object... args) {

	}

	protected String getProperty(String name) {
		return properties.getProperty(name);
	}

	public void processAction(String action, int tag) {

	}

	public void playSound() {
		try {
			if (mediaPlayer == null)
				if (playerWhich) {
					mediaPlayer = MediaPlayer.create(this, R.raw.beep);
				} else {
					mediaPlayer = MediaPlayer.create(this, R.raw.touch3);
				}
			mediaPlayer.stop();
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
