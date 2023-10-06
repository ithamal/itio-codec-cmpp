package io.github.ithmal.itio.codec.cmpp.base;

/**
 * @author: ken.lin
 * @since: 2023-10-01 08:45
 */
public enum Command {


    /**
     * 连接请求
     */
    CONNECT_REQUEST(0x00000001),
    /**
     * 连接响应
     */
    CONNECT_RESPONSE(0x80000001),


    /**
     * 连接请求
     */
    TERMINATE_REQUEST(0x00000002),
    /**
     * 连接响应
     */
    TERMINATE_RESPONSE(0x80000002),
    /**
     * 提交短信请求
     */
    SUBMIT_REQUEST(0x00000004),
    /**
     * 提交短信响应
     */
    SUBMIT_RESPONSE(0x80000004),
    /**
     * 提交短信请求
     */
    DELIVER_REQUEST(0x00000005),
    /**
     * 提交短信响应
     */
    DELIVER_RESPONSE(0x80000005),
    /**
     * 提交短信请求
     */
    QUERY_REQUEST(0x00000006),
    /**
     * 提交短信响应
     */
    QUERY_RESPONSE(0x80000006),
    /**
     * 取消
     */
    CANCEL_REQUEST(0x00000007),
    /**
     *
     */
    CANCEL_RESPONSE(0x80000007),
    /**
     * 取消
     */
    ACTIVE_TEST_REQUEST(0x00000008),
    /**
     *
     */
    ACTIVE_TEST_RESPONSE(0x80000008),
    ;

    private final int id;

    Command(int id) {
        this.id = id;
    }

    public static Command of(int commandId) {
        for (Command item : Command.values()) {
            if(item.id == commandId){
                return item;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }
}
