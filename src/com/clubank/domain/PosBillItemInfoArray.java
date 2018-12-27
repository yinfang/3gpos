package com.clubank.domain;

import java.util.Hashtable;
import java.util.Vector;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class PosBillItemInfoArray extends Vector<PosBillItemInfo> implements
		KvmSerializable {

	private static final long serialVersionUID = -7602851112958167916L;

	public Object getProperty(int index) {
		return super.get(index);
	}

	public int getPropertyCount() {
		return size();
	}

	@SuppressWarnings("rawtypes")
	public void getPropertyInfo(int index, Hashtable properties,
			PropertyInfo info) {
		info.name = "PosBillItemInfo";
		info.type = PosBillItemInfo.class;
	}

	public void setProperty(int index, Object value) {
		add((PosBillItemInfo) value);
	}

}
