package org.example.common;

import java.security.SecureRandom;

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
}
