package com.oncogene.wificlient;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private static int server_port = 4005;
    private static String server_address = "10.0.2.2";//"192.168.0.104";//
    //private static String server_address = "192.168.0.104";
    public static Socket socket = null;
    //
    private BufferedWriter bw;
    private BufferedReader br;
    private String tmp;
    //
    private Button mBtnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnStart = (Button)findViewById(R.id.btnSend);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Wake","onclick");
                new Thread(new SendThread()).start();
            }
        });
        //
        new Thread(new ClientThread()).start();
    }
    //
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1:
                {
                    TextView tv = findViewById(R.id.txtServer);
                    Bundle bundle = (Bundle)msg.obj;
                    tv.setText(bundle.getString("rcv"));
                    break;
                }

            }
        }
    };

    class SendThread implements Runnable{

        @Override
        public void run() {
            try {
                //EditText et = (EditText) findViewById(R.id.editTxt);
                //String str = et.getText().toString();
                InputStream inputStream = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                BufferedReader inputStream2 = new BufferedReader(new InputStreamReader(
                        inputStream));
                PrintWriter w = new PrintWriter(socket.getOutputStream());
                w.print("get it hello world");
                w.flush();
                Log.i("Wake","get it hello world");
            } catch (UnknownHostException e) {
                Log.i("Wake","get it hello world1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.i("Wake","get it hello world2");
                e.printStackTrace();
            } catch (Exception e) {
                Log.i("Wake","get it hello world3"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            socket = null;
            Log.i("Wake","run");
            SocketAddress address = new InetSocketAddress(server_address, server_port);
            socket = new Socket();
            try {
                Log.i("Wake","run001");
                socket.connect(address, 3000);
                bw = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
                //取得網路輸入串流
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //檢查是否已連線
                Log.i("Wake","start to read");
                while (socket.isConnected()) {
                    Log.i("Wake","is connected");
                    //宣告一個緩衝,從br串流讀取 Server 端傳來的訊息
                    tmp = br.readLine();
                    //int bytesRead = br.read();
                    Log.i("Wake","read line tmp bytesRead=" + tmp);
                    if(tmp!=null){
                        //將取到的String抓取{}範圍資料
                        //tmp=tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
                        //json_read=new JSONObject(tmp);
                        //從java伺服器取得值後做拆解,可使用switch做不同動作的處理
                        Log.i("Wake","read line");
                        Message msg = new Message();
                        msg.what = 1;
                        msg.arg1 = 123;
                        msg.arg2 = 456;
                        Bundle data = msg.getData();
                        data.putString("rcv",tmp);
                        msg.obj = data;
                        mHandler.sendMessage(msg);



                    }
                }



            } catch (IOException e) {
                Log.i("time","no worky X");
                e.printStackTrace();
            }
            try {
                socket.setSoTimeout(3000);
            } catch (SocketException e) {
                Log.d("timeout","server took too long to respond");
                e.printStackTrace();
            }
            //

        }

    }
}