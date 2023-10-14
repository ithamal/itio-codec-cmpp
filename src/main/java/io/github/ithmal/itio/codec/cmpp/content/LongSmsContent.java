package io.github.ithmal.itio.codec.cmpp.content;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author: ken.lin
 * @since: 2023-10-14 09:23
 */
@Getter
public class LongSmsContent implements MsgContent {

    private final MsgFormat format;

    private final short pkTotal;

    private final Collection<MsgContentPart> parts;

    public LongSmsContent(MsgFormat format, short pkTotal) {
        this.format = format;
        this.pkTotal = pkTotal;
        this.parts = new ArrayList<>(pkTotal);
    }

    public void append(MsgContentPart part) {
        this.parts.add(part);
    }

    @Override
    public int getMsgLength() {
        int total = 0;
        for (MsgContentPart part : parts) {
            total = part.getContent().getMsgLength();
        }
        return total;
    }

    @Override
    public void output(ByteBuf out) {
        for (MsgContentPart part : parts) {
            part.getContent().output(out);
        }
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (MsgContentPart part : parts) {
            sb.append(part.getContent().getText());
        }
        return sb.toString();
    }

    @Override
    public boolean validate() {
        for (MsgContentPart part : parts) {
            if (!part.getContent().validate()) {
                return false;
            }
        }
        return true;
    }
}
