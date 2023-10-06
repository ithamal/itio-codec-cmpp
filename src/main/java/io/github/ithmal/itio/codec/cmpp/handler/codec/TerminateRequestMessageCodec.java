package io.github.ithmal.itio.codec.cmpp.handler.codec;

import io.github.ithmal.itio.codec.cmpp.handler.ICmppCodec;
import io.github.ithmal.itio.codec.cmpp.message.TerminateRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 编码格式：源地址（6） +鉴别信息（16） + 版本（1） + 时间戳（4）
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:49
 */
public class TerminateRequestMessageCodec implements ICmppCodec<TerminateRequest> {

    @Override
    public TerminateRequest decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf byteBuf) throws Exception {
        return new TerminateRequest(sequenceId);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, TerminateRequest msg, ByteBuf byteBuf) throws Exception {

    }
}
