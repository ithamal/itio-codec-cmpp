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

    private final Collection<MsgContentSlice> slices;

    public LongSmsContent(MsgFormat format, short pkTotal) {
        this.format = format;
        this.pkTotal = pkTotal;
        this.slices = new ArrayList<>(pkTotal);
    }

    public void append(MsgContentSlice slice) {
        this.slices.add(slice);
    }

    @Override
    public int getMsgLength() {
        int total = 0;
        for (MsgContentSlice slice : slices) {
            total = slice.getContent().getMsgLength();
        }
        return total;
    }

    @Override
    public void output(ByteBuf out) {
        for (MsgContentSlice slices : slices) {
            slices.getContent().output(out);
        }
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (MsgContentSlice slices : slices) {
            sb.append(slices.getContent().getText());
        }
        return sb.toString();
    }

    @Override
    public boolean validate() {
        for (MsgContentSlice slices : slices) {
            if (!slices.getContent().validate()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "LongSmsContent{" +
                "format=" + format +
                ", pkTotal=" + pkTotal +
                ", slices=" + slices +
                '}';
    }
}
