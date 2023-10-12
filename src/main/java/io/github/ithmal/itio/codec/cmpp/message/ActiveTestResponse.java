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
public class ActiveTestResponse extends CmppMessage {

    public ActiveTestResponse(int sequenceId) {
        super(Command.ACTIVE_TEST_RESPONSE, sequenceId);
    }

    @Override
    public String toString() {
        return "ActiveTestResponse{" +
                "sequenceId=" + sequenceId +
                '}';
    }
}
