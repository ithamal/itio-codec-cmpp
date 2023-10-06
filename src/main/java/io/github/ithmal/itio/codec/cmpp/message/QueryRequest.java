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
public class QueryRequest extends CmppMessage {

    /**
     * 时间YYYYMMDD（精确至日）
     */
    private String time;

    /**
     * 查询类别，0：总数查询 1：按业务类型查询
     */
    private short queryType;

    /**
     * 查询码
     * 当Query_Type为0时，此项无效；当Query_Type为1时，此项填写业务类型Service_Id.
     */
    private String queryCode;

    public QueryRequest(int sequenceId) {
        super(Command.QUERY_REQUEST, sequenceId);
    }

    @Override
    public int getLength20() {
        return 27;
    }

    @Override
    public int getLength30() {
        return 27;
    }
}
