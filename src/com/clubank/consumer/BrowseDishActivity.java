package com.clubank.consumer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.clubank.domain.C;
import com.clubank.domain.Result;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.AsyncLocalTask;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;
import com.clubank.util.UI;

/** 
* @ClassName: BrowseDishActivity 
* @Description: 查看菜品 
* @author fengyq
* @date 2014-3-5 上午11:02:30 
*  
*/
public class BrowseDishActivity extends BaseActivity implements
		OnItemClickListener {

	private static final int WORK_LOAD_DISHDATABYCODE = 2;
	private static final int UPDATE_DATA = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dish);
		Intent it = getIntent();
		boolean click = it.getBooleanExtra("click", false);

		LinearLayout ll = (LinearLayout) findViewById(R.id.category);
		ll.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(this);
		for (int i = 0; i < S.dishCategory.size(); i++) {
			MyRow row = S.dishCategory.get(i);
			if (row != null) {
				String name = row.getString("Name");
				if (name != null && !name.equals("")) {
					if (name.length() > 4) {
						name = name.substring(0, 4);
					}
				}
				View v = inflater.inflate(R.layout.left_item, null);
				TextView tv = (TextView) v.findViewById(R.id.left_item);
				tv.setText(name);
				ll.addView(v);
			}
		}
		if (S.dishCategory.size() > 0) {// 显示第一个类别
			showDish(0);
		}
		ListView listView = (ListView) findViewById(R.id.dishListView);
		adapter = new BrowseDishAdapter(this, new MyData());
		listView.setAdapter(adapter);
		if (!click) {
			listView.setOnItemClickListener(this);
		}
	}

	public void selectCategory(View view) {
		adapter.clear();
		LinearLayout l = (LinearLayout) findViewById(R.id.category);
		int index = l.indexOfChild(view); // 点击时得到当前View的下标
		showDish(index);
	}

	private void showDish(int index) {
		LinearLayout ll = (LinearLayout) findViewById(R.id.category);
		for (int i = 0; i < ll.getChildCount(); i++) {
			View v = ll.getChildAt(i);
			View item = v.findViewById(R.id.left_item);
			View divider = v.findViewById(R.id.divider);
			if (i == index) {
				item.setBackgroundColor(getResources().getColor(
						R.color.left_band_selected));
				divider.setVisibility(View.INVISIBLE);
			} else {
				item.setBackgroundColor(getResources().getColor(
						R.color.light_green2));
				divider.setVisibility(View.VISIBLE);
			}
		}
		String code = S.dishCategory.get(index).getString("Code");
		if (code != null && !code.equals("")) {
			new AsyncLocalTask(this, WORK_LOAD_DISHDATABYCODE,
					C.LOCAL_GET_DISHDATABYCODE).execute(code);
		}
	}

	@Override
	public void afterWork(int id, Result result) {
		if (id == WORK_LOAD_DISHDATABYCODE) {
			MyData data = (MyData) result.obj;
			if (data != null && data.size() > 0) {
				S.dish = data;// 后面搜索用
				for (MyRow row : data) {
					adapter.add(row);
				}
				adapter.notifyDataSetChanged();
			} else {
				UI.showError(this, R.string.msg_notCategoryData);
				return;
			}
		}
	}

	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		MyRow row = (MyRow) parent.getItemAtPosition(pos);
		if (row != null) {
			Intent it = new Intent();
			it.putExtra("row", row);
			setResult(RESULT_OK, it);
			finish();
		}
	}

	@Override
	public void processDialogOK(int type, Object tag) {
		super.processDialogOK(type, tag);
		if (type == UPDATE_DATA) {
			openIntent(this, SettingActivity.class,
					getString(R.string.lbl_setting));
			finish();
		}
	}

}
