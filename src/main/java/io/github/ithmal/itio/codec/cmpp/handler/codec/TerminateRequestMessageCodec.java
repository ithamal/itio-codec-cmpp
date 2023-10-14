package io.github.ithmal.itio.codec.cmpp.handler.codec;

import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.TerminateRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 编码格式：源地址（6） +鉴别信息（16） + 版本（1） + 时间戳（4）
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:49
 */
public class TerminateRequestMessageCodec implements IMessageCodec<TerminateRequest> {

    @Override
    public TerminateRequest decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf in) throws Exception {
        return new TerminateRequest(sequenceId);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, TerminateRequest msg, ByteBuf out) throws Exception {

    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, TerminateRequest msg) {
        return 0;
    }
}
