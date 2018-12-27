package com.clubank.consumer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.RfidControll;
import com.clubank.domain.C;
import com.clubank.pos.R;

public class RfidActivity extends BaseActivity {

	private static final int RESULT_NO_RFID = 100;
	private RfidControll controll = new RfidControll();
	private TextView txtView;
	private static String keyA = "SEIN2K";
	private int countDown = 16;
	private byte buf[] = new byte[256];

    public RfidActivity() {  
         Log.e("", "");
    }  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sense);
		controll.API_OpenComm(); // 打开设备RFID所在的串口
		// int isOpenSuccess = controll.API_GetSerNum(0X00, buffer);
		// if (isOpenSuccess == 0) { // 0 打开设备成功，-1为不成功！
		txtView = (TextView) findViewById(R.id.countDown);
		handler.postDelayed(runnable, 1000);
		findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
		// } else {
		// UI.showInfo(this, R.string.lbl_noRFID, RESULT_NO_RFID);
		// return;
		// }
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			countDown--;
			txtView.setText(getString(R.string.lbl_countDown) + countDown
					+ getString(R.string.lbl_second));
			if (countDown == 0) {
				openIntent("");
				finish();
			}
			handler.postDelayed(this, 1000);
			readCard();
		}
	};

	private void readCard() {
		// 功能：API_MF_Read 集成寻卡，防冲突，选卡，验证密码，读卡等操作，一个命令完成。
		// 参数1：设备地址，要通讯的设备地址号，范围从0－255。
		// 参数2：mode读取模式控制
		// 　　0x00: Idle模式 + Key A
		// 　　0x01: All模式 + Key A
		// 　　0x02: Idle模式 + Key B
		// 　　0x03: All模式 + Key B
		// 参数3：要读的块的起点地址, 范围从0--63
		// 参数4：要读的块数长度值, 范围从1—4（m1卡）
		// 参数5：6个字节的密钥
		// 参数6：如果成功，4字节卡序列号 (从低到高) buffer[0-N]: 从卡上返回的数据(16*num_blk字节)
		// 　　 如果失败，buffer[0]: 返回状态，具体含义见附表。

		String v = settings.getString("version", C.VERSION);
		if (v.equals("7.5") || v.equals("5.0")||v.equals("8.0")) {
			/*
			 * int result = controll.API_MF_Read(0x00, 0x01, 1, 2,
			 * keyA.getBytes(), buffer);
			 */
			// res = rfid
			// .API_MF_Read(0x00, 0x01, 1, 2, keyA.getBytes(), bf);

			int result = controll.API_MF_Read(0x00, 0x01, 1, 2,
					keyA.getBytes(), buf);

			/*
			 * if (result == 0) { mUidxet.setText(toHexString(snr, 4));
			 * mRecetxet.setText(toHexString(buffer1, 1 * 16)); Log.d("012",
			 * toHexString(buffer1, 1 * 16)); }
			 */

			if (result == 0) {
				String card = "";
				try {
					for (int i = 0; i < buf.length; i++) {
						if (buf[i] == 0xFF) {
							buf[i] = 0x20;
						}
					}
					String info = new String(buf, "UTF-8");
//					String info = new String(buf, "gbk");
					Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
					
					
					if(v.equals("8.0")){
						String info2 = new String(buf, "gbk");
						card=info2.replace("TSPGC",""); //洛阳伏牛山滑雪体育公园info就是卡号   用gbk编码   其余的取上面的0-15   用UTF-8编码
 						card=card.substring(0,5);
					}else if (v.equals("7.5")) {
						card = info.substring(0, 15).trim();// 以前是0,4
						card=card.replace("TSPGC",""); 						
					} else if (v.equals("5.0")) {
//						card = info.substring(4, 9).trim();// 以前是5,9 4,14
						card = info.substring(5,10).trim();
					} else if (v.equals("MH")) {// 观澜湖,15扇区，62块，//TODO 这个暂时不管了。
						card = info.substring(1, 16).trim();// 截取字符串
					}
					if (card != null && !card.equals("")) {
						openIntent(card);
					} else {
						Toast.makeText(this, R.string.msg_readCard_failed,
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
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
						openIntent(card);
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

	private void openIntent(String card) {
		handler.removeCallbacks(runnable);
		txtView.setVisibility(View.GONE);
		findViewById(R.id.progressBar).setVisibility(View.GONE);
		Intent it = new Intent();
		it.putExtra("cardNo", card);
		setResult(RESULT_OK, it);
		if (!card.equals("")) {
			playSound();
		}
		finish();
	}

	protected void onDestroy() {
		super.onDestroy();
		// 关闭RFID串口
		controll.API_CloseComm();
		handler.removeCallbacks(runnable);
	}

	@Override
	public void processDialogOK(int type, Object tag) {
		super.processDialogOK(type, tag);
		if (type == RESULT_NO_RFID) {
			finish();
		}
	}

}
