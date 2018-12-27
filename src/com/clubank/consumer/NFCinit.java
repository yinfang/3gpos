package com.clubank.consumer;

import com.clubank.pos.R;
import com.clubank.util.UI;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.widget.TextView;

public class NFCinit {
	private static final int RESULT_NO_NFC = 100;
	private static NfcAdapter mAdapter; // 默认的NFC控制器
	private static PendingIntent mPendingIntent;
	private static IntentFilter[] mFilters;
	private static String[][] mTechLists;

   
        private static NFCinit instance;  
        private NFCinit (){}  
        @SuppressLint("NewApi")
		public static synchronized NFCinit getInstance(Context c) {  
        if (instance == null) {  
        	
        	
        			instance = new NFCinit();  
            		mAdapter = NfcAdapter.getDefaultAdapter(c.getApplicationContext());
            		
            		if(null != mAdapter ){
        				IntentFilter ndef = new IntentFilter(
        						NfcAdapter.ACTION_TECH_DISCOVERED);
        				try {
        					ndef.addDataType("*/*");
        				} catch (MalformedMimeTypeException e) {
        					throw new RuntimeException("fail", e);
        				}
        				mFilters = new IntentFilter[] { ndef, };
        				mTechLists = new String[][] { new String[] { MifareClassic.class
        						.getName() } };
        			}
            		int version = ((BaseActivity)c).settings.getInt("versionSwitch", 0);// 0默认为iData版
            		if(version == 0){
            			
            		}else{
            			mPendingIntent = PendingIntent.getActivity(c, 0,
            					new Intent(c, NFCMainActivity.class)
            							.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            		}
            		
     		if (mAdapter != null) {
     
     		} else {
     			UI.showInfo(c, R.string.lbl_noNFC, RESULT_NO_NFC);
     		}
        }  
       
        return instance;  
        }  
        
        
        public  NfcAdapter getAdapter(){
        	return mAdapter;
        }
        
        public  PendingIntent getmPendingIntent(){
        	return mPendingIntent;
        }
        public  IntentFilter[] getmFilters(){
        	return mFilters;
        }
        public  String[][] getmTechLists(){
        	return mTechLists;
        }
        
        public void releseInstance(){
        	instance = null;
        }
    
}
