package com.mojang.authlib;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.minecraft.MinecraftSessionService;

public interface AuthenticationService {
   UserAuthentication createUserAuthentication(Agent var1);

   MinecraftSessionService createMinecraftSessionService();

   GameProfileRepository createProfileRepository();
}
