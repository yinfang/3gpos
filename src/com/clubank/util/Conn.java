package com.clubank.util;

import android.content.Context;
import android.util.Log;

import com.clubank.domain.C;
import com.clubank.domain.PosBillItemInfo;
import com.clubank.domain.PosBillItemInfoArray;
import com.clubank.domain.Result;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.util.ArrayList;
import java.util.Vector;

public class Conn {

    Context context;
    private String wsUrl = C.wsUrl;

    public Conn(Context context) {
        this.context = context;
    }

    public Conn(Context context, int wsType) {
        this.context = context;
        if (wsType == 2) {
            wsUrl = C.wsConfUrl;
        }
    }

    private Object operate(String methodName, MyRow args) throws Exception {
        Object ret = 0;
        String soapAction = C.NameSpace + methodName;
        SoapObject request = new SoapObject(C.NameSpace, methodName);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        for (String key : args.keySet()) {
            Object o = args.get(key);
            request.addProperty(key, o);
        }
        envelope.implicitTypes = true;
        envelope.dotNet = true;

        // if (S.m.ID > 0) {
        envelope.headerOut = new Element[1];
        envelope.headerOut[0] = buildAuthHeader();
        // }

        new MarshalBase64().register(envelope);

        // envelope.bodyOut = request;
        envelope.setOutputSoapObject(request);
        envelope.encodingStyle = "UTF-8";
        HttpTransportSE ht = new HttpTransportSE(wsUrl, C.TIMEOUT);
        // HttpsTransportSE ht1 = new HttpsTransportSE(wsUrl,443,null,
        // C.TIMEOUT);

        ht.debug = true;
        // List headers = new ArrayList();
        ht.call(soapAction, envelope);
        String requestXml = ht.requestDump;
        Log.d("Request", methodName + requestXml);
        if (envelope.getResponse() != null) {
            String responseXml = ht.responseDump; // 只有当ht设为可以调试时，才可以调用，得到调试信息。
            Log.d("Response", methodName + responseXml); // 打印调试信息responseDump来观察soap是否正确工作。
            ret = envelope.getResponse();
        }
        return ret;
    }

    private Element buildAuthHeader() {
        Element h = new Element().createElement(C.NameSpace,
                "AuthenticationSoapHeader");

        String slang = context.getResources().getConfiguration().locale
                .toString().substring(0, 2);
        Element lang = new Element().createElement(C.NameSpace, "Lang");
        lang.addChild(Node.TEXT, slang);
        h.addChild(Node.ELEMENT, lang);

        return h;
    }

    private MyData getList(SoapObject v) {
        MyData list = new MyData();
        for (int i = 0; i < v.getPropertyCount(); i++) {
            SoapObject so = (SoapObject) v.getProperty(i);
            list.add(getRow(so));
        }
        return list;
    }

    private MyRow getRow(SoapObject so) {
        MyRow row2 = new MyRow();
        for (int j = 0; j < so.getPropertyCount(); j++) {
            PropertyInfo pi = new PropertyInfo();
            so.getPropertyInfo(j, pi);
            String value = "";
            if (pi.getType() == SoapPrimitive.class) {
                value = pi.getValue().toString();
            }
            row2.put(pi.getName(), value);
        }
        return row2;
    }

    public Result Login(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("userCode", args[0]);
        row.put("password", args[1]);
        Object obj = operate("Login", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            if (obj instanceof SoapPrimitive) {
                result.code = Integer.parseInt(obj.toString());
            } else if (obj instanceof Vector) {
                Vector<?> v = (Vector<?>) obj;
                result.code = Integer.parseInt(v.get(0).toString());
                SoapObject so = (SoapObject) v.get(1);
                result.obj = getRow(so);
            }
        }
        return result;
    }

    public Result GetDish(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("token", args[0]);
        row.put("shopcode", args[1]);
        Object obj = operate("GetDish", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            Vector<?> s = (Vector<?>) obj;
            result.code = Integer.parseInt(s.get(0).toString());
            SoapObject v = (SoapObject) s.get(1);
            result.obj = getList(v);
        }
        return result;
    }

    public Result QueryGuest(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("token", args[0]);
        row.put("cardNo", args[1]);
        Object obj = operate("QueryGuest", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            Vector<?> s = (Vector<?>) obj;
            result.code = Integer.parseInt(s.get(0).toString());
            SoapObject so = (SoapObject) s.get(1);
            result.obj = getRow(so);
        }
        return result;
    }

    public Result GetBillByCard(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("token", args[0]);
        row.put("billcode", args[1]);
        Object obj = operate("GetBillByCard", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            Vector<?> s = (Vector<?>) obj;
            result.code = Integer.parseInt(s.get(0).toString());
            SoapObject v = (SoapObject) s.get(1);
            result.obj = getList(v);
        }
        return result;
    }

