package com.example.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.res.Resources;

public class RawUtils {
	public static boolean copyFile(Resources resources, int id, String dstPath) {
		boolean isOk = false;
		try {
			// ��air.apk�ļ�������sdcard��
			File file = new File(dstPath);
			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			
			if (file.exists()) {
				file.delete();
			}
			
			file.createNewFile();
			InputStream ins = resources.openRawResource(id);// ��ȡrawĿ���µ�air.apk�ļ�
			// ִ�ж�ȡ��ת�����洢
			FileOutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, buffer.length)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
			isOk = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOk;
	}
}
