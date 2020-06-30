package org.apache.logging.log4j.core.net;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.logging.log4j.core.helpers.Charsets;

public class MimeMessageBuilder {
   private final MimeMessage message;

   public MimeMessageBuilder(Session session) {
      this.message = new MimeMessage(session);
   }

   public MimeMessageBuilder setFrom(String from) throws MessagingException {
      InternetAddress address = parseAddress(from);
      if(null != address) {
         this.message.setFrom(address);
      } else {
         try {
            this.message.setFrom();
         } catch (Exception var4) {
            this.message.setFrom((InternetAddress)null);
         }
      }

      return this;
   }

   public MimeMessageBuilder setReplyTo(String replyTo) throws MessagingException {
      InternetAddress[] addresses = parseAddresses(replyTo);
      if(null != addresses) {
         this.message.setReplyTo(addresses);
      }

      return this;
   }

   public MimeMessageBuilder setRecipients(RecipientType recipientType, String recipients) throws MessagingException {
      InternetAddress[] addresses = parseAddresses(recipients);
      if(null != addresses) {
         this.message.setRecipients(recipientType, addresses);
      }

      return this;
   }

   public MimeMessageBuilder setSubject(String subject) throws MessagingException {
      if(subject != null) {
         this.message.setSubject(subject, Charsets.UTF_8.name());
      }

      return this;
   }

   public MimeMessage getMimeMessage() {
      return this.message;
   }

   private static InternetAddress parseAddress(String address) throws AddressException {
      return address == null?null:new InternetAddress(address);
   }

   private static InternetAddress[] parseAddresses(String addresses) throws AddressException {
      return addresses == null?null:InternetAddress.parse(addresses, true);
   }
}
