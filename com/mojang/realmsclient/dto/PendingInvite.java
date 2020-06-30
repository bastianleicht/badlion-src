package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PendingInvite extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String invitationId;
   public String worldName;
   public String worldOwnerName;
   public String worldOwnerUuid;
   public Date date;

   public static PendingInvite parse(JsonObject json) {
      PendingInvite invite = new PendingInvite();

      try {
         invite.invitationId = JsonUtils.getStringOr("invitationId", json, "");
         invite.worldName = JsonUtils.getStringOr("worldName", json, "");
         invite.worldOwnerName = JsonUtils.getStringOr("worldOwnerName", json, "");
         invite.worldOwnerUuid = JsonUtils.getStringOr("worldOwnerUuid", json, "");
         invite.date = JsonUtils.getDateOr("date", json);
      } catch (Exception var3) {
         LOGGER.error("Could not parse PendingInvite: " + var3.getMessage());
      }

      return invite;
   }
}
