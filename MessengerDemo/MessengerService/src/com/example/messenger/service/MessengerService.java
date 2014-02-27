package com.example.messenger.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.example.messenger.common.Msg;

public class MessengerService extends Service {

	private Messenger mActivityMessenger;
    /**
     * ��Service����Activity��������Ϣ��Handler
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Msg.SAY_HELLO:
                	showToast("hello!");
                    break;
                case Msg.SET_MESSENGER:
                	mActivityMessenger = (Messenger) msg.obj;
                	mHandler.sendEmptyMessageDelayed(Msg.TICK, 1000);
                	break;
                case Msg.TICK:
                	sendTick();
                	mHandler.sendEmptyMessageDelayed(Msg.TICK, 1000);
                	break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private IncomingHandler mHandler = new IncomingHandler();

    /**
     * ���Messenger���Թ�����Service���Handler��Activity�����������Message��Service��Serviceͨ��Handler���д���
     */
    final Messenger mMessenger = new Messenger(mHandler);

    /**
     * ��Activity��Service��ʱ��ͨ�������������һ��IBinder��Activity�����IBinder��������Messenger���Ϳ�����Service��Handler����ͨ����
     */
    @Override
    public IBinder onBind(Intent intent) {
    	showToast("onBind()");
        return mMessenger.getBinder();
    }
    
    @Override
    public void onDestroy() {
    	showToast("onDestroy()");
    	mHandler.removeMessages(Msg.TICK);
    	super.onDestroy();
    }
    
    private void showToast(String msg) {
    	Toast.makeText(getApplicationContext(), 
    			"service: "+msg, Toast.LENGTH_SHORT).show();
    }
    
    //-----------------
    private void sendTick() {
        Message msg = Message.obtain(null, Msg.TICK, (int) System.currentTimeMillis(), 0);
        try {
        	mActivityMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}