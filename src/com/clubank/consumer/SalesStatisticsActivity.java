package com.clubank.consumer;

import java.util.Calendar;
import java.util.Date;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.clubank.pos.R.color;
import com.clubank.customview.PieChartView;
import com.clubank.domain.C;
import com.clubank.domain.Result;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.MyAsyncTask;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;
import com.clubank.util.U;
import com.clubank.util.UI;

public class SalesStatisticsActivity extends BaseActivity {

	private LinearLayout layout;
	private int modeIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sales_statistics);
		setVisibility("header_more", View.VISIBLE);

		layout = (LinearLayout) findViewById(R.id.customView);
		ListView listView = (ListView) findViewById(R.id.listView);
		adapter = new SalesStatisticsAdapter(this, new MyData());
		listView.setAdapter(adapter);

		Calendar c = Calendar.getInstance();
		setEText("date", C.df_yMd.format(c.getTime()));

		menus = new String[] { "menu_listmode", "menu_chartmode",
				"menu_details" };
	}

	public void selectDate(View view) {
		TextView v = (TextView) view;
		showDateDialog(v.getId(), v.getText().toString());
	}

	@Override
	protected void dateSet(int id, int year, int month, int day) {
		String date = U.getDateString(year, month, day);
		if (id == R.id.date) {
			setEText("date", date);
		}
	}

	@Override
	public void menuSelected(View view, int index) {
		if (index == 0) {// 列表模式
			setVisibility("chartModeLayout", View.GONE);
			setVisibility("chartTodayTotal", View.GONE);
			layout.removeAllViews();
		} else if (index == 1) {// 图表模式
			setVisibility("listView", View.GONE);
			setVisibility("listTodayTotal", View.GONE);
			adapter.clear();
			setVisibility("chartModeLayout", View.VISIBLE);
		} else if (index == 2) {
			layout.removeAllViews();
			String salePoint = settings.getString("salePoint", "");
			new MyAsyncTask(this, C.OP_GET_BILLITEMSINFO_BY_WORKSHOP, true)
					.execute(S.m.Token, salePoint);
		}
		modeIndex = index;
	}
      
	public void query(View view) {
		if (modeIndex != 0) {
			layout.removeAllViews();
			setVisibility("chartTodayTotal", View.GONE);
		} else {
			adapter.clear();
			setVisibility("listTodayTotal", View.GONE);
		}
		String now = C.df_yMd.format(new Date());
		String date = getEText("date");
		if (date.compareTo(now) > 0) {
			UI.showError(this, R.string.msg_choose_end_date);
			return;
		} else {
			boolean tag = settings.getBoolean("salePointTag", false);
			if (tag) {
				String shopCode = settings.getString("salePoint", "");
				S.m.ShopCode = shopCode;
			}
			new MyAsyncTask(this, C.OP_GET_SALE_STAT, true).execute(S.m.Token,
					S.m.ShopCode, date);
		}
	}

	@Override
	public void onPostExecute(String action, Result result) {
		super.onPostExecute(action, result);
		if (action.equals(C.OP_GET_SALE_STAT)) {
			if (result.code == C.RESULT_SUCCESS) {
				MyData data = (MyData) result.obj;
				if (data != null && data.size() > 0) {
					if (modeIndex == 0) {
						for (MyRow row : data) {
							adapter.add(row);
						}
						setVisibility("listView", View.VISIBLE);
						setVisibility("listTodayTotal", View.VISIBLE);
						adapter.notifyDataSetChanged();
					} else {
						showGraph(data);
					}
					showTodayTotal(data);
				}
			} else {
				setVisibility("chartTodayTotal", View.GONE);
				UI.showInfo(this, R.string.msg_noSalesData);
			}
		} else if (action.equals(C.OP_GET_BILLITEMSINFO_BY_WORKSHOP)) {
			if (result.code == C.RESULT_SUCCESS) {
				MyData data = (MyData) result.obj;
				if (data != null && data.size() > 0) {
					for (MyRow row : data) {
						adapter.add(row);// 别的adapter TODO
					}
					setVisibility("listView", View.VISIBLE);
					setVisibility("listTodayTotal", View.VISIBLE);
					adapter.notifyDataSetChanged();// 绑定别的adapter
				}
			}
		}
	}

	private void showGraph(MyData data) {
		try {
			String[] desc = new String[data.size()];
			int[] value = new int[data.size()];
			for (int i = 0; i < data.size(); i++) {
				MyRow row = data.get(i);
				String v = row.getString("Category") + ":";		//分类
				String saleTotal = row.getString("SaleTotal");	//分数
				v += saleTotal.substring(0, saleTotal.indexOf("."))
						+ getString(R.string.lbl_yuan) + "/";
				String q = row.getString("Quantity");
				v += q.substring(0, q.indexOf(".")) + getString(R.string.lbl_a);
				desc[i] = v;
				// String quantity = row.getString("Quantity");
				// value[i] = Integer.parseInt(quantity.substring(0,
				// quantity.indexOf(".")));
				value[i] = Integer.parseInt(saleTotal.substring(0,
						saleTotal.indexOf("."))); // 总金额
			}
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			int screenHeight = dm.heightPixels;

			int ALPHA = 100;
			int[] colors = new int[] { Color.RED, Color.GREEN, color.green5,
					Color.BLUE, color.dialog_ok_bg, color.green2, color.green5,
					Color.CYAN, Color.GRAY, Color.LTGRAY, Color.YELLOW,
					Color.argb(ALPHA, 255, 255, 0),
					Color.argb(ALPHA, 255, 0, 255), color.dialog_ok_bg,
					color.my_orange, color.my_orange2, color.firebrick,
					color.bright_bule, color.light_green2, color.light_blue,
					color.light_green4, color.blue3, color.my_orange2,
					color.my_darkblue, color.lvi_pressed_start,
					Color.argb(ALPHA, 249, 64, 64),
					Color.argb(ALPHA, 0, 255, 0),
					Color.argb(ALPHA, 255, 33, 66),
					Color.argb(ALPHA, 0, 255, 255), };

			View v = new PieChartView(this, desc, colors, value, screenWidth,
					screenHeight);
			if(screenWidth == 640){
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(80, 22, 0, 0);
				layout.addView(v, lp);
			}else if (screenWidth == 720) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(35, 30, 0, 0);
				layout.addView(v, lp);
			} else if (screenWidth == 800) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(66, 38, 0, 0);
				layout.addView(v, lp);
			} else if (screenWidth == 1080) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(60, 50, 0, 0);
				layout.addView(v, lp);
			} else {
				layout.addView(v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showTodayTotal(MyData data) {
		if (data != null && data.size() > 0) {
			double money = 0;
			double quantity = 0;
			for (MyRow row : data) {
				quantity += row.getDouble("Quantity");
				money += row.getDouble("SaleTotal");
			}
			// String saleSum = String.valueOf(saleTotalSum);
			// saleSum.substring(0,saleSum.indexOf("."));
			// String qSum = String.valueOf(quantitySum);
			// qSum.substring(0,qSum.indexOf("."));
			if (modeIndex == 0) {
				setEText("lt_money", "" + money);
				setEText("lt_quantity", "" + quantity);
			} else {
				setVisibility("chartTodayTotal", View.VISIBLE);
				setEText("t_money", "" + money);
				setEText("t_quantity", "" + quantity);
			}
		}
	}

}
