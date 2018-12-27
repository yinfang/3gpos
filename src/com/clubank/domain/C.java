package com.clubank.domain;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

@SuppressLint("SimpleDateFormat")
public class C {

	public static String keyA = "SEIN2K"; // NFC keyA
	public static String VERSION = "7.5";

	public static final int SERVER_INTRANET = 1;
	public static final int SERVER_INTERNET = 2;
	public static final int SERVER_TEST = 3;
	public static final String IMAGE_URL_CLUB = "http://img.golfbaba.com/";

	public static final String IMAGE_URL_MEMBER_INTRANET = "http://192.168.0.57:8030/member/";
	public static final String IMAGE_URL_MEMBER_INTERNET = "http://img.golfbaba.com/member/";
	public static final String IMAGE_URL_MEMBER_TEST = "http://sein.3322.org/member/";

	public static final String BASE_CONF_URL_INTRANET = "http://192.168.0.40:8080/";
	// public static final String BASE_URL_INTRANET ="http://192.168.0.40:8080/";
	public static final String BASE_URL_INTRANET = "http://192.168.0.7:8089/";
	public static final String DOWN_BASE_URL = BASE_URL_INTRANET + "posServer/";

	public static final String BASE_CONF_URL_INTERNET = "http://conf.golfbaba.com/";
	public static final String BASE_URL_INTERNET = "http://220.231.128.182:8015/";
	public static final String DOWN_BASE_URL_INTERNET = "http://192.168.0.40:8080/posServer/";

	public static final String BASE_CONF_URL_TEST = "http://confws.3322.org:8010/";
	public static final String BASE_URL_TEST = "http://managews.3322.org:8010/";

	public static final String NameSpace = "http://www.golfbaba.com/";

	public static final String COMMON_INFO_URL = "http://3g.golfbaba.com";
	public static final String APP_ID = "Manage";

	public static final DecimalFormat nf_i = new DecimalFormat("#,##0");// integer
	public static final DecimalFormat nf_a = new DecimalFormat("#,##0.00");// amount
	public static final DecimalFormat nf_l = new DecimalFormat("###0.000000");// GPS
	public static final DateFormat df_y = new SimpleDateFormat("yyyy");
	public static final DateFormat df_yM = new SimpleDateFormat("yyyy-MM");
	public static final DateFormat df_yMd = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat df_yMdHm = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	public static final DateFormat df_yMdHms = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static final DateFormat df_Hm = new SimpleDateFormat("HH:mm");

	public static final int cnt = 25;// 每页数据条数
	public static final int compress = 80;
	public static final String UNSPECIFIED_IMAGE = "image/*";

	public static final int RESULT_SUCCESS = 0;// 操作成功
	public static final int RESULT_AUTH_FAILED = -1;// 认证失败
	public static final int RESULT_SAVE_DATA_FAIL = -3;// 保存数据失败
	public static final int RESULT_GET_DATA_FAIL = -6;// 获取数据失败
	public static final int RESULT_TOKEN_ERROR = -7;// token令牌数据错误
	public static final int RESULT_POINT_NO_DATA = -8;// 营业点没有数据
	public static final  int RESULT_CANCELED = -9;//已经被取消或者已结帐
	public static final int RESULT_ATHENTICATION_FAILED = 11;// 操作权限校验失败
	public static final int RESULT_INVALID_USER_PASSWORD = 12;// 用户名或密码错误
	public static final int RESULT_INVALID_OLD_PASSWORD = 13;// 旧密码不对
	public static final int RESULT_INVALID_VERIFICATION_CODE = 14;// 验证码不对
	public static final int RESULT_MEMBER_ACTIVATED = 15;// 会员已经被激活
	public static final int RESULT_DUPLICATE_USERNAME = 30;// 重复用户名
	public static final int RESULT_DUPLICATE_MOBILE_NO = 31;// 重复用户名
	public static final int RESULT_DUPLICATE_EMAIL = 32;// 重复用户名
	public static final int RESULT_VERIFY_USERNAME_FAILED = 33;// 用户名校验失败
	public static final int RESULT_INVALID_MOBILE_NO = 40;// 无效的手机号
	public static final int RESULT_NOT_ONLINE_CLUB = 41;// 非在线球会
	public static final int RESULT_INVALID_EMAIL = 42;// 无效的Email
	public static final int RESULT_INVALID_MEMNO = 43;// 无效的会员号
	public static final int RESULT_CLUB_APP_OBSOLETE = 90;// 球会端程序已过时。
	public static final int RESULT_UNKNOWN_ERROR = 99;// 其它错误

