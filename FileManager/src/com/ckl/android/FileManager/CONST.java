package com.ckl.android.FileManager;

import android.content.Context;
import android.widget.Toast;

//һЩ���õĳ����ͷ���
public class CONST {
	public static final String TAG = "file_swf";
	
	public static void DisplayToast(Context context, int stringId)
	{
		Toast.makeText(context, stringId,  Toast.LENGTH_SHORT).show();
	}
}
