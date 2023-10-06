package io.github.ithmal.itio.codec.cmpp.base;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author: ken.lin
 * @since: 2023-10-05 22:16
 */
@Getter
public class UserDataHeader {

    private short msgId;

    private short pkTotal;

    private short pkNumber;

    @Getter(AccessLevel.PRIVATE)
    private short len;

    private UserDataHeader() {

    }

    public static UserDataHeader six(short msgId, short pkTotal, short pkNumber) {
        UserDataHeader header = new UserDataHeader();
        header.len = 0x5;
        header.msgId = msgId;
        header.pkTotal = pkTotal;
        header.pkNumber = pkNumber;
        return header;
    }

    public static UserDataHeader seven(short msgId, short pkTotal, short pkNumber) {
        UserDataHeader header = new UserDataHeader();
        header.len = 0x6;
        header.msgId = msgId;
        header.pkTotal = pkTotal;
        header.pkNumber = pkNumber;
        return header;
    }

    public static UserDataHeader read(ByteBuf byteBuf) {
        short len = byteBuf.readByte();
        byteBuf.readByte();
        byteBuf.readByte();
        UserDataHeader header = new UserDataHeader();
        if (len == 5) {
            header.msgId = (short) (byteBuf.readByte() & 0xff);
        } else {
            header.msgId = byteBuf.readShort();
        }
        header.len = len;
        header.pkTotal = byteBuf.readByte();
        header.pkNumber = byteBuf.readByte();
        return header;
    }

    public byte[] getBytes() {
        byte[] udhi = new byte[len + 1];
        if (len == 0x5) {
            udhi[0] = 0x05;
            udhi[1] = 0x00;
            udhi[2] = 0x03;
            udhi[3] = (byte) (msgId & 0xff);
            udhi[4] = (byte) pkTotal;
            udhi[5] = (byte) pkNumber;
        } else if (len == 0x6) {
            udhi[0] = 0x06;
            udhi[1] = 0x00;
            udhi[2] = 0x04;
            udhi[3] = (byte) (msgId >> 8);
            udhi[4] = (byte) (msgId & 0xff);
            udhi[5] = (byte) pkTotal;
            udhi[6] = (byte) pkNumber;
        } else {
            throw new RuntimeException("非法的长度");
        }
        return udhi;
    }

    public short getMsgLength() {
        return (short) (len + 1);
    }

    @Override
    public String toString() {
        return "UserDataHeader{" +
                "len=" + len +
                ", msgId=" + msgId +
                ", pkTotal=" + pkTotal +
                ", pkNumber=" + pkNumber +
                '}';
    }
}
