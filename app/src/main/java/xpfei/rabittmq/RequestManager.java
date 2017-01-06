package xpfei.rabittmq;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;

import java.util.List;

import winning.framework.BaseActivity;
import winning.framework.managers.ApiManager;

/**
 * 主要的请求接口
 * Created by xpf on 2016/9/23.
 */

public class RequestManager extends ApiManager {

    public RequestManager(Context context) {
        super((BaseActivity) context);
    }

    /**
     * 给指定用户的所有端都发送信息
     *
     * @param Users
     * @param msg
     * @return
     */
    public Api sendPriMsg(List<String> Users, String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("toUsers", Users);
        jsonObject.put("message", msg);
        return createAPI(Request.Method.POST, "http://172.16.0.217:8911/HRP.MessageService/MessageService.svc/SendPrivateMessage", jsonObject);
    }

    /**
     * 群发
     *
     * @param msg
     * @return
     */
    public Api sendBroMsg(String msg) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", msg);
        return createAPI(Request.Method.POST, "http://172.16.0.217:8911/HRP.MessageService/MessageService.svc/SendBroadcastMessage", jsonObject);
    }
}
