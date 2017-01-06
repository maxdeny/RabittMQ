package xpfei.rabittmq;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import winning.framework.BaseActivity;
import winning.framework.managers.ApiManager;
import winning.framework.network.NetworkEngine;

public class MainActivity extends BaseActivity {
    private final String ActionName = "winning.hrp.push";
    private MQProducer mqProducer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent ServiceIntenet = new Intent(this, MySerice.class);
        startService(ServiceIntenet);
        mqProducer = new MQProducer();
        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(ActionName);
                intent.setPackage(getPackageName());
                stopService(intent);

            }
        });
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            mqProducer.Init();
//                            mqProducer.send("测试的数据");
//                        } catch (Exception e) {
//                            Log.e("Tag", "发送失败");
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
                onSendPriMsg();
            }
        });
        findViewById(R.id.btnQunSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendQunMsg();
            }
        });
    }

    private void onSendPriMsg() {
        List<String> Users = new ArrayList<>();
        Users.add("00");
        RequestManager requestManager = new RequestManager(this);
        ApiManager.Api api = requestManager.sendPriMsg(Users, "给指定的用户发送消息");
        api.invoke(new NetworkEngine.Success() {
            @Override
            public void callback(Object data) {
                Log.e("Tag", data.toString());
            }
        }, new NetworkEngine.FailureOrError() {
            @Override
            public void callback(int i, String s, Map map) {
                Log.e("Tag", "报错：" + s);
            }
        });
    }

    private void onSendQunMsg() {
        RequestManager requestManager = new RequestManager(this);
        ApiManager.Api api = requestManager.sendBroMsg("我是群发消息，我来测试的");
        api.invoke(new NetworkEngine.Success() {
            @Override
            public void callback(Object data) {
                Log.e("Tag", data.toString());
            }
        }, new NetworkEngine.FailureOrError() {
            @Override
            public void callback(int i, String s, Map map) {
                Log.e("Tag", "报错：" + s);
            }
        });
    }
}
