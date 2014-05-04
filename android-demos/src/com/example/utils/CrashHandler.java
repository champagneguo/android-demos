package com.example.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.example.android_demos.R;

/** 
 * @brief �쳣����������
 * @details ��������δ�����쳣ʱ���ɸ������ӹܳ��򲢼�¼���ʹ��󱨸档 
 * @author join
 */
public class CrashHandler implements UncaughtExceptionHandler {

    /** ������־�ļ����� */
    static final String LOG_NAME = ".crash";

    /** ϵͳĬ�ϵ�UncaughtException������ */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /** �����Ķ��� */
    private Context mContext;

    /**
     * @brief ���캯��
     * @details ��ȡϵͳĬ�ϵ�UncaughtException�����������ø�CrashHandlerΪ�����Ĭ�ϴ����� ��
     * @param context ������
     */
    public CrashHandler(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /** 
     * @brief ��UncaughtException����ʱ��ת��ú��������� 
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // ����û�û�д�������ϵͳĬ�ϵ��쳣������������
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // �ȴ�����������
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }

    }

    /** 
     * @brief �Զ���������ռ�������Ϣ 
     * @details ���ʹ��󱨸�Ȳ������ڴ����
     * @param ex �쳣
     * @return true����������˸��쳣��Ϣ�����򷵻�false��
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return true;
        }
        // ��ʾ������Ϣ
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext,
                        mContext.getString(R.string.info_crash, Constants.APP_DIR + LOG_NAME),
                        Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        // ������󱨸��ļ�
        saveCrashInfoToFile(ex);
        return true;
    }

    /** 
     * @brief ���������Ϣ���ļ���
     * @param ex �쳣
     */
    private void saveCrashInfoToFile(Throwable ex) {
        final StackTraceElement[] stack = ex.getStackTrace();
        final String message = ex.getMessage();
        /* ׼��������־�ļ� */
        File logFile = new File(Constants.APP_DIR + LOG_NAME);
        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }
        /* д�������־ */
        FileWriter fw = null;
        CommonUtil mCommonUtil = CommonUtil.getSingleton();
        final String lineFeed = "\r\n";
        try {
            fw = new FileWriter(logFile, true);
            fw.write(mCommonUtil.currentTime(mCommonUtil.FORMAT_YMDHMS).toString() + lineFeed
                    + lineFeed);
            fw.write(message + lineFeed);
            for (int i = 0; i < stack.length; i++) {
                fw.write(stack[i].toString() + lineFeed);
            }
            fw.write(lineFeed);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fw)
                    fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}