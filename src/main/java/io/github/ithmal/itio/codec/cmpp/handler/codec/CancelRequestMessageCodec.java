package io.github.ithmal.itio.codec.cmpp.handler.codec;

import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.CancelRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 编码格式：源地址（6） +鉴别信息（16） + 版本（1） + 时间戳（4）
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:49
 */
public class CancelRequestMessageCodec implements IMessageCodec<CancelRequest> {

    @Override
    public CancelRequest decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf byteBuf) throws Exception {
        CancelRequest msg = new CancelRequest(sequenceId);
        msg.setMsgId(byteBuf.readLong());
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, CancelRequest msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeLong(msg.getMsgId());
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, CancelRequest msg) {
        return 8;
    }
}