	public static final int TYPE_SMS = 1;
	public static final int TYPE_EMAIL = 2;
	public static final int TIMEOUT = 50000;

	public static final int RESULT_SOCKET_TIMEOUT = 100;
	public static final int RESULT_SOCKET_ERROR = 101;// 网络不通
	public static final int RESULT_SERVER_ERROR = 102;
	public static final int RESULT_LOWER_CLIENT_VERSION = 105;
	public static final int RESULT_ILLEGAL_ACCESS = 403;
	public static int IS_SHOW_STOCK = 1001;//是否显示库存布局

	/* 远程方法名 */
	public static final String OP_CANCEL_BILL_BY_CODE = "CancelBillByCode";
	public static final String OP_CHECK_NEW_VERSION = "CheckNewVersion";
	public static final	String OP_GET_CAN_QUERY_STOCK="Get3GPOSCanQueryStockInHand";//是否支持库存查询
	public static final String OP_GET_BILL_BYCARD = "GetBillByCard";
	public static final String OP_GET_BILLITEMSINFO_BY_WORKSHOP = "GetBillItemsInfoByWorkShop";
	public static final String OP_GET_BILLS = "GetBills";
	public static final String OP_GET_DISH = "GetDish";
	public static final String OP_GET_DISH_CATEGORY = "GetDishCategory";
	public static final String OP_GET_SALE_STAT = "GetSaleStat";
	public static final	String OP_GET_STOCK_BYCODE="GetStockInHandQtyByStockCode";//获取库存信息
	public static final	String OP_GET_STOCK_LIST="GetStockList";//获取库存列表
	public static final String OP_LOGIN = "Login";
	public static final String OP_QUERY_GUEST = "QueryGuest";
	public static final String OP_SAVE_BILL = "SaveBill";
	public static final String OP_LOGOUT = "Logout";
	public static final String OP_GET_CHECKIN = "GetCheckin";
	
	
	public static final String LOCAL_GET_DISHCATEGORYDATA = "getDishCategoryData";
	public static final String LOCAL_GET_DISHDATABYCODE = "getDishDataByCode";
	public static final String LOCAL_SAVE_DISHCATEGORY = "saveDishCategory";
	public static final String LOCAL_SAVE_DISH = "saveDish";
	public static final String LOCAL_DELETE_DISHCATEGORY = "deleteDishCategory";
	public static final String LOCAL_DELETE_Dish = "deleteDish";

	

	public static int height = 32;

	public static String baseConfUrl = BASE_CONF_URL_INTERNET;
	public static String baseUrl = BASE_URL_INTERNET;
	public static String wsUrl = BASE_URL_INTERNET + "PosService.asmx";
	public static String wsConfUrl = BASE_CONF_URL_INTERNET
			+ "posServer/PosService.asmx";
	public static String imageUrlMember = IMAGE_URL_MEMBER_INTERNET;

	public static final int INTERVAL_EVENT_QUERY = 10; // 查询事件间隔

	public static final String TYPE_PORTRAIT = "p";
	public static final String TYPE_LANDSCAPE = "l";
	public static final int WV_AGREEMENT = 1; // webview content type
	public static final int WV_SHARE_DOWNLOAD = 2;
	public static final int WV_MONITOR = 3;
	public static final String IMAGE_CACHE_DIR = "images";

}
