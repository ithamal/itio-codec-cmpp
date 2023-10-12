package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.base.Command;
import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * CMPP连接响应
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:03
 */
@Getter
@Setter
public class SubmitResponse extends CmppMessage {

    /**
     * 消息id
     */
    private long msgId;

    /**
     * 结果:
     * 0：正确
     * 1：消息结构错
     * 2：命令字错
     * 3：消息序号重复
     * 4：消息长度错
     * 5：资费代码错
     * 6：超过最大信息长
     * 7：业务代码错
     * 8：流量控制错
     * 9~ ：其他错误
     */
    private int result;

    public SubmitResponse(int sequenceId) {
        super(Command.SUBMIT_RESPONSE, sequenceId);
    }

    @Override
    public String toString() {
        return "SubmitResponse{" +
                "msgId=" + msgId +
                ", result=" + result +
                ", sequenceId=" + sequenceId +
                '}';
    }
}
