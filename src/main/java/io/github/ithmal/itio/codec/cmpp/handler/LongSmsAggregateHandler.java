package io.github.ithmal.itio.codec.cmpp.handler;

import io.github.ithmal.itio.codec.cmpp.base.SubmitResult;
import io.github.ithmal.itio.codec.cmpp.content.UserDataHeader;
import io.github.ithmal.itio.codec.cmpp.message.*;
import io.github.ithmal.itio.codec.cmpp.store.LongSmsAssembler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author: ken.lin
 * @since: 2023-10-14 15:42
 */
public class LongSmsAggregateHandler extends ChannelDuplexHandler {

    private final LongSmsAssembler<SubmitRequest> submitSmsAssembler;

    private final LongSmsAssembler<DeliverRequest> deliverSmsAssembler;

    public LongSmsAggregateHandler(Channel channel, LongSmsAssembler<SubmitRequest> submitSmsAssembler,
                                   LongSmsAssembler<DeliverRequest> deliverSmsAssembler) {
        this.submitSmsAssembler = submitSmsAssembler;
        this.deliverSmsAssembler = deliverSmsAssembler;
        this.submitSmsAssembler.onTimeout((key, list) -> {
            for (SubmitRequest request : list) {
                SubmitResponse response = new SubmitResponse(request.getSequenceId());
                response.setMsgId(request.getMsgId());
                response.setResult(SubmitResult.TIMEOUT_ERR.getCode());
                channel.writeAndFlush(response);
            }
        });
        this.deliverSmsAssembler.onTimeout((key, list) -> {
            for (DeliverRequest request : list) {
                DeliverResponse response = new DeliverResponse(request.getSequenceId());
                response.setMsgId(request.getMsgId());
                response.setResult(SubmitResult.TIMEOUT_ERR.getCode());
                channel.writeAndFlush(response);
            }
        });
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 提交请求
        if (msg instanceof SubmitRequest) {
            FullSubmitRequest fullRequest = assembleSubmitRequest((SubmitRequest) msg);
            if (fullRequest != null) {
                super.channelRead(ctx, fullRequest);
            }
        }
        // 交付请求
        else if (msg instanceof DeliverRequest) {
            FullDeliverRequest fullRequest = assembleDeliverRequest((DeliverRequest) msg);
            if (fullRequest != null) {
                super.channelRead(ctx, fullRequest);
            }
        }
        // 其他请求
        else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // 完全提交请求
        if (msg instanceof FullSubmitRequest) {
            Collection<SubmitRequest> requests = ((FullSubmitRequest) msg).toRequests();
            for (SubmitRequest request : requests) {
                super.write(ctx, request, promise);
            }
        }
        // 完全交付请求
        else if (msg instanceof FullDeliverRequest) {
            Collection<DeliverRequest> requests = ((FullDeliverRequest) msg).toRequests();
            for (DeliverRequest request : requests) {
                super.write(ctx, request, promise);
            }
        } else {
            super.write(ctx, msg, promise);
        }
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.close(ctx, promise);
        // 释放提交请求消息组合器
        if (submitSmsAssembler != null) {
            submitSmsAssembler.release();
        }
        // 释放交付请求消息组合器
        if (deliverSmsAssembler != null) {
            deliverSmsAssembler.release();
        }
    }

    /**
     * 组装提交请求
     *
     * @param request
     * @return
     */
    private FullSubmitRequest assembleSubmitRequest(SubmitRequest request) {
        if (request.getTpUdhi() == 0) {
            return FullSubmitRequest.fromRequests(Collections.singleton(request));
        }
        UserDataHeader header = request.getMsgContent().getHeader();
        short pkTotal = header.getPkTotal();
        short pkNumber = header.getPkNumber();
        String msgKey = Arrays.toString(request.getDestTerminalIds()) + "-" + header.getMsgId();
        List<SubmitRequest> requests = submitSmsAssembler.put(msgKey, pkTotal, pkNumber, request);
        if (requests == null || requests.size() < pkTotal) {
            return null;
        } else {
            submitSmsAssembler.remove(msgKey);
            return FullSubmitRequest.fromRequests(requests);
        }
    }

    /**
     * 组装交付请求
     *
     * @param request
     * @return
     */
    private FullDeliverRequest assembleDeliverRequest(DeliverRequest request) {
        if (request.getTpUdhi() == 0) {
            return FullDeliverRequest.fromRequests(Collections.singleton(request));
        }
        UserDataHeader header = request.getMsgContent().getHeader();
        short pkTotal = header.getPkTotal();
        short pkNumber = header.getPkNumber();
        String msgKey = request.getSrcTerminalId() + "-" + header.getMsgId();
        List<DeliverRequest> requests = deliverSmsAssembler.put(msgKey, pkTotal, pkNumber, request);
        if (requests == null || requests.size() < pkTotal) {
            return null;
        } else {
            deliverSmsAssembler.remove(msgKey);
            return FullDeliverRequest.fromRequests(requests);
        }
    }
}
