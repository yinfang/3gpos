package com.clubank.consumer;

import com.clubank.domain.C;
import com.clubank.domain.Result;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.MyAsyncTask;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;
import com.clubank.util.UI;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class ShareBillDetailActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_bill_detail);
		Intent it = getIntent();
		String billCode = it.getStringExtra("billCode");
		reloadData(billCode);
		
		ListView listView = (ListView) findViewById(R.id.listView);
		adapter = new ShareBillDetailAdapter(this, new MyData());
		listView.setAdapter(adapter);
	}

	private void reloadData(String billCode) {
		new MyAsyncTask(this, C.OP_GET_BILL_BYCARD)
				.execute(S.m.Token, billCode);
	}

	@Override
	public void onPostExecute(String action, Result result) {
		super.onPostExecute(action, result);
		if (action.equals(C.OP_GET_BILL_BYCARD)) {
			if (result.code == C.RESULT_SUCCESS) {
				S.billInfo = (MyData) result.obj;
				if (S.billInfo != null && S.billInfo.size() > 0) {
					for (MyRow row : S.billInfo) {
						String name = row.getString("Name");
						String quantity = row.getString("Quantity");
						String price = row.getString("Price");
						String saleTotal = row.getString("SaleTotal");

						row.put("Names", name);
						row.put("Quantitys", quantity);
						row.put("Prices", price);
						row.put("SaleTotals", saleTotal);

						row.remove("Name");
						row.remove("Quantity");
						row.remove("Price");
						row.remove("SaleTotal");
						adapter.add(row);
					}
					adapter.notifyDataSetChanged();
				}
			} else if (result.code == C.RESULT_GET_DATA_FAIL) {
				UI.showError(this, getString(R.string.lbl_getDataFail));
				return;
			}
		}
	}

}
