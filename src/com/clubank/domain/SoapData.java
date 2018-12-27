package com.clubank.domain;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SoapData implements KvmSerializable, Serializable {

	/**
	 * webservice序列化对象参数，可以实现传递对象到WebService，因为ksoap2默认只支持4种类型。
	 */
	private static final long serialVersionUID = 1L;

	public Object getProperty(int arg0) {
		Field[] fields = getClass().getFields();
		try {
			return fields[arg0].get(this);
		} catch (Exception e) {
		}
		return null;
	}

	public int getPropertyCount() {
		Field[] fields = getClass().getFields();
		return fields.length;
	}

	@SuppressWarnings("rawtypes")
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {

		Field[] fields = getClass().getFields();

		arg2.type = fields[arg0].getType();
		arg2.name = fields[arg0].getName();
	}

	public void setProperty(int arg0, Object arg1) {
		Field[] fields = getClass().getFields();
		try {
			fields[arg0].set(this, arg1);
		} catch (Exception e) {
		}
	}
}
