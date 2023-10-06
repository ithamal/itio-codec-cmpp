package io.github.ithmal.itio.codec.cmpp;

import io.github.ithaml.itio.client.ItioClient;
import io.github.ithmal.itio.codec.cmpp.base.*;
import io.github.ithmal.itio.codec.cmpp.handler.CmppMessageCodec;
import io.github.ithmal.itio.codec.cmpp.handler.ActiveTestRequestHandler;
import io.github.ithmal.itio.codec.cmpp.message.ConnectRequest;
import io.github.ithmal.itio.codec.cmpp.message.ConnectResponse;
import io.github.ithmal.itio.codec.cmpp.message.SubmitRequest;
import io.github.ithmal.itio.codec.cmpp.message.SubmitResponse;
import io.github.ithmal.itio.codec.cmpp.util.LongSmsUtils;
import io.github.ithmal.itio.codec.cmpp.util.TimeUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.jupiter.api.Test;

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
        int timestamp =  TimeUtils.getTimestamp();
        //
        ItioClient client = new ItioClient();
        client.registerCodecHandler(new CmppMessageCodec());
        client.registerCodecHandler(ch -> new ActiveTestRequestHandler());
        client.registerBizHandler(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                System.out.println(msg);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                cause.printStackTrace();
            }
        });
        client.connect(host, port);
        System.out.println("已打开连接");
        // 请求
        AuthenticatorSource authenticatorSource = new AuthenticatorSource(sourceAddr, password, timestamp);
        ConnectRequest connectRequest = new ConnectRequest(1);
        connectRequest.setSourceAddr(sourceAddr);
        connectRequest.setSequenceId(1);
        connectRequest.setAuthenticatorSource(authenticatorSource);
        connectRequest.setTimestamp(timestamp);
        connectRequest.setVersion(CmppMessage.VERSION_20);
        client.writeAndFlush(connectRequest);
        System.out.println("已请求");
        ConnectResponse connectResponse = client.waitForResponse(ConnectResponse.class);
        System.out.println("已响应");
        System.out.println(connectResponse);
        AuthenticatorISMG authenticatorISMG = connectResponse.getAuthenticatorISMG();
        authenticatorISMG.setAuthenticatorSource(authenticatorSource);
        authenticatorISMG.setPassword(password);
        System.out.println("验证结果：" + authenticatorISMG.validate());
        if (connectResponse.getStatus() == 0) {
            System.out.println("连接成功");
        }
        SubmitRequest submitRequest = new SubmitRequest(2);
        submitRequest.setSrcId(sourceId);
        submitRequest.setMsgSrc(sourceAddr);
        submitRequest.setMsgId(System.currentTimeMillis());
        submitRequest.setPkTotal((short) 1);
        submitRequest.setPkNumber((short) 1);
        submitRequest.setRegisteredDelivery((short) 1);
        submitRequest.setFeeUserType((short) 2);
        submitRequest.setDestTerminalIds(new String[]{"13924604900"});
        submitRequest.setMsgContent(MsgContent.fromText("【测试签名】测试信息", MsgFormat.UCS2));
//        submitRequest.setContent(MsgContent.fromText("【测试签名】移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}", MsgFormat.UCS2));
        // 长短信分割处理
        for (SubmitRequest subSubmitRequest : LongSmsUtils.split(submitRequest)) {
            client.writeAndFlush(subSubmitRequest);
            System.out.println("提交请求");
            SubmitResponse submitResponse = client.waitForResponse(SubmitResponse.class);
            System.out.println("提交响应");
            System.out.println(submitResponse);
        }
        TimeUnit.SECONDS.sleep(30);
        client.disconnect();
        System.out.println("已断开连接");
    }
}
