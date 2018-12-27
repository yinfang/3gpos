package com.clubank.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.clubank.consumer.BaseActivity;
import com.clubank.pos.R;
import com.clubank.domain.C;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

public class U {

	public static Object clone(Object o) {
		return getObject(getBytes(o));
	}

	public static byte[] getBytes(Object o) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(o);
			out.flush();
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getObject(byte[] b) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			ObjectInputStream in = new ObjectInputStream(bais);
			return in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T toObject(MyRow row, Class<T> clazz) {
		try {
			T t = clazz.newInstance();
			Field[] fields = clazz.getFields();
			for (Field f : fields) {
				try {
					if (row.containsKey(f.getName())) {
						Object o = row.get(f.getName());
						if (f.getType() == String.class) {
							f.set(t, o);
						} else if (f.getType() == int.class) {
							f.set(t, Integer.parseInt(o.toString()));
						} else if (f.getType() == boolean.class) {
							f.set(t, Boolean.parseBoolean(o.toString()));
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void downloadApk(BaseActivity a, String apkurl) {
		try {
			URL url = new URL(apkurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setConnectTimeout(3 * 1000);
			conn.setReadTimeout(60 * 000);
			conn.setDoInput(true);

			conn.connect();
			InputStream is = conn.getInputStream();

			File cacheDir = a.getCacheDir();
			final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";

			File file = new File(cachePath);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			String command = "chmod 777 " + cachePath;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + cachePath),
					"application/vnd.android.package-archive");
			a.startActivity(intent);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}

	}

	/**
	 * get formatted number
	 * 
	 * @param row
	 * @param name
	 * @return
	 */
	public static String getNumber(MyRow row, String name, DecimalFormat nf) {
		return nf.format(Double.valueOf((String) row.get(name)));
	}

	public static String saveTempBitmap(Bitmap bmp, String prefix, String suffix) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				File file = File.createTempFile(prefix, suffix,
						Environment.getExternalStorageDirectory());
				FileOutputStream fos = new FileOutputStream(file);
				bmp.compress(CompressFormat.JPEG, 70, fos);
				fos.flush();
				fos.close();
				String filePath = file.getAbsolutePath();
				return filePath;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String saveTempBitmap(Bitmap bmp) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String fname = Environment.getExternalStorageDirectory() + "/"
					+ System.currentTimeMillis() + ".jpg";
			try {
				FileOutputStream fos = new FileOutputStream(fname);
				bmp.compress(CompressFormat.JPEG, 70, fos);
				fos.flush();
				fos.close();
				return fname;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String subStr(String str, int subSLength) {
		if (str == null) {
			return "";
		} else {
			int tempSubLength = subSLength;
			String subStr = str.substring(0,
					str.length() < subSLength ? str.length() : subSLength);
			try {
				int subStrByetsL = subStr.getBytes("GBK").length;
				while (subStrByetsL > tempSubLength) {
					int subSLengthTemp = --subSLength;
					subStr = str.substring(0,
							subSLengthTemp > str.length() ? str.length()
									: subSLengthTemp);
					subStrByetsL = subStr.getBytes("GBK").length;
					// subStrByetsL = subStr.getBytes().length;
				}
				return subStr;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static double max(double[] items) {
		double ret = 0;
		for (int i = 0; i < items.length; i++) {
			ret = Math.max(ret, items[i]);
		}
		return ret;
	}

	public static double min(double[] items) {
		double ret = Integer.MAX_VALUE;
		for (int i = 0; i < items.length; i++) {
			ret = Math.min(ret, items[i]);
		}
		return ret;
	}

	public static String getDateString(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		return C.df_yMd.format(c.getTime());
	}

	public static String getTimeString(int hour, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		return C.df_Hm.format(c.getTime());
	}

	public static void copy(final InputStream in, final OutputStream out) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();

	}

	public static void unzip(String zipFile, String outputDir) {

		byte[] buffer = new byte[1024];

		try {

			// create output directory is not exists
			File folder = new File(outputDir);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(
					new FileInputStream(zipFile));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				File newFile = new File(outputDir + File.separator + fileName);

				System.out.println("file unzip : " + newFile.getAbsoluteFile());

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			System.out.println("Done");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static Bitmap getBitmap(byte[] b) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = 1; // width，hight设为原来的十分一
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		return BitmapFactory.decodeStream(bais, null, opt);
	}

	// 格式化时间为:分钟、月、天
	public static String getTimeDiff(Context context, String stime) {
		String str = "*";
		long t0 = System.currentTimeMillis();
		long t1 = 0;
		try {
			Date date1 = C.df_yMdHms.parse(stime);
			t1 = date1.getTime();
		} catch (Exception e) {
			return str;
		}
		long m = (long) ((t0 - t1) * 1.0000 / (60 * 1000));
		if (m < 60) {
			if (m <= 0)
				m = 0;
			str = "" + m + " " + context.getText(R.string.minute);
		} else if (m < 60 * 24) {
			long h = Math.round(m * 1.00 / 60);
			str = "" + h + " " + context.getText(R.string.hour);
		} else if (m < 60 * 24 * 30) {
			int d = (int) (m * 1.00 / (60 * 24));
			str = "" + d + " " + context.getText(R.string.day);
		} else {
			str = " " + context.getText(R.string.month1);
		}
		return str;
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	public static boolean isExist(Context context, String packageName) {
		PackageManager man = context.getPackageManager();
		List<PackageInfo> infos = man.getInstalledPackages(0);
		for (PackageInfo info : infos) {
			if (info.packageName.equalsIgnoreCase(packageName)) {
				return true;
			}
		}
		return false;
	}

	public static MyRow getRow(Bundle b, String name) {
		return MyRow.fromMap((Map<?, ?>) b.getSerializable(name));
	}

	public static MyData getData(Bundle b, String name) {
		@SuppressWarnings("unchecked")
		List<Map<?, ?>> list = (List<Map<?, ?>>) b.getSerializable(name);
		MyData data = new MyData();
		for (Map<?, ?> map : list) {
			data.add(MyRow.fromMap(map));
		}
		return data;
	}

}
