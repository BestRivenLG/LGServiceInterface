package org.example.action;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import org.example.common.*;
import org.example.config.RequestWrapper;
import org.example.config.UserLoginInterceptor;
import org.example.entity.*;
import org.example.mapper.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Handler;

import okhttp3.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class TotalActions {

    /*服务器端口*/
    @Value("${server.port}")
    private int serPort;

    /*获取服务器IP*/
    @GetMapping("/hello")
    public RespResult<String> hello() {
        RespResult<String> result = new RespResult<String>();
        String ress = "";
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            ress = ipAddress + ":" + serPort;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        result.setData(ress);
        result.setCode(RespErrorCode.OK.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping(value = "/log/appsFlyerCallback")
    @PostMapping(value = "/log/appsFlyerCallback")
    public RespResult<String> appsFlyerCallback(HttpServletRequest request) {
        RespResult<String> result = new RespResult<>();
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setCode(RespErrorCode.OK.getCode());
        Map<String, String > maps = CommonTool.getParameterMapAll(request);
        String paramter = maps.toString();
        log.info("appsFlyerCallback parmater:" + paramter);
        String event_value = "";
        String opUrl = maps.get("original_url");
        String envent_name = maps.get("event_name");
        if (maps.get("event_value") != null) {
            if (maps.get("event_value") instanceof String) {
                event_value = maps.get("event_value");
            } else {
                event_value = maps.get("event_value").toString();
            }
        }
        String total = envent_name + " " + opUrl + " " + event_value;
        log.info(total);
        sendLarkWebhookMessage(total);
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping(value = "/log/send/text")
    private RespResult<String> sendLarkWebhookMessage(String text) {
        Map<String, String> maps = new HashMap<>();
        String str = "{\"text\":\""+ "iOS "+ text + " \"}";
        maps.put("msg_type", "text");
        maps.put("content", str);
        postRequest("https://open.larksuite.com/open-apis/bot/v2/hook/1c1bc503-6371-47b0-8bf6-a17115f3f2b3", maps);
        RespResult<String> result = new RespResult<>();
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setCode(RespErrorCode.OK.getCode());
        return result;
    }

    public static void postRequest(String url, Map<String, String> maps) {
        FormBody.Builder builder1 = new FormBody.Builder();
        for (String key : maps.keySet()) {
            //追加表单信息
            builder1.add(key, maps.get(key) == null ? "" : maps.get(key));
        }
        log.info("maps: = "+maps);
        RequestBody formBody = builder1.build();
        Request request = new Request.Builder().
                url(url)
                .post(formBody)
                .addHeader("Content-Type", "application/json")
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Call call1 = okHttpClient.newCall(request);
        call1.enqueue(new Callback() {
            //请求时失败时调用
            @Override
            public void onFailure(Call call, IOException e) {
                log.info("onFailure: " + call);
            }

            //请求成功时调用
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //处于子线程中，能够进行大文件下载，但是无法更新UI
                final String res = response.body().string();//请求成功时返回的东西
                log.info("res: " + res);
            }
        });
    }

    private static void callback(Call call) {
        call.enqueue(new Callback() {
            //请求时失败时调用
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure: " + call);
            }

            //请求成功时调用
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //处于子线程中，能够进行大文件下载，但是无法更新UI
                final String res = response.body().string();//请求成功时返回的东西
                System.out.println("res: " + res);
            }
        });
    }

}
