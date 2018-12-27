package com.nfc.dataobject;

public class MifareBlock {

	public int blockIndex;
	public boolean needRead = true;
	public boolean needWrite = false;

	public MifareBlock(byte[] dataValue) {
		if (dataValue == null || dataValue.length != 16) {
			throw new IllegalArgumentException("Invaid data array");
		}
		System.arraycopy(dataValue, 0, data, 0, dataValue.length);
	}

	public MifareBlock() {

	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] dataValue) {
		if (dataValue == null || dataValue.length != 16) {
			throw new IllegalArgumentException("Invaid data array");
		}
		System.arraycopy(dataValue, 0, data, 0, dataValue.length);
	}

	private final byte[] data = new byte[16];
}
