package org.lwjgl.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.DisplayMode;

public class XRandR {
   private static XRandR.Screen[] current;
   private static String primaryScreenIdentifier;
   private static XRandR.Screen[] savedConfiguration;
   private static Map screens;
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
   private static final Pattern SCREEN_HEADER_PATTERN = Pattern.compile("^(\\d+)x(\\d+)[+](\\d+)[+](\\d+)$");
   private static final Pattern SCREEN_MODELINE_PATTERN = Pattern.compile("^(\\d+)x(\\d+)$");
   private static final Pattern FREQ_PATTERN = Pattern.compile("^(\\d+)[.](\\d+)(?:\\s*[*])?(?:\\s*[+])?$");

   private static void populate() {
      if(screens == null) {
         screens = new HashMap();

         try {
            Process p = Runtime.getRuntime().exec(new String[]{"xrandr", "-q"});
            List<XRandR.Screen> currentList = new ArrayList();
            List<XRandR.Screen> possibles = new ArrayList();
            String name = null;
            int[] currentScreenPosition = new int[2];
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while((line = br.readLine()) != null) {
               line = line.trim();
               String[] sa = WHITESPACE_PATTERN.split(line);
               if("connected".equals(sa[1])) {
                  if(name != null) {
                     screens.put(name, possibles.toArray(new XRandR.Screen[possibles.size()]));
                     possibles.clear();
                  }

                  name = sa[0];
                  if("primary".equals(sa[2])) {
                     parseScreenHeader(currentScreenPosition, sa[3]);
                     primaryScreenIdentifier = name;
                  } else {
                     parseScreenHeader(currentScreenPosition, sa[2]);
                  }
               } else {
                  Matcher m = SCREEN_MODELINE_PATTERN.matcher(sa[0]);
                  if(m.matches()) {
                     parseScreenModeline(possibles, currentList, name, Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), sa, currentScreenPosition);
                  }
               }
            }

            screens.put(name, possibles.toArray(new XRandR.Screen[possibles.size()]));
            current = (XRandR.Screen[])currentList.toArray(new XRandR.Screen[currentList.size()]);
            if(primaryScreenIdentifier == null) {
               long totalPixels = Long.MIN_VALUE;

               for(XRandR.Screen screen : current) {
                  if(1L * (long)screen.width * (long)screen.height > totalPixels) {
                     primaryScreenIdentifier = screen.name;
                     totalPixels = 1L * (long)screen.width * (long)screen.height;
                  }
               }
            }
         } catch (Throwable var13) {
            LWJGLUtil.log("Exception in XRandR.populate(): " + var13.getMessage());
            screens.clear();
            current = new XRandR.Screen[0];
         }

      }
   }

   public static XRandR.Screen[] getConfiguration() {
      populate();

      for(XRandR.Screen screen : current) {
         if(screen.name.equals(primaryScreenIdentifier)) {
            return new XRandR.Screen[]{screen};
         }
      }

      return (XRandR.Screen[])current.clone();
   }

   public static void setConfiguration(boolean disableOthers, XRandR.Screen... screens) {
      if(screens.length == 0) {
         throw new IllegalArgumentException("Must specify at least one screen");
      } else {
         List<String> cmd = new ArrayList();
         cmd.add("xrandr");
         if(disableOthers) {
            for(XRandR.Screen screen : current) {
               boolean disable = true;

               for(XRandR.Screen screen1 : screens) {
                  if(screen1.name.equals(screen.name)) {
                     disable = false;
                     break;
                  }
               }

               if(disable) {
                  cmd.add("--output");
                  cmd.add(screen.name);
                  cmd.add("--off");
               }
            }
         }

         for(XRandR.Screen screen : screens) {
            screen.getArgs(cmd);
         }

         try {
            Process p = Runtime.getRuntime().exec((String[])cmd.toArray(new String[cmd.size()]));
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while((line = br.readLine()) != null) {
               LWJGLUtil.log("Unexpected output from xrandr process: " + line);
            }

            current = screens;
         } catch (IOException var12) {
            LWJGLUtil.log("XRandR exception in setConfiguration(): " + var12.getMessage());
         }

      }
   }

   public static void saveConfiguration() {
      populate();
      savedConfiguration = (XRandR.Screen[])current.clone();
   }

   public static void restoreConfiguration() {
      if(savedConfiguration != null) {
         setConfiguration(true, savedConfiguration);
      }

   }

   public static String[] getScreenNames() {
      populate();
      return (String[])screens.keySet().toArray(new String[screens.size()]);
   }

   public static XRandR.Screen[] getResolutions(String name) {
      populate();
      return (XRandR.Screen[])((XRandR.Screen[])screens.get(name)).clone();
   }

   private static void parseScreenModeline(List allModes, List current, String name, int width, int height, String[] modeLine, int[] screenPosition) {
      for(int i = 1; i < modeLine.length; ++i) {
         String freqS = modeLine[i];
         if(!"+".equals(freqS)) {
            Matcher m = FREQ_PATTERN.matcher(freqS);
            if(!m.matches()) {
               LWJGLUtil.log("Frequency match failed: " + Arrays.toString(modeLine));
               return;
            }

            int freq = Integer.parseInt(m.group(1));
            XRandR.Screen s = new XRandR.Screen(name, width, height, freq, 0, 0);
            if(freqS.contains("*")) {
               current.add(new XRandR.Screen(name, width, height, freq, screenPosition[0], screenPosition[1]));
               allModes.add(0, s);
            } else {
               allModes.add(s);
            }
         }
      }

   }

   private static void parseScreenHeader(int[] screenPosition, String resPos) {
      Matcher m = SCREEN_HEADER_PATTERN.matcher(resPos);
      if(!m.matches()) {
         screenPosition[0] = 0;
         screenPosition[1] = 0;
      } else {
         screenPosition[0] = Integer.parseInt(m.group(3));
         screenPosition[1] = Integer.parseInt(m.group(4));
      }
   }

   static XRandR.Screen DisplayModetoScreen(DisplayMode mode) {
      populate();
      XRandR.Screen primary = findPrimary(current);
      return new XRandR.Screen(primary.name, mode.getWidth(), mode.getHeight(), mode.getFrequency(), primary.xPos, primary.yPos);
   }

   static DisplayMode ScreentoDisplayMode(XRandR.Screen... screens) {
      populate();
      XRandR.Screen primary = findPrimary(screens);
      return new DisplayMode(primary.width, primary.height, 24, primary.freq);
   }

   private static XRandR.Screen findPrimary(XRandR.Screen... screens) {
      for(XRandR.Screen screen : screens) {
         if(screen.name.equals(primaryScreenIdentifier)) {
            return screen;
         }
      }

      return screens[0];
   }

   public static class Screen implements Cloneable {
      public final String name;
      public final int width;
      public final int height;
      public final int freq;
      public int xPos;
      public int yPos;

      Screen(String name, int width, int height, int freq, int xPos, int yPos) {
         this.name = name;
         this.width = width;
         this.height = height;
         this.freq = freq;
         this.xPos = xPos;
         this.yPos = yPos;
      }

      private void getArgs(List argList) {
         argList.add("--output");
         argList.add(this.name);
         argList.add("--mode");
         argList.add(this.width + "x" + this.height);
         argList.add("--rate");
         argList.add(Integer.toString(this.freq));
         argList.add("--pos");
         argList.add(this.xPos + "x" + this.yPos);
      }

      public String toString() {
         return this.name + " " + this.width + "x" + this.height + " @ " + this.xPos + "x" + this.yPos + " with " + this.freq + "Hz";
      }
   }
}
