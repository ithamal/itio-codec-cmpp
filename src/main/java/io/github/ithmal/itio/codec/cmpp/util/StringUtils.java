package io.github.ithmal.itio.codec.cmpp.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * @author: ken.lin
 * @since: 2023-10-01 12:11
 */
public class StringUtils {

    public static byte[] toBytes(String str, int length, Charset charset){
        return ensureLength(str,length).getBytes(charset);
    }

    public static byte[] toBytes(String str, int length){
        return ensureLength(str,length).getBytes();
    }


    public static String readString(ByteBuf byteBuf, int length, Charset charset){
        CharSequence charSequence = byteBuf.readCharSequence(length, charset);
        return charSequence.toString().trim();
    }

    private static String ensureLength(String str, int length) {
        if (str == null) {
            return new String(new char[length]);
        }
        if (str.length() == length) {
            return str;
        }
        char[] chars = new char[length];
        for (int i = 0; i < length && i < str.length(); i++) {
            chars[i] = str.charAt(i);
        }
        return new String(chars);
    }

    public static String leftPad(String str, int length, char ch) {
        if(str.length() == length) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        for (int i = str.length(); i < length; i++) {
            sb.insert(0, ch);
        }
        return sb.toString();
    }
}
