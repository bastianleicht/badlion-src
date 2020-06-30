package com.mojang.authlib;

import com.mojang.authlib.GameProfile;

public interface ProfileLookupCallback {
   void onProfileLookupSucceeded(GameProfile var1);

   void onProfileLookupFailed(GameProfile var1, Exception var2);
}
