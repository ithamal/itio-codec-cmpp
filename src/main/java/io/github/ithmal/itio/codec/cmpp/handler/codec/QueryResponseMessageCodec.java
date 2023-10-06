package io.github.ithmal.itio.codec.cmpp.handler.codec;

import io.github.ithmal.itio.codec.cmpp.handler.ICmppCodec;
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
public class QueryResponseMessageCodec implements ICmppCodec<QueryResponse> {

    @Override
    public QueryResponse decode(ChannelHandlerContext ctx, int sequenceId, ByteBuf byteBuf) throws Exception {
        QueryResponse msg = new QueryResponse(sequenceId);
        msg.setTime(StringUtils.readString(byteBuf, 8, StandardCharsets.US_ASCII));
        msg.setQueryType(byteBuf.readByte());
        msg.setQueryCode(StringUtils.readString(byteBuf, 10, StandardCharsets.US_ASCII));
        msg.setMtTotalMsg(byteBuf.readInt());
        msg.setMtTotalUser(byteBuf.readInt());
        msg.setMtSuccess(byteBuf.readInt());
        msg.setMtWait(byteBuf.readInt());
        msg.setMtFail(byteBuf.readInt());
        msg.setMoSuccess(byteBuf.readInt());
        msg.setMoWait(byteBuf.readInt());
        msg.setMoFail(byteBuf.readInt());
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, QueryResponse msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(StringUtils.toBytes(msg.getTime(), 8));
        byteBuf.writeByte(msg.getQueryType());
        byteBuf.writeBytes(StringUtils.toBytes(msg.getQueryCode(), 10));
        byteBuf.writeInt(msg.getMtTotalMsg());
        byteBuf.writeInt(msg.getMtTotalUser());
        byteBuf.writeInt(msg.getMtSuccess());
        byteBuf.writeInt(msg.getMtWait());
        byteBuf.writeInt(msg.getMtFail());
        byteBuf.writeInt(msg.getMoSuccess());
        byteBuf.writeInt(msg.getMoWait());
        byteBuf.writeInt(msg.getMoFail());

    }
}
