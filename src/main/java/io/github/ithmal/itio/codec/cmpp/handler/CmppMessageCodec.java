package io.github.ithmal.itio.codec.cmpp.handler;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.base.Command;
import io.github.ithmal.itio.codec.cmpp.handler.codec.*;
import io.github.ithmal.itio.codec.cmpp.handler.codec.v20.*;
import io.github.ithmal.itio.codec.cmpp.handler.codec.v30.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CmppMessageCodec extends ByteToMessageCodec<CmppMessage> {

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
    @SuppressWarnings("unchecked")
    protected void encode(ChannelHandlerContext ctx, CmppMessage msg, ByteBuf out) throws Exception {
        Command command = msg.getCommand();
        IMessageCodec<CmppMessage> codec = (IMessageCodec<CmppMessage>) codecMap.get(command);
        if (codec == null) {
            throw new Exception("can not find codec of command: " + command);
        }
        out.markWriterIndex();
        int sequenceId = msg.getSequenceId();
        int commandId = command.getId();
        int bodyLength = codec.getBodyLength(ctx, msg);
        int totalLength = bodyLength + HEAD_LENGTH;
        out.writeInt(totalLength);
        out.writeInt(commandId);
        out.writeInt(sequenceId);
        int beforeReadIndex = out.readableBytes();
        codec.encode(ctx, msg, out);
        int afterReadIndex = out.readableBytes();
        if (afterReadIndex - beforeReadIndex != bodyLength) {
            out.resetWriterIndex();
            String message = "excepted data length of codec: " + msg.getClass()
                    + ", except:" + bodyLength + ",actual:" + (afterReadIndex - afterReadIndex);
            throw new Exception(message);
        }
    }



    @Override
    @SuppressWarnings("unchecked")
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < HEAD_LENGTH) {
            return;
        }
        in.markReaderIndex();
        int totalLength = in.readInt();
        int commandId = in.readInt();
        int sequenceId = in.readInt();
        int bodyLength = totalLength - HEAD_LENGTH;
        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex();
            return;
        }
        Command command = Command.of(commandId);
        if (command == null) {
            logger.error("not supported command id： {}", commandId);
            in.readBytes(bodyLength).release();
            return;
        }
        IMessageCodec<CmppMessage> codec = (IMessageCodec<CmppMessage>) codecMap.get(command);
        if (codec == null) {
            logger.error("can not find codec for commandId: {}", commandId);
            in.readBytes(bodyLength).release();
            return;
        }
        int beforeReadIndex = in.readerIndex();
        CmppMessage message = codec.decode(ctx, sequenceId, in);
        int afterReadIndex = in.readerIndex();
        if (afterReadIndex - beforeReadIndex != bodyLength) {
            throw new Exception("data length except for codec: " + out.getClass()
                    + ", except:" + bodyLength + ",actual:" + (afterReadIndex - beforeReadIndex));
        }
        out.add(message);
    }

}




