package io.github.ithmal.itio.codec.cmpp.base;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: ken.lin
 * @since: 2023-10-01 13:45
 */
@Getter
@Setter
public class MsgReport {

    public MsgReport(){

    }

    public MsgReport(long msgId, String stat, String destTerminalId) {
        this.msgId = msgId;
        this.stat = stat;
        this.destTerminalId = destTerminalId;
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        this.submitTime = sdf.format(new Date());
        this.doneTime = sdf.format(new Date());
    }

    /**
     * 信息标识
     * SP交短信（CMPP_SUBMIT）操作时，与SP 相连的ISMG产生的Msg_Id。
     */
    private long msgId;

    /**
     * 发送短信的应答结果，含义与SMPP协议要求中stat字段定义相同，详见表一。SP根据该字段确定CMPP_SUBMIT消息的处理状态。
     */
    private String stat;

    /**
     * YYMMDDHHMM（YY为年的后两位00-99，MM：01-12，DD：01-31，HH：00-23，MM：00-59）
     */
    private String submitTime;

    /**
     * YYMMDDHHMM
     */
    private String doneTime;

    /**
     * 目的终端MSISDN号码(SPCMPP_SUBMIT消息的目标终端)
     */
    private String destTerminalId;

    /**
     * 取自SMSC发送状态报告的消息体中的消息标识。
     */
    private int smscSequence;

    @Override
    public String toString() {
        return "MsgReport{" +
                "msgId=" + msgId +
                ", stat='" + stat + '\'' +
                ", submitTime='" + submitTime + '\'' +
                ", doneTime='" + doneTime + '\'' +
                ", destTerminalId='" + destTerminalId + '\'' +
                ", smscSequence=" + smscSequence +
                '}';
    }

    public int getMsgLength() {
        return 8 + 7 + 10 + 10 + 21 + 4;
    }
}
