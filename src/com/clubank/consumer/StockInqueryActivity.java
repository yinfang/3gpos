package com.clubank.consumer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.clubank.domain.C;
import com.clubank.domain.Result;
import com.clubank.pos.R;
import com.clubank.util.MyAsyncTask;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;
import com.clubank.util.UI;

import java.util.ArrayList;

/**
 * 库存查询 （南京中山需求）
 *
 * @author zyf
 * @date 2016.10.21
 */
public class StockInqueryActivity extends BaseActivity {
    private String[] names;
    private ArrayList<String> codes;
    private TextView name;
    private MyData data;
    private int pos;
    private StockBillAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_bill);
        initView();
    }

    private void initView() {
        names = new String[]{};
        data = new MyData();
        name = (TextView) findViewById(R.id.name);
        ListView list = (ListView) findViewById(R.id.listView);
        adapter = new StockBillAdapter(this, data);
        list.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new MyAsyncTask(this, C.OP_GET_STOCK_LIST).execute();
    }

    /**
     * 显示所有可选查询类别
     */
    public void showData(View v) {
        showListDialog(v, names);
    }

    /**
     * 查询
     */
    public void ok(View v) {
        if (TextUtils.isEmpty(name.getText())) {
            UI.showInfo(this, getText(R.string.input_query), 0);
            return;
        }
        new MyAsyncTask(this, C.OP_GET_STOCK_BYCODE).execute(codes.get(pos));
    }

    @Override
    protected void listSelected(View view, int index) {
        super.listSelected(view, index);
        name.setText(names[index]);
        pos = index;
    }

    @Override
    public void onPostExecute(String action, Result result) {
        super.onPostExecute(action, result);
        if (action.equals(C.OP_GET_STOCK_LIST)) {//获取库存列表
            if (result.code == C.RESULT_SUCCESS) {
                MyData da = (MyData) result.obj;
                codes = new ArrayList();
                if (da.size() > 0) {
                    names = new String[da.size()];
                    for (int i = 0; i < da.size(); i++) {
                        names[i] = da.get(i).getString("ShopName");
                        codes.add(da.get(i).getString("ShopCode"));
                    }
                    name.setText(names[0]);
                    new MyAsyncTask(this, C.OP_GET_STOCK_BYCODE).execute(codes.get(0));
                }
            }
        } else if (action.equals(C.OP_GET_STOCK_BYCODE)) {//获取库存信息
            if (result.code == C.RESULT_SUCCESS) {
                data = (MyData) result.obj;
                adapter.clear();
                if (data.size() > 0) {
                    findViewById(R.id.listView).setVisibility(View.VISIBLE);
                    findViewById(R.id.empty_info).setVisibility(View.GONE);
                    for (MyRow row : data) {
                        adapter.add(row);
                    }
                } else {
                    findViewById(R.id.listView).setVisibility(View.GONE);
                    findViewById(R.id.empty_info).setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

}
