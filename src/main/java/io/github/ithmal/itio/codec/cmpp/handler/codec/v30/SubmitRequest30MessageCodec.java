package io.github.ithmal.itio.codec.cmpp.handler.codec.v30;

import io.github.ithmal.itio.codec.cmpp.base.MsgContent;
import io.github.ithmal.itio.codec.cmpp.base.MsgFormat;
import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.SubmitRequest;
import io.github.ithmal.itio.codec.cmpp.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

/**
 * 编码格式：源地址（6） +鉴别信息（16） + 版本（1） + 时间戳（4）
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:49
 */
public class SubmitRequest30MessageCodec implements IMessageCodec<SubmitRequest> {

    @Override
    public SubmitRequest decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf byteBuf) throws Exception {
        SubmitRequest msg = new SubmitRequest(sequenceId);
        msg.setMsgId(byteBuf.readLong());
        msg.setPkTotal(byteBuf.readByte());
        msg.setPkNumber(byteBuf.readByte());
        msg.setRegisteredDelivery(byteBuf.readByte());
        msg.setMsgLevel(byteBuf.readByte());
        msg.setServiceId(StringUtils.readString(byteBuf, 10, StandardCharsets.US_ASCII));
        msg.setFeeUserType(byteBuf.readByte());
        msg.setFeeTerminalId(StringUtils.readString(byteBuf, 32, StandardCharsets.US_ASCII));
        short feeTerminalType = byteBuf.readByte();
        msg.setTpPid(byteBuf.readByte());
        msg.setTpUdhi(byteBuf.readByte());
        short msgFmt = byteBuf.readByte();
        msg.setMsgSrc(StringUtils.readString(byteBuf, 6, StandardCharsets.US_ASCII));
        msg.setFeeType(StringUtils.readString(byteBuf, 2, StandardCharsets.US_ASCII));
        msg.setFeeCode(StringUtils.readString(byteBuf, 6, StandardCharsets.US_ASCII));
        msg.setValidTime(StringUtils.readString(byteBuf, 17, StandardCharsets.US_ASCII));
        msg.setAtTime(StringUtils.readString(byteBuf, 17, StandardCharsets.US_ASCII));
        msg.setSrcId(StringUtils.readString(byteBuf, 21, StandardCharsets.US_ASCII));
        short destUsrTl = byteBuf.readByte();
        String[] destTerminalIds = new String[destUsrTl];
        for (int i = 0; i < destUsrTl; i++) {
            destTerminalIds[i] = StringUtils.readString(byteBuf, 32, StandardCharsets.US_ASCII);
        }
        int destTerminalType = byteBuf.readByte();
        msg.setDestTerminalIds(destTerminalIds);
        msg.setMsgContent(MsgContent.read(byteBuf, MsgFormat.of(msgFmt), msg.getTpUdhi()));
        byte[] linkId = new byte[20];
        byteBuf.readBytes(linkId);
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, SubmitRequest msg, ByteBuf byteBuf) throws Exception {
        assert msg.getMsgContent() != null;
        assert msg.getMsgContent().validate();
        byteBuf.writeLong(msg.getMsgId());
        byteBuf.writeByte(msg.getPkTotal());
        byteBuf.writeByte(msg.getPkNumber());
        byteBuf.writeByte(msg.getRegisteredDelivery());
        byteBuf.writeByte(msg.getMsgLevel());
        byteBuf.writeBytes(StringUtils.toBytes(msg.getServiceId(), 10));
        byteBuf.writeByte(msg.getFeeUserType());
        byteBuf.writeBytes(StringUtils.toBytes(msg.getFeeTerminalId(), 32));
        byteBuf.writeByte(0); //源终端号码类型，0：真实号码；1：伪码。
        byteBuf.writeByte(msg.getTpPid());
        byteBuf.writeByte(msg.getTpUdhi());
        byteBuf.writeByte(msg.getMsgContent().getFormat().getId());
        byteBuf.writeBytes(StringUtils.toBytes(msg.getMsgSrc(), 6));
        byteBuf.writeBytes(StringUtils.toBytes(msg.getFeeType(), 2));
        byteBuf.writeBytes(StringUtils.toBytes(msg.getFeeCode(), 6));
        byteBuf.writeBytes(StringUtils.toBytes(msg.getValidTime(), 17));
        byteBuf.writeBytes(StringUtils.toBytes(msg.getAtTime(), 17));
        byteBuf.writeBytes(StringUtils.toBytes(msg.getSrcId(), 21));
        byteBuf.writeByte(msg.getDestTerminalIds().length);
        for (String destTerminalId : msg.getDestTerminalIds()) {
            byteBuf.writeBytes(StringUtils.toBytes(destTerminalId, 32));
        }
        byteBuf.writeByte(0); //接收短信的用户的号码类型，0：真实号码；1：伪码。
        msg.getMsgContent().write(byteBuf);
        byteBuf.writeBytes(StringUtils.toBytes(null, 20));
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, SubmitRequest msg) {
        return 151 + 32 * msg.getDestTerminalIds().length + msg.getMsgContent().getMsgLength();
    }
}
