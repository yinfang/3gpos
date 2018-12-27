package com.clubank.consumer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.MyRow;
import com.clubank.util.UI;

public class SelectedProductActivity extends BaseActivity {

	private static final int RESULT_RETURN_CARD = 10;
	private boolean deleted;
	private TableLayout table;
	private TextView t2 = null;
	private TextView t3 = null;
	private TableRow v = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selected_product);
		table = (TableLayout) this.findViewById(R.id.tableLayout);
		showData();
	}

	private void showData() {
		if (S.oneDish != null && S.oneDish.size() > 0) {
			for (int i = 0; i < S.oneDish.size(); i++) {
				TableRow row = (TableRow) LayoutInflater.from(this).inflate(
						R.layout.select_product_item, null);
				TextView t1 = (TextView) row.findViewById(R.id.project);
				TextView t2 = (TextView) row.findViewById(R.id.quantity);
				TextView t3 = (TextView) row.findViewById(R.id.price);

				((ImageView) row.findViewById(R.id.minus)).setTag(i); // 加tag，用来标识点击的是哪一个位置的按钮。
				((ImageView) row.findViewById(R.id.add)).setTag(i);
				((ImageView) row.findViewById(R.id.delete)).setTag(i);

				String name = S.oneDish.get(i).getString("Names");
				double quantity = S.oneDish.get(i).getDouble("Quantitys");
				String price = S.oneDish.get(i).getString("Prices");

				if (name != null && !name.equals("")) {
					if (name.length() >= 7) {
						t1.setText(name.substring(0, 6));
					} else {
						t1.setText(name);
					}
				}
				t2.setText("" + quantity);
				if (price != null && !price.equals("")) {
					t3.setText(price);
				}
				table.addView(row);
			}
		}
	}

	public void minus(View view) {
		int i = (Integer) view.getTag();
		calculatePrice(1, i);
	}

	public void add(View view) {
		int i = (Integer) view.getTag();
		calculatePrice(2, i);
	}

	private void calculatePrice(int tag, int id) {
		try {
			if (deleted) {
				findView(1, id);
			} else {
				findView(0, id);
			}
			MyRow rw = null;
			if (deleted) {
				rw = S.oneDish.get(id - 1);// 删除某一条tableRow之后，tag的index从1开始
			} else {
				rw = S.oneDish.get(id);
			}

			double quantity = rw.getDouble("Quantitys");
			double price = rw.getDouble("Prices");
			double p = rw.getDouble("BasePrices");// 基准价格
			if (tag == 1) {
				quantity--;
				price -= p;
			} else {
				quantity++;
				price += p;
			}
			t2.setText("" + quantity);
			t3.setText("" + price);

			MyRow row = null;
			if (deleted) {
				row = S.oneDish.get(id - 1);
			} else {
				row = S.oneDish.get(id);
			}
			row.put("Quantitys", quantity);
			row.put("Prices", "" + price);

			if (deleted) {
				S.oneDish.remove(id - 1);
				S.oneDish.add(id - 1, row);
			} else {
				S.oneDish.remove(id); // 如果不删除之前位置的Row对象，在S.oneDish中会有相同的Row对象。
				S.oneDish.add(id, row);// 再往相同的位置添加Row对象，如果S.oneDish.add(row)这样子写的话，S.oneDish后面的Row的索引就会在删除了Row对象的前面，到时数据会错乱。
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(View view) {
		View v = ((View) view.getParent().getParent());
		v.setVisibility(View.GONE);
		table.removeView(v);
		int i = (Integer) view.getTag();
		if (deleted) {
			S.oneDish.remove(i - 1);
		} else {
			S.oneDish.remove(i);
		}
		deleted = true;
		resetTag();
	}

	private void findView(int index, int id) {
		for (int j = index; j < table.getChildCount(); j++) { // 最外面还有一层title，所以实际的大小是会加上title的
			if (deleted) {
				v = (TableRow) table.getChildAt(id);
			} else {
				v = (TableRow) table.getChildAt(id + 1);// 在table中找到tableRow对象TODO
			}
			t2 = (TextView) v.findViewById(R.id.quantity);// 在tableRow中找到quantity对象
			t3 = (TextView) v.findViewById(R.id.price);
		}
	}

	// 删除了table中的某一行，如果不重新设置Tag，计算价格和数据会错乱，重新给这个table的数据设置Tag，加减是根据tag来计算。
	private void resetTag() {
		if (deleted) {
			for (int j = 1; j < table.getChildCount(); j++) {// table中包含了title，所以从1开始
				TableRow v = (TableRow) table.getChildAt(j);
				((ImageView) v.findViewById(R.id.minus)).setTag(j);
				((ImageView) v.findViewById(R.id.add)).setTag(j);
				((ImageView) v.findViewById(R.id.delete)).setTag(j);
				table.removeView(v);
				table.addView(v, j);
			}
		}
	}

	public void submit(View view) {
		if (S.oneDish.size() > 0) {
			String cardNo = S.expendBill.getString("CardNo");
			if (cardNo != null && !cardNo.equals("")) {
				Intent it = new Intent(this, ShareBillActivity.class);
				it.putExtra("selectedSave", true);
				it.putExtra("title", getString(R.string.lbl_consumerBill));
				startActivity(it);
			} else {
				UI.showOKCancel(this, RESULT_RETURN_CARD,
						R.string.msg_enterCardNo, R.string.lbl_Remind);
			}
		} else {
			UI.showInfo(this, R.string.msg_noProductSave);
		}
	}

	@Override
	public void processDialogOK(int type, Object tag) {
		super.processDialogOK(type, tag);
		if (type == RESULT_RETURN_CARD) {
			Intent it = new Intent();
			setResult(RESULT_OK, it);
			finish();
		}
	}

}
