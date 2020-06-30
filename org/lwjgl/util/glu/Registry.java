package org.lwjgl.util.glu;

import org.lwjgl.util.glu.Util;

public class Registry extends Util {
   private static final String versionString = "1.3";
   private static final String extensionString = "GLU_EXT_nurbs_tessellator GLU_EXT_object_space_tess ";

   public static String gluGetString(int name) {
      return name == 100800?"1.3":(name == 100801?"GLU_EXT_nurbs_tessellator GLU_EXT_object_space_tess ":null);
   }

   public static boolean gluCheckExtension(String extName, String extString) {
      return extString != null && extName != null?extString.indexOf(extName) != -1:false;
   }
}
