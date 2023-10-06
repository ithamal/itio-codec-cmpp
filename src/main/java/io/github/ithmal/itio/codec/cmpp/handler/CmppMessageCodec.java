package io.github.ithmal.itio.codec.cmpp.handler;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.base.Command;
import io.github.ithmal.itio.codec.cmpp.handler.codec.*;
import io.github.ithmal.itio.codec.cmpp.handler.codec.v20.*;
import io.github.ithmal.itio.codec.cmpp.handler.codec.v30.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: cmpp的编解码器
 * @author: ken.lin
 * @date: 2023/2/4
 **/
@ChannelHandler.Sharable
public class CmppMessageCodec extends MessageToMessageCodec<ByteBuf, CmppMessage> {

    private final ConcurrentHashMap<Command, ICmppCodec<? extends CmppMessage>> codecMap = new ConcurrentHashMap<>(16);

    private static final Logger logger = LoggerFactory.getLogger(CmppMessageCodec.class);

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
    protected void encode(ChannelHandlerContext ctx, CmppMessage msg, List<Object> out) throws Exception {
        try {
            Command command = msg.getCommand();
            int sequenceId = msg.getSequenceId();
            int commandId = command.getId();
            //消息总长度
            int bodyLength = MessageCodecVersionAdapter.isCmppV2(ctx)? msg.getLength20(): msg.getLength30();
            int totalLength = bodyLength + (4 + 4 + 4);
            ByteBuf byteBuf = ctx.alloc().buffer(totalLength);
            byteBuf.writeInt(totalLength);
            byteBuf.writeInt(commandId);
            byteBuf.writeInt(sequenceId);
            ICmppCodec codec = codecMap.get(command);
            if (codec == null) {
                logger.error("Cann't find codec for commandId: {}", commandId);
                return;
            }
            int beforeByteCount = byteBuf.readableBytes();
            codec.encode(ctx, msg, byteBuf);
            int afterByteCount = byteBuf.readableBytes();
            if (afterByteCount - beforeByteCount != bodyLength) {
                String message = "data length except for codec: " + msg.getClass()
                                + ", except:" + bodyLength + ",actual:" + (afterByteCount - beforeByteCount);
                throw new Exception(message);
            }
            out.add(byteBuf);
        } catch (Throwable e) {
            exceptionCaught(ctx, e);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
        try {
            int totalLength = bytebuf.readInt();
            int commandId = bytebuf.readInt();
            int sequenceId = bytebuf.readInt();
            Command command = Command.of(commandId);
            if (command == null) {
                logger.error("Not supported command id： {}", commandId);
                return;
            }
            ICmppCodec codec = codecMap.get(command);
            if (codec == null) {
                logger.error("Cann't find codec for commandId: {}", commandId);
                return;
            }
            CmppMessage message = codec.decode(ctx, sequenceId, bytebuf);
            out.add(message);
        } catch (Throwable e) {
            exceptionCaught(ctx, e);
        }
    }
}
