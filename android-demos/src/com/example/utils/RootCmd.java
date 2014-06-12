package com.example.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import android.util.Log;

public final class RootCmd {

	private static final String TAG = "RootCmd";
	private static boolean mHaveRoot = false;

	// �жϻ���Android�Ƿ��Ѿ�root�����Ƿ��ȡrootȨ��
	public static boolean haveRoot() {
		if (!mHaveRoot) {
			int ret = execRootCmdSilent("echo test"); // ͨ��ִ�в������������
			if (ret != -1) {
				Log.i(TAG, "have root!");
				mHaveRoot = true;
			} else {
				Log.i(TAG, "not root!");
			}
		} else {
			Log.i(TAG, "mHaveRoot = true, have root!");
		}
		return mHaveRoot;
	}

	// ִ�������������
	public static String execRootCmd(String cmd) {
		String result = "";
		DataOutputStream dos = null;
		DataInputStream dis = null;
		
		try {
			Process p = Runtime.getRuntime().exec("su");// ����Root�����androidϵͳ����su����
			dos = new DataOutputStream(p.getOutputStream());
			dis = new DataInputStream(p.getInputStream());

			Log.i(TAG, cmd);
			dos.writeBytes(cmd + "\n");
			dos.flush();
			dos.writeBytes("exit\n");
			dos.flush();
			String line = null;
			while ((line = dis.readLine()) != null) {
				Log.d("result", line);
				result += line;
			}
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	// ִ���������ע������
	public static int execRootCmdSilent(String cmd) {
		int result = -1;
		DataOutputStream dos = null;
		
		try {
			Process p = Runtime.getRuntime().exec("su");
			dos = new DataOutputStream(p.getOutputStream());
			
			Log.i(TAG, cmd);
			dos.writeBytes(cmd + "\n");
			dos.flush();
			dos.writeBytes("exit\n");
			dos.flush();
			p.waitFor();
			result = p.exitValue();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static void createLink(String path, String link) {
		File file = new File(link);
		if (file.exists()) {
			file.delete();
		}
		
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		
		String cmd = "ln -s \"" + path + "\" \"" + link + "\"";
		exec(cmd);
	}
	
	public static int exec(String cmd) {
		int result = 0;
		try {
//			Log.i(TAG, cmd);
			Process p = Runtime.getRuntime().exec("sh");
			PrintWriter pw = new PrintWriter(p.getOutputStream());
			pw.println(cmd);    pw.flush();
			pw.println("exit"); pw.flush();
			p.waitFor();
			pw.close();
			p.destroy();
			result = p.exitValue();
			Log.i(TAG, "--------> "+result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return result;
	}
}
