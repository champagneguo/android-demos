package com.example.messenger.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.common.Msg;

public class MessengerActivity extends Activity {

    /** ��Service����Message��Messenger���� */
    private Messenger mService = null;

    /** �ж���û�а�Service */
    private boolean mBound;
    
    private TextView mTvTick;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Activity�Ѿ�����Service
            // ͨ������service������Messenger����������������Service����Message����Service����ͨ��
            mService = new Messenger(service);
            mBound = true;
            setActivityMessenger();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };

    public void sayHello(View v) {
        if (!mBound) return;
        // ��Service����һ��Message
        Message msg = Message.obtain(null, Msg.SAY_HELLO, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        
        findViewById(R.id.btn_say_hello).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sayHello(v);
			}
		});
        
        mTvTick = (TextView) findViewById(R.id.tv_tick);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ��Service
        Intent intent = new Intent();
        intent.setClassName("com.example.messenger.service", 
        		"com.example.messenger.service.MessengerService");
        bindService(intent, mConnection,
            Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ���
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
    
    //-----------------
    private Handler mHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case Msg.TICK:
				mTvTick.setText("tick: " + msg.arg1);
				break;
			default:
				break;
			}
    	};
    };
    
    private final Messenger mActivityMessenger = new Messenger(mHandler);
    
    private void setActivityMessenger() {
        if (!mBound) return;
        Message msg = Message.obtain(null, Msg.SET_MESSENGER, 0, 0, mActivityMessenger);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    private void showToast(String msg) {
    	Toast.makeText(getApplicationContext(), 
    			"activity: "+msg, Toast.LENGTH_SHORT).show();
    }
}