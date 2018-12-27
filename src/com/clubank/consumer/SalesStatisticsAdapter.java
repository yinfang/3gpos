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

public class SalesStatisticsAdapter extends ArrayAdapter<MyRow> {

	LayoutInflater inflater;

	public SalesStatisticsAdapter(Context context, List<MyRow> objects) {
		super(context, 0, objects);
		inflater = LayoutInflater.from(context);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		MyRow item = getItem(position);
		if (view == null) {
			view = inflater.inflate(R.layout.sales_statistics_item, null);
		}
		TextView t1 = (TextView) view.findViewById(R.id.totalPrice);// 总价
		TextView t2 = (TextView) view.findViewById(R.id.category);// 类别
		TextView t3 = (TextView) view.findViewById(R.id.quantity);// 数量

		String saleTotal = item.getString("SaleTotal");
		String category = item.getString("Category");
		String quantity = item.getString("Quantity");

		if (saleTotal != null && !saleTotal.equals("")) {
			t1.setText(saleTotal);
		}

		if (category != null && !category.equals("")) {
			t2.setText(category);
		}

		if (quantity != null && !quantity.equals("")) {
			t3.setText(quantity);
		}

		return view;
	}
}