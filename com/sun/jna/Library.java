package com.sun.jna;

import com.sun.jna.Function;
import com.sun.jna.FunctionMapper;
import com.sun.jna.InvocationMapper;
import com.sun.jna.NativeLibrary;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public interface Library {
   String OPTION_TYPE_MAPPER = "type-mapper";
   String OPTION_FUNCTION_MAPPER = "function-mapper";
   String OPTION_INVOCATION_MAPPER = "invocation-mapper";
   String OPTION_STRUCTURE_ALIGNMENT = "structure-alignment";
   String OPTION_ALLOW_OBJECTS = "allow-objects";
   String OPTION_CALLING_CONVENTION = "calling-convention";

   public static class Handler implements InvocationHandler {
      static final Method OBJECT_TOSTRING;
      static final Method OBJECT_HASHCODE;
      static final Method OBJECT_EQUALS;
      private final NativeLibrary nativeLibrary;
      private final Class interfaceClass;
      private final Map options;
      private FunctionMapper functionMapper;
      private final InvocationMapper invocationMapper;
      private final Map functions = new WeakHashMap();

      public Handler(String libname, Class interfaceClass, Map options) {
         if(libname != null && "".equals(libname.trim())) {
            throw new IllegalArgumentException("Invalid library name \"" + libname + "\"");
         } else {
            this.interfaceClass = interfaceClass;
            HashMap var5 = new HashMap(options);
            int callingConvention = (null.class$com$sun$jna$AltCallingConvention == null?(null.class$com$sun$jna$AltCallingConvention = null.class$("com.sun.jna.AltCallingConvention")):null.class$com$sun$jna$AltCallingConvention).isAssignableFrom(interfaceClass)?1:0;
            if(var5.get("calling-convention") == null) {
               var5.put("calling-convention", new Integer(callingConvention));
            }

            this.options = var5;
            this.nativeLibrary = NativeLibrary.getInstance(libname, var5);
            this.functionMapper = (FunctionMapper)var5.get("function-mapper");
            if(this.functionMapper == null) {
               this.functionMapper = new Library.Handler.FunctionNameMap(var5);
            }

            this.invocationMapper = (InvocationMapper)var5.get("invocation-mapper");
         }
      }

      public NativeLibrary getNativeLibrary() {
         return this.nativeLibrary;
      }

      public String getLibraryName() {
         return this.nativeLibrary.getName();
      }

      public Class getInterfaceClass() {
         return this.interfaceClass;
      }

      public Object invoke(Object proxy, Method method, Object[] inArgs) throws Throwable {
         if(OBJECT_TOSTRING.equals(method)) {
            return "Proxy interface to " + this.nativeLibrary;
         } else if(OBJECT_HASHCODE.equals(method)) {
            return new Integer(this.hashCode());
         } else if(OBJECT_EQUALS.equals(method)) {
            Object o = inArgs[0];
            return o != null && Proxy.isProxyClass(o.getClass())?Function.valueOf(Proxy.getInvocationHandler(o) == this):Boolean.FALSE;
         } else {
            Library.Handler.FunctionInfo f = null;
            synchronized(this.functions) {
               f = (Library.Handler.FunctionInfo)this.functions.get(method);
               if(f == null) {
                  f = new Library.Handler.FunctionInfo();
                  f.isVarArgs = Function.isVarArgs(method);
                  if(this.invocationMapper != null) {
                     f.handler = this.invocationMapper.getInvocationHandler(this.nativeLibrary, method);
                  }

                  if(f.handler == null) {
                     String methodName = this.functionMapper.getFunctionName(this.nativeLibrary, method);
                     if(methodName == null) {
                        methodName = method.getName();
                     }

                     f.function = this.nativeLibrary.getFunction(methodName, method);
                     f.options = new HashMap(this.options);
                     f.options.put("invoking-method", method);
                  }

                  this.functions.put(method, f);
               }
            }

            if(f.isVarArgs) {
               inArgs = Function.concatenateVarArgs(inArgs);
            }

            return f.handler != null?f.handler.invoke(proxy, method, inArgs):f.function.invoke(method.getReturnType(), inArgs, f.options);
         }
      }

      static {
         try {
            OBJECT_TOSTRING = (null.class$java$lang$Object == null?(null.class$java$lang$Object = null.class$("java.lang.Object")):null.class$java$lang$Object).getMethod("toString", new Class[0]);
            OBJECT_HASHCODE = (null.class$java$lang$Object == null?(null.class$java$lang$Object = null.class$("java.lang.Object")):null.class$java$lang$Object).getMethod("hashCode", new Class[0]);
            OBJECT_EQUALS = (null.class$java$lang$Object == null?(null.class$java$lang$Object = null.class$("java.lang.Object")):null.class$java$lang$Object).getMethod("equals", new Class[]{null.class$java$lang$Object == null?(null.class$java$lang$Object = null.class$("java.lang.Object")):null.class$java$lang$Object});
         } catch (Exception var1) {
            throw new Error("Error retrieving Object.toString() method");
         }
      }

      private static class FunctionInfo {
         InvocationHandler handler;
         Function function;
         boolean isVarArgs;
         Map options;

         private FunctionInfo() {
         }
      }

      private static class FunctionNameMap implements FunctionMapper {
         private final Map map;

         public FunctionNameMap(Map map) {
            this.map = new HashMap(map);
         }

         public String getFunctionName(NativeLibrary library, Method method) {
            String name = method.getName();
            return this.map.containsKey(name)?(String)this.map.get(name):name;
         }
      }
   }
}
