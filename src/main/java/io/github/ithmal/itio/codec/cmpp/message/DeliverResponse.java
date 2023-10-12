package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.base.Command;
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
public class DeliverResponse extends CmppMessage {

    private long msgId;

    /**
     * 0：正确
     * 1：消息结构错
     * 2：命令字错
     * 3：消息序号重复
     * 4：消息长度错
     * 5：资费代码错
     * 6：超过最大信息长
     * 7：业务代码错
     * 8: 流量控制错
     * 9~ ：其他错误
     */
    private int result;

    public DeliverResponse(int sequenceId) {
        super(Command.DELIVER_RESPONSE, sequenceId);
    }

}
