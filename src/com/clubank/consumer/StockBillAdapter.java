package com.clubank.consumer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.clubank.pos.R;
import com.clubank.util.MyRow;

import java.util.List;

/**
 * 库存适配
 *
 * @author zyf 2016.10.24
 */
public class StockBillAdapter extends ArrayAdapter<MyRow> {

    LayoutInflater inflater;

    public StockBillAdapter(Context context, List<MyRow> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        MyRow item = getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.stock_bill_item, null);
        }
        TextView code = (TextView) view.findViewById(R.id.code);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView quantity = (TextView) view.findViewById(R.id.quantity);

        code.setText(item.getString("ItemCode"));
        String na = item.getString("ItemName");
        quantity.setText("" + item.getDouble("Quantity"));

        if (na != null && !na.equals("")) {
            if (na.length() >= 10) {
                name.setText(na.substring(0, 10));
            } else {
                name.setText(na);
            }
        }
        return view;
    }
}