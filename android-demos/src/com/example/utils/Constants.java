package com.example.utils;

import android.os.Environment;

/**
 * @brief Ӧ�����ó���
 * @author join
 */
public final class Constants {

    public static String APP_DIR_NAME = "/.ws/";
    public static String APP_DIR = Environment.getExternalStorageDirectory() + APP_DIR_NAME;

    public static class Config {
        public static final boolean DEV_MODE = false;

        public static int PORT = 7766;
        public static String WEBROOT = "/";

        /** ������Դ�ļ� */
        public static final String SERV_ROOT_DIR = APP_DIR + "root/";

        /** ��Ⱦģ��Ŀ¼ */
        public static final String SERV_TEMP_DIR = SERV_ROOT_DIR + "temp/";

        /** ͳһ���� */
        public static final String ENCODING = "UTF-8";

        /** �Ƿ��������� */
        public static boolean ALLOW_DOWNLOAD = true;
        /** �Ƿ�����ɾ�� */
        public static boolean ALLOW_DELETE = true;
        /** �Ƿ������ϴ� */
        public static boolean ALLOW_UPLOAD = true;

        /** The threshold, in bytes, below which items will be retained in memory and above which they will be stored as a file. */
        public static final int THRESHOLD_UPLOAD = 1024 * 1024; // 1MB

        /** �Ƿ�ʹ��GZip */
        public static boolean USE_GZIP = true;
        /** GZip��չ�� */
        public static final String EXT_GZIP = ".gz"; // used in cache

        /** �Ƿ�ʹ���ļ����� */
        public static boolean USE_FILE_CACHE = true;
        /** �ļ�����Ŀ¼ */
        public static final String FILE_CACHE_DIR = APP_DIR + "cache/";

        /** �����ֽڳ���=1024*4B */
        public static final int BUFFER_LENGTH = 4096;
    }

}
