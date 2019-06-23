package com.zhku161022.audioassistant.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Created by TableBear on 2018/5/13.
 * @Describe:
 */

class MD5 {
    private static final char[] hexDigits =
            { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String getMD5Code(String input){
        if (input==null){
            return null;
        }else{
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                byte[] inputBytes = input.getBytes("utf-8");
                messageDigest.update(inputBytes);
                byte[] resultBytes = messageDigest.digest();
                return byteArrayToHex(resultBytes);
            } catch (NoSuchAlgorithmException e) {
                return null;
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
    }

    private static String byteArrayToHex(byte[] byteArray) {
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);

    }
}
