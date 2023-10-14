package io.github.ithmal.itio.codec.cmpp.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author: ken.lin
 * @since: 2023-10-14 09:22
 */
@Getter
@RequiredArgsConstructor
public class MsgContentPart {

    private final long msgId;

    private final short pkNumber;

    private final ShortMsgContent content;
}
