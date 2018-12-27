package com.clubank.consumer;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.clubank.domain.C;
import com.clubank.domain.Result;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.AsyncLocalTask;
import com.clubank.util.DownloadFile;
import com.clubank.util.MyAsyncTask;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;
import com.clubank.util.UI;

public class MainActivity extends BaseActivity {

    private static final int WORK_LOAD_DISHCATEGORY = 1;
    private static final int DOWNLOAD_NEW_VERSION = 101;
    private static final int UPDATE_DATA = 102;
    private static final int DIALOG_EXIT = 1;
    private int currVersion = 1;
    private boolean autoChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            currVersion = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (C.IS_SHOW_STOCK == 1002) {
            findViewById(R.id.shahe_ll).setVisibility(View.VISIBLE);
            findViewById(R.id.base_ll).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!autoChecked) {
            autoChecked = true;
            new MyAsyncTask(this, C.OP_CHECK_NEW_VERSION, false).execute(
                    S.m.Token, currVersion, 1, false);
        }
    }

    // 销售开单
    public void salesBill(View view) {
        openIntent(this, ConsumerBillActivity.class,
                getString(R.string.lbl_consumerBill));
    }

    // 商品浏览
    public void productBrowse(View view) {
        if (S.dishCategory.size() > 0) {
            Intent it = new Intent(this, BrowseDishActivity.class);
            it.putExtra("title", getString(R.string.lbl_productBrowse));
            it.putExtra("click", true);
            startActivity(it);
        } else {
            new AsyncLocalTask(this, WORK_LOAD_DISHCATEGORY,
                    C.LOCAL_GET_DISHCATEGORYDATA).execute();
        }
    }

    // 账单查询
    public void billInquery(View view) {
        openIntent(this, BillInqueryActivity.class,
                getString(R.string.lbl_billInquery));
    }

    // 库存查询
    public void stockInquery(View view) {
        openIntent(this, StockInqueryActivity.class,
                getString(R.string.lbl_stockInquery));
    }

    // 客人查询
    public void guestInquery(View view) {
        openIntent(this, GuestInqueryActivity.class,
                getString(R.string.lbl_guestInquery));
    }

    public void setting(View view) {
        openIntent(this, SettingActivity.class, getString(R.string.lbl_setting));
    }

    // 销售统计
    public void salesStatistics(View view) {
        openIntent(this, SalesStatisticsActivity.class,
                getString(R.string.lbl_salesStatistics));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UI.showOKCancel(this, DIALOG_EXIT, R.string.msg_exit,
                    R.string.app_name);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPostExecute(String action, Result result) {
        super.onPostExecute(action, result);
        if (action.equals(C.OP_CHECK_NEW_VERSION)) {
            if (result.code == C.RESULT_SUCCESS) {
                MyRow row = (MyRow) result.obj;
                checkVersionResult(row);
            }
        }
    }

    private void checkVersionResult(MyRow row) {
        String sVersionCode = (String) row.get("versionCode");
        int versionCode = Integer.valueOf(sVersionCode);
        String versionName = (String) row.get("versionName");
        String description = (String) row.get("description");
        boolean manual = (Boolean) row.get("manual");
        if (versionCode <= currVersion) {
            if (manual) {// 如果是手动检查版本
                UI.showInfo(this, R.string.msg_no_new_version);
            }
        } else if (versionCode > currVersion) {
            String ver = getText(R.string.msg_found_new_version).toString();
            String msg = String.format(ver, versionName);
            String[] descs = description.split(";");
            String url = C.DOWN_BASE_URL + "Consumer.apk";
            for (int i = 0; i < descs.length; i++) {
                msg += System.getProperty("line.separator") + descs[i];
            }
            msg += System.getProperty("line.separator")
                    + getText(R.string.msg_ask_download).toString();
            UI.showOKCancel(this, DOWNLOAD_NEW_VERSION, msg,
                    getText(R.string.m_check_version), url);
        }
    }

    public void processDialogOK(int type, final Object tag) {
        if (type == DIALOG_EXIT) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            S.clear();
            finish();
        } else if (type == DOWNLOAD_NEW_VERSION) {
            new DownloadFile(this).execute((String) tag);
        } else if (type == UPDATE_DATA) {
            openIntent(this, SettingActivity.class,
                    getString(R.string.lbl_setting));
        }
    }

    @Override
    public void afterWork(int id, Result result) {
        if (id == WORK_LOAD_DISHCATEGORY) {
            MyData data = (MyData) result.obj;
            if (data.size() > 0) {
                S.dishCategory = data;
                Intent it = new Intent(this, BrowseDishActivity.class);
                it.putExtra("title", getString(R.string.lbl_productBrowse));
                it.putExtra("click", true);
                startActivity(it);
            } else {
                UI.showOKCancel(this, UPDATE_DATA, R.string.msg_notData,
                        R.string.lbl_getDataFail);
            }
        }
    }

}
