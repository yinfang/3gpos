package com.clubank.domain;

import com.clubank.util.MyData;
import com.clubank.util.MyRow;

public class S {

	public static MemberInfo m = new MemberInfo();
	public static MyData dishCategory = new MyData(); // 商品类别
	public static MyData dish = new MyData(); // 商品
	public static MyRow expendBill = new MyRow(); // 消费开单信息
	public static MyData billInfo = new MyData();
	public static MyData oneDish = new MyData();

	public static void clear() {
		expendBill.clear();
		oneDish.clear();
	}
}
