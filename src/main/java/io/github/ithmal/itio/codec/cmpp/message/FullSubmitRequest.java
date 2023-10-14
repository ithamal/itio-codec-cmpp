package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.content.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author: ken.lin
 * @since: 2023-10-14 09:08
 */
@Data
public class FullSubmitRequest {

    /**
     * 序号
     */
    private int sequenceId;

    /**
     * 信息标识，由SP侧短信网关本身产生
     */
    private long msgId;

    /**
     * 是否要求返回状态确认报告：
     * 0：不需要
     * 1：需要
     * 2：产生SMC话单
     */
    private short registeredDelivery = 0;

    /**
     * 信息级别
     */
    private short msgLevel = 0;

    /**
     * 业务类型，是数字、字母和符号的组合。10位
     */
    private String serviceId;

    /**
     * 计费用户类型字段
     * 0：对目的终端MSISDN计费；
     * 1：对源终端MSISDN计费；
     * 2：对SP计费;
     */
    private short feeUserType = 2;

    /**
     * 被计费用户的号码,与Fee_UserType字段互斥
     */
    private String feeTerminalId;

    /**
     * GSM协议类型。详细是解释请参考GSM03.40中的9.2.3.9
     */
    private short tpPid;

    /**
     * GSM协议类型。详细是解释请参考
     * GSM03.40中的9.2.3.23,仅使用1位，右对齐
     */
    private short tpUdhi;


    /**
     * 信息内容来源(SP_Id)
     */
    private String msgSrc;

    /**
     * 资费类别
     * 01：对“计费用户号码”免费
     * 02：对“计费用户号码”按条计信息费
     * 03：对“计费用户号码”按包月收取信息费
     * 04：对“计费用户号码”的信息费封顶
     * 05：对“计费用户号码”的收费是由SP 实现
     */
    private String feeType;

    /**
     * 资费代码（以分为单位）
     */
    private String feeCode;


    /**
     * 存活时间
     */
    private String validTime;

    /**
     * 定时发送时间，格式遵循SMPP3.3协议。
     */
    private String atTime;

    /**
     * 源号码 SP的服务代码或前缀为服务代码的长号码, 网关将该号码完整的填到SMPP协议Submit_SM消息相应的source_addr字段，该号码最终在用户手机上显示为短消息的主叫号码
     */
    private String srcId;

    /**
     * 接收短信的MSISDN号码
     */
    private String[] destTerminalIds;

    /**
     * 长短信内容
     */
    private LongSmsContent content;

    /**
     * 分拆请求
     *
     * @return
     */
    public Collection<SubmitRequest> toRequests() {
        List<SubmitRequest> requests = new ArrayList<>(content.getPkTotal());
        for (MsgContentPart part : content.getParts()) {
            ShortMsgContent msgContent = part.getContent();
            UserDataHeader header = msgContent.getHeader();
            SubmitRequest request = new SubmitRequest(this.sequenceId);
            request.setMsgId(part.getMsgId() == 0 ? this.msgId : part.getMsgId());
            request.setTpUdhi(requests.size() > 1 ? (short) 1 : 0);
            request.setTpPid(this.tpPid);
            request.setTpUdhi(header != null ? 1: this.tpUdhi);
            request.setPkTotal(header == null ? 1 : header.getPkTotal());
            request.setPkNumber(header == null ? 1 : header.getPkNumber());
            request.setMsgContent(msgContent);
            request.setRegisteredDelivery(this.registeredDelivery);
            request.setMsgLevel(this.msgLevel);
            request.setServiceId(this.serviceId);
            request.setFeeUserType(this.feeUserType);
            request.setFeeTerminalId(this.feeTerminalId);
            request.setFeeType(this.feeType);
            request.setFeeCode(this.feeCode);
            request.setValidTime(this.validTime);
            request.setAtTime(this.atTime);
            request.setSrcId(this.srcId);
            request.setDestTerminalIds(this.destTerminalIds);
            requests.add(request);
        }
        return requests;
    }

    /**
     * 合并请求
     *
     * @param requests
     * @return
     */
    public static FullSubmitRequest fromRequests(Collection<SubmitRequest> requests) {
        FullSubmitRequest fullRequest = new FullSubmitRequest();
        SubmitRequest request = requests.iterator().next();
        fullRequest.sequenceId = request.getSequenceId();
        fullRequest.msgId = request.getMsgId();
        fullRequest.tpPid = request.getTpPid();
        fullRequest.tpUdhi = request.getTpUdhi();
        fullRequest.registeredDelivery = request.getRegisteredDelivery();
        fullRequest.msgLevel = request.getMsgLevel();
        fullRequest.serviceId = request.getServiceId();
        fullRequest.feeUserType = request.getFeeUserType();
        fullRequest.feeTerminalId = request.getFeeTerminalId();
        fullRequest.feeType = request.getFeeType();
        fullRequest.feeCode = request.getFeeCode();
        fullRequest.validTime = request.getValidTime();
        fullRequest.atTime = request.getAtTime();
        fullRequest.srcId = request.getSrcId();
        fullRequest.destTerminalIds = request.getDestTerminalIds();
        fullRequest.setContent(createContent(requests));
        return fullRequest;
    }

    /**
     * 收集内容
     *
     * @param requests
     * @return
     */
    private static LongSmsContent createContent(Collection<SubmitRequest> requests) {
        SubmitRequest firstRequest = requests.iterator().next();
        short pkTotal = firstRequest.getPkTotal();
        MsgFormat msgFormat = firstRequest.getMsgContent().getFormat();
        LongSmsContent longSmsContent = new LongSmsContent(msgFormat, pkTotal);
        for (SubmitRequest request : requests) {
            long msgId = request.getMsgId();
            ShortMsgContent content = request.getMsgContent();
            if (content.getFormat() != msgFormat) {
                throw new IllegalArgumentException("content format is inconsistent: " + msgFormat + "," + content.getFormat());
            }
            UserDataHeader header = content.getHeader();
            short pkNumber = header.getPkNumber();
            longSmsContent.append(new MsgContentPart(msgId, pkNumber, content));
        }
        return longSmsContent;
    }

}
