package com.clubank.consumer;

import com.clubank.domain.C;
import com.clubank.domain.Result;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.MyAsyncTask;
import com.clubank.util.MyRow;
import com.clubank.util.UI;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GuestInqueryActivity extends BaseActivity {

	private static final int RESULT_CODE_FROM_NFC = 110;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guest_inquery);
	}

	public void ok(View view) {
		String cardNo = getEText("card");
		if (cardNo != null && !cardNo.equals("")) {
			reloadData(cardNo);
		} else {
			UI.showError(this, getString(R.string.lbl_enterCard));
		}
	}

	private void reloadData(String cardNo) {
		new MyAsyncTask(this, C.OP_QUERY_GUEST).execute(S.m.Token, cardNo);
	}

	// 感卡
	public void senseCards(View view) {
		setEText("card", "");
		Intent it;
		int version = settings.getInt("versionSwitch", 0);// 0默认为iData版
		if (version == 0) {
			it = new Intent(this, RfidActivity.class);
		} else {
			it = new Intent(this, NFCMainActivity.class);
		}
		it.putExtra("title", getString(R.string.lbl_senseCard));
		startActivityForResult(it, RESULT_CODE_FROM_NFC);
	}

	@Override
	public void onPostExecute(String action, Result result) {
		super.onPostExecute(action, result);
		if (action.equals(C.OP_QUERY_GUEST)) {
			if (result.code == C.RESULT_SUCCESS) {
				MyRow row = (MyRow) result.obj;
				if (row != null && row.size() > 0) {
					setVisibility("guestInfoLayout", View.VISIBLE);

					String name = row.getString("GuestName");
					if (name != null && !name.equals("")) {
						setRowValue("row1", "name", name);
					} else {
						setVisibility("row1", View.GONE);
					}

					String memNo = row.getString("MemNo");
					if (memNo != null && !memNo.equals("")) {
						setRowValue("row2", "memNo", memNo);
					} else {
						setVisibility("row2", View.GONE);
					}

					String date = row.getString("CheckinTime");
					if (date != null && !date.equals("")) {
						String d = date.substring(0, 10);
						d += " " + date.substring(11, 16);
						setRowValue("row3", "playTime", d);
					} else {
						setVisibility("row3", View.GONE);
					}

					String identity = row.getString("IdentityName");
					if (identity != null && !identity.equals("")) {
						setRowValue("row4", "identity", identity);
					} else {
						setVisibility("row4", View.GONE);
					}

					String nat = row.getString("Nationality");
					if (nat != null && !nat.equals("")) {
						setRowValue("row5", "nationality", nat);
					} else {
						setVisibility("row5", View.GONE);
					}
				}
			} else if (result.code == C.RESULT_GET_DATA_FAIL) {
				setVisibility("guestInfoLayout", View.GONE);
				UI.showError(this, getString(R.string.lbl_cardNotRegistered));
				return;
			}
		}
	}

	private void setRowValue(String v, String resCode, String value) {
		setVisibility(v, View.VISIBLE);
		setEText(resCode, value);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		if (requestCode == RESULT_CODE_FROM_NFC && data != null) {
			Bundle b = data.getExtras();
			String codeNo = b.getString("cardNo");
			int statusCode = b.getInt("statusCode");
			if (codeNo != null && !codeNo.equals("") && statusCode == 0) {
				reloadData(codeNo);
				setEText("card", codeNo);
			} else {
				UI.showError(this, R.string.msg_readCard_timeout);
			}
		}
	}

}
