package com.clubank.consumer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

import com.android.RfidControll;
import com.clubank.domain.C;
import com.clubank.domain.Result;
import com.clubank.domain.S;
import com.clubank.pos.R;
import com.clubank.util.AsyncLocalTask;
import com.clubank.util.MyAsyncTask;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;
import com.clubank.util.U;
import com.clubank.util.UI;
import com.nfc.dataobject.MifareBlock;
import com.nfc.dataobject.MifareClassCard;
import com.nfc.dataobject.MifareSector;

/** 
* @ClassName: ConsumerBillActivity 
* @Description: 销售开单
*                          1.修改商品数据：showCustomDialog方法展示需要修改的数据，通过modifiQuantityAndBasePrice方法，提交修改后的数据
* @author fengyq
* @date 2014-3-5 上午9:49:06 
*  
*/
@SuppressLint("HandlerLeak")
public class ConsumerBillActivity extends BaseActivity implements
		OnItemClickListener {

	private static final int WORK_LOAD_DISHCATEGORY = 1;
	private static final int WORK_DELETE_DISHCATEGORY = 2;
	private static final int WORK_DELETE_DISH = 3;
	private static final int UPDATE_DATA = 101;
	private static final int RESULT_BROWSE = 100;
	private static final int RESULT_SELECTED = 200;
	private static final int RESULT_CODE_FROM_NFC = 110;
	private static final int RESULT_TOKEN_ERROR = 120;
	private static final int MODIFY_QUANTITY_PRICE = 121;
	protected boolean switchCardNo;
	private Handler mHandler = new MainHandler();
	RfidControll rfid = new RfidControll();
	private ListView listView;
	private MyRow row;
	private View v;
	private String time_test;
	private long long_time;
	private int countDown = 16;
	private int index;
	private RfidControll controll ;
	private byte buf[] = new byte[256];
	private static String keyA = "SEIN2K";
	private int versionSwitch;
	private byte[] testByte = { 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2,
			0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2 };// 16个分区,每个分区4个块，每个块可以存放16个字节的数据=1024byte=1K
	private NfcAdapter mAdapter;

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consumer_bill);
		
