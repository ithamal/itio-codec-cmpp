package io.github.ithmal.itio.codec.cmpp;

import io.github.ithaml.itio.handler.ServerAfterHandshakeHandler;
import io.github.ithaml.itio.server.Connection;
import io.github.ithaml.itio.server.ConnectionId;
import io.github.ithaml.itio.server.HandshakeAdapter;
import io.github.ithaml.itio.server.ItioServer;
import io.github.ithaml.itio.server.impl.ItioServerImpl;
import io.github.ithmal.itio.codec.cmpp.base.*;
import io.github.ithmal.itio.codec.cmpp.content.MsgContentSlice;
import io.github.ithmal.itio.codec.cmpp.content.MsgFormat;
import io.github.ithmal.itio.codec.cmpp.content.MsgReport;
import io.github.ithmal.itio.codec.cmpp.content.ShortMsgContent;
import io.github.ithmal.itio.codec.cmpp.handler.ActiveTestRequestHandler;
import io.github.ithmal.itio.codec.cmpp.handler.CmppMessageCodec;
import io.github.ithmal.itio.codec.cmpp.handler.LongSmsAggregateHandler;
import io.github.ithmal.itio.codec.cmpp.message.*;
import io.github.ithmal.itio.codec.cmpp.sequence.SequenceManager;
import io.github.ithmal.itio.codec.cmpp.store.MemoryLongSmsAssembler;
import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: ken.lin
 * @since: 2023-10-01 10:34
 */
public class CmppServerTests {

    @Test
    public void testListen() throws InterruptedException {
        int port = 7890;
        String sourceAddr = "301001";
        String password = "2ymsc7";
        //
        SequenceManager sequenceManager = new SequenceManager();
        ItioServer server = new ItioServerImpl();
        server.option(ChannelOption.SO_RCVBUF, 512);
//        server.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(20));
//        server.setWorkEventLoopGroup(new DefaultEventLoopGroup(1));
        server.registerCodecHandler(ch -> new CmppMessageCodec());
        server.registerCodecHandler(ch -> new LongSmsAggregateHandler(ch,
                new MemoryLongSmsAssembler<>(300),
                new MemoryLongSmsAssembler<>(300)));
        server.registerBizHandler(ch -> new ActiveTestRequestHandler());
        server.setHandshake(new HandshakeAdapter<ConnectRequest, ConnectResponse>() {

            @Override
            public ConnectionId getConnectionId(ConnectRequest request) {
                String sessionId = UUID.randomUUID().toString();
                return new ConnectionId(sessionId, request.getSourceAddr());
            }

            @Override
            public Future<ConnectResponse> handleRequest(ConnectRequest request) {
                return ImmediateEventExecutor.INSTANCE.submit(() -> {
                    AuthenticatorSource authenticatorSource = request.getAuthenticatorSource();
                    authenticatorSource.setSourceAddr(sourceAddr);
                    authenticatorSource.setPassword(password);
                    short status = (authenticatorSource.validate() ? ConnectResult.OK : ConnectResult.AUTH_ERR).getCode();
                    status = ConnectResult.OK.getCode();
                    ConnectResponse response = new ConnectResponse(request.getSequenceId());
                    response.setVersion(request.getVersion());
                    response.setStatus(status);
                    response.setAuthenticatorISMG(new AuthenticatorISMG(status, authenticatorSource, password));
                    return response;
                });
            }

            @Override
            public Object buildErrorResponse(ConnectRequest request, Throwable cause) {
                short status = ConnectResult.SYS_ERR.getCode();
                AuthenticatorSource authenticatorSource = request.getAuthenticatorSource();
                ConnectResponse response = new ConnectResponse(request.getSequenceId());
                response.setVersion(request.getVersion());
                response.setStatus(status);
                response.setAuthenticatorISMG(new AuthenticatorISMG(status, authenticatorSource, password));
                return response;
            }
        });
        // 接受消息
        server.registerBizHandler(ch -> new ServerAfterHandshakeHandler<FullSubmitRequest>() {
            @Override
            public void channelRead(Connection connection, FullSubmitRequest msg) {
                String userName = connection.getId().getUserName();
                System.out.println("接收到,来自用户[" + userName + "]的消息：" + msg);
                // 响应
                List<Long> respMsgIdList = new ArrayList<>();
                for (MsgContentSlice slice : msg.getContent().getSlices()) {
                    long respMsgId = System.nanoTime();
                    respMsgIdList.add(respMsgId);
                    SubmitResponse response = new SubmitResponse(slice.getSequenceId());
                    response.setMsgId(respMsgId);
                    connection.writeAndFlush(response);
                }
                // 报告
                if (msg.getRegisteredDelivery() == 1) {
                    for (Long respMsgId : respMsgIdList) {
                        DeliverRequest deliverRequest = new DeliverRequest(sequenceManager.nextValue());
                        deliverRequest.setMsgId(respMsgId);
                        deliverRequest.setDestId(msg.getSrcId());
                        deliverRequest.setSrcTerminalId("100000");
                        deliverRequest.setReport(new MsgReport(msg.getMsgId(), "DELIVRD", msg.getDestTerminalIds()[0]));
                        connection.writeAndFlush(deliverRequest);
                    }
                }
                // 上行
                if (msg.getRegisteredDelivery() == 1) {
                    DeliverRequest deliverRequest = new DeliverRequest(sequenceManager.nextValue());
                    deliverRequest.setMsgId(System.currentTimeMillis());
                    deliverRequest.setDestId(msg.getSrcId());
                    deliverRequest.setSrcTerminalId("100000");
                    deliverRequest.setMsgContent(ShortMsgContent.fromText("回复短信X", MsgFormat.UCS2));
                    connection.writeAndFlush(deliverRequest);
                }
            }

            @Override
            public Object buildUnAuthResponse(FullSubmitRequest request) {
                SubmitResponse response = new SubmitResponse(0);
                response.setResult(SubmitResult.COMMAND_ID_ERR.getCode());
                response.setMsgId(request.getMsgId());
                return response;
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                cause.printStackTrace();
                ctx.close();
            }
        });
        server.listen(port);
        System.out.println("已监听端口");
        TimeUnit.SECONDS.sleep(1800);
    }
}
