package net.java.games.input;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Keyboard;
import net.java.games.input.LinuxAbstractController;
import net.java.games.input.LinuxCombinedController;
import net.java.games.input.LinuxComponent;
import net.java.games.input.LinuxDevice;
import net.java.games.input.LinuxDeviceTask;
import net.java.games.input.LinuxDeviceThread;
import net.java.games.input.LinuxEventComponent;
import net.java.games.input.LinuxEventDevice;
import net.java.games.input.LinuxJoystickAbstractController;
import net.java.games.input.LinuxJoystickAxis;
import net.java.games.input.LinuxJoystickButton;
import net.java.games.input.LinuxJoystickDevice;
import net.java.games.input.LinuxJoystickPOV;
import net.java.games.input.LinuxKeyboard;
import net.java.games.input.LinuxMouse;
import net.java.games.input.LinuxNativeTypesMap;
import net.java.games.input.LinuxPOV;
import net.java.games.input.Mouse;
import net.java.games.input.Rumbler;
import net.java.games.util.plugins.Plugin;

public final class LinuxEnvironmentPlugin extends ControllerEnvironment implements Plugin {
   private static final String LIBNAME = "jinput-linux";
   private static final String POSTFIX64BIT = "64";
   private static boolean supported = false;
   private final Controller[] controllers;
   private final List devices = new ArrayList();
   private static final LinuxDeviceThread device_thread = new LinuxDeviceThread();

   static void loadLibrary(final String lib_name) {
      AccessController.doPrivileged(new PrivilegedAction() {
         public final Object run() {
            String lib_path = System.getProperty("net.java.games.input.librarypath");

            try {
               if(lib_path != null) {
                  System.load(lib_path + File.separator + System.mapLibraryName(lib_name));
               } else {
                  System.loadLibrary(lib_name);
               }
            } catch (UnsatisfiedLinkError var3) {
               ControllerEnvironment.logln("Failed to load library: " + var3.getMessage());
               var3.printStackTrace();
               LinuxEnvironmentPlugin.supported = false;
            }

            return null;
         }
      });
   }