//		rfid.OpenComm();
		listView = (ListView) findViewById(R.id.listView);
		int m = settings.getInt("isModify", 1);// 为0,可以修改数量和基准价格
		if (m == 0) {
			listView.setOnItemClickListener(this);
		}

		versionSwitch = settings.getInt("versionSwitch", 0);// 如果版本为iData版。
		if (versionSwitch == 0) {
			HadwareControll.Open();
			HadwareControll.m_handler = mHandler;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		if (versionSwitch == 0) {
			controll = new RfidControll();
			controll.API_OpenComm(); // 打开设备RFID所在的串口
			handler.postDelayed(runnable, 1000);
//			findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
		} else {	
			NFCinit nfcinit = NFCinit.getInstance(this);
			mAdapter = nfcinit.getAdapter();
			PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, this.getClass())
							.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			mAdapter.enableForegroundDispatch(this, mPendingIntent, nfcinit.getmFilters(),
					nfcinit.getmTechLists());
		}
		
		if (S.oneDish.size() <= 0) {
			setVisibility("tableLayout", View.GONE);
		}
	}
	
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			handler.postDelayed(this, 1000);
			readCard();
		}
	};

	private void readCard() {
		String v = settings.getString("version", C.VERSION);
		if (v.equals("7.5") || v.equals("5.0")||v.equals("8.0")) {
			int result = controll.API_MF_Read(0x00, 0x01, 1, 2,
					keyA.getBytes(), buf);
			if (result == 0) {
				String card = "";
				try {
					for (int i = 0; i < buf.length; i++) {
						if (buf[i] == 0xFF) {
							buf[i] = 0x20;
						}
					}
					String info = new String(buf, "UTF-8");
					Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
					if(v.equals("8.0")){
						String info2 = new String(buf, "gbk");
						card=info2.replace("TSPGC",""); //洛阳伏牛山滑雪体育公园info就是卡号   用gbk编码   其余的取上面的0-15   用UTF-8编码
 						card=card.substring(0,5);
					}else if (v.equals("7.5")) {
						card = info.substring(0, 15).trim();// 以前是0,4
						card=card.replace("TSPGC",""); 						
					} else if (v.equals("5.0")) {
						card = info.substring(5,10).trim();
					} else if (v.equals("MH")) {// 观澜湖,15扇区，62块，//TODO 这个暂时不管了。
						card = info.substring(1, 16).trim();// 截取字符串
					}
					if (card != null && !card.equals("")) {
						reloadData(card);
						setEText("card", card);		
					} else {
						Toast.makeText(this, R.string.msg_readCard_failed,
								Toast.LENGTH_SHORT).show();
					
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(result == -4){
				return;
			}
		} else if (v.equals("MH")) {
			int result = controll.API_MF_Read(0x00, 0x01, 62, 1,
					keyA.getBytes(), buf);
			if (result == 0) {
				String card = "";
				try {
					String info = new String(buf, "UTF-8");
					Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
					if (v.equals("MH")) {// 观澜湖,15扇区，62块，
						card = info.substring(0, 16);// 截取字符串
					}
					if (card != null && !card.equals("")) {
//						openIntent(card);
					} else {
						Toast.makeText(this, R.string.msg_readCard_failed,
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if(versionSwitch == 1){
			resolveIntent(intent);
		}
	}
	
	@SuppressLint("NewApi")
	void resolveIntent(Intent intent) {
		Date date = new Date();
		long time_old;
		long time_new;
		String action = intent.getAction();

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			time_old = date.getTime();
			MifareClassic mfc = MifareClassic.get(tagFromIntent);
			date = new Date();
			time_new = date.getTime();

			long_time = time_new - time_old;
			time_old = time_new;
			time_test = "初始化NFC卡时间:" + Long.toString(long_time) + ";";
			MifareClassCard mifareClassCard = null;

			try {
				mfc.connect();
				date = new Date();
				time_new = date.getTime();

				long_time = time_new - time_old;
				time_old = time_new;
				time_test += "连接NFC卡时间:" + Long.toString(long_time) + ";";
				boolean auth = false;
				int secCount = mfc.getSectorCount();
				mifareClassCard = new MifareClassCard(secCount);
				int bCount = 0;
				int bIndex = 0;
				long_time = date.getTime() - long_time;
				time_test += "连接NFC卡后到开始循环读取扇区:" + Long.toString(long_time)
						+ ";";
				for (int j = 0; j < secCount; j++) {
					MifareSector mifareSector = new MifareSector();
					mifareSector.sectorIndex = j;
					auth = mfc.authenticateSectorWithKeyA(j, C.keyA.getBytes());// MifareClassic.KEY_DEFAULT
					mifareSector.authorized = auth;
					if (auth) {// 认证校验keyA密码是否正确，匹配每扇区的第4块，如果OK，就可以读取数据。
						bCount = mfc.getBlockCountInSector(j);
						bCount = Math.min(bCount, MifareSector.BLOCKCOUNT);
						bIndex = mfc.sectorToBlock(j);
						for (int i = 0; i < bCount; i++) {
							byte[] data = mfc.readBlock(bIndex);
							if (j == 12 && i == 2) {
								try {
									mfc.writeBlock(bIndex, testByte);
								} catch (IOException e) {
									time_test += "写错误信息" + e.toString() + ";";
								}
							}
							MifareBlock mifareBlock = new MifareBlock(data);
							mifareBlock.blockIndex = bIndex;
							bIndex++;
							mifareSector.blocks[i] = mifareBlock;
						}
						mifareClassCard.setSector(mifareSector.sectorIndex,
								mifareSector);
					} else {

					}
					date = new Date();
					time_new = date.getTime();

					long_time = time_new - time_old;
					time_old = time_new;
					time_test += "第" + (j + 1) + "个扇区读取时间:"
							+ Long.toString(long_time) + ";";
				}
				getCardNo(mifareClassCard);
			} catch (IOException e) {
				time_test += "写错误信息" + e.toString() + ";";
			} finally {
				if (mifareClassCard != null) {
					mifareClassCard.debugPrint();
				}
			}
		}
	}

	// 根据版本号得到相对应的卡号，读取卡号的位置不同。
	private void getCardNo(MifareClassCard mcc) {
		String v = settings.getString("version", C.VERSION);
		readCardNo(mcc, v);
	}

	private void readCardNo(MifareClassCard mcc, String v) {
		if (v.equals("7.5") || v.equals("5.0")) {
			MifareSector mifareSector = mcc.getSector(0); // 0扇区
			MifareBlock mifareBlock = mifareSector.blocks[1];// 第1块
			byte[] data = mifareBlock.getData(); // 取块数据.
			String card = "";
			if (v.equals("7.5")) {
				for (int i = 0; i < data.length; i++) {
					if (data[i] == -1) {
						data[i] = 0x20;
					}
				}
				card = new String(data).trim();
				card=card.replace("TSPGC","");
			} else if (v.equals("5.0")) {// 6-10位为card
				for (int i = 0; i < data.length; i++) {
					if (data[i] == 32) {
						data[i] = 0x20;
					}
				}
				String s = new String(data).trim();
				if (s != null && !s.equals("")) {
					card = s.substring(5, 10);			
				}
			}
			
			if (card != null && !card.equals("")) {
				reloadData(card);
				setEText("card", card);
			} else {
				UI.showError(this, R.string.msg_readCard_timeout);
			}
			
//			openIntent(card, 0);
		} else if (v.equals("MH")) {
			MifareSector mifareSector = mcc.getSector(15); // 第15扇区，索引从0开始
			MifareBlock mifareBlock = mifareSector.blocks[2];// 索引从0开始
			byte[] data = mifareBlock.getData(); // 取块数据
			for (int i = 0; i < data.length; i++) {
				if (data[i] == 32) {
					data[i] = 0x20;
				}
			}
			String s = new String(data).trim();
			if (s != null && !s.equals("")) {
				reloadData(s);
				setEText("card", s);
			}
		}

	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(versionSwitch == 0){
			controll.API_CloseComm();
			handler.removeCallbacks(runnable);
		}
		// 清空已添加的临时数据，不然更换营业点时，上次营业点的缓存数据会出现在新的营业点上。
		S.oneDish.clear();
		HadwareControll.Close();
		HadwareControll.m_handler = null;
	}

	private class MainHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HadwareControll.BARCODE_READ: {
				String result = msg.obj + "\n";
				setEText("productCode", result);
				playSound();
				break;
			}
			default:
				break;
			}
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10
				|| keyCode == KeyEvent.KEYCODE_F11) {
			((EditText) findViewById(R.id.productCode)).requestFocus();
			playerWhich = true;
			HadwareControll.scan_start();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10
				|| keyCode == KeyEvent.KEYCODE_F11) {
			HadwareControll.scan_stop();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	/** 
	* @Title: ok 
	* @Description: 确定
	*/
	public void ok(View view) {
		((EditText) this.findViewById(R.id.card)).requestFocus();
		String cardNo = getEText("card");
		if (cardNo != null && !cardNo.equals("")) {
			reloadData(cardNo);
		} else {
			Intent i = new Intent(this,ShareBillActivity.class);
			i.putExtra("type", "0");
			startActivity(i);
		}
	}

	// 取开单数据
	private void reloadData(String cardNo) {
		new MyAsyncTask(this, C.OP_QUERY_GUEST).execute(S.m.Token, cardNo);
	}

	@Override
	public void onPostExecute(String action, Result result) {
		super.onPostExecute(action, result);
		if (action.equals(C.OP_QUERY_GUEST)) {
			if (result.code == C.RESULT_SUCCESS) {
				S.expendBill = (MyRow) result.obj;
				if (S.expendBill != null && S.expendBill.size() > 0) {
					setVisibility("billDetailLayout", View.VISIBLE);
					setVisibility("billInqueryBtn", View.VISIBLE);
					setVisibility("resetCardBtn", View.VISIBLE);
					setVisibility("showBillDataLayout", View.GONE);
					setEText("cardNo", S.expendBill.getString("CardNo"));
					setEText("name", S.expendBill.getString("GuestName"));
					setEText("expendDate", S.expendBill.getString("ExpendDate"));
				}
			} else if (result.code == C.RESULT_GET_DATA_FAIL) {
				UI.showError(this, getString(R.string.lbl_cardNotRegistered));
				return;
			} else if (result.code == C.RESULT_TOKEN_ERROR) {
				UI.showInfo(this, getString(R.string.lbl_tokenError),
						RESULT_TOKEN_ERROR);
				return;
			}
		}
	}

//	// 重置卡号
//	public void resetCard(View view) {
//		S.expendBill.clear();
//		setEText("card", "");
//		((EditText) this.findViewById(R.id.card)).requestFocus();
//		setVisibility("billDetailLayout", View.GONE);
//		setVisibility("showBillDataLayout", View.VISIBLE);
//	}

	// 感卡
	public void senseCards(View view) {
		String s = getCardUid();
		EditText mUidxet = (EditText)findViewById(R.id.card);
		mUidxet.setText(s);
		
		setEText("card", "");
		int version = settings.getInt("versionSwitch", 0);// 0默认为iData版
		Intent it;
		if (version == 0) {
			it = new Intent(this, RfidActivity.class);
		} else {
			
			it = new Intent(this, NFCMainActivity.class);
		}
		it.putExtra("title", getString(R.string.lbl_senseCard));
		startActivityForResult(it, RESULT_CODE_FROM_NFC);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onPause() {
		super.onPause();
		if(versionSwitch == 1){
			NFCinit nfcinit = NFCinit.getInstance(this);
			NfcAdapter mAdapter = nfcinit.getAdapter();
			if (mAdapter != null) {
				
				mAdapter.disableForegroundDispatch(this);
			}
		}
	}
	
	public String getCardUid(){
		int res = 1;
		byte[] uid = new byte[4];
		byte[] pdata = new byte[1];
		pdata[0] = 0x00;
		byte buffer[] = new byte[256];
		res = rfid.API_MF_Request(0, 0x26, buffer);
		if (res == 0) {
			res = rfid.API_MF_Anticoll(0, pdata, buffer);
			if (res == 0) {
				System.arraycopy(buffer, 0, uid, 0, 4);
				return toHexString(uid, 4)+"";
			}
			if (res == 0) {
				res = rfid.API_MF_Select(0, uid, buffer);
			}
		}
		return null;
	}
	
	private String toHexString(byte[] byteArray, int size) {
		if (byteArray == null || byteArray.length < 1)
			throw new IllegalArgumentException(
					"this byteArray must not be null or empty");
		final StringBuilder hexString = new StringBuilder(2 * size);
		for (int i = 0; i < size; i++) {
			if ((byteArray[i] & 0xff) < 0x10)//
				hexString.append("0");
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
			if (i != (byteArray.length - 1))
				hexString.append("");
		}
		return hexString.toString().toUpperCase();
	}


	// 确定添加商品
	public void addProduct(View view) {
		((EditText) findViewById(R.id.productCode)).requestFocus();
		String code = getEText("productCode");
		if (code != null && !code.equals("")) {
			if (isExist(S.dish, code)) {
				setEText("productCode", "");
				selectedProduct(S.oneDish);
				return;
			} else {
				UI.showInfo(this, R.string.msg_notProduct);
				return;
			}
		} else {
			UI.showError(this, getString(R.string.msg_productNumber));
			return;
		}
	}

	// 浏览
	public void browse(View view) {
		setEText("productCode", "");
		// tag营业点标记，如果切换了营业点，清空之前所有的缓存数据.
//		boolean tag = settings.getBoolean("salePointTag", false);//TODO 2014.5.14 13:37
		boolean updated = settings.getBoolean("updated", false);
		if (!updated) {// 如果切换了营业点，没有更新数据，删除缓存数据。
			new AsyncLocalTask(this, WORK_DELETE_DISHCATEGORY,
					C.LOCAL_DELETE_DISHCATEGORY).execute();
			new AsyncLocalTask(this, WORK_DELETE_DISH, C.LOCAL_DELETE_Dish)
					.execute();
			S.dish.clear();
			S.dishCategory.clear();
		}
		if (S.dishCategory.size() > 0) {
			browseIntent();
		} else {
			// 首先加载数据库的缓存数据,本地没有缓存数据，则联网更新。
			new AsyncLocalTask(this, WORK_LOAD_DISHCATEGORY,
					C.LOCAL_GET_DISHCATEGORYDATA).execute();
		}
	}

	private void browseIntent() {
		Intent it = new Intent(this, BrowseDishActivity.class);
		it.putExtra("title", getString(R.string.lbl_browseProduct));
		it.putExtra("browse", true);
		startActivityForResult(it, RESULT_BROWSE);
	}

	// 已选
	public void selected(View view) {
		if (S.oneDish.size() > 0) {
			Intent it = new Intent(this, SelectedProductActivity.class);
			it.putExtra("title", getString(R.string.lbl_selectedProduct));
			startActivityForResult(it, RESULT_SELECTED);
		}
	}

	// 账单查询
	public void billInquery(View view) {
		Intent it = new Intent(this, ShareBillActivity.class);
		it.putExtra("title", getString(R.string.lbl_billInquery));
		it.putExtra("billInquery", true);
		it.putExtra("switchCardNo", switchCardNo);
		startActivity(it);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		if (requestCode == RESULT_BROWSE && data != null) {
			Bundle b = data.getExtras();
			MyRow row = U.getRow(b, "row");
			String code = row.getString("Code");
			if (code != null && !code.equals("")) {
				((EditText) this.findViewById(R.id.productCode)).requestFocus();
				setEText("productCode", code);
			}
		} else if (requestCode == RESULT_CODE_FROM_NFC && data != null) {
			Bundle b = data.getExtras();
			String codeNo = b.getString("cardNo");
			int statusCode = b.getInt("statusCode");
			if (codeNo != null && !codeNo.equals("") && statusCode == 0) {
				reloadData(codeNo);
				setEText("card", codeNo);
			} else {
				UI.showError(this, R.string.msg_readCard_timeout);
			}
		} else if (requestCode == RESULT_SELECTED) {
			((EditText) this.findViewById(R.id.card)).requestFocus();
		}
	}

	private boolean isExist(MyData data, String code) {
		double quantity = 1;// 默认商品数量
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getString("Code").equals(code)) {// 商品编号存在于S.dish，添加到结果集。
				boolean exist = exitsData(code);
				if (!exist) {
					MyRow row = data.get(i);// 如果直接将取出来的MyRow放到一个新的容器中,会出现,更改价格和数量时,S.dish容器的数据也会更改.
					String name = row.getString("Name");
					String price = row.getString("Price");
					String codeNo = row.getString("Code");

					MyRow rw = new MyRow();
					rw.put("Names", name);
					rw.put("Prices", price);
					rw.put("BasePrices", price);
					rw.put("Codes", codeNo);
					rw.put("Quantitys", quantity);
					rw.put("Operators", S.m.UserCode);
					S.oneDish.add(rw);
				}
				return true;// 找到了返回真。
			}
		}
		return false;
	}

	private boolean exitsData(String code) {
		// 如果code存在于S.oneDish集合中，将S.oneDish中Row对象的数量与价格增加。
		double quantity = 1;// 默认商品数量
		for (int k = 0; k < S.oneDish.size(); k++) {
			if (code.equals(S.oneDish.get(k).getString("Codes"))) {
				MyRow row = S.oneDish.get(k);
				double q = row.getDouble("Quantitys");
				double basePrice = Double.parseDouble(row
						.getString("BasePrices")); // 基准价格(不变)
				double price = Double.parseDouble(row.getString("Prices"));
				q += quantity;
				price += basePrice;

				row.put("Quantitys", "" + q);
				row.put("Prices", "" + price);
				S.oneDish.remove(k);
				S.oneDish.add(k, row);
				return true;
			}
		}
		return false;
	}

	private void selectedProduct(MyData data) {
		if (data.size() > 0) {
			setVisibility("tableLayout", View.VISIBLE);
			adapter = new ConsumerBillAdapter(this, data);
			listView.setAdapter(adapter);
		}
	}

	@Override
	public void afterWork(int id, Result result) {
		if (id == WORK_LOAD_DISHCATEGORY) {
			MyData data = (MyData) result.obj;
			if (data.size() > 0) {
				S.dishCategory = data;
				//浏览
				browseIntent();
			} else {
				// 本地没有缓存数据，则联网更新。
				UI.showOKCancel(this, UPDATE_DATA, R.string.msg_notData,
						R.string.lbl_getDataFail);
			}
		}
	}

	@Override
	public void processDialogOK(int type, Object tag) {
		super.processDialogOK(type, tag);
		if (type == RESULT_TOKEN_ERROR) {
			Intent it = new Intent(this, LoginActivity.class);
			it.putExtra("title", getString(R.string.lbl_SystemLogin));
			it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(it);
			finish();
		} else if (type == UPDATE_DATA) {
			openIntent(this, SettingActivity.class,
					getString(R.string.lbl_setting));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		index = pos;
		row = (MyRow) parent.getItemAtPosition(pos);
		showCustomDialog(MODIFY_QUANTITY_PRICE,
				getString(R.string.lbl_enter_quantity_price),
				R.layout.modify_input_text, 1);
	}

	private void setValue(View v) {
		EditText quantity = (EditText) v.findViewById(R.id.quantity);
		EditText price = (EditText) v.findViewById(R.id.basePrice);
		EditText Name = (EditText) v.findViewById(R.id.dishName);
		if (row != null) {
			quantity.setText("" + row.getDouble("Quantitys"));
			price.setText(row.getString("BasePrices"));
			Name.setText(row.getString("Names"));
		}
	}

	@Override
	protected void initDialog(int type, View view) {
		if (type == MODIFY_QUANTITY_PRICE) {
			v = view;
			setValue(view);
		}
	}

	/**
	 * 确定对话框
	 */
	@Override
	protected boolean finishDialog(int type, Dialog dialog) {
		if (type == MODIFY_QUANTITY_PRICE) {
			modifiQuantityAndBasePrice();
		}
		return true;
	}

	/** 
	* @Title: modifiQuantityAndBasePrice 
	* @Description:修改菜品数据
	*/
	public void modifiQuantityAndBasePrice() {
		EditText et1 = (EditText) v.findViewById(R.id.quantity);
		EditText et2 = (EditText) v.findViewById(R.id.basePrice);
		EditText et3 = (EditText) v.findViewById(R.id.dishName);
		double q = Double.parseDouble(et1.getText().toString());
		double basePrice = Double.parseDouble(et2.getText().toString());
		double totalPrice = q * basePrice;
        String  dishName = et3.getText().toString();
		MyRow row = S.oneDish.get(index);
		S.oneDish.remove(index);
		row.put("Quantitys", q);
		row.put("BasePrices", "" + basePrice);
		row.put("Prices", "" + totalPrice);
		row.put("Names", dishName);
		S.oneDish.add(index, row);
		adapter.notifyDataSetChanged();
	}

}
