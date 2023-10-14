package io.github.ithmal.itio.codec.cmpp.handler.codec.v20;

import io.github.ithmal.itio.codec.cmpp.content.MsgFormat;
import io.github.ithmal.itio.codec.cmpp.content.ShortMsgContent;
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
public class SubmitRequest20MessageCodec implements IMessageCodec<SubmitRequest> {

    @Override
    public SubmitRequest decode(ChannelHandlerContext ctx, int sequenceId, ByteBuf in) throws Exception {
        SubmitRequest msg = new SubmitRequest(sequenceId);
        msg.setMsgId(in.readLong());
        msg.setPkTotal(in.readByte());
        msg.setPkNumber(in.readByte());
        msg.setRegisteredDelivery(in.readByte());
        msg.setMsgLevel(in.readByte());
        msg.setServiceId(StringUtils.readString(in, 10, StandardCharsets.US_ASCII));
        msg.setFeeUserType(in.readByte());
        msg.setFeeTerminalId(StringUtils.readString(in, 21, StandardCharsets.US_ASCII));
        msg.setTpPid(in.readByte());
        msg.setTpUdhi(in.readByte());
        short msgFmt = in.readByte();
        msg.setMsgSrc(StringUtils.readString(in, 6, StandardCharsets.US_ASCII));
        msg.setFeeType(StringUtils.readString(in, 2, StandardCharsets.US_ASCII));
        msg.setFeeCode(StringUtils.readString(in, 6, StandardCharsets.US_ASCII));
        msg.setValidTime(StringUtils.readString(in, 17, StandardCharsets.US_ASCII));
        msg.setAtTime(StringUtils.readString(in, 17, StandardCharsets.US_ASCII));
        msg.setSrcId(StringUtils.readString(in, 21, StandardCharsets.US_ASCII));
        short destUsrTl = in.readByte();
        String[] destTerminalIds = new String[destUsrTl];
        for (int i = 0; i < destUsrTl; i++) {
            destTerminalIds[i] = StringUtils.readString(in, 21, StandardCharsets.US_ASCII);
        }
        msg.setDestTerminalIds(destTerminalIds);
        msg.setMsgContent(ShortMsgContent.read(in, MsgFormat.of(msgFmt), msg.getTpUdhi()));
        byte[] reversed = new byte[8];
        in.readBytes(reversed);
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, SubmitRequest msg, ByteBuf out) throws Exception {
        assert msg.getMsgContent() != null;
        assert msg.getMsgContent().validate();
        out.writeLong(msg.getMsgId());
        out.writeByte(msg.getPkTotal());
        out.writeByte(msg.getPkNumber());
        out.writeByte(msg.getRegisteredDelivery());
        out.writeByte(msg.getMsgLevel());
        out.writeBytes(StringUtils.toBytes(msg.getServiceId(), 10));
        out.writeByte(msg.getFeeUserType());
        out.writeBytes(StringUtils.toBytes(msg.getFeeTerminalId(), 21));
        out.writeByte(msg.getTpPid());
        out.writeByte(msg.getTpUdhi());
        out.writeByte(msg.getMsgContent().getFormat().getId());
        out.writeBytes(StringUtils.toBytes(msg.getMsgSrc(), 6));
        out.writeBytes(StringUtils.toBytes(msg.getFeeType(), 2));
        out.writeBytes(StringUtils.toBytes(msg.getFeeCode(), 6));
        out.writeBytes(StringUtils.toBytes(msg.getValidTime(), 17));
        out.writeBytes(StringUtils.toBytes(msg.getAtTime(), 17));
        out.writeBytes(StringUtils.toBytes(msg.getSrcId(), 21));
        out.writeByte(msg.getDestTerminalIds().length);
        for (String destTerminalId : msg.getDestTerminalIds()) {
            out.writeBytes(StringUtils.toBytes(destTerminalId, 21));
        }
        out.writeByte(msg.getMsgContent().getMsgLength());
        msg.getMsgContent().output(out);
        out.writeBytes(StringUtils.toBytes(null, 8));
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, SubmitRequest msg) {
        return 126 + 21 * msg.getDestTerminalIds().length + msg.getMsgContent().getMsgLength();
    }
}
