package com.clubank.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyRow extends HashMap<String, Object> implements Serializable {

	private static final long serialVersionUID = 1L;

	public String getString(String name) {
		return (String) get(name);
	}

	public int getInt(String name) {
		int ret = 0;
		try {
			ret = Integer.valueOf(get(name).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public double getDouble(String name) {
		double ret = 0;
		try {
			ret = Double.valueOf(get(name).toString());
		} catch (Exception e) {
			// ignore error;
		}
		return ret;
	}

	public boolean getBoolean(String name) {
		boolean ret = false;
		try {
			ret = Boolean.valueOf(get(name).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static MyRow fromMap(Map<?, ?> map) {
		if (map == null) {
			return null;
		}
		MyRow row = new MyRow();
		Iterator<?> it = map.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			row.put(key.toString(), map.get(key));
		}
		return row;
	}
}
