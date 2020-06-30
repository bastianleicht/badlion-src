package net.minecraft.launchwrapper.injector;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

public class AlphaVanillaTweakInjector implements IClassTransformer {
   public byte[] transform(String name, String transformedName, byte[] bytes) {
      return bytes;
   }

   public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Class<?> clazz;
      try {
         clazz = getaClass("net.minecraft.client.MinecraftApplet");
      } catch (ClassNotFoundException var10) {
         clazz = getaClass("com.mojang.minecraft.MinecraftApplet");
      }

      System.out.println("AlphaVanillaTweakInjector.class.getClassLoader() = " + AlphaVanillaTweakInjector.class.getClassLoader());
      Constructor<?> constructor = clazz.getConstructor(new Class[0]);
      Object object = constructor.newInstance(new Object[0]);

      Field[] var7;
      for(Field field : var7 = clazz.getDeclaredFields()) {
         String name = field.getType().getName();
         if(!name.contains("awt") && !name.contains("java") && !name.equals("long")) {
            System.out.println("Found likely Minecraft candidate: " + field);
            Field fileField = getWorkingDirField(name);
            if(fileField != null) {
               System.out.println("Found File, changing to " + Launch.minecraftHome);
               fileField.setAccessible(true);
               fileField.set((Object)null, Launch.minecraftHome);
               break;
            }
         }
      }

      startMinecraft((Applet)object, args);
   }

   private static void startMinecraft(final Applet applet, String[] args) {
      final Map<String, String> params = new HashMap();
      String name = "Player" + System.currentTimeMillis() % 1000L;
      if(args.length > 0) {
         name = args[0];
      }

      String sessionId = "-";
      if(args.length > 1) {
         sessionId = args[1];
      }

      params.put("username", name);
      params.put("sessionid", sessionId);
      Frame launcherFrameFake = new Frame();
      launcherFrameFake.setTitle("Minecraft");
      launcherFrameFake.setBackground(Color.BLACK);
      JPanel panel = new JPanel();
      launcherFrameFake.setLayout(new BorderLayout());
      panel.setPreferredSize(new Dimension(854, 480));
      launcherFrameFake.add(panel, "Center");
      launcherFrameFake.pack();
      launcherFrameFake.setLocationRelativeTo((Component)null);
      launcherFrameFake.setVisible(true);
      launcherFrameFake.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            System.exit(1);
         }
      });
      class LauncherFake extends Applet implements AppletStub {
         private static final long serialVersionUID = 1L;

         public void appletResize(int width, int height) {
         }

         public boolean isActive() {
            return true;
         }

         public URL getDocumentBase() {
            try {
               return new URL("http://www.minecraft.net/game/");
            } catch (MalformedURLException var2) {
               var2.printStackTrace();
               return null;
            }
         }

         public URL getCodeBase() {
            try {
               return new URL("http://www.minecraft.net/game/");
            } catch (MalformedURLException var2) {
               var2.printStackTrace();
               return null;
            }
         }

         public String getParameter(String paramName) {
            if(params.containsKey(paramName)) {
               return (String)params.get(paramName);
            } else {
               System.err.println("Client asked for parameter: " + paramName);
               return null;
            }
         }
      }

      LauncherFake fakeLauncher = new LauncherFake();
      applet.setStub(fakeLauncher);
      fakeLauncher.setLayout(new BorderLayout());
      fakeLauncher.add(applet, "Center");
      fakeLauncher.validate();
      launcherFrameFake.removeAll();
      launcherFrameFake.setLayout(new BorderLayout());
      launcherFrameFake.add(fakeLauncher, "Center");
      launcherFrameFake.validate();
      applet.init();
      applet.start();
      Runtime.getRuntime().addShutdownHook(new Thread() {
         public void run() {
            applet.stop();
         }
      });
   }

   private static Class getaClass(String name) throws ClassNotFoundException {
      return Launch.classLoader.findClass(name);
   }

   private static Field getWorkingDirField(String name) throws ClassNotFoundException {
      Class<?> clazz = getaClass(name);

      Field[] var5;
      for(Field field : var5 = clazz.getDeclaredFields()) {
         if(Modifier.isStatic(field.getModifiers()) && field.getType().getName().equals("java.io.File")) {
            return field;
         }
      }

      return null;
   }
}
