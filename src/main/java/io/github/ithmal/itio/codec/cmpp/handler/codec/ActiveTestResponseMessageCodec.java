package io.github.ithmal.itio.codec.cmpp.handler.codec;

import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.ActiveTestResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 编码格式：源地址（6） +鉴别信息（16） + 版本（1） + 时间戳（4）
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:49
 */
public class ActiveTestResponseMessageCodec implements IMessageCodec<ActiveTestResponse> {

    @Override
    public ActiveTestResponse decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf byteBuf) throws Exception {
        byteBuf.readByte();
        return new ActiveTestResponse(sequenceId);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ActiveTestResponse msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeByte(0);
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, ActiveTestResponse msg) {
        return 1;
    }
}
