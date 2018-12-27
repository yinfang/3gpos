package com.clubank.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import com.clubank.pos.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

//usually, subclasses of AsyncTask are declared inside the activity class. 
// that way, you can easily modify the UI thread from here 
public class DownloadFile extends AsyncTask<String, Integer, String> {

	ProgressDialog pdialog;
	Context context;

	public DownloadFile(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... sUrl) {
		try {
			URL url = new URL(sUrl[0]);
			URLConnection connection = url.openConnection();
			connection.connect();
			// this will be useful so that you can show a typical 0-100%
			// progress bar
			int fileLength = connection.getContentLength();
			// download the file
			InputStream input = new BufferedInputStream(url.openStream());

			File outputDir = context.getCacheDir(); // context being the
													// Activity pointer
			File f = File.createTempFile("temp_name", "apk", outputDir);
			String file = f.getAbsolutePath();

			OutputStream output = new FileOutputStream(f);
			byte data[] = new byte[1024];
			long total = 0;
			int count;
			int last_percent = 0;
			while ((count = input.read(data)) != -1) {
				total += count;
				int percent = (int) (total * 100.00 / fileLength);
				if (percent > last_percent) {
					publishProgress(percent);
				}
				last_percent = percent;
				output.write(data, 0, count);
			}
			output.flush();
			output.close();
			input.close();

			// 执行覆盖安装
			String command = "chmod 777 " + file;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + file),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		} catch (Exception e) {
			pdialog.dismiss();// 如果出现异常，关闭Dialog
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pdialog = new ProgressDialog(context);
		// dialog.setTitle("Indeterminate");
		pdialog.setMessage(context.getText(R.string.msg_please_wait));
		pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pdialog.setIndeterminate(false);
		pdialog.setMax(100);
		pdialog.incrementProgressBy(1);
		pdialog.setCancelable(true);
		pdialog.show();
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		if (progress[0] >= 100) {
			pdialog.dismiss();
		}
		pdialog.setProgress(progress[0]);
	}
}
