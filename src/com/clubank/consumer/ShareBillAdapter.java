package com.clubank.consumer;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.clubank.pos.R;
import com.clubank.util.MyRow;

/** 
* @ClassName: ShareBillAdapter 
* @Description: 账单查询
* @author fengyq
* @date 2014-3-8 上午9:34:23 
*  
*/
public class ShareBillAdapter extends ArrayAdapter<MyRow> {

	LayoutInflater inflater;

	public ShareBillAdapter(Context context, List<MyRow> objects) {
		super(context, 0, objects);
		inflater = LayoutInflater.from(context);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		MyRow item = getItem(position);
		if (view == null) {
			view = inflater.inflate(R.layout.share_bill_item, null);
		}
		TextView t1 = (TextView) view.findViewById(R.id.billCode);// 账单编号
		TextView t2 = (TextView) view.findViewById(R.id.billMoney);// 账单金额
		TextView t3 = (TextView) view.findViewById(R.id.billTime);// 开单时间
		TextView t4 = (TextView) view.findViewById(R.id.billPeople);// 开单人
		TextView  deleteBillB= (TextView) view.findViewById(R.id.deleteBillB);// 取消账单

		String billCode = item.getString("BillCode");
		String billTotal = item.getString("BillTotal");
		String openTime = item.getString("OpenTime");
		String openUser = item.getString("OpenUser");
		
		if (billCode != null && !billCode.equals("")) {
			t1.setText(billCode);
			//可通过按钮的tag删除账单
			deleteBillB.setTag(billCode);
		}

		if (billTotal != null && !billTotal.equals("")) {
			t2.setText(billTotal);
		}

		if (openTime != null && !openTime.equals("")) {
			t3.setText(openTime);
		}

		if (openUser != null && !openUser.equals("")) {
			t4.setText(openUser);
		}

		return view;
	}
}