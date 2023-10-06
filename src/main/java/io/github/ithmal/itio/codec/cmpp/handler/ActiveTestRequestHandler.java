package io.github.ithmal.itio.codec.cmpp.handler;

import io.github.ithmal.itio.codec.cmpp.message.ActiveTestRequest;
import io.github.ithmal.itio.codec.cmpp.message.ActiveTestResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: ken.lin
 * @since: 2023-10-06 13:51
 */
public class ActiveTestRequestHandler extends SimpleChannelInboundHandler<ActiveTestRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ActiveTestRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ActiveTestRequest msg) throws Exception {
         logger.info("active test message received.");
        ctx.writeAndFlush(new ActiveTestResponse(msg.getSequenceId()));
    }
}
