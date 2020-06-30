package org.apache.logging.log4j.message;

import org.apache.logging.log4j.message.Message;

public interface MultiformatMessage extends Message {
   String getFormattedMessage(String[] var1);

   String[] getFormats();
}
