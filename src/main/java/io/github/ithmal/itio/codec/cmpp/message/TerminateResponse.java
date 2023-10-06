package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.base.Command;

/**
 * @author: ken.lin
 * @since: 2023-10-06 08:45
 */
public class TerminateResponse extends CmppMessage {

    public TerminateResponse(int sequenceId) {
        super(Command.TERMINATE_RESPONSE, sequenceId);
    }

    @Override
    public int getLength20() {
        return 0;
    }

    @Override
    public int getLength30() {
        return 0;
    }
}
