package io.github.ithmal.itio.codec.cmpp.handler.codec.v20;

import io.github.ithmal.itio.codec.cmpp.base.AuthenticatorISMG;
import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.ConnectResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 编码格式：状态（1） +鉴别信息（16） + 版本（1）
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:49
 */
public class ConnectResponse20MessageCodec implements IMessageCodec<ConnectResponse> {

    @Override
    public ConnectResponse decode(ChannelHandlerContext ctx, int sequenceId, ByteBuf byteBuf) throws Exception {
        byte[] authenticatorISMGBytes = new byte[16];
        ConnectResponse msg = new ConnectResponse(sequenceId);
        msg.setStatus(byteBuf.readByte());
        byteBuf.readBytes(authenticatorISMGBytes);
        msg.setVersion(byteBuf.readByte());
        msg.setAuthenticatorISMG(new AuthenticatorISMG(msg.getStatus(), authenticatorISMGBytes));
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ConnectResponse msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeByte(msg.getStatus());
        byteBuf.writeBytes(msg.getAuthenticatorISMG().getDigestBytes());
        byteBuf.writeByte(msg.getVersion());
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, ConnectResponse msg) {
        return 18;
    }
}
