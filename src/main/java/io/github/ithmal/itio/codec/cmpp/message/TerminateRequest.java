package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.base.Command;

/**
 * @author: ken.lin
 * @since: 2023-10-06 08:45
 */
public class TerminateRequest extends CmppMessage {

    public TerminateRequest(int sequenceId) {
        super(Command.TERMINATE_REQUEST, sequenceId);
    }

    @Override
    public String toString() {
        return "TerminateRequest{" +
                "sequenceId=" + sequenceId +
                '}';
    }
}
