package com.clubank.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBBusiness {

	private DBOpenHelper dbOpenHelper;
	public boolean firstDelete = true;

	public DBBusiness(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
	}

	// 保存菜品类别
	public void saveDishCategory(Object... args) {
		MyData data = (MyData) args[0];
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (firstDelete) {// 先清空之前的数据
			deleteDishCategory();
		}
		ContentValues values = new ContentValues();
		for (MyRow row : data) {
			values.put("code", row.getString("Code"));
			values.put("name", row.getString("Name"));
			db.insert("dishCategory", null, values);
		}
	}

	// 保存菜品
	public void saveDish(Object... args) {
		MyData data = (MyData) args[0];
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (firstDelete) {// 先清空之前的数据
			deleteDish();
		}
		ContentValues values = new ContentValues();
		for (MyRow row : data) {
			values.put("name", row.getString("Name"));
			values.put("price", row.getString("Price"));
			values.put("code", row.getString("Code"));
			values.put("category", row.getString("Category"));
			db.insert("dish", null, values);
		}
	}

	public void deleteDishCategory() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete("dishCategory", null, null);
	}

	public void deleteDish() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete("dish", null, null);
	}

	// 获取全部菜品类别
	public MyData getDishCategoryData() {
		MyData data = new MyData();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from dishCategory", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String code = cursor.getString(cursor.getColumnIndex("code"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			MyRow row = new MyRow();
			row.put("Code", code);
			row.put("Name", name);
			data.add(row);
			cursor.moveToNext();
		}
		cursor.close();
		return data;
	}

	// 根据(类型)category找到dish
	public MyData getDishDataByCode(Object... args) {
		MyData data = new MyData();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from dish where category=?",
				new String[] { args[0].toString() });
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String code = cursor.getString(cursor.getColumnIndex("code"));
			String price = cursor.getString(cursor.getColumnIndex("price"));
			String category = cursor.getString(cursor
					.getColumnIndex("category"));
			MyRow row = new MyRow();
			row.put("Name", name);
			row.put("Code", code);
			row.put("Price", price);
			row.put("Category", category);
			data.add(row);
			cursor.moveToNext();
		}
		cursor.close();
		return data;
	}

	// 获取全部菜品数据
	public MyData getDishData() {
		MyData data = new MyData();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from dish", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String code = cursor.getString(cursor.getColumnIndex("code"));
			String price = cursor.getString(cursor.getColumnIndex("price"));
			String category = cursor.getString(cursor
					.getColumnIndex("category"));
			MyRow row = new MyRow();
			row.put("Name", name);
			row.put("Code", code);
			row.put("Price", price);
			row.put("Category", category);
			data.add(row);
			cursor.moveToNext();
		}
		cursor.close();
		return data;
	}

}
