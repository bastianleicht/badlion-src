package net.badlion.client.tweaker;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class BadlionTweaker implements ITweaker {
   public static boolean OPTIFINE_LOADED = false;
   private List args;

   public void acceptOptions(List args, File gameDir, File assetsDir, String profile) {
      this.debug("Badlion Tweak Loader: Options");
      Method method = null;

      try {
         method = ClassLoader.class.getDeclaredMethod("findLoadedClass", new Class[]{String.class});
         method.setAccessible(true);
         ClassLoader classloader = ClassLoader.getSystemClassLoader();
         Object object = method.invoke(classloader, new Object[]{"optifine.OptiFineTweaker"});
         System.out.println("Optifine loaded: " + (object != null));
         OPTIFINE_LOADED = object != null;
      } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException var8) {
         var8.printStackTrace();
      }

      if(!OPTIFINE_LOADED) {
         this.args = new ArrayList(args);
         this.args.add("--gameDir");
         this.args.add(gameDir.getAbsolutePath());
         this.args.add("--assetsDir");
         this.args.add(assetsDir.getAbsolutePath());
         this.args.add("--version");
         this.args.add(profile);
      } else {
         this.args = new ArrayList();
      }

   }

   public void injectIntoClassLoader(LaunchClassLoader classLoader) {
      this.debug("Badlion Tweak Loader: Injecting into class loader");
      classLoader.registerTransformer("net.badlion.client.tweaker.BadlionTransformer");
   }

   public String getLaunchTarget() {
      this.debug("Badlion Tweak Loader: Launch target");
      return "net.minecraft.client.main.Main";
   }

   public String[] getLaunchArguments() {
      this.debug("Badlion Tweak Loader: Launch Args");
      return (String[])this.args.toArray(new String[this.args.size()]);
   }

   private void debug(String str) {
      System.out.println(str);
   }
}
