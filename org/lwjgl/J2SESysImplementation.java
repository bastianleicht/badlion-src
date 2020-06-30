package org.lwjgl;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.lwjgl.DefaultSysImplementation;
import org.lwjgl.LWJGLUtil;

abstract class J2SESysImplementation extends DefaultSysImplementation {
   public long getTime() {
      return System.currentTimeMillis();
   }

   public void alert(String title, String message) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception var4) {
         LWJGLUtil.log("Caught exception while setting LAF: " + var4);
      }

      JOptionPane.showMessageDialog((Component)null, message, title, 2);
   }

   public String getClipboard() {
      try {
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         Transferable transferable = clipboard.getContents((Object)null);
         if(transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return (String)transferable.getTransferData(DataFlavor.stringFlavor);
         }
      } catch (Exception var3) {
         LWJGLUtil.log("Exception while getting clipboard: " + var3);
      }

      return null;
   }
}
