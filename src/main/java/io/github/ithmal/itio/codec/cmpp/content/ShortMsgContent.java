package io.github.ithmal.itio.codec.cmpp.content;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

/**
 * @author: ken.lin
 * @since: 2023-10-14 07:32
 */
public class ShortMsgContent implements MsgContent {

    private MsgFormat format;

    private UserDataHeader header;

    private ByteBuf body;

    public ShortMsgContent(MsgFormat format, UserDataHeader header, ByteBuf body) {
        this.format = format;
        this.header = header;
        this.body = body;
    }


    @Override
    public MsgFormat getFormat() {
        return format;
    }

    public static int getMsgLengthLimit(MsgFormat format) {
        if (format.getId() == 0) {
            return 159;
        } else {
            return 140;
        }
    }

    @Override
    public int getMsgLength() {
        if (header == null) {
            return body.capacity();
        } else {
            return header.getMsgLength() + body.capacity();
        }
    }

    public UserDataHeader getHeader() {
        return header;
    }

    @Override
    public boolean validate() {
        return getMsgLength() <= getMsgLengthLimit(format);
    }

    @Override
    public void output(ByteBuf out) {
        if (header != null) {
            header.output(out);
            out.writeBytes(body.slice());
        } else {
            out.writeBytes(body.slice());
        }
    }

    @Override
    public String getText() {
        return body.slice(0, body.capacity()).toString(format.getCharset());
    }

    @Override
    protected void finalize() throws Throwable {
        if (body != null) {
            ReferenceCountUtil.release(body);
        }
        super.finalize();
    }

    @Override
    public String toString() {
        return "ShortMsgContent{" +
                "header=" + header +
                ", format=" + format +
                ", body=" + getText() +
                '}';
    }

    public static ShortMsgContent fromText(String text, MsgFormat format) {
        int msgLengthLimit = getMsgLengthLimit(format);
        ByteBuf body = Unpooled.copiedBuffer(text, format.getCharset());
        if (body.readableBytes() > msgLengthLimit) {
            body.release();
            throw new IllegalArgumentException("text length too large, must be less than " + msgLengthLimit);
        }
        return new ShortMsgContent(format, null, body);
    }

    public static ShortMsgContent read(ByteBuf in, MsgFormat format, short udhi) {
        short msgLength = (short) (in.readByte() & 0xff);
        UserDataHeader header;
        ByteBuf body;
        if (udhi == 1) {
            header = UserDataHeader.read(in);
            body = Unpooled.buffer(msgLength - header.getMsgLength());
            in.readBytes(body);
        } else {
            body = Unpooled.buffer(msgLength);
            in.readBytes(body);
            header = null;
        }
        return new ShortMsgContent(format, header, body);
    }
}
