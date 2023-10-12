package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.base.AuthenticatorISMG;
import io.github.ithmal.itio.codec.cmpp.base.Command;
import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * CMPP连接响应
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:03
 */
@Getter
@Setter
public class ConnectResponse extends CmppMessage {

    /**
     * 状态
     * 0：正确
     * 1：消息结构错
     * 2：非法源地址
     * 3：认证错
     * 4：版本太高
     * 5~ ：其他错误
     */
    private int status;

    /**
     * ISMG 认证码，用于鉴别 ISMG。
     */
    private AuthenticatorISMG authenticatorISMG;

    /**
     * 服务器支持的最高版本号
     */
    private short version;

    public ConnectResponse(int sequenceId) {
        super(Command.CONNECT_RESPONSE, sequenceId);
    }

    @Override
    public String toString() {
        return "ConnectResponse{" +
                "status=" + status +
                ", authenticatorISMG=" + authenticatorISMG +
                ", version=" + version +
                ", sequenceId=" + sequenceId +
                '}';
    }
}
