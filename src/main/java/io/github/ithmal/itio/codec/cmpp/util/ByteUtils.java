package io.github.ithmal.itio.codec.cmpp.util;

/**
 * 公用的工具类
 *
 * @author liuyanning
 */
public final class ByteUtils {

    public static byte[] ensureLength(byte[] array, int minLength) {
        return ensureLength(array, minLength, 0);
    }

    /**
     * 保证byte数组的长度
     *
     * @param array     原数组
     * @param minLength 最小长度
     * @param padding   扩展长度
     * @return 字节数组
     */
    public static byte[] ensureLength(byte[] array, int minLength, int padding) {
        if (array.length == minLength) {
            return array;
        }
        return array.length > minLength ? copyOf(array, minLength) : copyOf(array, minLength + padding);
    }

    /**
     * original扩展为length长度的数组，右侧默认0
     *
     * @param original
     * @param length
     * @return
     */
    private static byte[] copyOf(byte[] original, int length) {
        byte copy[] = new byte[length];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
        return copy;
    }


    public static byte[] concat(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }
        byte[] result = new byte[length];
        int pos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }
        return result;
    }
}
