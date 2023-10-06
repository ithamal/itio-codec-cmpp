package io.github.ithmal.itio.codec.cmpp.handler.codec.v20;

import io.github.ithmal.itio.codec.cmpp.handler.ICmppCodec;
import io.github.ithmal.itio.codec.cmpp.message.CancelResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 编码格式：源地址（6） +鉴别信息（16） + 版本（1） + 时间戳（4）
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:49
 */
public class CancelResponse20MessageCodec implements ICmppCodec<CancelResponse> {

    @Override
    public CancelResponse decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf byteBuf) throws Exception {
        CancelResponse msg = new CancelResponse(sequenceId);
        msg.setSuccess(byteBuf.readByte());
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, CancelResponse msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeByte(msg.getSuccess());
    }
}
