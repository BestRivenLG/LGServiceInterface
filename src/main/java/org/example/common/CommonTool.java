package org.example.common;

import org.example.entity.PhotoCollect;

import java.security.SecureRandom;
import java.util.List;
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
}
