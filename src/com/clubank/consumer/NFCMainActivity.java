package com.clubank.consumer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.clubank.domain.C;
import com.clubank.pos.R;
import com.clubank.util.UI;
import com.nfc.dataobject.MifareBlock;
import com.nfc.dataobject.MifareClassCard;
import com.nfc.dataobject.MifareSector;

@SuppressLint("NewApi")
public class NFCMainActivity extends BaseActivity {

	private static final int RESULT_NO_NFC = 100;
	
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private TextView txtView;
	private String[][] mTechLists;
	private String time_test;
	private long long_time;
	private int countDown = 16;
	private byte[] testByte = { 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2,
			0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2 };// 16个分区,每个分区4个块，每个块可以存放16个字节的数据=1024byte=1K

	// 关于MifareClassic卡的背景介绍：数据分为16个区(Sector) ,每个区有4个块(Block) ，每个块可以存放16字节的数据。
	// 每个区最后一个块称为Trailer ，主要用来存放读写该区Block数据的Key ，可以有A，B两个Key，每个Key
	// 长度为6个字节，缺省的Key值一般为全FF或是0. 由 MifareClassic.KEY_DEFAULT 定义。
	// 因此读写Mifare Tag 首先需要有正确的Key值（起到保护的作用），如果鉴权成功
	// 然后才可以读写该区数据。
	 

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.sense);

//		mAdapter = NfcAdapter.getDefaultAdapter(this);
//		if (mAdapter != null) {
			findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
			
		
//			// Setup an intent filter for all MIME based dispatches
			NFCinit nfcinit = NFCinit.getInstance(this);
			
			
//
			txtView = (TextView) findViewById(R.id.countDown);
			handler.postDelayed(runnable, 1000);
//		} else {
//			UI.showInfo(this, R.string.lbl_noNFC, RESULT_NO_NFC);
//			return;
//		}
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			countDown--;
			txtView.setText(getString(R.string.lbl_countDown) + countDown
					+ getString(R.string.lbl_second));
			if (countDown == 0) {
				openIntent("", -2);
				finish();
			}
			handler.postDelayed(this, 1000);
		}
	};

	@Override
	public void processDialogOK(int type, Object tag) {
		super.processDialogOK(type, tag);
		if (type == RESULT_NO_NFC) {
			finish();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
			NFCinit nfcinit = NFCinit.getInstance(this);
			NfcAdapter mAdapter = nfcinit.getAdapter();
		
			mAdapter.enableForegroundDispatch(this, nfcinit.getmPendingIntent(), nfcinit.getmFilters(),
					nfcinit.getmTechLists());
		
	}

	@Override
	public void onNewIntent(Intent intent) {
		resolveIntent(intent);
	}

	@Override
	public void onPause() {
		super.onPause();
		NFCinit nfcinit = NFCinit.getInstance(this);
		NfcAdapter mAdapter = nfcinit.getAdapter();
		if (mAdapter != null) {
			
			mAdapter.disableForegroundDispatch(this);
		}
	}

	void myResolveIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
			// When a tag is discovered we send it to the service to be save. We
			// include a PendingIntent for the service to call back onto. This
			// will cause this activity to be restarted with onNewIntent(). At
			// that time we read it from the database and view it.
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			finish();
			return;
		}
	}

	// 读卡
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
			MifareBlock mifareBlock = mifareSector.blocks[0];// 第1块
			byte[] data = mifareBlock.getData(); // 取块数据.
			String card = "";
			if (v.equals("7.5")) {
				for (int i = 0; i < data.length; i++) {
					if (data[i] == -1) {
						data[i] = 0x20;
					}
				}
				int onlyCode = getOnlyCode(data);
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
			openIntent(card, 0);
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
				openIntent(s, 0);
			}
		}

	}
	
	public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

	private void openIntent(String card, int statusCode) {
		if (statusCode != -1) {
			txtView.setVisibility(View.GONE);
			findViewById(R.id.progressBar).setVisibility(View.GONE);
		}
		Intent it = new Intent();
		it.putExtra("cardNo", card);
		it.putExtra("statusCode", statusCode);
		setResult(RESULT_OK, it);
		finish();
	}
	//滑雪卡获取全球唯一标识
	public int getOnlyCode(byte[] data){
		String c = bytesToHexString(data);
		String str = c.substring(0, 8);
		BigInteger bi = new BigInteger(str,16);
		int e = bi.intValue();
		return e;
	}

}