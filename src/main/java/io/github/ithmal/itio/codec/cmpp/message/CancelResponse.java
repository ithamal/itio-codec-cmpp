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
public class CancelResponse extends CmppMessage {

    /**
     * 成功标识
     * 0：成功
     * 1：失败
     */
    private int success;

    public CancelResponse(int sequenceId) {
        super(Command.CANCEL_RESPONSE, sequenceId);
    }

    @Override
    public String toString() {
        return "CancelResponse{" +
                "success=" + success +
                ", sequenceId=" + sequenceId +
                '}';
    }
}
