package io.github.ithmal.itio.codec.cmpp.handler.codec.v30;

import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.SubmitResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 编码格式：源地址（6） +鉴别信息（16） + 版本（1） + 时间戳（4）
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:49
 */
public class SubmitResponse30MessageCodec implements IMessageCodec<SubmitResponse> {

    @Override
    public SubmitResponse decode(ChannelHandlerContext ctx, int sequenceId, ByteBuf in) throws Exception {
        SubmitResponse msg = new SubmitResponse(sequenceId);
        msg.setMsgId(in.readLong());
        msg.setResult(in.readInt());
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, SubmitResponse msg, ByteBuf out) throws Exception {
        out.writeLong(msg.getMsgId());
        out.writeInt(msg.getResult());
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, SubmitResponse msg) {
        return 12;
    }
}
