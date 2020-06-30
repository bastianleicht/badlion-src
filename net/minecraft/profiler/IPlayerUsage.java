package net.minecraft.profiler;

import net.minecraft.profiler.PlayerUsageSnooper;

public interface IPlayerUsage {
   void addServerStatsToSnooper(PlayerUsageSnooper var1);

   void addServerTypeToSnooper(PlayerUsageSnooper var1);

   boolean isSnooperEnabled();
}
