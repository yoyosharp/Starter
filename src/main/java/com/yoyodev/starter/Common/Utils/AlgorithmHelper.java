package com.yoyodev.starter.Common.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class AlgorithmHelper {

    private AlgorithmHelper() {
    }

    public static String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating MD5 hash", e);
        }
    }

    public static boolean validateMD5(String hashedText, String rawText) throws Exception {
        String md5 = generateMD5(rawText);
        return md5.equals(hashedText);
    }
}
