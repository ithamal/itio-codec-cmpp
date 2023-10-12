package io.github.ithmal.itio.codec.cmpp.handler.codec.v20;

import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.base.MsgContent;
import io.github.ithmal.itio.codec.cmpp.base.MsgFormat;
import io.github.ithmal.itio.codec.cmpp.base.MsgReport;
import io.github.ithmal.itio.codec.cmpp.message.DeliverRequest;
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
public class DeliverRequest20MessageCodec implements IMessageCodec<DeliverRequest> {

    @Override
    public DeliverRequest decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf byteBuf) throws Exception {
        DeliverRequest msg = new DeliverRequest(sequenceId);
        msg.setMsgId(byteBuf.readLong());
        msg.setDestId(StringUtils.readString(byteBuf, 21, StandardCharsets.US_ASCII));
        msg.setServiceId(StringUtils.readString(byteBuf, 10, StandardCharsets.US_ASCII));
        msg.setTpPid(byteBuf.readByte());
        msg.setTpUdhi(byteBuf.readByte());
        short msgFmt = byteBuf.readByte();
        msg.setSrcTerminalId(StringUtils.readString(byteBuf, 21, StandardCharsets.US_ASCII));
        short registeredDelivery = byteBuf.readByte();
        // 非报告
        if (registeredDelivery == 0) {
            msg.setMsgContent(MsgContent.read(byteBuf, MsgFormat.of(msgFmt), msg.getTpUdhi()));
        } else {
            short msgLength = byteBuf.readByte();
            MsgReport report = new MsgReport();
            report.setMsgId(byteBuf.readLong());
            report.setStat(StringUtils.readString(byteBuf, 7, StandardCharsets.US_ASCII));
            report.setSubmitTime(StringUtils.readString(byteBuf, 10, StandardCharsets.US_ASCII));
            report.setDoneTime(StringUtils.readString(byteBuf, 10, StandardCharsets.US_ASCII));
            report.setDestTerminalId(StringUtils.readString(byteBuf, 21, StandardCharsets.US_ASCII));
            report.setSmscSequence(byteBuf.readInt());
            msg.setReport(report);
        }
        byte[] reversed = new byte[8];
        byteBuf.readBytes(reversed);
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, DeliverRequest msg, ByteBuf byteBuf) throws Exception {
        assert msg.getMsgContent() != null || msg.getReport() != null;
        assert msg.getMsgContent() == null || msg.getMsgContent().validate();
        byteBuf.writeLong(msg.getMsgId());
        byteBuf.writeBytes(StringUtils.toBytes(msg.getDestId(), 21));
        byteBuf.writeBytes(StringUtils.toBytes(msg.getServiceId(), 10));
        byteBuf.writeByte(msg.getTpPid());
        byteBuf.writeByte(msg.getTpUdhi());
        if (msg.getReport() == null) {
            byteBuf.writeByte(msg.getMsgContent().getFormat().getId());
        } else {
            byteBuf.writeByte(0);
        }
        byteBuf.writeBytes(StringUtils.toBytes(msg.getSrcTerminalId(), 21));
        byteBuf.writeByte(msg.getReport() == null ? 0 : 1);
        // 非报告
        if (msg.getReport() == null) {
            msg.getMsgContent().write(byteBuf);
        } else {
            MsgReport report = msg.getReport();
            byteBuf.writeByte(report.getMsgLength());
            byteBuf.writeLong(report.getMsgId());
            byteBuf.writeBytes(StringUtils.toBytes(report.getStat(), 7));
            byteBuf.writeBytes(StringUtils.toBytes(report.getSubmitTime(), 10));
            byteBuf.writeBytes(StringUtils.toBytes(report.getDoneTime(), 10));
            byteBuf.writeBytes(StringUtils.toBytes(report.getDestTerminalId(), 21));
            byteBuf.writeInt(report.getSmscSequence());
        }
        byteBuf.writeBytes(new byte[8]);
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, DeliverRequest msg) {
        int length = 73;
        if (msg.getReport() != null) {
            return length + 60;
        } else {
            return length + msg.getMsgContent().getMsgLength();
        }
    }
}
