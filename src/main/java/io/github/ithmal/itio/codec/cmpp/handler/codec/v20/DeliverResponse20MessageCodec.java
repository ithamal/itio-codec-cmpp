package io.github.ithmal.itio.codec.cmpp.handler.codec.v20;

import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.DeliverResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 编码格式：源地址（6） +鉴别信息（16） + 版本（1） + 时间戳（4）
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:49
 */
public class DeliverResponse20MessageCodec implements IMessageCodec<DeliverResponse> {

    @Override
    public DeliverResponse decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf byteBuf) throws Exception {
        DeliverResponse msg = new DeliverResponse(sequenceId);
        msg.setMsgId(byteBuf.readLong());
        msg.setResult(byteBuf.readByte());
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, DeliverResponse msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeLong(msg.getMsgId());
        byteBuf.writeByte(msg.getResult());
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, DeliverResponse msg) {
        return 9;
    }
}
