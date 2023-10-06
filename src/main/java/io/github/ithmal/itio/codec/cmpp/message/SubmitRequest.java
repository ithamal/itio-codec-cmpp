package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.base.Command;
import io.github.ithmal.itio.codec.cmpp.base.MsgContent;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * CMPP连接
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:03
 */
@Getter
@Setter
public class SubmitRequest extends CmppMessage {

    /**
     * 信息标识，由SP侧短信网关本身产生
     */
    private long msgId;

    /**
     * 相同Msg_Id的信息总条数，从1开始
     */
    private short pkTotal =1;

    /**
     * 相同Msg_Id的信息序号，从1开始
     */
    private short pkNumber = 1;

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
     * 信息长度(Msg_Fmt值为0时：<160个字节；其它<=140个字节)
     */
    private MsgContent msgContent;

    public SubmitRequest(int sequenceId) {
        super(Command.SUBMIT_REQUEST, sequenceId);
    }


    public SubmitRequest copy(){
        SubmitRequest newObj = new SubmitRequest(this.getSequenceId());
        newObj.msgId = this.msgId;
        newObj.pkTotal = this.pkTotal;
        newObj.pkNumber = this.pkNumber;
        newObj.registeredDelivery = this.registeredDelivery;
        newObj.msgLevel = this.msgLevel;
        newObj.serviceId = this.serviceId;
        newObj.feeUserType = this.feeUserType;
        newObj.feeTerminalId = this.feeTerminalId;
        newObj.tpPid = this.tpPid;
        newObj.tpUdhi = this.tpUdhi;
        newObj.feeType = this.feeType;
        newObj.feeCode = this.feeCode;
        newObj.validTime = this.validTime;
        newObj.atTime = this.atTime;
        newObj.srcId = this.srcId;
        newObj.destTerminalIds = this.destTerminalIds;
        newObj.msgContent = this.msgContent;
        return newObj;
    }


    @Override
    public int getLength20() {
        return 126 + 21 * destTerminalIds.length + msgContent.getMsgLength();
    }

    @Override
    public int getLength30() {
        return 151 + 32 * destTerminalIds.length + msgContent.getMsgLength();
    }

    @Override
    public String toString() {
        return "CmppSubmitRequest{" +
                "msgId=" + msgId +
                ", pkTotal=" + pkTotal +
                ", pkNumber=" + pkNumber +
                ", registeredDelivery=" + registeredDelivery +
                ", destTerminalIds=" + Arrays.toString(destTerminalIds) +
                ", content=" + msgContent +
                '}';
    }
}
