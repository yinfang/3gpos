package com.clubank.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context) {
		super(context, "dish.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {// 数据库第一次被创建的时候调用的
		db.execSQL("create table dishCategory(id integer primary key autoincrement, code varchar(20) NULL, name varchar(15) NULL)");
		db.execSQL("create table dish(id integer primary key autoincrement, name varchar(20) NULL, code varchar(15) NULL,price varchar(10) NULL,category varchar(10) NULL)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("alter table dish add amount integer");
	}

}
