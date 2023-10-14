package io.github.ithmal.itio.codec.cmpp.base;

import io.netty.buffer.ByteBuf;

/**
 * @author: ken.lin
 * @since: 2023-10-01 13:01
 */
public interface MsgContent {

   MsgFormat getFormat();

   int getMsgLength();

   void output(ByteBuf out);

   boolean validate();
}
