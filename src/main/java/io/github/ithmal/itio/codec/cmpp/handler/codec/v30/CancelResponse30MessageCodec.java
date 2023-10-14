package io.github.ithmal.itio.codec.cmpp.handler.codec.v30;

import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.CancelResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 编码格式：源地址（6） +鉴别信息（16） + 版本（1） + 时间戳（4）
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:49
 */
public class CancelResponse30MessageCodec implements IMessageCodec<CancelResponse> {

    @Override
    public CancelResponse decode(ChannelHandlerContext ctx, int sequenceId, ByteBuf in) throws Exception {
        CancelResponse msg = new CancelResponse(sequenceId);
        msg.setSuccess(in.readInt());
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, CancelResponse msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getSuccess());
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, CancelResponse msg) {
        return 4;
    }
}
