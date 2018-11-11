package com.dadac.testrosbridge;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dadac.testrosbridge.R;
import com.jilk.ros.ROSClient;
import com.jilk.ros.rosbridge.ROSBridgeClient;
import com.jilk.ros.rosbridge.implementation.PublishEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;


/**
 * @ Create by dadac on 2018/10/8.
 * @Function: 开始啪啦啪啦的进行数据的传输，需要放在service里面进行传输，比较保险，可以一直在传输数据
 * @Return:
 */
public class RosBridgeActivity extends Activity implements View.OnClickListener {

    ROSBridgeClient client;
    String ip = "192.168.2.119";   //虚拟机的 IP
    // String ip = "192.168.10.20";     //半残废机器人的IP
    // String ip = "192.168.10.200";     //机器人的IP
    String port = "9090";

    boolean isSubscrible = true;
    private static int flagSubscrible = 0;

    private Button DC_Button_Subscrible;
    private Button DC_Button_Publish;
    private EditText DC_EditText_EnterWord;
    private TextView DC_TextView_ShowData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rosdatashow);
        EventBus.getDefault().register(this);
        onConnect(ip, port);
        subMenuShow();
    }

    //初始化界面的参数
    private void subMenuShow() {
        DC_Button_Subscrible = (Button) findViewById(R.id.DC_Button_Subscrible);
        DC_Button_Subscrible.setOnClickListener(this);
        DC_Button_Publish = (Button) findViewById(R.id.DC_Button_Publish);
        DC_Button_Publish.setOnTouchListener(new ComponentOnTouch());
        DC_EditText_EnterWord = (EditText) findViewById(R.id.DC_EditText_EnterWord);
        DC_TextView_ShowData = (TextView) findViewById(R.id.DC_TextView_ShowData);
    }


    /**
     * @Function: 建立连接
     * @Return:
     */
    public void onConnect(String ip, String port) {

        client = new ROSBridgeClient("ws://" + ip + ":" + port);
        boolean conneSucc = client.connect(new ROSClient.ConnectionStatusListener() {
            @Override
            public void onConnect() {
                client.setDebug(true);
                ((RCApplication) getApplication()).setRosClient(client);
                showTip("Connect ROS success");
                Log.d("dachen", "Connect ROS success");
            }


            @Override
            public void onDisconnect(boolean normal, String reason, int code) {
                showTip("ROS disconnect");
                Log.d("dachen", "ROS disconnect");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showTip("ROS communication error");
                Log.d("dachen", "ROS communication error");
            }
        });
    }

    //接收来自Ros端的数据
    private void ReceiveDataToRos() {
        if (isSubscrible == true) {
            String msg1 = "{\"op\":\"subscribe\",\"topic\":\"/chatter\"}";
            client.send(msg1);
        } else if (isSubscrible == false) {
            String msg2 = "{\"op\":\"unsubscribe\",\"topic\":\"/chatter\"}";
            client.send(msg2);
        }
    }

    //发送数据到ROS端
    private void SendDataToRos(String data) {
        String msg1 = "{ \"op\": \"publish\", \"topic\": \"/chatter\", \"msg\": { \"data\": \"" + data + " \" }}";
        //        String msg2 = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + 0 + ",\"y\":" +
        //                0 + ",\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":" + 0.5 + "}}}";
        client.send(msg1);
    }


    private void showTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RosBridgeActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onEvent(final PublishEvent event) {
        if ("/chatter".equals(event.name)) {
            parseChatterTopic(event);
            return;
        }
        Log.d("dachen", event.msg);
    }

    private void parseChatterTopic(PublishEvent event) {
        try {
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(event.msg);
            String jsondata = (String) jsonObject.get("data");
            DC_TextView_ShowData.setText(jsondata);
            Log.i("dachen", jsondata);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.DC_Button_Subscrible:
                if (flagSubscrible % 2 == 0) {
                    isSubscrible = true;
                    DC_Button_Subscrible.setText("Subscrible");
                }
                if (flagSubscrible % 2 == 1) {
                    isSubscrible = false;
                    DC_Button_Subscrible.setText("unSubscrible");
                }
                flagSubscrible++;
                ReceiveDataToRos();
                break;
            default:
                break;
        }
    }

    private class ComponentOnTouch implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.DC_Button_Publish:
                    onTouchChange("up", event.getAction());
                    break;
                default:
                    break;
            }
            return true;
        }
    }
    private boolean Btn_LongPress = false;
    class MyThread extends Thread {
        @Override
        public void run() {
            while (Btn_LongPress) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SendDataToRos("Start");
            }
        }
    }
    private void onTouchChange(String methodName, int eventAction) {
        MyThread myThread = new MyThread();
        // 按下松开分别对应启动停止线程方法
        if ("up".equals(methodName)) {
            if (eventAction == MotionEvent.ACTION_DOWN) {
                myThread.start();
                Btn_LongPress = true;
            } else if (eventAction == MotionEvent.ACTION_UP) {
                SendDataToRos("Stop");
                if (myThread != null)
                    Btn_LongPress = false;
            }
        }
    }

}

