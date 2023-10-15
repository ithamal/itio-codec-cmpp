package io.github.ithmal.itio.codec.cmpp.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 标准结果码
 * @author: ken.lin
 * @since: 2023-10-14 16:09
 */
@Getter
@RequiredArgsConstructor
public enum Result {

    /**
     * 正确
     */
    OK((short) 0, "正确"),
    /**
     * 消息结构错
     */
    MSG_CODEC_ERR((short) 1, "消息结构错"),
    /**
     * 命令字错
     */
    COMMAND_ID_ERR((short) 2, "命令字错"),
    /**
     * 消息序号重复
     */
    SEQ_ID_ERR((short) 3, "消息序号重复"),
    /**
     * 资费代码错
     */
    FEE_CODE_ERR((short) 4, "资费代码错"),
    /**
     * 超过最大信息长
     */
    EXCEED_MSG_LEN_ERR((short) 5, "超过最大信息长"),
    /**
     * 业务代码错
     */
    SERVICE_ID_ERR((short) 6, "业务代码错"),
    /**
     * 流量控制错
     */
    FLOW_CTRL_ERR((short) 7, "流量控制错"),
    /**
     * 超时异常
     */
    TIMEOUT_ERR((short) 9, "超时异常"),
    /**
     * 其他异常
     */
    SYS_ERR((short) 10, "系统异常"),
    /**
     * 超时异常
     */
    OTHER_ERR((short) 99, "其他异常"),
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
