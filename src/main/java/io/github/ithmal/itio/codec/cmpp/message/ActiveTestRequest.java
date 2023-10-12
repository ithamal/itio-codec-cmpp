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
public class ActiveTestRequest extends CmppMessage {

    public ActiveTestRequest(int sequenceId) {
        super(Command.ACTIVE_TEST_REQUEST, sequenceId);
    }
}
