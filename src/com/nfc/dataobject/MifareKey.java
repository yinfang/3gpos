package com.nfc.dataobject;

import android.annotation.SuppressLint;
import android.nfc.tech.MifareClassic;

@SuppressLint("NewApi")
public class MifareKey {

	/**
	 * Constructor.
	 * 
	 * @param keyValue
	 *            the key value.
	 */
	public MifareKey(byte[] keyValue) {
		if (keyValue == null || keyValue.length != 6) {
			throw new IllegalArgumentException("Invaid key");
		}
		key = new byte[6];
		System.arraycopy(keyValue, 0, key, 0, key.length);
	}

	/**
	 * Default constructor.
	 * 
	 */
	public MifareKey() {
		key = MifareClassic.KEY_DEFAULT;
	}

	/**
	 * Get the key value.
	 * 
	 * @return the byte array of the key.
	 */
	public byte[] getKey() {
		return key;
	}

	/**
	 * set the new key.
	 * 
	 * @param keyValue
	 *            the key value.
	 */
	public void setKey(byte[] keyValue) {
		if (keyValue == null || keyValue.length != 6) {
			throw new IllegalArgumentException("Invaid key");
		}
		key = new byte[6];
		System.arraycopy(keyValue, 0, key, 0, key.length);
	}

	/**
	 * this is the key values (6 bytes)
	 */
	private byte[] key;
}
