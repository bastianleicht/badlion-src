package com.sun.jna.win32;

import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;
import java.util.HashMap;
import java.util.Map;

public interface W32APIOptions extends StdCallLibrary {
   Map UNICODE_OPTIONS = new HashMap() {
      {
         this.put("type-mapper", W32APITypeMapper.UNICODE);
         this.put("function-mapper", W32APIFunctionMapper.UNICODE);
      }
   };
   Map ASCII_OPTIONS = new HashMap() {
      {
         this.put("type-mapper", W32APITypeMapper.ASCII);
         this.put("function-mapper", W32APIFunctionMapper.ASCII);
      }
   };
   Map DEFAULT_OPTIONS = Boolean.getBoolean("w32.ascii")?ASCII_OPTIONS:UNICODE_OPTIONS;
}
