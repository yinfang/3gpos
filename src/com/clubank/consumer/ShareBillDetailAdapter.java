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

public class ShareBillDetailAdapter extends ArrayAdapter<MyRow> {

	LayoutInflater inflater;

	public ShareBillDetailAdapter(Context context, List<MyRow> objects) {
		super(context, 0, objects);
		inflater = LayoutInflater.from(context);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		MyRow item = getItem(position);
		if (view == null) {
			view = inflater.inflate(R.layout.share_bill_detail_item, null);
		}
		TextView t1 = (TextView) view.findViewById(R.id.project);// 项目
		TextView t2 = (TextView) view.findViewById(R.id.quantity);// 数量
		TextView t3 = (TextView) view.findViewById(R.id.unitPrice);// 单价
		TextView t4 = (TextView) view.findViewById(R.id.money);// 金额

		String name = item.getString("Names");
		String quantity = item.getString("Quantitys");
		String price = item.getString("Prices");
		String saleTotal = item.getString("SaleTotals");

		if (name != null && !name.equals("")) {
			t1.setText(name);
		}

		if (quantity != null && !quantity.equals("")) {
			t2.setText(quantity);
		}

		if (price != null && !price.equals("")) {
			t3.setText(price);
		}

		if (saleTotal != null && !saleTotal.equals("")) {
			t4.setText(saleTotal);
		}
		return view;
	}
}