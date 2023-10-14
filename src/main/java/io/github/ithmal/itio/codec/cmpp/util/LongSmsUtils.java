package io.github.ithmal.itio.codec.cmpp.util;

import io.github.ithmal.itio.codec.cmpp.base.MsgContent;
import io.github.ithmal.itio.codec.cmpp.base.MsgFormat;
import io.github.ithmal.itio.codec.cmpp.base.UserDataHeader;
import io.github.ithmal.itio.codec.cmpp.content.ShortMsgContent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author: ken.lin
 * @since: 2023-10-06 11:27
 */
public class LongSmsUtils {

    public static MsgContent[] fromText(String text, MsgFormat format) {
        ByteBuf all = Unpooled.copiedBuffer(text, format.getCharset());
        int allLength = all.readableBytes();
        int msgLengthLimit = ShortMsgContent.getMsgLengthLimit(format);
        if (allLength <= msgLengthLimit) {
            return new ShortMsgContent[]{new ShortMsgContent(format, null, all)};
        }
        short msgId = (short) (System.nanoTime() % 255);
        int bodyLength = msgLengthLimit - 6;
        short pkTotal = (short) Math.ceil(allLength  / (double)bodyLength);
        ShortMsgContent[] slices = new ShortMsgContent[pkTotal];
        for (short pkNumber = 1; pkNumber <= pkTotal; pkNumber++) {
            int from = (pkNumber - 1) * bodyLength;
            int length = from + bodyLength > allLength ? (allLength - from) : bodyLength;
            ByteBuf body = all.retainedSlice(from, length);
            UserDataHeader header = UserDataHeader.six(msgId, pkTotal, pkNumber);
            slices[pkNumber - 1]  = new ShortMsgContent(format, header, body);
        }
        return slices;
    }
}
