package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.base.Command;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: ken.lin
 * @since: 2023-10-04 21:41
 */
@Getter
@Setter
public class CancelRequest extends CmppMessage {

    /**
     * 消息标识
     */
    private long msgId;

    public CancelRequest(int sequenceId) {
        super(Command.CANCEL_REQUEST, sequenceId);
    }

    @Override
    public String toString() {
        return "CancelRequest{" +
                "msgId=" + msgId +
                ", sequenceId=" + sequenceId +
                '}';
    }
}
