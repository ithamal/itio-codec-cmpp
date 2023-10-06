package io.github.ithmal.itio.codec.cmpp.handler;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: ken.lin
 * @since: 2023-10-01 08:27
 */
public interface ICmppCodec<T extends CmppMessage> {

    /**
     * 解码
     *
     */
    T decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf byteBuf) throws Exception;

    /**
     * 编码
     *
     */
    void encode(ChannelHandlerContext ctx, T msg, ByteBuf byteBuf) throws Exception;
}
