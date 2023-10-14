package io.github.ithmal.itio.codec.cmpp.handler.codec;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @author: ken.lin
 * @since: 2023-10-06 11:55
 */
public class MessageCodecVersionAdapter<T extends CmppMessage> implements IMessageCodec<T> {

    public IMessageCodec<T> v2Codec;

    public IMessageCodec<T> v3Codec;

    public static final AttributeKey<Short> VERSION_ATTR_KEY = AttributeKey.newInstance("version");

    public MessageCodecVersionAdapter(IMessageCodec<T> v2Codec, IMessageCodec<T> v3Codec) {
        this.v2Codec = v2Codec;
        this.v3Codec = v3Codec;
    }

    public static boolean isVersion2(ChannelHandlerContext ctx) {
        Short version = ctx.channel().attr(VERSION_ATTR_KEY).get();
        return version == null || version < CmppMessage.VERSION_30;
    }

    @Override
    public T decode(ChannelHandlerContext ctx, int sequenceId, ByteBuf in) throws Exception {
        if (isVersion2(ctx)) {
            return v2Codec.decode(ctx, sequenceId, in);
        } else {
            return v3Codec.decode(ctx, sequenceId, in);
        }
    }

    @Override
    public void encode(ChannelHandlerContext ctx, T msg, ByteBuf out) throws Exception {
        if (isVersion2(ctx)) {
            v2Codec.encode(ctx, msg, out);
        } else {
            v3Codec.encode(ctx, msg, out);
        }
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, T msg) {
        if (isVersion2(ctx)) {
            return v2Codec.getBodyLength(ctx, msg);
        } else {
            return v3Codec.getBodyLength(ctx, msg);
        }
    }
}
