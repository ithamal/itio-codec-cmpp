package io.github.ithmal.itio.codec.cmpp.handler;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.base.Command;
import io.github.ithmal.itio.codec.cmpp.handler.codec.*;
import io.github.ithmal.itio.codec.cmpp.handler.codec.v20.*;
import io.github.ithmal.itio.codec.cmpp.handler.codec.v30.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: cmpp的编解码器
 * @author: ken.lin
 * @date: 2023/2/4
 **/
@ChannelHandler.Sharable
@SuppressWarnings("unchecked")
public class CmppMessageCodec extends ChannelDuplexHandler {

    private final ConcurrentHashMap<Command, IMessageCodec<? extends CmppMessage>> codecMap = new ConcurrentHashMap<>(16);

    private static final Logger logger = LoggerFactory.getLogger(CmppMessageCodec.class);

    private final int HEAD_LENGTH = 12;

    public CmppMessageCodec() {
        codecMap.put(Command.CONNECT_REQUEST, new ConnectRequestMessageCodec());
        codecMap.put(Command.CONNECT_RESPONSE, new MessageCodecVersionAdapter<>(new ConnectResponse20MessageCodec(), new ConnectResponse30MessageCodec()));
        codecMap.put(Command.TERMINATE_REQUEST, new TerminateRequestMessageCodec());
        codecMap.put(Command.TERMINATE_RESPONSE, new TerminateResponseMessageCodec());
        codecMap.put(Command.SUBMIT_REQUEST, new MessageCodecVersionAdapter<>(new SubmitRequest20MessageCodec(), new SubmitRequest30MessageCodec()));
        codecMap.put(Command.SUBMIT_RESPONSE, new MessageCodecVersionAdapter<>(new SubmitResponse20MessageCodec(), new SubmitResponse30MessageCodec()));
        codecMap.put(Command.DELIVER_REQUEST, new MessageCodecVersionAdapter<>(new DeliverRequest20MessageCodec(), new DeliverRequest30MessageCodec()));
        codecMap.put(Command.DELIVER_RESPONSE, new MessageCodecVersionAdapter<>(new DeliverResponse20MessageCodec(), new DeliverResponse30MessageCodec()));
        codecMap.put(Command.QUERY_REQUEST, new QueryRequestMessageCodec());
        codecMap.put(Command.QUERY_RESPONSE, new QueryResponseMessageCodec());
        codecMap.put(Command.CANCEL_REQUEST, new CancelRequestMessageCodec());
        codecMap.put(Command.CANCEL_RESPONSE, new MessageCodecVersionAdapter<>(new CancelResponse20MessageCodec(), new CancelResponse30MessageCodec()));
        codecMap.put(Command.ACTIVE_TEST_REQUEST, new ActiveTestRequestMessageCodec());
        codecMap.put(Command.ACTIVE_TEST_RESPONSE, new ActiveTestResponseMessageCodec());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            super.channelRead(ctx, msg);
            return;
        }
        try {
            ByteBuf in = (ByteBuf) msg;
            List<CmppMessage> out = new ArrayList<>(3);
            decode(ctx, in, out);
            for (CmppMessage message : out) {
                super.channelRead(ctx, message);
            }
        } catch (Throwable cause) {
            exceptionCaught(ctx, cause);
        } finally {
            if (!ReferenceCountUtil.release(msg)) {
                ((ByteBuf) msg).release();
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof CmppMessage)) {
            super.write(ctx, msg, promise);
            return;
        }
        try {
            ByteBuf out = encode(ctx, (CmppMessage) msg);
            if (out != null) {
                super.write(ctx, out, promise);
            }
        } catch (Throwable cause) {
            exceptionCaught(ctx, cause);
        }
    }


    protected ByteBuf encode(ChannelHandlerContext ctx, CmppMessage msg) throws Exception {
        ByteBuf out = null;
        try {
            Command command = msg.getCommand();
            IMessageCodec<CmppMessage> codec = (IMessageCodec<CmppMessage>) codecMap.get(command);
            if (codec == null) {
                logger.error("can not find codec for command: {}", command);
                out.release();
                out = null;
                return null;
            }
            int sequenceId = msg.getSequenceId();
            int commandId = command.getId();
            int bodyLength = codec.getBodyLength(ctx, msg);
            int totalLength = bodyLength + HEAD_LENGTH;
            out = ctx.alloc().buffer(totalLength);
            out.writeInt(totalLength);
            out.writeInt(commandId);
            out.writeInt(sequenceId);
            int beforeReadIndex = out.readableBytes();
            codec.encode(ctx, msg, out);
            int afterReadIndex = out.readableBytes();
            if (afterReadIndex - beforeReadIndex != bodyLength) {
                String message = "data length except for codec: " + msg.getClass()
                        + ", except:" + bodyLength + ",actual:" + (afterReadIndex - afterReadIndex);
                throw new Exception(message);
            }
            return out;
        } catch (Throwable e) {
            if (out != null) {
                out.release();
            }
            exceptionCaught(ctx, e);
            return null;
        }
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<CmppMessage> out) throws Exception {
        for (; ; ) {
            CmppMessage msg = decode(ctx, in);
            if (msg == null) {
                break;
            } else {
                out.add(msg);
            }
        }
    }

    protected CmppMessage decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in.readableBytes() < HEAD_LENGTH) {
            return null;
        }
        int totalLength = in.readInt();
        int commandId = in.readInt();
        int sequenceId = in.readInt();
        int bodyLength = totalLength - HEAD_LENGTH;
        if (in.readableBytes() < bodyLength) {
            // 可能是ChannelOption.SO_RCVBUF、ChannelOption.RCVBUF_ALLOCATOR设置过小
            String message = String.format("readable bytes insufficiently： %s, %s, expect: %s, actual: %s",
                    ctx.channel(), commandId, bodyLength, in.readableBytes());
            throw new Exception(message);
        }
        Command command = Command.of(commandId);
        if (command == null) {
            logger.error("not supported command id： {}", commandId);
            in.readBytes(bodyLength).release();
            return null;
        }
        IMessageCodec<CmppMessage> codec = (IMessageCodec<CmppMessage>) codecMap.get(command);
        if (codec == null) {
            logger.error("can not find codec for commandId: {}", commandId);
            in.readBytes(bodyLength).release();
            return null;
        }
        int beforeReadIndex = in.readerIndex();
        CmppMessage out = codec.decode(ctx, sequenceId, in);
        int afterReadIndex = in.readerIndex();
        if (afterReadIndex - beforeReadIndex != bodyLength) {
            String message = "data length except for codec: " + out.getClass()
                    + ", except:" + bodyLength + ",actual:" + (afterReadIndex - beforeReadIndex);
            throw new Exception(message);
        }
        return out;
    }
}
