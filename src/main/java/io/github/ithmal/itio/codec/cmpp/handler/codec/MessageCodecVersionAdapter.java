package io.github.ithmal.itio.codec.cmpp.handler.codec;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.handler.ICmppCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @author: ken.lin
 * @since: 2023-10-06 11:55
 */
public class MessageCodecVersionAdapter<T extends CmppMessage> implements ICmppCodec<T> {

    public ICmppCodec<T> v2Codec;

    public ICmppCodec<T> v3Codec;

    public static final AttributeKey<Short> VERSION_ATTR_KEY = AttributeKey.newInstance("version");

    public MessageCodecVersionAdapter(ICmppCodec<T> v2Codec, ICmppCodec<T> v3Codec) {
        this.v2Codec = v2Codec;
        this.v3Codec = v3Codec;
    }

    public static boolean isCmppV2(ChannelHandlerContext ctx) {
        Short version = ctx.channel().attr(VERSION_ATTR_KEY).get();
        return version == null || version < CmppMessage.VERSION_30;
    }

    @Override
    public T decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf byteBuf) throws Exception {
        if (isCmppV2(ctx)) {
            return v2Codec.decode(ctx, sequenceId, byteBuf);
        } else {
            return v3Codec.decode(ctx, sequenceId, byteBuf);
        }
    }

    @Override
    public void encode(ChannelHandlerContext ctx, T msg, ByteBuf byteBuf) throws Exception {
        if(isCmppV2(ctx)) {
            v2Codec.encode(ctx, msg, byteBuf);
        }else{
            v3Codec.encode(ctx, msg, byteBuf);
        }
    }
}
