package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Subscription extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public long startDate;
   public int daysLeft;
   public Subscription.SubscriptionType type = Subscription.SubscriptionType.NORMAL;

   public static Subscription parse(String json) {
      Subscription sub = new Subscription();

      try {
         JsonParser parser = new JsonParser();
         JsonObject jsonObject = parser.parse(json).getAsJsonObject();
         sub.startDate = JsonUtils.getLongOr("startDate", jsonObject, 0L);
         sub.daysLeft = JsonUtils.getIntOr("daysLeft", jsonObject, 0);
         sub.type = typeFrom(JsonUtils.getStringOr("subscriptionType", jsonObject, Subscription.SubscriptionType.NORMAL.name()));
      } catch (Exception var4) {
         LOGGER.error("Could not parse Subscription: " + var4.getMessage());
      }

      return sub;
   }

   private static Subscription.SubscriptionType typeFrom(String subscriptionType) {
      try {
         return Subscription.SubscriptionType.valueOf(subscriptionType);
      } catch (Exception var2) {
         return Subscription.SubscriptionType.NORMAL;
      }
   }

   public static enum SubscriptionType {
      NORMAL,
      RECURRING;
   }
}
