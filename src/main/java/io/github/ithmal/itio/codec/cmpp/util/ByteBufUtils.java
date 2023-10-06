package io.github.ithmal.itio.codec.cmpp.util;

import io.netty.buffer.ByteBuf;

/**
 * @author: ken.lin
 * @date: 2023/2/4
 */
public class ByteBufUtils {

    public static byte[] toArray(ByteBuf buf, int length) {
        byte[] result = new byte[length];
        buf.readBytes(result);
        return result;
    }
}
