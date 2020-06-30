package org.apache.logging.log4j.message;

import org.apache.logging.log4j.message.Message;

public interface MessageFactory {
   Message newMessage(Object var1);

   Message newMessage(String var1);

   Message newMessage(String var1, Object... var2);
}
