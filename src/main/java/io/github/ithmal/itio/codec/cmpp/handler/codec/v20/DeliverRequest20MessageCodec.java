package io.github.ithmal.itio.codec.cmpp.handler.codec.v20;

import io.github.ithmal.itio.codec.cmpp.content.MsgFormat;
import io.github.ithmal.itio.codec.cmpp.content.MsgReport;
import io.github.ithmal.itio.codec.cmpp.content.ShortMsgContent;
import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
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
    public DeliverRequest decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf in) throws Exception {
        DeliverRequest msg = new DeliverRequest(sequenceId);
        msg.setMsgId(in.readLong());
        msg.setDestId(StringUtils.readString(in, 21, StandardCharsets.US_ASCII));
        msg.setServiceId(StringUtils.readString(in, 10, StandardCharsets.US_ASCII));
        msg.setTpPid(in.readByte());
        msg.setTpUdhi(in.readByte());
        short msgFmt = in.readByte();
        msg.setSrcTerminalId(StringUtils.readString(in, 21, StandardCharsets.US_ASCII));
        short registeredDelivery = in.readByte();
        // 非报告
        if (registeredDelivery == 0) {
            msg.setMsgContent(ShortMsgContent.read(in, MsgFormat.of(msgFmt), msg.getTpUdhi()));
        } else {
            short msgLength = in.readByte();
            MsgReport report = new MsgReport();
            report.setMsgId(in.readLong());
            report.setStat(StringUtils.readString(in, 7, StandardCharsets.US_ASCII));
            report.setSubmitTime(StringUtils.readString(in, 10, StandardCharsets.US_ASCII));
            report.setDoneTime(StringUtils.readString(in, 10, StandardCharsets.US_ASCII));
            report.setDestTerminalId(StringUtils.readString(in, 21, StandardCharsets.US_ASCII));
            report.setSmscSequence(in.readInt());
            msg.setReport(report);
        }
        byte[] reversed = new byte[8];
        in.readBytes(reversed);
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, DeliverRequest msg, ByteBuf out) throws Exception {
        assert msg.getMsgContent() != null || msg.getReport() != null;
        assert msg.getMsgContent() == null || msg.getMsgContent().validate();
        out.writeLong(msg.getMsgId());
        out.writeBytes(StringUtils.toBytes(msg.getDestId(), 21));
        out.writeBytes(StringUtils.toBytes(msg.getServiceId(), 10));
        out.writeByte(msg.getTpPid());
        out.writeByte(msg.getTpUdhi());
        if (msg.getReport() == null) {
            out.writeByte(msg.getMsgContent().getFormat().getId());
        } else {
            out.writeByte(0);
        }
        out.writeBytes(StringUtils.toBytes(msg.getSrcTerminalId(), 21));
        out.writeByte(msg.getReport() == null ? 0 : 1);
        // 非报告
        if (msg.getReport() == null) {
            out.writeByte(msg.getMsgContent().getMsgLength());
            msg.getMsgContent().output(out);
        } else {
            MsgReport report = msg.getReport();
            out.writeByte(report.getMsgLength());
            out.writeLong(report.getMsgId());
            out.writeBytes(StringUtils.toBytes(report.getStat(), 7));
            out.writeBytes(StringUtils.toBytes(report.getSubmitTime(), 10));
            out.writeBytes(StringUtils.toBytes(report.getDoneTime(), 10));
            out.writeBytes(StringUtils.toBytes(report.getDestTerminalId(), 21));
            out.writeInt(report.getSmscSequence());
        }
        out.writeBytes(new byte[8]);
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
