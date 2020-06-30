package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListEntry;
import net.minecraft.server.management.UserListOpsEntry;

public class UserListOps extends UserList {
   public UserListOps(File saveFile) {
      super(saveFile);
   }

   protected UserListEntry createEntry(JsonObject entryData) {
      return new UserListOpsEntry(entryData);
   }

   public String[] getKeys() {
      String[] astring = new String[this.getValues().size()];
      int i = 0;

      for(UserListOpsEntry userlistopsentry : this.getValues().values()) {
         astring[i++] = ((GameProfile)userlistopsentry.getValue()).getName();
      }

      return astring;
   }

   public boolean func_183026_b(GameProfile p_183026_1_) {
      UserListOpsEntry userlistopsentry = (UserListOpsEntry)this.getEntry(p_183026_1_);
      return userlistopsentry != null?userlistopsentry.func_183024_b():false;
   }

   protected String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }

   public GameProfile getGameProfileFromName(String username) {
      for(UserListOpsEntry userlistopsentry : this.getValues().values()) {
         if(username.equalsIgnoreCase(((GameProfile)userlistopsentry.getValue()).getName())) {
            return (GameProfile)userlistopsentry.getValue();
         }
      }

      return null;
   }
}
