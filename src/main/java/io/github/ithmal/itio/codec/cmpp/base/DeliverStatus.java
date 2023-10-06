package io.github.ithmal.itio.codec.cmpp.base;

/**
 * @author: ken.lin
 * @since: 2023-10-01 13:56
 */
public enum DeliverStatus {

    /**
     * Message is delivered todestination
     */
    DELIVRD,

    /**
     * Message validity period has expired
     */
    EXPIRED,

    /**
     * Message has been deleted.
     */
    DELETED,

    /**
     * Message is undeliverable
     */
    UNDELIV,

    /**
     * Message is in accepted state(i.e. has been manually readon behalf of the subscriber bycustomer service)
     */
    ACCEPTD,

    /**
     * Message is in invalid state
     */
    UNKNOWN,

    /**
     * Message is in a rejected state
     */
    REJECTD,


}
