package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.base.Command;
import io.github.ithmal.itio.codec.cmpp.base.SubmitResult;
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
     * 结果码
     * @see SubmitResult
     */
    private int result;

    public DeliverResponse(int sequenceId) {
        super(Command.DELIVER_RESPONSE, sequenceId);
    }

    @Override
    public String toString() {
        return "DeliverResponse{" +
                "msgId=" + msgId +
                ", result=" + result +
                ", sequenceId=" + sequenceId +
                '}';
    }
}
