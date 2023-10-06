package io.github.ithmal.itio.codec.cmpp.base;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * @author: ken.lin
 * @since: 2023-10-01 13:01
 */
@Getter
public class MsgContent {

    /**
     * 用户数据头udhi=1时有值
     */
    private UserDataHeader header;

    /**
     * 0：ASCII串
     * 3：短信写卡操作
     * 4：二进制信息
     * 8：UCS2编码15：含GB汉字。
     */
    private MsgFormat format;

    /**
     * 信息长度(Msg_Fmt值为0时：<160个字节；其它<=140个字节)
     */
    private byte[] body;

    public MsgContent(UserDataHeader header, MsgFormat format, byte[] body) {
        this.header = header;
        this.format = format;
        this.body = body;
    }

    public void write(ByteBuf byteBuf) {
        UserDataHeader header = this.getHeader();
        if (header != null) {
            byteBuf.writeByte(body.length + header.getMsgLength());
            byteBuf.writeBytes(header.getBytes());
            byteBuf.writeBytes(body);
        } else {
            byteBuf.writeByte(body.length);
            byteBuf.writeBytes(body);
        }
    }

    public String getText() {
        return new String(body, format.getCharset());
    }


    public static MsgContent fromText(String text, MsgFormat format) {
        byte[] bytes = text.getBytes(format.getCharset());
        return new MsgContent(null, format, bytes);
    }


    public static MsgContent read(ByteBuf byteBuf, MsgFormat format, short udhi) {
        short msgLength = (short) (byteBuf.readByte() & 0xff);
        UserDataHeader header;
        byte[] body;
        if (udhi == 1) {
            header = UserDataHeader.read(byteBuf);
            body = new byte[msgLength - header.getMsgLength()];
            byteBuf.readBytes(body);
        } else {
            body = new byte[msgLength];
            byteBuf.readBytes(body);
            header = null;
        }
        return new MsgContent(header, format, body);
    }

    public short getMsgLength() {
        if (header != null) {
            return (short) (body.length + header.getMsgLength());
        } else {
            return (short) body.length;
        }
    }


    public int getLimitMsgLength() {
        if (format.getId() == 0) {
            return 159;
        } else {
            return 140;
        }
    }

    public boolean validate() {
        short headLength = header == null ? 0 : header.getMsgLength();
        return body.length + headLength <= getLimitMsgLength();
    }

    @Override
    public String toString() {
        return "MsgContent{" +
                "header=" + header +
                ", format=" + format +
                ", body=" + new String(body, format.getCharset()) +
                '}';
    }
}
