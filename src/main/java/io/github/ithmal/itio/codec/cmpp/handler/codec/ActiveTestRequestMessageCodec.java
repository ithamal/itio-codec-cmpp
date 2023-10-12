package io.github.ithmal.itio.codec.cmpp.handler.codec;

import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.ActiveTestRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 编码格式：源地址（6） +鉴别信息（16） + 版本（1） + 时间戳（4）
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:49
 */
public class ActiveTestRequestMessageCodec implements IMessageCodec<ActiveTestRequest> {

    @Override
    public ActiveTestRequest decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf byteBuf) throws Exception {
        return new ActiveTestRequest(sequenceId);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ActiveTestRequest msg, ByteBuf byteBuf) throws Exception {

    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, ActiveTestRequest msg) {
        return 0;
    }
}
