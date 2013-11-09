package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.os.StrictMode;

/**
 * ͨ��urlȡ���ļ�����InputStream��������
 * @author gugf
 *
 */
@SuppressLint("NewApi")
public class HttpUtils {

	HttpURLConnection mConnection;
	InputStream mInputStream;
	
	/**
	 * ͨ��url����InputStream
	 * @param urlStr
	 * @return InputStream
	 */
	public InputStream getInputStream(String urlStr) {
		URL url = null;
		try {
			if (urlStr == null) {
				return null;
			}
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return getInputStream(url);
	}
	
	/**
	 * ͨ��url����InputStream
	 * @param url
	 * @return InputStream
	 */
	public InputStream getInputStream(URL url) {
		mInputStream = null;
		try {
			if (url == null) {
				return mInputStream;
			}
			mConnection = (HttpURLConnection) url.openConnection();//����HttpURLConnection����,���ǿ��Դ������л�ȡ��ҳ����.
			mConnection.setDoInput(true);
			mConnection.connect();
			mInputStream = mConnection.getInputStream();	//�õ����緵�ص�������
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mInputStream;
	}
	
	public void close() {
		if (mInputStream != null) {
			try {
				mInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (mConnection != null) {
			mConnection.disconnect();
		}
	}
	
	public static void initStrictMode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());
	}
}