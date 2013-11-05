package com.example.utils;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class GuessCodepage {
	/**
	 * ���õ�������Դ��cpdetector��ȡ�ļ������ʽ
	 * @param path
	 *            Ҫ�ж��ļ������ʽ��Դ�ļ���·��
	 */
	public static String getFileEncode(String path) {
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
	public static String getFileEncode(URL url) {
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
	 *          ������ı�������
	 * @param length
	 * 			������������Ķ����ֽ���
	 */
	public static String getSteamEncode(InputStream inputStream, int length) {
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
}
