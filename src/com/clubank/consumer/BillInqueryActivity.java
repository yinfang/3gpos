package com.clubank.consumer;

import com.clubank.domain.C;
import com.clubank.domain.Result;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.MyRow;
import android.os.Bundle;
import android.view.View;

/** 
* @ClassName: BillInqueryActivity 
* @Description: 账单查询，查询功能在父类中查看
* @author fengyq
* @date 2014-3-5 上午10:50:13 
*  
*/
public class BillInqueryActivity extends ConsumerBillActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consumer_bill);
		setVisibility("productLayout", View.GONE);
		setVisibility("buttonLayout", View.GONE);
	}

	@Override
	public void onPostExecute(String action, Result result) {
		super.onPostExecute(action, result);
		if (action.equals(C.OP_QUERY_GUEST)) {
			if (result.code == C.RESULT_SUCCESS) {
				MyRow row = (MyRow) result.obj;
				if (row != null && row.size() > 0) {
					String cardNo = row.getString("CardNo");
					if (cardNo != null && !cardNo.equals("")) {
						row.put("BillCardNo", cardNo);
						row.remove("CardNo");
					}
					S.expendBill = row;
					if (S.expendBill != null && S.expendBill.size() > 0) {
						switchCardNo = true;
						setVisibility("billDetailLayout", View.VISIBLE);
						setVisibility("billInqueryBtn", View.VISIBLE);
						setVisibility("resetCardBtn", View.VISIBLE);
						setVisibility("showBillDataLayout", View.GONE);
						setEText("cardNo", S.expendBill.getString("BillCardNo"));
						setEText("name", S.expendBill.getString("GuestName"));
						setEText("expendDate",
								S.expendBill.getString("ExpendDate"));
					}
				}
			}
		}
	}

}
