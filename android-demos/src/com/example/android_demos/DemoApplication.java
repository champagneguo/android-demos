package com.example.android_demos;

import android.app.Application;

import com.example.utils.CrashHandler;

public class DemoApplication extends Application {
	private static DemoApplication self;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		self = this;
		
        /* ȫ���쳣�������� */
        new CrashHandler(this);
	}
	
    public static DemoApplication getInstance() {
        return self;
    }
}
