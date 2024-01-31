package io.github.ithmal.itio.codec.cmpp.util;

import io.github.ithmal.itio.codec.cmpp.content.*;
import io.github.ithmal.itio.codec.cmpp.sequence.SequenceManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author: ken.lin
 * @since: 2023-10-06 11:27
 */
public class LongSmsUtils {

    public static ShortMsgContent[] fromText(String text, MsgFormat format) {
        ByteBuf all = Unpooled.copiedBuffer(text, format.getCharset());
        int allLength = all.readableBytes();
        int msgLengthLimit = ShortMsgContent.getMsgLengthLimit(format);
        if (allLength <= msgLengthLimit) {
            return new ShortMsgContent[]{new ShortMsgContent(format, null, all)};
        }
        short msgId = (short) (System.nanoTime() % 255);
        int bodyLength = msgLengthLimit - 6;
        short pkTotal = (short) Math.ceil(allLength / (double) bodyLength);
        ShortMsgContent[] slices = new ShortMsgContent[pkTotal];
        for (short pkNumber = 1; pkNumber <= pkTotal; pkNumber++) {
            int from = (pkNumber - 1) * bodyLength;
            int length = from + bodyLength > allLength ? (allLength - from) : bodyLength;
            ByteBuf body = all.retainedSlice(from, length);
            UserDataHeader header = UserDataHeader.six(msgId, pkTotal, pkNumber);
            slices[pkNumber - 1] = new ShortMsgContent(format, header, body);
        }
        return slices;
    }

    public static LongSmsContent fromText(SequenceManager sequenceManager, long msgId, String text, MsgFormat format) {
        ShortMsgContent[] shortMsgContents = fromText(text, format);
        LongSmsContent longSmsContent = new LongSmsContent(format, (short) shortMsgContents.length);
        for (short pkNumber = 1; pkNumber <= shortMsgContents.length; pkNumber++) {
            ShortMsgContent shortMsgContent = shortMsgContents[pkNumber - 1];
            longSmsContent.append(new MsgContentSlice(sequenceManager.nextValue(), msgId, pkNumber, shortMsgContent));
        }
        return longSmsContent;
    }
}
