package com.clubank.consumer;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.clubank.domain.C;
import com.clubank.domain.PosBillItemInfo;
import com.clubank.domain.Result;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.MyAsyncTask;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;
import com.clubank.util.UI;

/**
 * @ClassName: ShareBillActivity
 * @Description: 账单查询 1.删除账单:调用deleteBill方法，在onPostExecute中更新UI
 * @author fengyq
 * @date 2014-3-5 上午11:11:40
 * 
 */
public class ShareBillActivity extends BaseActivity implements
		OnItemClickListener {
	private static final int SAVE_SUCCESS_CODE = 200;
	private static final int ENTER_PEOPLE = 2;
	private boolean switchCardNo;
	private TableLayout table;
	private ListView listView;
	private int guestNum = 1;
	private EditText et;
	private String tag;
	private boolean typeNull = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_bill);
		setVisibility("header_more", View.VISIBLE);

		menus = new String[] { "menu_mumber_of_people" };
		table = (TableLayout) this.findViewById(R.id.tableLayout);
		Intent it = getIntent();
		boolean billInquery = it.getBooleanExtra("billInquery", false);
		switchCardNo = it.getBooleanExtra("switchCardNo", false);
		boolean selectedSave = it.getBooleanExtra("selectedSave", false);
		
		if(null != it.getStringExtra("type")){
			if("0".endsWith(it.getStringExtra("type"))){
				typeNull = true;
				setHeaderTitle("账单列表");
				setVisibility("billDetailLayout", View.GONE);
				setVisibility("header_more", View.GONE);
			}
		}

		if (selectedSave) {
			if (S.oneDish != null && S.oneDish.size() > 0) {
				for (MyRow row : S.oneDish) {
					showData(row);
				}
			}
		} else {
			listView = (ListView) findViewById(R.id.listView);
			adapter = new ShareBillAdapter(this, new MyData());
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
//			if (billInquery) {
//				setVisibility("confirmSubmitView", View.GONE);
//				if (switchCardNo) {
//					// 账单查询cardNo
//					reloadData(S.expendBill.getString("BillCardNo"));
//				} else {
//					reloadData(S.expendBill.getString("CardNo"));
//				}
//			}
			if(typeNull){
				billInquery = true;
				setVisibility("confirmSubmitView", View.GONE);
			}
			if (billInquery) {
					setVisibility("confirmSubmitView", View.GONE);
				if (switchCardNo) {
					// 账单查询cardNo
					reloadData(S.expendBill.getString("BillCardNo"));
				} else {
					reloadData(S.expendBill.getString("CardNo"));
				}
			}
		}
		if(!typeNull){
			showExpendBillData(); // 顶部有关卡号信息。
		}
	}

	/**
	 * @Title: deleteBill
	 * @Description: 取消账单
	 */
	public void deleteBill(View view) {

		tag = view.getTag().toString();
		//增加输入取消订单的原因对话框
//		UI.showOKCancel(this, 0, getText(R.string.msg_confirm_delete), "", tag);

		showCustomDialog(0, getResources().getString(R.string.lbl_deleteBill_beacuse),
				R.layout.input_text, 1);
	}



	@Override
	public void menuSelected(View view, int index) {
		if (index == 0) {
			showCustomDialog(ENTER_PEOPLE,
					getString(R.string.lbl_enter_people), R.layout.input_text,
					-1);
		}
	}

	@Override
	protected void initDialog(int type, View view) {
		if (type == ENTER_PEOPLE) {
			EditText et = (EditText) view.findViewById(R.id.input);
			et.setText("1");
		}
	}

	@Override
	protected boolean finishDialog(int type, Dialog dialog) {
		et = (EditText) dialog.findViewById(R.id.input);

		if (type == ENTER_PEOPLE) {

			guestNum = Integer.parseInt(et.getText().toString());
		}else if(type == 0){

			String cancle_bus = et.getText().toString().trim();

			if(TextUtils.isEmpty(cancle_bus) || (cancle_bus.length() < 3)){
			
				Toast.makeText(this,getResources().getString(R.string.cancle_bill_unok),Toast.LENGTH_SHORT).show();

			}else{

				new MyAsyncTask(this, C.OP_CANCEL_BILL_BY_CODE).execute(S.m.Token,
						tag.toString(),cancle_bus);
			}
		}
		return true;
	}

	private void reloadData(String cardNo) {
		boolean tag = settings.getBoolean("salePointTag", false);
		if (tag) {
			String shopCode = settings.getString("salePoint", "");
			S.m.ShopCode = shopCode;
		}
		if(typeNull){
			new MyAsyncTask(this, C.OP_GET_BILLS).execute(S.m.Token,"",S.m.ShopCode);
		}else{
			new MyAsyncTask(this, C.OP_GET_BILLS).execute(S.m.Token, cardNo,S.m.ShopCode);
		}
		
	}

	@Override
	public void onPostExecute(String action, Result result) {
		super.onPostExecute(action, result);
		if (action.equals(C.OP_SAVE_BILL)) {
			if (result.code == C.RESULT_SUCCESS) {
				UI.showInfo(this, R.string.msg_save_success, SAVE_SUCCESS_CODE);
			} else if (result.code == C.RESULT_SAVE_DATA_FAIL) {
				UI.showInfo(this, R.string.msg_save_failed);
			} else {
				UI.showInfo(this, R.string.msg_operation_failed);
			}
		} else if (action.equals(C.OP_GET_BILLS)) {
			if (result.code == C.RESULT_SUCCESS) {
				MyData data = (MyData) result.obj;
				if (data != null && data.size() > 0) {
					for (MyRow rw : data) {
						adapter.add(rw);
					}
					adapter.notifyDataSetChanged();
					listView.setVisibility(View.VISIBLE);
					setVisibility("bottomLayout", View.GONE);
				}
			} else if (result.code == C.RESULT_POINT_NO_DATA) {
				String cardNo = "";
				if (switchCardNo) {
					cardNo = S.expendBill.getString("BillCardNo");
				} else {
					cardNo = S.expendBill.getString("CardNo");
				}
				String msg = getString(R.string.msg_noBillData);
				if(TextUtils.isEmpty(cardNo) || "null".equals(cardNo)){
					msg = "当前没有消费账单数据！";
				}else{
					msg = String.format(msg, cardNo);
				}
				UI.showError(this, msg);
			}
			/**
			 * 取消订单更新UI
			 */
		} else if (action.equals(C.OP_CANCEL_BILL_BY_CODE)) {

			if (result.code == C.RESULT_SUCCESS) {
				Toast.makeText(this, R.string.msg_del_success,
						Toast.LENGTH_SHORT).show();
				adapter.clear();
				if (switchCardNo) {
					// 账单查询cardNo
					reloadData(S.expendBill.getString("BillCardNo"));
				} else {
					reloadData(S.expendBill.getString("CardNo"));
				}
			}else if (result.code == C.RESULT_POINT_NO_DATA){
				String cardNo = "";
				if (switchCardNo) {
					cardNo = S.expendBill.getString("BillCardNo");
				} else {
					cardNo = S.expendBill.getString("CardNo");
				}
				String msg = getString(R.string.msg_noBillData);
				msg = String.format(msg, cardNo);
				UI.showError(this, msg);
			}else if(result.code == C.RESULT_CANCELED){
				
				UI.showError(this, getString(R.string.msg_noBill));
			}
		}
	}

	// 消费开单信息
	private void showExpendBillData() {
		if (switchCardNo) {
			setEText("cardNo", S.expendBill.getString("BillCardNo"));
		} else {
			setEText("cardNo", S.expendBill.getString("CardNo"));
		}
		setEText("name", S.expendBill.getString("GuestName"));
		setEText("expendDate", S.expendBill.getString("ExpendDate"));
	}

	private void showData(MyRow row) {
		View tableRow = (TableRow) LayoutInflater.from(this).inflate(
				R.layout.table_item, null);
		TextView t1 = (TextView) tableRow.findViewById(R.id.project);
		TextView t2 = (TextView) tableRow.findViewById(R.id.quantity);
		TextView t3 = (TextView) tableRow.findViewById(R.id.unitPrice);
		TextView t4 = (TextView) tableRow.findViewById(R.id.money);

		if (row.containsKey("Names")) {
			String project = row.getString("Names");
			if (project != null && !project.equals("")) {
				if (project.length() >= 7) {
					t1.setText(project.substring(0, 6));
				} else {
					t1.setText(project);
				}
			}
		}
		if (row.containsKey("Quantitys")) {
			double quantity = row.getDouble("Quantitys");
			t2.setText("" + quantity);
		}
		if (row.containsKey("BasePrices")) {
			String basePrice = row.getString("BasePrices");
			double quantity = row.getDouble("Quantitys");
			if (basePrice != null && !basePrice.equals("")) {
				if (quantity == 0) {
					t3.setText("0.0");
				} else {
					t3.setText(basePrice);
				}
			}
		}
		if (row.containsKey("Prices")) {
			String price = row.getString("Prices");
			if (price != null && !price.equals("")) {
				t4.setText(price);
			}
		}
		setVisibility("scrollView", View.VISIBLE);
		table.addView(tableRow);
	}

	// 确定并提交
	public void confirmSubmit(View view) {
		ArrayList<PosBillItemInfo> list = new ArrayList<PosBillItemInfo>();
		for (MyRow row : S.oneDish) {
			PosBillItemInfo info = new PosBillItemInfo();
			double quantity = row.getDouble("Quantitys");
			if (quantity > 0 || quantity != 0) {
				info.Quantity = String.valueOf(quantity);
				info.Name = row.getString("Names");
				info.ItemCode = row.getString("Codes");
				info.Price = (Double.parseDouble(row.getString("BasePrices")))+"";
				list.add(info);
			}
		}
		if (list.size() > 0) {
			boolean tag = settings.getBoolean("salePointTag", false);
			if (tag) {
				String shopCode = settings.getString("salePoint", "");
				S.m.ShopCode = shopCode;
			}
			new MyAsyncTask(this, C.OP_SAVE_BILL).execute(S.m.Token,
					S.expendBill.getString("CardNo"), S.m.ShopCode, guestNum,
					list);
		} else {
			UI.showError(this, R.string.msg_filtered);
			return;
		}
	}

	@Override
	public void processDialogOK(int type, Object tag) {
		super.processDialogOK(type, tag);
		if (type == SAVE_SUCCESS_CODE) {
			S.clear();
			Intent it = new Intent(this, ConsumerBillActivity.class);
			it.putExtra("title", getString(R.string.lbl_consumerBill));
			it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(it);
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		MyRow row = (MyRow) parent.getItemAtPosition(pos);
		if (row != null) {
			String billCode = row.getString("BillCode");
			Intent it = new Intent(this, ShareBillDetailActivity.class);
			it.putExtra("title", "账单明细");
			it.putExtra("billCode", billCode);
			startActivity(it);
		}
	}
}
