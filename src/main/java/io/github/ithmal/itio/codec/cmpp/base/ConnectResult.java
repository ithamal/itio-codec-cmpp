package io.github.ithmal.itio.codec.cmpp.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 连接结果码
 * @author: ken.lin
 * @since: 2023-10-14 16:09
 */
@Getter
@RequiredArgsConstructor
public enum ConnectResult {

    /**
     * 正确
     */
    OK((short) 0, "正确"),
    /**
     * 消息结构错
     */
    MSG_CODEC_ERR((short) 1, "消息结构错"),
    /**
     * 非法源地址
     */
    ILLEGAL_SOURCE_ADDR((short) 2, "非法源地址"),
    /**
     * 认证错
     */
    AUTH_ERR((short) 3, "认证错"),
    /**
     * 版本太高
     */
    VERSION_HIGH_ERR((short) 4, "版本太高"),
    /**
     * 连接数过多
     */
    CONNECT_TOO_MANY((short) 5, "连接数过多"),
    /**
     * 其他异常
     */
    SYS_ERR((short) 99, "系统异常")
    ;

    /**
     * 代码
     */
    private final short code;

    /**
     * 消息
     */
    private final String message;
}
