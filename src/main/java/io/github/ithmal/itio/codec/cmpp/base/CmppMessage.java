package io.github.ithmal.itio.codec.cmpp.base;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: ken.lin
 * @since: 2023-10-01 09:57
 */
@Getter
public abstract class CmppMessage {

    @Setter
    private int sequenceId;

    private final Command command;

    public static final short VERSION_30 = 0x30;

    public static final short VERSION_20 = 0x20;

    public CmppMessage(Command command, int sequenceId) {
        this.command = command;
        this.sequenceId = sequenceId;
    }

    public abstract int getLength20();

    public abstract int getLength30();
}
