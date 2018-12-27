package com.clubank.consumer;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.clubank.domain.C;
import com.clubank.domain.Result;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.AsyncLocalTask;
import com.clubank.util.DBBusiness;
import com.clubank.util.DownloadFile;
import com.clubank.util.MyAsyncTask;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;
import com.clubank.util.UI;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingActivity extends BaseActivity {

	private int currVersion = 1;
	private static final int WORK_SAVE_DISHCATEGORY = 99;
	private static final int WORK_SAVE_DISH = 100;
	private static final int DOWNLOAD_NEW_VERSION = 101;
	private static final int UPDATE_SUCCESS = 200;
	private static final int RELOGIN = 205;
	private static final int SALE_POINT = 1;
	private static final int SERVER_URL = 2;
	private DBBusiness service = new DBBusiness(this);
	private String version[] = new String[] { "7.5", "5.0", "MH","8.0" };
	private String modify[] = new String[] { "是", "否" };
	private String[] versionSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		versionSwitch = getResources().getStringArray(R.array.versionSwitch);
		setEText("versionCode", settings.getString("version", "7.5"));

		int m = settings.getInt("isModify", 1);
		setEText("modify", modify[m]);

		int index = settings.getInt("versionSwitch", 0);
		setEText("versionType", versionSwitch[index]);
	}

	public void salePointSetting(View view) {
		showCustomDialog(SALE_POINT, getString(R.string.lbl_enter_salePoint),
				R.layout.input_text, 1);
	}

	public void IPSetting(View view) {
		showCustomDialog(SERVER_URL, getString(R.string.lbl_enter_url),
				R.layout.input_text, -1);
	}

	public void versionSetting(View view) {
		showListDialog(view, R.string.lbl_select, version);
	}

	public void versionSwitch(View view) {
		showListDialog(view, R.string.lbl_select, versionSwitch);
	}

	// 更新数据
	public void update(View view) {
		boolean tag = settings.getBoolean("salePointTag", false);
		if (tag) {
			String shopCode = settings.getString("salePoint", "");
			S.m.ShopCode = shopCode;
		}
		loadData(S.m.ShopCode);
	}

	// 检查版本
	public void updateVersion(View view) {
		new MyAsyncTask(this, C.OP_CHECK_NEW_VERSION, true).execute(S.m.Token,
				currVersion, 1, true);
	}

	public void modifyQuantityPrice(View view) {
		showListDialog(view, R.string.lbl_select, modify);
	}

	private void loadData(String shopCode) {
		// WifiManager wm = (WifiManager)
		// getSystemService(Context.WIFI_SERVICE);
		// if (!wm.isWifiEnabled()) {
		// UI.showError(this, R.string.msg_turn_on_wifi);
		// return;
		// }
		new MyAsyncTask(this, C.OP_GET_DISH_CATEGORY).execute(S.m.Token,
				shopCode);
	}

	@Override
	public void onPostExecute(String action, Result result) {
		super.onPostExecute(action, result);
		if (action.equals(C.OP_GET_DISH_CATEGORY)) {
			if (result.code == C.RESULT_SUCCESS) {
				S.dishCategory = (MyData) result.obj;
				if (S.dishCategory != null && S.dishCategory.size() > 0) {
					new AsyncLocalTask(this, WORK_SAVE_DISHCATEGORY,
							C.LOCAL_SAVE_DISHCATEGORY).execute(S.dishCategory);
					service.firstDelete = false;
					saveSetting("updated", true); // 是否更新
//					saveSetting("salePointTag", false); //TODO 2014.5.14 13:37
				}
			} else if (result.code == C.RESULT_POINT_NO_DATA) {
				UI.showInfo(this, R.string.msg_point_NoData);
				return;
			} else {
				UI.showInfo(this, R.string.msg_serverException, RELOGIN);
				return;
			}
		} else if (action.equals(C.OP_GET_DISH)) {
			if (result.code == C.RESULT_SUCCESS) {
				S.dish = (MyData) result.obj;
				if (S.dish != null && S.dish.size() > 0) {
					new AsyncLocalTask(this, WORK_SAVE_DISH, C.LOCAL_SAVE_DISH)
							.execute(S.dish);
					service.firstDelete = false;
				}
			} else if (result.code == C.RESULT_POINT_NO_DATA) {
				UI.showInfo(this, R.string.msg_point_NoData);
			}
		} else if (action.equals(C.OP_CHECK_NEW_VERSION)) {
			if (result.code == C.RESULT_SUCCESS) {
				MyRow row = (MyRow) result.obj;
				checkVersionResult(row);
			}
		}
	}

	@Override
	public void afterWork(int id, Result result) {
		if (id == WORK_SAVE_DISHCATEGORY) {
			if (result.code == C.RESULT_SUCCESS) {
				new MyAsyncTask(this, C.OP_GET_DISH).execute(S.m.Token,
						S.m.ShopCode);
			}
		} else if (id == WORK_SAVE_DISH) {
			if (result.code == C.RESULT_SUCCESS) {
				UI.showInfo(this, getString(R.string.msg_update_success),
						UPDATE_SUCCESS);
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
			if (manual) {// 手动检查版本
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

	@Override
	public void processDialogOK(int type, Object tag) {
		super.processDialogOK(type, tag);
		if (type == UPDATE_SUCCESS) {
			finish();
		} else if (type == DOWNLOAD_NEW_VERSION) {
			new DownloadFile(this).execute((String) tag);
		} else if (type == RELOGIN) {
			Intent it = new Intent(this, LoginActivity.class);
			it.putExtra("title", getString(R.string.lbl_SystemLogin));
			it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(it);
			finish();
		}
	}

	@Override
	protected void initDialog(int type, View view) {
		if (type == SALE_POINT) {
			setValue(view, "salePoint", S.m.ShopCode);
		} else if (type == SERVER_URL) {
			setValue(view, "wsUrl", C.wsUrl);
		}
	}

	@Override
	protected boolean finishDialog(int type, Dialog dialog) {
		if (type == SALE_POINT) {
			saveValue(dialog, S.m.ShopCode, "salePoint");
			saveSetting("salePointTag", true);
		} else if (type == SERVER_URL) {
			saveValue(dialog, C.wsUrl, "wsUrl");
		}
		return true;
	}

	@Override
	protected void listSelected(View view, int index) {
		int id = view.getId();
		if (id == R.id.versionSetting) {
			String v = version[index];
			setEText("versionCode", v);
			saveSetting("version", v);
		} else if (id == R.id.versionSwitch) {
			String v = versionSwitch[index];
			setEText("versionType", v);
			saveSetting("versionSwitch", index);
			 NFCinit.getInstance(this).releseInstance();;
		} else if (id == R.id.modifyQuantityPrice) {
			String v = modify[index];
			setEText("modify", v);
			saveSetting("isModify", index);
		}
	}

	private void setValue(View v, String key, String defaultValue) {
		EditText input = (EditText) v.findViewById(R.id.input);
		String sv = settings.getString(key, defaultValue);
		if (sv != null && !sv.equals("")) {
			input.setText(sv);
		}
	}

	private void saveValue(Dialog d, String setDefaultValue, String key) {
		EditText input = (EditText) d.findViewById(R.id.input);
		String v = input.getText().toString();
		if (!v.equals("")) {
			setDefaultValue = v;
			setEText("input", v);
			S.m.ShopCode = v;
			saveSetting(key, v);
		}
	}

}
