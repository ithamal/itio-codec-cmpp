package io.github.ithmal.itio.codec.cmpp;

import io.github.ithaml.itio.server.ItioServer;
import io.github.ithmal.itio.codec.cmpp.base.*;
import io.github.ithmal.itio.codec.cmpp.content.ShortMsgContent;
import io.github.ithmal.itio.codec.cmpp.handler.ActiveTestRequestHandler;
import io.github.ithmal.itio.codec.cmpp.handler.CmppMessageCodec;
import io.github.ithmal.itio.codec.cmpp.message.*;
import io.github.ithmal.itio.codec.cmpp.sequence.SequenceManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author: ken.lin
 * @since: 2023-10-01 10:34
 */
public class CmppServerTests {

    @Test
    public void testListen() throws InterruptedException {
        int port =  7890;
        String sourceAddr = "301001";
        String password = "2ymsc7";
        //
        SequenceManager sequenceManager = new SequenceManager();
        ItioServer server = new ItioServer();
        server.setIoThreads(3);
        server.registerCodecHandler(ch -> new CmppMessageCodec());
        server.registerBizHandler(ch -> new ActiveTestRequestHandler());
        // 连接请求
        server.registerBizHandler(ch -> new SimpleChannelInboundHandler<ConnectRequest>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, ConnectRequest msg) throws Exception {
                AuthenticatorSource authenticatorSource = msg.getAuthenticatorSource();
                authenticatorSource.setSourceAddr(sourceAddr);
                authenticatorSource.setPassword(password);
                short status = (short) (authenticatorSource.validate() ? 0 : 3);
                ConnectResponse response = new ConnectResponse(msg.getSequenceId());
                response.setVersion(msg.getVersion());
                response.setStatus(status);
                response.setAuthenticatorISMG(new AuthenticatorISMG(status, authenticatorSource, password));
                ctx.writeAndFlush(response);
            }
        });
        // 接受消息
        server.registerBizHandler(ch -> new SimpleChannelInboundHandler<SubmitRequest>() {

            @Override
            protected void channelRead0(ChannelHandlerContext ctx, SubmitRequest msg) throws Exception {
                System.out.println("接收到消息:" + msg);
                SubmitResponse response = new SubmitResponse(msg.getSequenceId());
                response.setMsgId(msg.getMsgId());
                ctx.writeAndFlush(response);
                // 报告
                if (msg.getRegisteredDelivery() == 1) {
                    DeliverRequest deliverRequest = new DeliverRequest(sequenceManager.nextValue());
                    deliverRequest.setMsgId(System.currentTimeMillis());
                    deliverRequest.setDestId(msg.getSrcId());
                    deliverRequest.setSrcTerminalId("100000");
                    deliverRequest.setReport(new MsgReport(msg.getMsgId(), DeliverStatus.UNDELIV.toString(), msg.getDestTerminalIds()[0]));
                    ctx.writeAndFlush(deliverRequest);
                }
                // 上行
                if (msg.getRegisteredDelivery() == 1) {
                    DeliverRequest deliverRequest = new DeliverRequest(sequenceManager.nextValue());
                    deliverRequest.setMsgId(System.currentTimeMillis());
                    deliverRequest.setDestId(msg.getSrcId());
                    deliverRequest.setSrcTerminalId("100000");
                    deliverRequest.setMsgContent(ShortMsgContent.fromText("回复短信X", MsgFormat.UCS2));
                    ctx.writeAndFlush(deliverRequest);
                }
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
