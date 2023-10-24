package io.github.ithmal.itio.codec.cmpp;

import io.github.ithaml.itio.client.Connection;
import io.github.ithaml.itio.client.HandshakeAdapter;
import io.github.ithaml.itio.client.ItioClient;
import io.github.ithaml.itio.client.impl.ItioClientImpl;
import io.github.ithaml.itio.exception.HandshakeException;
import io.github.ithmal.itio.codec.cmpp.base.AuthenticatorISMG;
import io.github.ithmal.itio.codec.cmpp.base.AuthenticatorSource;
import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.content.MsgFormat;
import io.github.ithmal.itio.codec.cmpp.handler.ActiveTestRequestHandler;
import io.github.ithmal.itio.codec.cmpp.handler.CmppMessageCodec;
import io.github.ithmal.itio.codec.cmpp.handler.LongSmsAggregateHandler;
import io.github.ithmal.itio.codec.cmpp.message.*;
import io.github.ithmal.itio.codec.cmpp.sequence.SequenceManager;
import io.github.ithmal.itio.codec.cmpp.store.MemoryLongSmsAssembler;
import io.github.ithmal.itio.codec.cmpp.util.LongSmsUtils;
import io.github.ithmal.itio.codec.cmpp.util.TimeUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author: ken.lin
 * @since: 2023-10-01 10:34
 */
public class CmppClientTests {

    @Test
    public void testConnect() throws InterruptedException {
//        String host = "123.249.84.242";
//        int port = 7890;
        String host = "127.0.0.1";
        int port = 7890;
        String sourceAddr = "301001";
        String password = "2ymsc7";
        String sourceId = "106908887002";
        int timestamp = TimeUtils.getTimestamp();
        //
        SequenceManager sequenceManager = new SequenceManager();
        ItioClient client = new ItioClientImpl();
        client.option(ChannelOption.SO_RCVBUF, 512);
//        client.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(13));
        client.registerCodecHandler(ch -> new CmppMessageCodec());
        client.registerCodecHandler(ch -> new LongSmsAggregateHandler(ch, new MemoryLongSmsAssembler<>(300),
                new MemoryLongSmsAssembler<>(300)));
        client.registerCodecHandler(ch -> new ActiveTestRequestHandler());
        client.registerBizHandler(ch -> new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                System.out.println(msg);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                cause.printStackTrace();
            }
        });
        client.setHandShake(new HandshakeAdapter<ConnectRequest, ConnectResponse>() {

            @Override
            public ConnectRequest buildRequest() {
                AuthenticatorSource authenticatorSource = new AuthenticatorSource(sourceAddr, password, timestamp);
                ConnectRequest connectRequest = new ConnectRequest(sequenceManager.nextValue());
                connectRequest.setSourceAddr(sourceAddr);
                connectRequest.setSequenceId(1);
                connectRequest.setAuthenticatorSource(authenticatorSource);
                connectRequest.setTimestamp(timestamp);
                connectRequest.setVersion(CmppMessage.VERSION_20);
                System.out.println("已请求");
                return connectRequest;
            }

            @Override
            public void handleResponse(Connection connection, ConnectResponse response) throws HandshakeException {
                AuthenticatorSource authenticatorSource = new AuthenticatorSource(sourceAddr, password, timestamp);
                AuthenticatorISMG authenticatorISMG = response.getAuthenticatorISMG();
                authenticatorISMG.setAuthenticatorSource(authenticatorSource);
                authenticatorISMG.setPassword(password);
                System.out.println("验证结果：" + authenticatorISMG.validate());
                if (response.getStatus() == 0) {
                    System.out.println("连接成功");
                } else {
                    throw new HandshakeException();
                }
            }
        });
        client.setAddress(host, port);
        // 请求
        Connection connection = client.getConnection().syncUninterruptibly().getNow();
        String text = "【测试签名】测试信息";
//        String text = "【测试签名】移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP" +
//                "短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}.";
        int sequenceId = sequenceManager.nextValue();
        long msgId = System.currentTimeMillis();
//        List<SubmitRequest> submitRequests = new ArrayList<>();
//        for (ShortMsgContent msgContent : LongSmsUtils.fromText(text, MsgFormat.UCS2)) {
//            SubmitRequest submitRequest = new SubmitRequest(sequenceId);
//            submitRequest.setSrcId(sourceId);
//            submitRequest.setMsgSrc(sourceAddr);
//            submitRequest.setMsgId(System.currentTimeMillis());
//            submitRequest.setPkTotal((short) 1);
//            submitRequest.setPkNumber((short) 1);
//            submitRequest.setRegisteredDelivery((short) 1);
//            submitRequest.setFeeUserType((short) 2);
//            submitRequest.setDestTerminalIds(new String[]{"13924604900"});
//            submitRequest.setMsgContent(msgContent);
//            submitRequests.add(submitRequest);
//        }
        FullSubmitRequest fullSubmitRequest = new FullSubmitRequest();
        fullSubmitRequest.setSequenceId(sequenceId);
        fullSubmitRequest.setSrcId(sourceId);
        fullSubmitRequest.setMsgSrc(sourceAddr);
        fullSubmitRequest.setMsgId(System.currentTimeMillis());
        fullSubmitRequest.setRegisteredDelivery((short) 1);
        fullSubmitRequest.setFeeUserType((short) 2);
        fullSubmitRequest.setDestTerminalIds(new String[]{"13924604900"});
        fullSubmitRequest.setContent(LongSmsUtils.fromText(0, text, MsgFormat.UCS2));
        Collection<SubmitRequest> submitRequests = fullSubmitRequest.toRequests();
        Collection<SubmitResponse> submitResponses = connection.writeForResponses(Arrays.asList(fullSubmitRequest), SubmitResponse.class,
                submitRequests.size()).syncUninterruptibly().getNow();
//        List<SubmitResponse> submitResponses = client.writeWaitResponses(submitRequests, SubmitResponse.class);
        for (SubmitResponse submitResponse : submitResponses) {
            System.out.println("提交响应：" + submitResponse);
        }
        System.gc();
        TimeUnit.SECONDS.sleep(60);
        client.shutdown().syncUninterruptibly();
        System.out.println("已断开连接");
    }
}
