package com.clubank.util;

import java.lang.reflect.Method;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.clubank.consumer.BaseActivity;
import com.clubank.domain.C;
import com.clubank.domain.Result;

public class AsyncLocalTask extends AsyncTask<Object, Result, Result> {

	Context context;
	WaitingDialog wd;
	int id;
	String action;
	DBBusiness dbs;
	public boolean showWaiting = true;

	public AsyncLocalTask(Context context, int id, String action) {
		this.context = context;
		this.id = id;
		this.action = action;
		this.dbs = new DBBusiness(context);
	}

	public AsyncLocalTask(Context context, int id, boolean showWaiting) {
		this.showWaiting = showWaiting;
	}

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
			Method[] m = DBBusiness.class.getMethods();
			for (int i = 0; i < m.length; i++) {
				if (m[i].getName().equals(action)) {
					if (args.length <= 0) {
						result.code = C.RESULT_SUCCESS;
						result.obj = (MyData) m[i].invoke(dbs);
					} else {
						result.code = C.RESULT_SUCCESS;
						result.obj = (MyData) m[i].invoke(dbs,
								new Object[] { args });
					}
					break;
				}
			}
		} catch (Exception e) {
			result.obj = e.getCause().getMessage();
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
			((BaseActivity) context).afterWork(id, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onProgressUpdate(Result... values) {
	}
}