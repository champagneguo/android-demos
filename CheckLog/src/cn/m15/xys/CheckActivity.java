package cn.m15.xys;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CheckActivity extends Activity implements Runnable{

    Button button = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.check);
	
	/**�õ������ť����**/
	button = (Button)findViewById(R.id.button0);
	
	/**���������ť**/
	button.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View view) {
		/**���һ��Log��Ϣ**/
		Log.i("Mytest", "this is a test");
		
		/**�����߳����ڼ���log�������Ϣ**/
		new Thread(CheckActivity.this).start();
	    }
	});
	

    }
    
    
    @Override
    public void run() {
	Process mLogcatProc = null;
	BufferedReader reader = null;
	try {
	    	//��ȡlogcat��־��Ϣ
		mLogcatProc = Runtime.getRuntime().exec(new String[] { "logcat"/*,"Mytest:I *:S"*/ });
		reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));

		String line;
		
		while ((line = reader.readLine()) != null) {
			
//			if (line.indexOf("this is a test") > 0) {
			    //logcat��ӡ��Ϣ��������Լ�����
			    // ʹ��looper �Ѹ�����һ����ʾ
			    Looper.prepare();  
			    Toast.makeText(this, "line:"+line, Toast.LENGTH_SHORT).show(); 
			    Looper.loop();  
//			}
		}

	} catch (Exception e) {

		e.printStackTrace();
	}
	
    }
}