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
    public ConnectRequest decode(ChannelHandlerContext ctx, int sequenceId, ByteBuf in) throws Exception {
        byte[] authenticatorSourceBytes = new byte[16];
        ConnectRequest msg = new ConnectRequest(sequenceId);
        msg.setSourceAddr(StringUtils.readString(in, 6, StandardCharsets.US_ASCII));
        in.readBytes(authenticatorSourceBytes);
        msg.setVersion(in.readByte());
        msg.setTimestamp(in.readInt());
        msg.setAuthenticatorSource(new AuthenticatorSource(msg.getTimestamp(), authenticatorSourceBytes));
        ctx.channel().attr(MessageCodecVersionAdapter.VERSION_ATTR_KEY).set(msg.getVersion());
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ConnectRequest msg, ByteBuf out) throws Exception {
        ctx.channel().attr(MessageCodecVersionAdapter.VERSION_ATTR_KEY).set(msg.getVersion());
        out.writeBytes(StringUtils.toBytes(msg.getSourceAddr(), 6));
        out.writeBytes(msg.getAuthenticatorSource().getDigestBytes());
        out.writeByte(msg.getVersion());
        out.writeInt(msg.getTimestamp());
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, ConnectRequest msg) {
        return 27;
    }
}
