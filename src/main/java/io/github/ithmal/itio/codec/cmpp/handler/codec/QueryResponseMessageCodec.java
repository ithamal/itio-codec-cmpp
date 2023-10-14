package io.github.ithmal.itio.codec.cmpp.handler.codec;

import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.QueryResponse;
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
public class QueryResponseMessageCodec implements IMessageCodec<QueryResponse> {

    @Override
    public QueryResponse decode(ChannelHandlerContext ctx, int sequenceId, ByteBuf in) throws Exception {
        QueryResponse msg = new QueryResponse(sequenceId);
        msg.setTime(StringUtils.readString(in, 8, StandardCharsets.US_ASCII));
        msg.setQueryType(in.readByte());
        msg.setQueryCode(StringUtils.readString(in, 10, StandardCharsets.US_ASCII));
        msg.setMtTotalMsg(in.readInt());
        msg.setMtTotalUser(in.readInt());
        msg.setMtSuccess(in.readInt());
        msg.setMtWait(in.readInt());
        msg.setMtFail(in.readInt());
        msg.setMoSuccess(in.readInt());
        msg.setMoWait(in.readInt());
        msg.setMoFail(in.readInt());
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, QueryResponse msg, ByteBuf out) throws Exception {
        out.writeBytes(StringUtils.toBytes(msg.getTime(), 8));
        out.writeByte(msg.getQueryType());
        out.writeBytes(StringUtils.toBytes(msg.getQueryCode(), 10));
        out.writeInt(msg.getMtTotalMsg());
        out.writeInt(msg.getMtTotalUser());
        out.writeInt(msg.getMtSuccess());
        out.writeInt(msg.getMtWait());
        out.writeInt(msg.getMtFail());
        out.writeInt(msg.getMoSuccess());
        out.writeInt(msg.getMoWait());
        out.writeInt(msg.getMoFail());

    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, QueryResponse msg) {
        return 51;
    }
}
