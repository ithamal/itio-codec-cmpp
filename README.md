## itio-codec-cmpp
适配Netty框架的CMPP协议


### 客户端
```java
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
connectRequest.setVersion(CmppMessage.VERSION_30);
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
//        submitRequest.setMsgContent(MsgContent.fromText("【测试签名】移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}移动CMPP短信测试{time}", MsgFormat.UCS2));
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
```
### 服务器端
```java
int port =  7890;
String sourceAddr = "301001";
String password = "2ymsc7";
//
ItioServer server = new ItioServer();
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
        response.setSequenceId(1);
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
        ctx.writeAndFlush(response);
        // 报告
        if (msg.getRegisteredDelivery() == 1) {
            DeliverRequest deliverRequest = new DeliverRequest(1);
            deliverRequest.setMsgId(System.currentTimeMillis());
            deliverRequest.setDestId(msg.getSrcId());
            deliverRequest.setSrcTerminalId("100000");
            deliverRequest.setReport(new MsgReport(msg.getMsgId(), DeliverStatus.UNDELIV.toString(), msg.getDestTerminalIds()[0]));
            ctx.writeAndFlush(deliverRequest);
        }
        // 上行
        if (msg.getRegisteredDelivery() == 1) {
            DeliverRequest deliverRequest = new DeliverRequest(2);
            deliverRequest.setMsgId(System.currentTimeMillis());
            deliverRequest.setDestId(msg.getSrcId());
            deliverRequest.setSrcTerminalId("100000");
            deliverRequest.setMsgContent(MsgContent.fromText("回复短信X", MsgFormat.UCS2));
            ctx.writeAndFlush(deliverRequest);
        }
    }

});
server.listen(port);
System.out.println("已监听端口");
TimeUnit.SECONDS.sleep(1800);
```