    public Result GetBills(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("token", args[0]);
        row.put("cardno", args[1]);
        row.put("shopcode", args[2]);
        Object obj = operate("GetBills", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            Vector<?> s = (Vector<?>) obj;
            result.code = Integer.parseInt(s.get(0).toString());
            SoapObject v = (SoapObject) s.get(1);
            result.obj = getList(v);
        }
        return result;
    }

    public Result GetBillItemsInfoByWorkShop(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("token", args[0]);
        row.put("shopcode", args[1]);
        Object obj = operate("GetBillItemsInfoByWorkShop", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            Vector<?> s = (Vector<?>) obj;
            result.code = Integer.parseInt(s.get(0).toString());
            SoapObject v = (SoapObject) s.get(1);
            result.obj = getList(v);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public Result SaveBill(Object... args) throws Exception {
        MyRow row = new MyRow();
        ArrayList<PosBillItemInfo> al = (ArrayList<PosBillItemInfo>) args[4];
        PosBillItemInfoArray info = new PosBillItemInfoArray();
        for (int i = 0; i < al.size(); i++) {
            info.add((PosBillItemInfo) al.get(i));
        }
        row.put("token", args[0]);
        row.put("cardNo", args[1]);
        row.put("shopCode", args[2]);
        row.put("guestNum", args[3]);
        row.put("list", info);
        Object obj = operate("SaveBill", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            if (obj instanceof SoapPrimitive) {
                result.code = Integer.parseInt(obj.toString());
            } else if (obj instanceof Vector) {
                Vector<?> v = (Vector<?>) obj;
                result.code = Integer.parseInt(v.get(0).toString());
                result.obj = v.get(1).toString();
            }
        }
        return result;
    }

    public Result GetDishCategory(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("token", args[0]);
        row.put("shopcode", args[1]);
        Object obj = operate("GetDishCategory", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            Vector<?> s = (Vector<?>) obj;
            result.code = Integer.parseInt(s.get(0).toString());
            SoapObject v = (SoapObject) s.get(1);
            result.obj = getList(v);
        }
        return result;
    }

    public Result GetSaleStat(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("token", args[0]);
        row.put("shopcode", args[1]);
        row.put("date", args[2]);
        Object obj = operate("GetSaleStat", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            Vector<?> s = (Vector<?>) obj;
            result.code = Integer.parseInt(s.get(0).toString());
            SoapObject v = (SoapObject) s.get(1);
            result.obj = getList(v);
        }
        return result;
    }

    public Result Logout(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("token", args[0]);
        Object obj = operate("Logout", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            SoapPrimitive s = (SoapPrimitive) obj;
            result.code = Integer.parseInt(s.toString());
        }
        return result;
    }

    public Result CheckNewVersion(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("token", args[0]);
        row.put("currVersion", args[1]);
        row.put("type", args[2]);
        Object obj = operate("CheckNewVersion", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            Vector<?> s = (Vector<?>) obj;
            result.code = Integer.parseInt(s.get(0).toString());
            SoapObject so = (SoapObject) s.get(1);
            MyRow row1 = getRow(so);
            row1.put("manual", args[3]);
            result.obj = row1;
        }
        return result;
    }

    public Result CancelBillByCode(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("token", args[0]);
        row.put("billcode", args[1]);
        row.put("reason", args[2]);
        Object obj = operate("CancelBillByCode", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            SoapPrimitive s = (SoapPrimitive) obj;
            result.code = Integer.parseInt(s.toString());
        }
        return result;
    }

    public Result Get3GPOSCanQueryStockInHand(Object... args) throws Exception {
        MyRow row = new MyRow();
        Object obj = operate("Get3GPOSCanQueryStockInHand", row);
        Result result = new Result(C.RESULT_UNKNOWN_ERROR);
        if (obj != null) {
            SoapPrimitive s = (SoapPrimitive) obj;
            result.code = Integer.parseInt(s.toString());
        }
        return result;
    }

    public Result GetStockList(Object... args) throws Exception {
        MyRow row = new MyRow();
        Object obj = operate("GetStockList", row);
        Result result = new Result();
        if (obj != null) {
            SoapObject v = (SoapObject) obj;
            result.obj = getList(v);
        }
        return result;
    }

    public Result GetStockInHandQtyByStockCode(Object... args) throws Exception {
        MyRow row = new MyRow();
        row.put("stockCode", args[0]);
        Object obj = operate("GetStockInHandQtyByStockCode", row);
        Result result = new Result();
        if (obj != null) {
            SoapObject v = (SoapObject) obj;
            result.obj = getList(v);
        }
        return result;
    }
}
