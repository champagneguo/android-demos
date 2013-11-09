package com.example.utils;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.nio.charset.Charset;

public class GuessCodepage {
	/**
	 * ���õ�������Դ��cpdetector��ȡ�ļ������ʽ
	 * @param path
	 *            Ҫ�ж��ļ������ʽ��Դ�ļ���·��
	 */
	private static String getFileEncode(String path) {
		/*
		 * detector��̽����������̽�����񽻸������̽��ʵ�����ʵ����ɡ�
		 * cpDetector������һЩ���õ�̽��ʵ���࣬��Щ̽��ʵ�����ʵ������ͨ��add���� �ӽ�������ParsingDetector��
		 * JChardetFacade��ASCIIDetector��UnicodeDetector��
		 * detector���ա�˭���ȷ��طǿյ�̽���������Ըý��Ϊ׼����ԭ�򷵻�̽�⵽��
		 * �ַ������롣ʹ����Ҫ�õ�����������JAR����antlr.jar��chardet.jar��cpdetector.jar
		 * cpDetector�ǻ���ͳ��ѧԭ��ģ�����֤��ȫ��ȷ��
		 */
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		addDetector(detector);
		
		java.nio.charset.Charset charset = null;
		File f = new File(path);
		try {
			charset = detector.detectCodepage(f.toURI().toURL());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (charset != null)
			return charset.name();
		else
			return null;
	}
	
	/**
	 * ��ȡURL��Ӧ���ļ�����
	 * @param path
	 *            Ҫ�ж��ļ������ʽ��Դ�ļ���URL
	 */
	private static String getUrlEncode(URL url) {
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		addDetector(detector);
		java.nio.charset.Charset charset = null;
		try {
			charset = detector.detectCodepage(url);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (charset != null)
			return charset.name();
		else
			return null;
	}
	
	/**
	 * ��ȡinputStream��Ӧ���ı�����
	 * @param inputStream
	 *          ������ı�������������֧��mark
	 * @param length
	 * 			������������Ķ����ֽ���
	 */
	private static String getSteamEncode(InputStream inputStream, int length) {
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		addDetector(detector);
		java.nio.charset.Charset charset = null;
		try {
			charset = detector.detectCodepage(inputStream, length);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (charset != null)
			return charset.name();
		else
			return null;
	}
	
	private static void addDetector(CodepageDetectorProxy detector) {
		/*
		 * ParsingDetector�����ڼ��HTML��XML���ļ����ַ����ı���,���췽���еĲ�������
		 * ָʾ�Ƿ���ʾ̽����̵���ϸ��Ϣ��Ϊfalse����ʾ��
		 */
		detector.add(new ParsingDetector(false));
		/*
		 * JChardetFacade��װ����Mozilla��֯�ṩ��JChardet����������ɴ�����ļ��ı���
		 * �ⶨ�����ԣ�һ���������̽�����Ϳ�����������Ŀ��Ҫ������㻹�����ģ�����
		 * �ٶ�Ӽ���̽���������������ASCIIDetector��UnicodeDetector�ȡ�
		 */
		detector.add(JChardetFacade.getInstance());// �õ�antlr.jar��chardet.jar
		// ASCIIDetector����ASCII����ⶨ
		detector.add(ASCIIDetector.getInstance());
		// UnicodeDetector����Unicode�������Ĳⶨ
		detector.add(UnicodeDetector.getInstance());
	}
	
	/*******************/
	private static final int BOM_SIZE = 4;
	
	private static String detectByHeadBytes(String path) throws IOException {
		FileInputStream fi = new FileInputStream(path);
		String charset = detectByHeadBytes(fi);
		fi.close();
		return charset;
	}
	
	private static String detectByHeadBytes(URL url) throws IOException {
		HttpUtils http = new HttpUtils();
		String charset = detectByHeadBytes(http.getInputStream(url));
		http.close();
		return charset;
	}
	
	private static String detectByHeadBytes(InputStream in) throws IOException {
		if (in == null) return null;
		
		PushbackInputStream internalIn = new PushbackInputStream(in, BOM_SIZE);
		String encoding = null;
		byte bom[] = new byte[BOM_SIZE];
		int n;
		n = internalIn.read(bom, 0, bom.length);

		if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00)
				&& (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			encoding = "UTF-32BE";
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)
				&& (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			encoding = "UTF-32LE";
		} else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB)
				&& (bom[2] == (byte) 0xBF)) {
			encoding = "UTF-8";
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			encoding = "UTF-16BE";
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			encoding = "UTF-16LE";
		} else {
			// Unicode BOM mark not found, unread all bytes
		}
		System.out.println("encoding="+encoding+", read=" + n);

		internalIn.unread(bom, 0, n);

		return encoding;
	}
	
	/********************************/
	public static String getCodepage(String path) {
		String charset = null;
		try {
			charset = detectByHeadBytes(path);
			if (charset != null) {
				return charset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return getFileEncode(path);
	}
	
	public static String getCodepage(URL url) {
		String charset = null;
		try {
			charset = detectByHeadBytes(url);
			if (charset != null) {
				return charset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return getUrlEncode(url);
	}
	
	public static String getCodepage(InputStream in) {
		String charset = null;
		try {
			charset = detectByHeadBytes(in);
			if (charset != null) {
				return charset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			return getSteamEncode(in, in.available());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}