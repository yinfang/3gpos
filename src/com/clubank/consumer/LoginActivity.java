package com.clubank.consumer;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.clubank.domain.C;
import com.clubank.domain.MemberInfo;
import com.clubank.domain.Result;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.MyAsyncTask;
import com.clubank.util.MyRow;
import com.clubank.util.U;
import com.clubank.util.UI;

public class LoginActivity extends BaseActivity {

    private static final int SERVER_URL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setVisibility("header_back", View.GONE);
        setVisibility("header_more", View.VISIBLE);
        menus = new String[]{"menu_setting"};
    }

    @Override
    public void menuSelected(View view, int index) {
        if (index == 0) {
            showCustomDialog(SERVER_URL, getString(R.string.lbl_enter_url),
                    R.layout.input_text, -1);
        }
    }

    @Override
    protected void initDialog(int type, View view) {
        if (type == SERVER_URL) {
            EditText url = (EditText) view.findViewById(R.id.input);
            String wsUrl = settings.getString("wsUrl", C.wsUrl);
            if (wsUrl != null && !wsUrl.equals("")) {
                url.setText(wsUrl);
            }
        }
    }

    /**
     * 是否支持库存查询
     */
    public void getIfStock() {
        new MyAsyncTask(this, C.OP_GET_CAN_QUERY_STOCK).execute();
    }

    @Override
    protected boolean finishDialog(int type, Dialog dialog) {
        if (type == SERVER_URL) {
            EditText url = (EditText) dialog.findViewById(R.id.input);
            String wsUrl = url.getText().toString();
            C.wsUrl = wsUrl;
            setEText("url", wsUrl);
            saveSetting("wsUrl", wsUrl);
        }
        return true;
    }

    public void login(View view) {
        String userCode = getEText("userCode");
        String password = getEText("password");
        if ("".equals(userCode) || "".equals(password)) {
            return;
        }
        new MyAsyncTask(this, C.OP_LOGIN).execute(userCode, password);
    }

    @Override
    public void onPostExecute(String action, Result result) {
        super.onPostExecute(action, result);
        if (action.equals(C.OP_LOGIN)) {
            if (result.code == C.RESULT_SUCCESS) {
                MyRow row = (MyRow) result.obj;
                S.m = U.toObject(row, MemberInfo.class);
                String password = getEText("password");
                S.m.Password = password;

                boolean tag = settings.getBoolean("salePointTag", false);// 如果营业点改更改过，那就用更改过的营业点。
                if (tag) {
                    String shopCode = settings.getString("salePoint", "");
                    S.m.ShopCode = shopCode;
                }

                // 保存参数
                Editor editor = getSharedPreferences(C.APP_ID, MODE_PRIVATE)
                        .edit();
                editor.putString("userCode", S.m.UserCode);
                editor.putString("salePoint", S.m.ShopCode);

                boolean rememberPassword = ((CheckBox) findViewById(R.id.rememberPassword))
                        .isChecked();
                editor.putBoolean("rememberPassword", rememberPassword);
                if (result.code == C.RESULT_SUCCESS && rememberPassword) {
                    editor.putString("password", password);
                } else {
                    editor.remove("password");
                }
                editor.commit();
                getIfStock();
            } else if (result.code == C.RESULT_AUTH_FAILED) {
                UI.showError(this, getString(R.string.msg_auth_failed));
                return;
            }
        } else if (action.equals(C.OP_GET_CAN_QUERY_STOCK)) {
            if (result.code == 1) {
               C.IS_SHOW_STOCK=1002;
            }
            Intent it = new Intent(this, MainActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(it);
            finish();
        }
    }

    @Override
    public void onStart() {
        String userCode = settings.getString("userCode", "");
        String password = settings.getString("password", "");
        boolean rememberPassword = settings
                .getBoolean("rememberPassword", true);
        if (!"".equals(userCode)) {
            setEText("userCode", userCode);
        }
        ((CheckBox) findViewById(R.id.rememberPassword))
                .setChecked(rememberPassword);
        if (rememberPassword) {
            setEText("password", password);
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        String wsUrl = settings.getString("wsUrl", "");
        if (wsUrl != null && !wsUrl.equals("")) {
            setEText("wsUrl", wsUrl);
        }
    }

    public void toggleViewPassword(View view) {
        CheckBox c = (CheckBox) view;
        TextView v = (TextView) findViewById(R.id.password);
        if (c.isChecked()) {
            v.setTransformationMethod(null);
        } else {
            v.setTransformationMethod(new PasswordTransformationMethod());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int i = Menu.FIRST;
        i++;
        menu.add(Menu.NONE, i, 1, "Intranet").setIcon(R.drawable.cm_setting);
        i++;
        menu.add(Menu.NONE, i, 2, "Internet").setIcon(R.drawable.cm_setting);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Editor editor = getSharedPreferences(C.APP_ID, MODE_PRIVATE).edit();
        String url = "";
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                url = UI.setServerType(C.SERVER_INTRANET);
                editor.putInt("serverType", C.SERVER_INTRANET);
                break;
            case Menu.FIRST + 2:
                url = UI.setServerType(C.SERVER_INTERNET);
                editor.putInt("serverType", C.SERVER_INTERNET);
                break;
        }
        editor.commit();
        saveSetting("wsUrl", url);
        return super.onOptionsItemSelected(item);
    }
}
