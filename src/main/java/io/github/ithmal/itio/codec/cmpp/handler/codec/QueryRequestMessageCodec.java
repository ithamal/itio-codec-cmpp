package io.github.ithmal.itio.codec.cmpp.handler.codec;

import io.github.ithmal.itio.codec.cmpp.handler.IMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.QueryRequest;
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
public class QueryRequestMessageCodec implements IMessageCodec<QueryRequest> {

    @Override
    public QueryRequest decode(ChannelHandlerContext ctx, int sequenceId,  ByteBuf byteBuf) throws Exception {
        QueryRequest msg = new QueryRequest(sequenceId);
        msg.setTime(StringUtils.readString(byteBuf, 8, StandardCharsets.US_ASCII));
        msg.setQueryType(byteBuf.readByte());
        msg.setTime(StringUtils.readString(byteBuf, 8, StandardCharsets.US_ASCII));
        byte[] reserve = new byte[8];
        byteBuf.readBytes(reserve);
        return msg;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, QueryRequest msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(StringUtils.toBytes(msg.getTime(), 8));
        byteBuf.writeByte(msg.getQueryType());
        byteBuf.writeBytes(StringUtils.toBytes(msg.getQueryCode(), 10));
        byte[] reserve = new byte[8];
        byteBuf.writeBytes(reserve);
    }

    @Override
    public int getBodyLength(ChannelHandlerContext ctx, QueryRequest msg) {
        return 27;
    }
}
