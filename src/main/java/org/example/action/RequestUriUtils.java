package org.example.action;

import io.netty.channel.ChannelHandler;
import org.example.entity.Account;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RequestUriUtils {



    /**
     * 将路径参数转换成Map对象，如果路径参数出现重复参数名，将以最后的参数值为准
     * @param uri 传入的携带参数的路径
     * @return
     */
    public static Map<String, String> getParams(String uri) {
        Map<String, String> params = new HashMap<>(10);

        int idx = uri.indexOf("?");
        if (idx != -1) {
            String[] paramsArr = uri.substring(idx + 1).split("&");

            for (String param : paramsArr) {
                idx = param.indexOf("=");
                params.put(param.substring(0, idx), param.substring(idx + 1));
            }
        }

        return params;
    }

    /**
     * 获取URI中参数以外部分路径
     * @param uri
     * @return
     */
    public static String getBasePath(String uri) {
        if (uri == null || uri.isEmpty())
            return null;

        int idx = uri.indexOf("?");
        if (idx == -1)
            return uri;

        return uri.substring(0, idx);
    }

    public static boolean tokenIsVail(HttpServletRequest httpServletRequest) {
        {
            Account account = (Account) httpServletRequest.getSession().getAttribute("user");
            boolean isVail = account != null;
            String content = isVail ? "已登录" : "未登录";
            System.out.println(content);
            return  isVail;
        }
    }

    public static String mdfive(String input) {
        try {
            // 创建MessageDigest对象，指定算法为MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 将字符串转换为字节数组
            byte[] bytes = input.getBytes();

            // 更新MessageDigest以处理字节数组
            md.update(bytes);

            // 计算MD5哈希值
            byte[] digest = md.digest();

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }

            String string = hexString.toString();
            // 打印MD5哈希值
            System.out.println("MD5 Hash: " + string);
            return string;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
