package com.example.book;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.*;
import com.example.book.Util;

import java.net.URL;

public class MainActivity extends Activity {

    private TextView tx1;
    private Button btn;
    private Handler hd;
    private ProgressDialog mpd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StringBuffer str=new StringBuffer();
        str.append("1.����ɨ�谴ť����������ɨ�������ɨ�貢��ȡ�鼮�������룻").append("\n");
        str.append("2.���ö���API��ѯ�鼮������Ϣ��").append("\n");
        str.append("3.��ʾ�ڽ�����").append("\n");
        tx1=(TextView)findViewById(R.id.main_textview01);
        tx1.setText(str.toString());
        btn=(Button)findViewById(R.id.main_button01);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator=new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();
            }
        });
        hd=	new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                BookInfo book= (BookInfo)msg.obj;

                //��������ʧ
                mpd.dismiss();

                Intent intent=new Intent(MainActivity.this,BookView.class);
                //Bundle bd=new Bundle();
                //bundle.putSerializable(key,object);
                //bd.putSerializable(BookInfo.class.getName(),book);
                //intent.putExtras(bd);
                intent.putExtra(BookInfo.class.getName(),book);
                startActivity(intent);
            }
        };
    }
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if((result==null)||(result.getContents()==null))
        {
            return ;
        }
        mpd=new ProgressDialog(this);
        mpd.setMessage("���Ժ����ڶ�ȡ��Ϣ...");
        mpd.show();

        String urlstr="https://api.douban.com/v2/book/isbn/"+result.getContents();
        Log.i("OUTPUT",urlstr);
        //ɨ��ISBN�����������߳�����ͼ����Ϣ
        new DownloadThread(urlstr).start();
    }

    private class DownloadThread extends Thread
    {
        String url=null;
        public DownloadThread(String urlstr)
        {
            url=urlstr;
        }
        public void run()
        {
            String result=Util.Download(url);
            Log.i("OUTPUT", "download over");
            BookInfo book=new Util().parseBookInfo(result);
            Log.i("OUTPUT", "parse over");
            Log.i("OUTPUT",book.getSummary()+book.getAuthor());
            //�����߳�UI���淢��Ϣ������������Ϣ��������Ϣ���

            Message msg=Message.obtain();
            msg.obj=book;
            hd.sendMessage(msg);
            Log.i("OUTPUT","send over");
        }
    }
}
