package net.minecraft.launchwrapper;

import java.io.File;
import java.util.List;
import net.minecraft.launchwrapper.LaunchClassLoader;

public interface ITweaker {
   void acceptOptions(List var1, File var2, File var3, String var4);

   void injectIntoClassLoader(LaunchClassLoader var1);

   String getLaunchTarget();

   String[] getLaunchArguments();
}
