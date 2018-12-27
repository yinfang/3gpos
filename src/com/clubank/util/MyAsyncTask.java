package com.clubank.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clubank.consumer.BaseActivity;
import com.clubank.pos.R;
import com.clubank.domain.C;
import com.clubank.domain.Result;

import org.ksoap2.SoapFault;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 通用异步处理类
 */
public class MyAsyncTask extends AsyncTask<Object, Result, Result> {

    Context context;
    WaitingDialog wd;
    String action;
    Conn conn;
    public boolean showWaiting = true;

    public MyAsyncTask(Context context, String action) {
        this.context = context;
        this.action = action;
        this.conn = new Conn(context);
    }

    public MyAsyncTask(Context context, String action, int wsType) {
        this.context = context;
        this.action = action;
        this.conn = new Conn(context, wsType);
    }

    public MyAsyncTask(Context context, String action, boolean showWaiting) {
        this(context, action);
        this.showWaiting = showWaiting;
    }

    @Override
    public void onPreExecute() {
        if (showWaiting) {
            wd = new WaitingDialog(context);
            wd.show();
        }
    }

    @Override
    public Result doInBackground(Object... args) {
        Result result = new Result();
        try {
            Method[] m = Conn.class.getMethods();
            for (int i = 0; i < m.length; i++) {
                if (m[i].getName().equals(action)) {
                    result = (Result) m[i].invoke(conn, new Object[]{args});
                    break;
                }
            }
            // publishProgress((int) ((count / (float) length) * 100));
        } catch (Exception e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                // 网络超时
                result.code = C.RESULT_SOCKET_TIMEOUT;
            } else if (e.getCause() instanceof SocketException
                    || e.getCause() instanceof UnknownHostException) {
                // 网络错误
                result.code = C.RESULT_SOCKET_ERROR;
            } else if (e.getCause() instanceof SoapFault) {
                String msg = e.getCause().getMessage();
                if (msg.indexOf("SOAPAction") > 0) {
                    if (!action.equals("get3GPOSCanQueryStockInHand")) {//其他营业点调库存查询接口不提示异常
                        result.code = C.RESULT_LOWER_CLIENT_VERSION;
                    }
                } else {
                    result.code = C.RESULT_SERVER_ERROR;
                }

                System.out.println(e.getCause().getMessage());
            } else if (e.getCause() instanceof IOException) {
                result.code = C.RESULT_SERVER_ERROR;
            } else {
                result.code = C.RESULT_UNKNOWN_ERROR;
                result.obj = e.getCause().getMessage();
            }
            Log.e(e.getMessage(), "");
            e.printStackTrace();
            e.getMessage();
        }
        return result;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Result result) {
        try {
            if (showWaiting) {
                wd.dismiss();
            }
            if (showWaiting) {
                if (result.code == C.RESULT_UNKNOWN_ERROR) {
                    UI.showError(context, R.string.msg_operation_failed);
                    return;
                } else if (result.code == C.RESULT_SOCKET_ERROR) {
                    UI.showError(context, R.string.msg_network_error);
                    return;
                } else if (result.code == C.RESULT_LOWER_CLIENT_VERSION) {
//					UI.showError(context, R.string.msg_lower_client_version);
                    UI.showError(context, R.string.msg_server_error);
                    return;
                } else if (result.code == C.RESULT_SOCKET_TIMEOUT) {
                    UI.showError(context, R.string.msg_network_timeout);
                    return;
                } else if (result.code == C.RESULT_SERVER_ERROR) {
                    UI.showError(context, R.string.msg_server_error);
                    return;
                }
            }
            ((BaseActivity) context).onPostExecute(action, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Result... values) {
        // 此处可处理进度条变化
        // System.out.println("" + values[0]);
        // message.setText(""+values[0]);
        // pdialog.setProgress(values[0]);
    }
}