package com.eko;

import java.io.File;

import air.app.AppEntry;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainApp extends AppEntry {
	@Override
	public void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		FrameLayout root = (FrameLayout) findViewById(android.R.id.content);
		TextView text = new TextView(this);
		text.setText("game over");
		root.addView(text);
	}

	@Override
	public void showDialog() {
		// ��������û��װair��ʱ��ִ�еĺ���
		new AlertDialog.Builder(this).setTitle("����").setMessage("��װ��Air������ȷ��")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// ������������
						AlarmManager mgr = (AlarmManager) MainApp.super
								.getSystemService(Context.ALARM_SERVICE);
						mgr.set(AlarmManager.RTC,
								System.currentTimeMillis() + 500, PendingIntent
										.getActivity(
												MainApp.super.getBaseContext(),
												0, new Intent(getIntent()),
												getIntent().getFlags()));
						System.exit(2);
					}
				}).show();
		try {
			// ��air.apk�ļ�������sdcard��
			File sd = Environment.getExternalStorageDirectory();
			String path = sd.toString()+"/.eko/air.apk";
			boolean ok = RawUtils.copyFile(getResources(), R.raw.air, path);
			if (ok) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.fromFile(new File(path));
				intent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				// ִ��air.apk���а�װ
				startActivity(intent);
			}
		} catch (Exception e) {
			// TODO: handle exception 1
		}
	}
}