   static String getPrivilegedProperty(final String property) {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(property);
         }
      });
   }

   static String getPrivilegedProperty(final String property, final String default_value) {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(property, default_value);
         }
      });
   }

   public static final Object execute(LinuxDeviceTask task) throws IOException {
      return device_thread.execute(task);
   }

   public LinuxEnvironmentPlugin() {
      if(this.isSupported()) {
         this.controllers = this.enumerateControllers();
         logln("Linux plugin claims to have found " + this.controllers.length + " controllers");
         AccessController.doPrivileged(new PrivilegedAction() {
            public final Object run() {
               Runtime.getRuntime().addShutdownHook(LinuxEnvironmentPlugin.this.new ShutdownHook());
               return null;
            }
         });
      } else {
         this.controllers = new Controller[0];
      }

   }

   public final Controller[] getControllers() {
      return this.controllers;
   }

   private static final Component[] createComponents(List event_components, LinuxEventDevice device) {
      LinuxEventComponent[][] povs = new LinuxEventComponent[4][2];
      List components = new ArrayList();

      for(int i = 0; i < event_components.size(); ++i) {
         LinuxEventComponent event_component = (LinuxEventComponent)event_components.get(i);
         Component.Identifier identifier = event_component.getIdentifier();
         if(identifier == Component.Identifier.Axis.POV) {
            int native_code = event_component.getDescriptor().getCode();
            switch(native_code) {
            case 16:
               povs[0][0] = event_component;
               break;
            case 17:
               povs[0][1] = event_component;
               break;
            case 18:
               povs[1][0] = event_component;
               break;
            case 19:
               povs[1][1] = event_component;
               break;
            case 20:
               povs[2][0] = event_component;
               break;
            case 21:
               povs[2][1] = event_component;
               break;
            case 22:
               povs[3][0] = event_component;
               break;
            case 23:
               povs[3][1] = event_component;
               break;
            default:
               logln("Unknown POV instance: " + native_code);
            }
         } else if(identifier != null) {
            LinuxComponent component = new LinuxComponent(event_component);
            components.add(component);
            device.registerComponent(event_component.getDescriptor(), component);
         }
      }

      for(int i = 0; i < povs.length; ++i) {
         LinuxEventComponent x = povs[i][0];
         LinuxEventComponent y = povs[i][1];
         if(x != null && y != null) {
            LinuxComponent controller_component = new LinuxPOV(x, y);
            components.add(controller_component);
            device.registerComponent(x.getDescriptor(), controller_component);
            device.registerComponent(y.getDescriptor(), controller_component);
         }
      }

      Component[] components_array = new Component[components.size()];
      components.toArray(components_array);
      return components_array;
   }

   private static final Mouse createMouseFromDevice(LinuxEventDevice device, Component[] components) throws IOException {
      Mouse mouse = new LinuxMouse(device, components, new Controller[0], device.getRumblers());
      return mouse.getX() != null && mouse.getY() != null && mouse.getPrimaryButton() != null?mouse:null;
   }

   private static final Keyboard createKeyboardFromDevice(LinuxEventDevice device, Component[] components) throws IOException {
      Keyboard keyboard = new LinuxKeyboard(device, components, new Controller[0], device.getRumblers());
      return keyboard;
   }

   private static final Controller createJoystickFromDevice(LinuxEventDevice device, Component[] components, Controller.Type type) throws IOException {
      Controller joystick = new LinuxAbstractController(device, components, new Controller[0], device.getRumblers(), type);
      return joystick;
   }

   private static final Controller createControllerFromDevice(LinuxEventDevice device) throws IOException {
      List event_components = device.getComponents();
      Component[] components = createComponents(event_components, device);
      Controller.Type type = device.getType();
      return (Controller)(type == Controller.Type.MOUSE?createMouseFromDevice(device, components):(type == Controller.Type.KEYBOARD?createKeyboardFromDevice(device, components):(type != Controller.Type.STICK && type != Controller.Type.GAMEPAD?null:createJoystickFromDevice(device, components, type))));
   }

   private final Controller[] enumerateControllers() {
      List controllers = new ArrayList();
      List eventControllers = new ArrayList();
      List jsControllers = new ArrayList();
      this.enumerateEventControllers(eventControllers);
      this.enumerateJoystickControllers(jsControllers);

      for(int i = 0; i < ((List)eventControllers).size(); ++i) {
         for(int j = 0; j < ((List)jsControllers).size(); ++j) {
            Controller evController = (Controller)eventControllers.get(i);
            Controller jsController = (Controller)jsControllers.get(j);
            if(evController.getName().equals(jsController.getName())) {
               Component[] evComponents = evController.getComponents();
               Component[] jsComponents = jsController.getComponents();
               if(evComponents.length == jsComponents.length) {
                  boolean foundADifference = false;

                  for(int k = 0; k < evComponents.length; ++k) {
                     if(evComponents[k].getIdentifier() != jsComponents[k].getIdentifier()) {
                        foundADifference = true;
                     }
                  }

                  if(!foundADifference) {
                     controllers.add(new LinuxCombinedController((LinuxAbstractController)eventControllers.remove(i), (LinuxJoystickAbstractController)jsControllers.remove(j)));
                     --i;
                     --j;
                     break;
                  }
               }
            }
         }
      }

      controllers.addAll(eventControllers);
      controllers.addAll(jsControllers);
      Controller[] controllers_array = new Controller[controllers.size()];
      controllers.toArray(controllers_array);
      return controllers_array;
   }

   private static final Component.Identifier.Button getButtonIdentifier(int index) {
      switch(index) {
      case 0:
         return Component.Identifier.Button._0;
      case 1:
         return Component.Identifier.Button._1;
      case 2:
         return Component.Identifier.Button._2;
      case 3:
         return Component.Identifier.Button._3;
      case 4:
         return Component.Identifier.Button._4;
      case 5:
         return Component.Identifier.Button._5;
      case 6:
         return Component.Identifier.Button._6;
      case 7:
         return Component.Identifier.Button._7;
      case 8:
         return Component.Identifier.Button._8;
      case 9:
         return Component.Identifier.Button._9;
      case 10:
         return Component.Identifier.Button._10;
      case 11:
         return Component.Identifier.Button._11;
      case 12:
         return Component.Identifier.Button._12;
      case 13:
         return Component.Identifier.Button._13;
      case 14:
         return Component.Identifier.Button._14;
      case 15:
         return Component.Identifier.Button._15;
      case 16:
         return Component.Identifier.Button._16;
      case 17:
         return Component.Identifier.Button._17;
      case 18:
         return Component.Identifier.Button._18;
      case 19:
         return Component.Identifier.Button._19;
      case 20:
         return Component.Identifier.Button._20;
      case 21:
         return Component.Identifier.Button._21;
      case 22:
         return Component.Identifier.Button._22;
      case 23:
         return Component.Identifier.Button._23;
      case 24:
         return Component.Identifier.Button._24;
      case 25:
         return Component.Identifier.Button._25;
      case 26:
         return Component.Identifier.Button._26;
      case 27:
         return Component.Identifier.Button._27;
      case 28:
         return Component.Identifier.Button._28;
      case 29:
         return Component.Identifier.Button._29;
      case 30:
         return Component.Identifier.Button._30;
      case 31:
         return Component.Identifier.Button._31;
      default:
         return null;
      }
   }

   private static final Controller createJoystickFromJoystickDevice(LinuxJoystickDevice device) {
      List components = new ArrayList();
      byte[] axisMap = device.getAxisMap();
      char[] buttonMap = device.getButtonMap();
      LinuxJoystickAxis[] hatBits = new LinuxJoystickAxis[6];

      for(int i = 0; i < device.getNumButtons(); ++i) {
         Component.Identifier button_id = LinuxNativeTypesMap.getButtonID(buttonMap[i]);
         if(button_id != null) {
            LinuxJoystickButton button = new LinuxJoystickButton(button_id);
            device.registerButton(i, button);
            components.add(button);
         }
      }

      for(int i = 0; i < device.getNumAxes(); ++i) {
         Component.Identifier.Axis axis_id = (Component.Identifier.Axis)LinuxNativeTypesMap.getAbsAxisID(axisMap[i]);
         LinuxJoystickAxis axis = new LinuxJoystickAxis(axis_id);
         device.registerAxis(i, axis);
         if(axisMap[i] == 16) {
            hatBits[0] = axis;
         } else if(axisMap[i] == 17) {
            hatBits[1] = axis;
            LinuxJoystickPOV var11 = new LinuxJoystickPOV(Component.Identifier.Axis.POV, hatBits[0], hatBits[1]);
            device.registerPOV((LinuxJoystickPOV)var11);
            components.add(var11);
         } else if(axisMap[i] == 18) {
            hatBits[2] = axis;
         } else if(axisMap[i] == 19) {
            hatBits[3] = axis;
            LinuxJoystickPOV var12 = new LinuxJoystickPOV(Component.Identifier.Axis.POV, hatBits[2], hatBits[3]);
            device.registerPOV((LinuxJoystickPOV)var12);
            components.add(var12);
         } else if(axisMap[i] == 20) {
            hatBits[4] = axis;
         } else if(axisMap[i] == 21) {
            hatBits[5] = axis;
            LinuxJoystickPOV var13 = new LinuxJoystickPOV(Component.Identifier.Axis.POV, hatBits[4], hatBits[5]);
            device.registerPOV((LinuxJoystickPOV)var13);
            components.add(var13);
         } else {
            components.add(axis);
         }
      }

      return new LinuxJoystickAbstractController(device, (Component[])((Component[])components.toArray(new Component[0])), new Controller[0], new Rumbler[0]);
   }

   private final void enumerateJoystickControllers(List controllers) {
      File[] joystick_device_files = enumerateJoystickDeviceFiles("/dev/input");
      if(joystick_device_files == null || joystick_device_files.length == 0) {
         joystick_device_files = enumerateJoystickDeviceFiles("/dev");
         if(joystick_device_files == null) {
            return;
         }
      }

      for(int i = 0; i < joystick_device_files.length; ++i) {
         File event_file = joystick_device_files[i];

         try {
            String path = getAbsolutePathPrivileged(event_file);
            LinuxJoystickDevice device = new LinuxJoystickDevice(path);
            Controller controller = createJoystickFromJoystickDevice(device);
            if(controller != null) {
               controllers.add(controller);
               this.devices.add(device);
            } else {
               device.close();
            }
         } catch (IOException var8) {
            logln("Failed to open device (" + event_file + "): " + var8.getMessage());
         }
      }

   }

   private static final File[] enumerateJoystickDeviceFiles(String dev_path) {
      File dev = new File(dev_path);
      return listFilesPrivileged(dev, new FilenameFilter() {
         public final boolean accept(File dir, String name) {
            return name.startsWith("js");
         }
      });
   }

   private static String getAbsolutePathPrivileged(final File file) {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return file.getAbsolutePath();
         }
      });
   }

   private static File[] listFilesPrivileged(final File dir, final FilenameFilter filter) {
      return (File[])((File[])AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            File[] files = dir.listFiles(filter);
            Arrays.sort(files, new Comparator() {
               public int compare(Object f1, Object f2) {
                  return ((File)f1).getName().compareTo(((File)f2).getName());
               }
            });
            return files;
         }
      }));
   }

   private final void enumerateEventControllers(List controllers) {
      File dev = new File("/dev/input");
      File[] event_device_files = listFilesPrivileged(dev, new FilenameFilter() {
         public final boolean accept(File dir, String name) {
            return name.startsWith("event");
         }
      });
      if(event_device_files != null) {
         for(int i = 0; i < event_device_files.length; ++i) {
            File event_file = event_device_files[i];

            try {
               String path = getAbsolutePathPrivileged(event_file);
               LinuxEventDevice device = new LinuxEventDevice(path);

               try {
                  Controller controller = createControllerFromDevice(device);
                  if(controller != null) {
                     controllers.add(controller);
                     this.devices.add(device);
                  } else {
                     device.close();
                  }
               } catch (IOException var9) {
                  logln("Failed to create Controller: " + var9.getMessage());
                  device.close();
               }
            } catch (IOException var10) {
               logln("Failed to open device (" + event_file + "): " + var10.getMessage());
            }
         }

      }
   }

   public boolean isSupported() {
      return supported;
   }

   static {
      String osName = getPrivilegedProperty("os.name", "").trim();
      if(osName.equals("Linux")) {
         supported = true;
         if("i386".equals(getPrivilegedProperty("os.arch"))) {
            loadLibrary("jinput-linux");
         } else {
            loadLibrary("jinput-linux64");
         }
      }

   }

   private final class ShutdownHook extends Thread {
      private ShutdownHook() {
      }

      public final void run() {
         for(int i = 0; i < LinuxEnvironmentPlugin.this.devices.size(); ++i) {
            try {
               LinuxDevice device = (LinuxDevice)LinuxEnvironmentPlugin.this.devices.get(i);
               device.close();
            } catch (IOException var3) {
               ControllerEnvironment.logln("Failed to close device: " + var3.getMessage());
            }
         }

      }
   }
}
