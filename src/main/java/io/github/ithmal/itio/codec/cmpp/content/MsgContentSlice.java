package io.github.ithmal.itio.codec.cmpp.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author: ken.lin
 * @since: 2023-10-14 09:22
 */
@Getter
@RequiredArgsConstructor
public class MsgContentSlice {

    private final long msgId;

    private final short pkNumber;

    private final ShortMsgContent content;

    @Override
    public String toString() {
        return "MsgContentSlice{" +
                "msgId=" + msgId +
                ", pkNumber=" + pkNumber +
                ", content=" + content +
                '}';
    }
}
