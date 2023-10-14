package io.github.ithmal.itio.codec.cmpp.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author: ken.lin
 * @since: 2023-10-01 12:45
 */
@Getter
@RequiredArgsConstructor
public class MsgFormat {

    /**
     * ASCII串
     */
    public final static MsgFormat ASCII = new MsgFormat(0, StandardCharsets.US_ASCII);

    /**
     * UCS2编码
     */
    public final static MsgFormat UCS2 = new MsgFormat(8, StandardCharsets.UTF_16BE);

    /**
     * 含GB汉字
     */
    public final static MsgFormat GBK = new MsgFormat(15, Charset.forName("GBK"));


    private final int id;

    private final Charset charset;

    public static MsgFormat of(int id) {
        switch (id) {
            case 0:
                return ASCII;
            case 8:
                return UCS2;
            case 15:
                return GBK;
            default:
                return new MsgFormat(id, StandardCharsets.US_ASCII);
        }
    }

    @Override
    public String toString() {
        return "MsgFormat{" +
                "id=" + id +
                ", charset=" + charset +
                '}';
    }
}
