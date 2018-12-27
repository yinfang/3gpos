package com.clubank.domain;

public class MemberInfo extends SoapData {

	private static final long serialVersionUID = -4345725373205415905L;
	public int ID;
	public String UserName;// 用户名
	public String UserCode;
	public String Password;
	public String ShopCode;// 售卖点code
	public String ShopName;// 售卖点名称
	public String LoginTime;
	public String LastTime;
	public String Token;// 只在登录时用

}
