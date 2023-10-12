package io.github.ithmal.itio.codec.cmpp.message;

import io.github.ithmal.itio.codec.cmpp.base.CmppMessage;
import io.github.ithmal.itio.codec.cmpp.base.Command;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: ken.lin
 * @since: 2023-10-04 21:41
 */
@Getter
@Setter
public class QueryResponse extends CmppMessage {

    /**
     * 时间（确认到日）
     */
    private String time;

    /**
     * 查询类别，0：总数查询 1：按业务类型查询
     */
    private short queryType;

    /**
     * 查询码
     */
    private String queryCode;

    /**
     * 从SP接收的信息总数
     */
    private int mtTotalMsg;

    /**
     * 从SP接收的用户总数
     */
    private int mtTotalUser;

    /**
     * 成功转发总数
     */
    private int mtSuccess;

    /**
     * 待转发总数
     */
    private int mtWait;

    /**
     * 转发失败数量
     */
    private int mtFail;

    /**
     * 向SP成功送达数量
     */
    private int moSuccess;

    /**
     * 向SP待送达数量
     */
    private int moWait;

    /**
     * 向SP送达失败数量
     */
    private int moFail;

    public QueryResponse(int sequenceId) {
        super(Command.QUERY_RESPONSE, sequenceId);
    }

    @Override
    public String toString() {
        return "QueryResponse{" +
                "time='" + time + '\'' +
                ", queryType=" + queryType +
                ", queryCode='" + queryCode + '\'' +
                ", mtTotalMsg=" + mtTotalMsg +
                ", mtTotalUser=" + mtTotalUser +
                ", mtSuccess=" + mtSuccess +
                ", mtWait=" + mtWait +
                ", mtFail=" + mtFail +
                ", moSuccess=" + moSuccess +
                ", moWait=" + moWait +
                ", moFail=" + moFail +
                ", sequenceId=" + sequenceId +
                '}';
    }
}
