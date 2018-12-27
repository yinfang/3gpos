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
* @ClassName: ConsumerBillAdapter 
* @Description:
* @author fengyq
* @date 2014-3-5 下午2:35:51 
*  
*/
public class ConsumerBillAdapter extends ArrayAdapter<MyRow> {

	LayoutInflater inflater;

	public ConsumerBillAdapter(Context context, List<MyRow> objects) {
		super(context, 0, objects);
		inflater = LayoutInflater.from(context);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		MyRow item = getItem(position);
		if (view == null) {
			view = inflater.inflate(R.layout.consumer_bill_item, null);
		}
		TextView t1 = (TextView) view.findViewById(R.id.project);
		TextView t2 = (TextView) view.findViewById(R.id.quantity);
		TextView t3 = (TextView) view.findViewById(R.id.price);

		String name = item.getString("Names");
		double quantity = item.getDouble("Quantitys");
		String price = item.getString("Prices");

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
		return view;
	}
}