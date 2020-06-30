package com.sun.jna.win32;

import com.sun.jna.Callback;
import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.win32.StdCall;
import com.sun.jna.win32.StdCallFunctionMapper;

public interface StdCallLibrary extends Library, StdCall {
   int STDCALL_CONVENTION = 1;
   FunctionMapper FUNCTION_MAPPER = new StdCallFunctionMapper();

   public interface StdCallCallback extends Callback, StdCall {
   }
}
