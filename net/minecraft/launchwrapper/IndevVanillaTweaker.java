package net.minecraft.launchwrapper;

import java.io.File;
import java.util.List;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class IndevVanillaTweaker implements ITweaker {
   private List args;

   public void acceptOptions(List args, File gameDir, File assetsDir, String profile) {
      this.args = args;
   }

   public void injectIntoClassLoader(LaunchClassLoader classLoader) {
      classLoader.registerTransformer("net.minecraft.launchwrapper.injector.IndevVanillaTweakInjector");
   }

   public String getLaunchTarget() {
      return "net.minecraft.launchwrapper.injector.AlphaVanillaTweakInjector";
   }

   public String[] getLaunchArguments() {
      return (String[])this.args.toArray(new String[this.args.size()]);
   }
}
