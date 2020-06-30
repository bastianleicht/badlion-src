package io.netty.channel.sctp;

import com.sun.nio.sctp.AbstractNotificationHandler;
import com.sun.nio.sctp.AssociationChangeNotification;
import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.PeerAddressChangeNotification;
import com.sun.nio.sctp.SendFailedNotification;
import com.sun.nio.sctp.ShutdownNotification;
import io.netty.channel.sctp.SctpChannel;

public final class SctpNotificationHandler extends AbstractNotificationHandler {
   private final SctpChannel sctpChannel;

   public SctpNotificationHandler(SctpChannel sctpChannel) {
      if(sctpChannel == null) {
         throw new NullPointerException("sctpChannel");
      } else {
         this.sctpChannel = sctpChannel;
      }
   }

   public HandlerResult handleNotification(AssociationChangeNotification notification, Object o) {
      this.fireEvent(notification);
      return HandlerResult.CONTINUE;
   }

   public HandlerResult handleNotification(PeerAddressChangeNotification notification, Object o) {
      this.fireEvent(notification);
      return HandlerResult.CONTINUE;
   }

   public HandlerResult handleNotification(SendFailedNotification notification, Object o) {
      this.fireEvent(notification);
      return HandlerResult.CONTINUE;
   }

   public HandlerResult handleNotification(ShutdownNotification notification, Object o) {
      this.fireEvent(notification);
      this.sctpChannel.close();
      return HandlerResult.RETURN;
   }

   private void fireEvent(Notification notification) {
      this.sctpChannel.pipeline().fireUserEventTriggered(notification);
   }
}
