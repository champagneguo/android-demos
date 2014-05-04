package com.example.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.android_demos.DemoApplication;

public class CommonUtil {

	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		return status.equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * ��ȡ����ڴ�
	 * @return
	 */
	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory() / 1024;
	}

	/**
	 * �������
	 * @param context
	 * @return
	 */
	public static boolean checkNetState(Context context) {
		boolean netstate = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						netstate = true;
						break;
					}
				}
			}
		}
		return netstate;
	}

	public static void showToast(Context context, String tip) {
		Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
	}

	public static DisplayMetrics metric = new DisplayMetrics();

	/**
	 * �õ���Ļ�߶�
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Activity context) {
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}

	/**
	 * �õ���Ļ���
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Activity context) {
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	/**
	 * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 */

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * ��ѯ�ֻ��ڷ�ϵͳӦ��
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getAllApps(Context context) {
		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pManager = context.getPackageManager();
		// ��ȡ�ֻ�������Ӧ��
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = (PackageInfo) paklist.get(i);
			// �ж��Ƿ�Ϊ��ϵͳԤװ��Ӧ�ó���
			if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				// customs applications
				apps.add(pak);
			}
		}
		return apps;
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	/**
	 * ��ȡ�汾�źͰ汾����
	 * @param context
	 * @return
	 */
	public static String getVersionCode(Context context, int type) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			if (type == 1) {
				return String.valueOf(pi.versionCode);
			} else {
				return pi.versionName;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ͨ��Service���������ж��Ƿ�����ĳ������
	public static boolean messageServiceIsStart(
			List<ActivityManager.RunningServiceInfo> mServiceList,
			String className) {
		for (int i = 0; i < mServiceList.size(); i++) {
			if (className.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �ж��ֻ�����*/
	public static boolean isMobileNO(String mobiles){
		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  
		Matcher matcher = pattern.matcher(mobiles);  
		return matcher.matches();
	}
	
	/**
	 * ��ȡ�ֻ�״̬���߶�
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}
	
	//------------------
    static final class Holder {
        static CommonUtil instance = new CommonUtil();
    }

    public static CommonUtil getSingleton() {
        return Holder.instance;
    }

    private Context mContext;

    private CommonUtil() {
        mContext = DemoApplication.getInstance().getBaseContext();
    }

    /**
     * @brief ����Ŀ¼
     * @param dirPath Ŀ¼·��
     * @return true: success; false: failure or already existed and not a
     *         directory.
     */
    public boolean makeDirs(String dirPath) {
        File file = new File(dirPath);
        if (file.exists()) {
            if (file.isDirectory()) {
                return true;
            }
            return false;
        }
        return file.mkdirs();
    }

    /**
     * ��ȡ�ļ���׺��������`.`
     * @param file �ļ�
     * @return �ļ���׺
     */
    public String getExtension(File file) {
        String name = file.getName();
        int i = name.lastIndexOf('.');
        int p = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        return i > p ? name.substring(i + 1) : "";
    }

    /**
     * @brief �ж������Ƿ����
     * @warning need ACCESS_NETWORK_STATE permission
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (null == info) {
            return false;
        }
        return info.isAvailable();
    }

    /**
     * @brief ��ȡ��ǰIP��ַ
     * @return null if network off
     */
    public String getLocalIpAddress() {
        try {
            // ��������ӿ�
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                // ����IP��ַ
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    // �ǻش���ַʱ����
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @brief �ж��ⲿ�洢�Ƿ����
     */
    public boolean isExternalStorageMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * @brief ��ȡ�ļ�ϵͳ·���ڵĿ��ÿռ䣬��λbytes
     * @param path �ļ�ϵͳ·��
     */
    public int getAvailableBytes(String path) {
        StatFs sf = new StatFs(path);
        int blockSize = sf.getBlockSize();
        int availCount = sf.getAvailableBlocks();
        return blockSize * availCount;
    }

    /**
     * @brief �洢��С��ʽ��Ϊ���Ķ����ִ�
     */
    public String readableFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " "
                + units[digitGroups];
    }

    /**
     * @brief �жϷ����Ƿ�������
     * @param servClsName ��������
     * @return �Ƿ�������
     */
    public boolean isServiceRunning(String servClsName) {
        ActivityManager mActivityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> mServiceList = mActivityManager
                .getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo servInfo : mServiceList) {
            if (servClsName.equals(servInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @brief ��ȡ����Ĭ����ʾ��Ϣ
     */
    public Display getDisplay() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay();
    }

    /**
     * dp -> px
     * @param dipValue dp��λ��ֵ
     * @return px��λ��ֵ
     */
    public int dp2px(float dipValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px - > dp
     * @param pxValue px��λ��ֵ
     * @return dp��λ��ֵ
     */
    public int px2dp(float pxValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /** 
     * sp -> px
     * @param spValue sp��λ��ֵ
     * @return px��λ��ֵ
     */
    public int sp2px(float spValue) {
        float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px -> sp
     * @param pxValue px��λ��ֵ
     * @return sp��λ��ֵ
     */
    public int px2sp(float pxValue) {
        float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /** ������ʱ���� */
    public final String FORMAT_YMDHMS = "yyyy-MM-dd kk:mm:ss";
    /** ������ */
    public final String FORMAT_YMD = "yyyy-MM-dd";
    /** ʱ���� */
    public final String FORMAT_HMS = "kk:mm:ss";

    /** ��õ�ǰʱ�� */
    public CharSequence currentTime(CharSequence inFormat) {
        return DateFormat.format(inFormat, System.currentTimeMillis());
    }

    /**
     * @brief ��鱾�ض˿��Ƿ�ռ��
     * @param port �˿�
     * @return true: already in use, false: not.
     */
    public boolean isLocalPortInUse(int port) {
        boolean flag = true;
        try {
            flag = isPortInUse("127.0.0.1", port);
        } catch (Exception e) {
        }
        return flag;
    }

    /**
     * @brief ��������˿��Ƿ�ռ��
     * @param host ����
     * @param port �˿�
     * @return true: already in use, false: not.
     * @throws UnknownHostException 
     */
    public boolean isPortInUse(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(theAddress, port);
            socket.close();
            flag = true;
        } catch (IOException e) {
        }
        return flag;
    }
}

