package org.example.action;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


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

    @GetMapping(value = "/log/appsFlyerCallback")
    public RespResult<String> appsFlyerCallback(HttpServletRequest request) {
        RespResult<String> result = new RespResult<>();
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setCode(RespErrorCode.OK.getCode());
        Map<String, String > maps = CommonTool.getParameterMapAll(request);
        String paramter = maps.toString();
        log.info("appsFlyerCallback parmater:" + paramter);
        return result;
    }

}
