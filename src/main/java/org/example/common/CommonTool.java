package org.example.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.entity.Account;
import org.example.entity.PhotoCollect;
import org.example.mapper.AccountMapper;
import org.example.mapper.PhotoCollectMapper;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommonTool {

    public static String generateRandomString() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[16]; // 16 bytes = 128 bits
        secureRandom.nextBytes(randomBytes);

        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : randomBytes) {
            stringBuilder.append(String.format("%02x", b)); // 将每个字节转换为两位十六进制
        }

        return stringBuilder.toString();
    }

    public static String generateLimitString(int count) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[count]; // 16 bytes = 128 bits
        secureRandom.nextBytes(randomBytes);

        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : randomBytes) {
            stringBuilder.append(String.format("%02x", b)); // 将每个字节转换为两位十六进制
        }

        return stringBuilder.toString();
    }

    public static List<Long> mapPhotoIds(List<PhotoCollect> collects) {
        List<Long> listIds = collects.stream()
                .map(collect -> {
                    return collect.getPhotoId();
                })
                .collect(Collectors.toList());
        return listIds;
    }

    public static Account tokenIsVaild(AccountMapper accountMapper, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        QueryWrapper<Account> query = new QueryWrapper<Account>();
        query.eq("token", token);
        query.last("limit 1");
        Account account = accountMapper.selectOne(query);
        request.getSession().setAttribute("user", account);
        return account;
    }

    public static List<Long>getMyCollectionPhotoIds(PhotoCollectMapper photoCollectMapper, Account account) {
        QueryWrapper<PhotoCollect> query = new QueryWrapper<PhotoCollect>();
        query.eq("user_id", account.getId());
        List<PhotoCollect> collects = photoCollectMapper.selectList(query);
        List<Long> listIds = CommonTool.mapPhotoIds(collects);
        return listIds;
    }


    public static Map<String, String> getParameterMapAll(HttpServletRequest request) {
        Enumeration<String> parameters = request.getParameterNames();

        Map<String, String> params = new HashMap<>();
        while (parameters.hasMoreElements()) {
            String parameter = parameters.nextElement();
            String value = request.getParameter(parameter);
            params.put(parameter, value);
        }

        return params;
    }

    /**
     * 获取请求Body
     *
     * @param request
     * @return
     */
    public static String getBodyString(ServletRequest request) {
        StringBuilder sb = new StringBuilder();

        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
