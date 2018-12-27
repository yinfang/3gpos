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
* @ClassName: BrowseDishAdapter 
* @Description: 浏览商品
* @author fengyq
* @date 2014-3-5 下午3:19:24 
*  
*/
public class BrowseDishAdapter extends ArrayAdapter<MyRow> {

	LayoutInflater inflater;

	public BrowseDishAdapter(Context context, List<MyRow> objects) {
		super(context, 0, objects);
		inflater = LayoutInflater.from(context);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		MyRow item = getItem(position);
		if (view == null) {
			view = inflater.inflate(R.layout.lvi_dish, null);
		} else {
			view = convertView;
		}
		TextView t1 = (TextView) view.findViewById(R.id.dishName);
		TextView t2 = (TextView) view.findViewById(R.id.dishCode);
		TextView t3 = (TextView) view.findViewById(R.id.dishPrice);

		String name = item.getString("Name");
		String code = item.getString("Code");
		String price = item.getString("Price");

		if (name != null && !name.equals("")) {
			if (name.length() >= 7) {
				t1.setText(name.substring(0, 7));
			} else {
				t1.setText(name);
			}
		}
		if (code != null && !code.equals("")) {
			t2.setText(code);
		}
		if (price != null && !price.equals("")) {
			t3.setText(price);
		}
		return view;
	}

}