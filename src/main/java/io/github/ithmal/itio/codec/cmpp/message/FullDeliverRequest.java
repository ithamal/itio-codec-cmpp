package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.content.*;
import io.github.ithmal.itio.codec.cmpp.sequence.SequenceManager;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author: ken.lin
 * @since: 2023-10-14 09:08
 */
@Data
public class FullDeliverRequest {

    /**
     * 消息id
     */
    private long msgId;

    /**
     * 目的号码
     * SP的服务代码，一般4--6位，或者是前缀为服务代码的长号码；该号码是手机用户短消息的被叫号码。
     */
    private String destId;

    /**
     * 业务类型，是数字、字母和符号的组合。
     */
    private String serviceId;

    /**
     * GSM协议类型。详细解释请参考GSM03.40中的9.2.3.9
     */
    private short tpPid;

    /**
     * GSM协议类型。详细解释请参考GSM03.40中的9.2.3.23，仅使用1 位，右对齐
     */
    private short tpUdhi;

    /**
     * 源终端MSISDN号码（状态报告时填为CMPP_SUBMIT消息的目的终端号码）
     */
    private String srcTerminalId;

    /**
     * 报告
     */
    private MsgReport report;

    /**
     * 长短信内容
     */
    private LongSmsContent content;

    @Override
    public String toString() {
        return "FullDeliverRequest{" +
                ", msgId=" + msgId +
                ", destId='" + destId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", tpPid=" + tpPid +
                ", tpUdhi=" + tpUdhi +
                ", srcTerminalId='" + srcTerminalId + '\'' +
                ", report=" + report +
                ", content=" + content +
                '}';
    }

    /**
     * 分拆请求
     *
     * @return
     */
    public Collection<DeliverRequest> toRequests() {
        if(content.getSlices().isEmpty()){
            throw new RuntimeException("The FullDeliverRequest slices is empty");
        }
        List<DeliverRequest> requests = new ArrayList<>(content.getPkTotal());
        for (MsgContentSlice slice : content.getSlices()) {
            if(slice.getSequenceId() == 0){
                throw new IllegalArgumentException();
            }
            if(report != null){
                DeliverRequest request = new DeliverRequest(slice.getSequenceId());
                request.setMsgId(request.getMsgId());
                request.setTpUdhi((short) 0);
                request.setTpPid((short) 0);
                request.setServiceId(this.serviceId);
                request.setSrcTerminalId(this.srcTerminalId);
                request.setDestId(this.destId);
                request.setReport(this.report);
                requests.add(request);
            }else{
                UserDataHeader header = slice.getContent().getHeader();
                DeliverRequest request = new DeliverRequest(slice.getSequenceId());
                request.setMsgId(slice.getMsgId() == 0 ? this.msgId : slice.getMsgId());
                request.setTpPid(this.tpPid);
                request.setTpUdhi(header != null ? 1 : this.tpUdhi);
                request.setMsgContent(slice.getContent());
                request.setServiceId(this.serviceId);
                request.setSrcTerminalId(this.srcTerminalId);
                request.setDestId(this.destId);
                request.setReport(this.report);
                requests.add(request);
            }
        }
        return requests;
    }

    /**
     * 合并请求
     *
     * @param requests
     * @return
     */
    public static FullDeliverRequest fromRequests(Collection<DeliverRequest> requests) {
        FullDeliverRequest fullRequest = new FullDeliverRequest();
        DeliverRequest request = requests.iterator().next();
        fullRequest.msgId = request.getMsgId();
        fullRequest.tpPid = request.getTpPid();
        fullRequest.tpUdhi = request.getTpUdhi();
        fullRequest.serviceId = request.getServiceId();
        fullRequest.destId = request.getDestId();
        fullRequest.srcTerminalId = request.getSrcTerminalId();
        fullRequest.report = request.getReport();
        if (request.getMsgContent() != null) {
            fullRequest.setContent(createContent(requests));
        }
        return fullRequest;
    }

    /**
     * 收集内容
     *
     * @param requests
     * @return
     */
    private static LongSmsContent createContent(Collection<DeliverRequest> requests) {
        DeliverRequest firstRequest = requests.iterator().next();
        MsgFormat msgFormat = firstRequest.getMsgContent().getFormat();
        UserDataHeader header = firstRequest.getMsgContent().getHeader();
        short pkTotal = header == null ? 1 : header.getPkTotal();
        if (pkTotal != requests.size()) {
            throw new IllegalArgumentException("pkTotal and requests size is inconsistent");
        }
        LongSmsContent longSmsContent = new LongSmsContent(msgFormat, pkTotal);
        for (DeliverRequest request : requests) {
            long msgId = request.getMsgId();
            ShortMsgContent content = request.getMsgContent();
            if (content.getFormat() != msgFormat) {
                throw new IllegalArgumentException("content format is inconsistent: " + msgFormat + "," + content.getFormat());
            }
            short pkNumber = pkTotal == 1 ? 1 : content.getHeader().getPkNumber();
            longSmsContent.append(new MsgContentSlice(request.getSequenceId(), msgId, pkNumber, content));
        }
        return longSmsContent;
    }

}
