package xpfei.rabittmq;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.UnsupportedEncodingException;


/**
 * 描 述: (这里用一句话描述这个类的作用)
 * 作 者: xpfei
 * 时 间: 2016/12/7
 */
public class MySerice extends Service {
    private MQConsumer mConsumer;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private boolean isPush = true;
    private int num = 1;

    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //取到用户的对象
        mConsumer = new MQConsumer();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isPush) {
                    try {
                        mConsumer.Init();
                        mConsumer.setConfig();
                        isPush = false;
                    } catch (Exception e) {
                        if (num < 5) {
                            isPush = true;
                            num++;
                        } else {
                            isPush = false;
                        }
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        Log.e("Tag", "通道创建失败：" + e.getMessage());
                    }
                }
            }
        }).start();
        mConsumer.setOnReceiveMessageHandler(new MQConsumer.OnReceiveMessageHandler() {
            public void onReceiveMessage(byte[] message) {
                String text;
                try {
                    text = new String(message, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    text = "您有新的消息提醒";
                    e.printStackTrace();
                }
                showDefaultNotification(text);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConsumer.dispush();
    }

    // 默认显示的的Notification
    private void showDefaultNotification(String message) {
        mBuilder.setContentTitle("系统消息")//设置通知栏标题
                .setContentText(message) //设置通知栏显示内容
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
                .setTicker("您有新的通知，请注意查看") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
        mNotificationManager.notify(0, mBuilder.build());
    }

    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }
}
