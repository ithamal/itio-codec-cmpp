package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.base.Command;
import io.github.ithmal.itio.codec.cmpp.base.MsgContent;
import io.github.ithmal.itio.codec.cmpp.base.MsgReport;
import lombok.Getter;
import lombok.Setter;

/**
 * CMPP连接
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:03
 */
@Getter
@Setter
public class DeliverRequest extends CmppMessage {

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
     * 内容
     * 状态报告时为空
     */
    private MsgContent msgContent;

    /**
     * 报告
     * 非报告时为空
     */
    private MsgReport report;

    public DeliverRequest(int sequenceId) {
        super(Command.DELIVER_REQUEST, sequenceId);
    }

    public DeliverRequest copy() {
        DeliverRequest newObj = new DeliverRequest(getSequenceId());
        newObj.msgId = this.msgId;
        newObj.destId = this.destId;
        newObj.serviceId = this.serviceId;
        newObj.tpPid = this.tpPid;
        newObj.tpUdhi = this.tpUdhi;
        newObj.srcTerminalId = this.srcTerminalId;
        newObj.msgContent = this.msgContent;
        return newObj;
    }

    @Override
    public String toString() {
        return "DeliverRequest{" +
                "msgId=" + msgId +
                ", destId='" + destId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", tpPid=" + tpPid +
                ", tpUdhi=" + tpUdhi +
                ", srcTerminalId='" + srcTerminalId + '\'' +
                ", msgContent=" + msgContent +
                ", report=" + report +
                ", sequenceId=" + sequenceId +
                '}';
    }
}
