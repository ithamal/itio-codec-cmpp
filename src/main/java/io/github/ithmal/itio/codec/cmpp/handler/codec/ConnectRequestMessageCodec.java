package io.github.ithmal.itio.codec.cmpp.handler.codec;

import io.github.ithmal.itio.codec.cmpp.base.AuthenticatorSource;
import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.ConnectRequest;
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
public class ConnectRequestMessageCodec implements IMessageCodec<ConnectRequest> {

    @Override
    public ConnectRequest decode(ChannelHandlerContext ctx, int sequenceId, ByteBuf byteBuf) throws Exception {
        byte[] authenticatorSourceBytes = new byte[16];
        ConnectRequest msg = new ConnectRequest(sequenceId);
        msg.setSourceAddr(StringUtils.readString(byteBuf, 6, StandardCharsets.US_ASCII));
        byteBuf.readBytes(authenticatorSourceBytes);
        msg.setVersion(byteBuf.readByte());
        msg.setTimestamp(byteBuf.readInt());
        msg.setAuthenticatorSource(new AuthenticatorSource(msg.getTimestamp(), authenticatorSourceBytes));
        ctx.channel().attr(MessageCodecVersionAdapter.VERSION_ATTR_KEY).set(msg.getVersion());
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ConnectRequest msg, ByteBuf byteBuf) throws Exception {
        ctx.channel().attr(MessageCodecVersionAdapter.VERSION_ATTR_KEY).set(msg.getVersion());
        byteBuf.writeBytes(StringUtils.toBytes(msg.getSourceAddr(), 6));
        byteBuf.writeBytes(msg.getAuthenticatorSource().getDigestBytes());
        byteBuf.writeByte(msg.getVersion());
        byteBuf.writeInt(msg.getTimestamp());
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, ConnectRequest msg) {
        return 27;
    }
}
