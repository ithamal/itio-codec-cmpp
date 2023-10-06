package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.base.AuthenticatorSource;
import io.github.ithmal.itio.codec.cmpp.base.Command;
import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * CMPP连接
 *
 * @author: ken.lin
 * @since: 2023-10-01 08:03
 */
@Getter
@Setter
public class ConnectRequest extends CmppMessage {

    /**
     * 源地址
     */
    private String sourceAddr;

    /**
     * 鉴别信息
     */
    private AuthenticatorSource authenticatorSource;

    /**
     * 版本
     */
    private short version;

    /**
     * 时间戳的明文,由客户端产生,格式为MMDDHHMMSS，即月日时分秒，10位数字的整型，右对齐
     */
    private int timestamp;

    public ConnectRequest(int sequenceId) {
        super(Command.CONNECT_REQUEST, sequenceId);
    }


    @Override
    public int getLength20() {
        return 27;
    }

    @Override
    public int getLength30() {
        return 27;
    }

    @Override
    public String toString() {
        return "CmppConnectRequest{" +
                "sourceAddr='" + sourceAddr + '\'' +
                ", authenticatorSource=" + authenticatorSource +
                ", version=" + version +
                ", timestamp=" + timestamp +
                '}';
    }
}
