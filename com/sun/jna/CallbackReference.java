package com.sun.jna;

import com.sun.jna.AltCallingConvention;
import com.sun.jna.Callback;
import com.sun.jna.CallbackParameterContext;
import com.sun.jna.CallbackProxy;
import com.sun.jna.CallbackResultContext;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.NativeString;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

class CallbackReference extends WeakReference {
   static final Map callbackMap = new WeakHashMap();
   static final Map allocations = new WeakHashMap();
   private static final Method PROXY_CALLBACK_METHOD;
   private static final Map initializers;
   Pointer cbstruct;
   CallbackProxy proxy;
   Method method;

   static void setCallbackThreadInitializer(Callback cb, CallbackThreadInitializer initializer) {
      synchronized(callbackMap) {
         if(initializer != null) {
            initializers.put(cb, initializer);
         } else {
            initializers.remove(cb);
         }

      }
   }

   private static ThreadGroup initializeThread(Callback cb, CallbackReference.AttachOptions args) {
      CallbackThreadInitializer init = null;
      if(cb instanceof CallbackReference.DefaultCallbackProxy) {
         cb = ((CallbackReference.DefaultCallbackProxy)cb).getCallback();
      }

      synchronized(initializers) {
         init = (CallbackThreadInitializer)initializers.get(cb);
      }

      ThreadGroup group = null;
      if(init != null) {
         group = init.getThreadGroup(cb);
         args.name = init.getName(cb);
         args.daemon = init.isDaemon(cb);
         args.detach = init.detach(cb);
         args.write();
      }

      return group;
   }

   public static Callback getCallback(Class type, Pointer p) {
      return getCallback(type, p, false);
   }

   private static Callback getCallback(Class type, Pointer p, boolean direct) {
      if(p == null) {
         return null;
      } else if(!type.isInterface()) {
         throw new IllegalArgumentException("Callback type must be an interface");
      } else {
         Map map = callbackMap;
         synchronized(map) {
            for(Callback cb : map.keySet()) {
               if(type.isAssignableFrom(cb.getClass())) {
                  CallbackReference cbref = (CallbackReference)map.get(cb);
                  Pointer cbp = cbref != null?cbref.getTrampoline():getNativeFunctionPointer(cb);
                  if(p.equals(cbp)) {
                     return cb;
                  }
               }
            }

            int ctype = AltCallingConvention.class.isAssignableFrom(type)?1:0;
            Map foptions = new HashMap();
            Map options = Native.getLibraryOptions(type);
            if(options != null) {
               foptions.putAll(options);
            }

            foptions.put("invoking-method", getCallbackMethod(type));
            CallbackReference.NativeFunctionHandler h = new CallbackReference.NativeFunctionHandler(p, ctype, foptions);
            Callback cb = (Callback)Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, h);
            map.put(cb, (Object)null);
            return cb;
         }
      }
   }

   private CallbackReference(Callback callback, int callingConvention, boolean direct) {
      super(callback);
      TypeMapper mapper = Native.getTypeMapper(callback.getClass());
      String arch = System.getProperty("os.arch").toLowerCase();
      boolean ppc = "ppc".equals(arch) || "powerpc".equals(arch);
      if(direct) {
         Method m = getCallbackMethod(callback);
         Class[] ptypes = m.getParameterTypes();

         for(int i = 0; i < ptypes.length; ++i) {
            if(ppc && (ptypes[i] == Float.TYPE || ptypes[i] == Double.TYPE)) {
               direct = false;
               break;
            }

            if(mapper != null && mapper.getFromNativeConverter(ptypes[i]) != null) {
               direct = false;
               break;
            }
         }

         if(mapper != null && mapper.getToNativeConverter(m.getReturnType()) != null) {
            direct = false;
         }
      }

      if(direct) {
         this.method = getCallbackMethod(callback);
         Class[] nativeParamTypes = this.method.getParameterTypes();
         Class returnType = this.method.getReturnType();
         long peer = Native.createNativeCallback(callback, this.method, nativeParamTypes, returnType, callingConvention, true);
         this.cbstruct = peer != 0L?new Pointer(peer):null;
      } else {
         if(callback instanceof CallbackProxy) {
            this.proxy = (CallbackProxy)callback;
         } else {
            this.proxy = new CallbackReference.DefaultCallbackProxy(getCallbackMethod(callback), mapper);
         }

         Class[] nativeParamTypes = this.proxy.getParameterTypes();
         Class returnType = this.proxy.getReturnType();
         if(mapper != null) {
            for(int i = 0; i < nativeParamTypes.length; ++i) {
               FromNativeConverter rc = mapper.getFromNativeConverter(nativeParamTypes[i]);
               if(rc != null) {
                  nativeParamTypes[i] = rc.nativeType();
               }
            }

            ToNativeConverter tn = mapper.getToNativeConverter(returnType);
            if(tn != null) {
               returnType = tn.nativeType();
            }
         }

         for(int i = 0; i < nativeParamTypes.length; ++i) {
            nativeParamTypes[i] = this.getNativeType(nativeParamTypes[i]);
            if(!isAllowableNativeType(nativeParamTypes[i])) {
               String msg = "Callback argument " + nativeParamTypes[i] + " requires custom type conversion";
               throw new IllegalArgumentException(msg);
            }
         }

         returnType = this.getNativeType(returnType);
         if(!isAllowableNativeType(returnType)) {
            String msg = "Callback return type " + returnType + " requires custom type conversion";
            throw new IllegalArgumentException(msg);
         }

         long peer = Native.createNativeCallback(this.proxy, PROXY_CALLBACK_METHOD, nativeParamTypes, returnType, callingConvention, false);
         this.cbstruct = peer != 0L?new Pointer(peer):null;
      }

   }

   private Class getNativeType(Class cls) {
      if(Structure.class.isAssignableFrom(cls)) {
         Structure.newInstance(cls);
         if(!Structure.ByValue.class.isAssignableFrom(cls)) {
            return Pointer.class;
         }
      } else {
         if(NativeMapped.class.isAssignableFrom(cls)) {
            return NativeMappedConverter.getInstance(cls).nativeType();
         }

         if(cls == String.class || cls == WString.class || cls == String[].class || cls == WString[].class || Callback.class.isAssignableFrom(cls)) {
            return Pointer.class;
         }
      }

      return cls;
   }

   private static Method checkMethod(Method m) {
      if(m.getParameterTypes().length > 256) {
         String msg = "Method signature exceeds the maximum parameter count: " + m;
         throw new UnsupportedOperationException(msg);
      } else {
         return m;
      }
   }

   static Class findCallbackClass(Class type) {
      if(!Callback.class.isAssignableFrom(type)) {
         throw new IllegalArgumentException(type.getName() + " is not derived from com.sun.jna.Callback");
      } else if(type.isInterface()) {
         return type;
      } else {
         Class[] ifaces = type.getInterfaces();
         int i = 0;

         while(true) {
            if(i < ifaces.length) {
               if(!Callback.class.isAssignableFrom(ifaces[i])) {
                  ++i;
                  continue;
               }

               try {
                  getCallbackMethod(ifaces[i]);
                  return ifaces[i];
               } catch (IllegalArgumentException var4) {
                  ;
               }
            }

            if(Callback.class.isAssignableFrom(type.getSuperclass())) {
               return findCallbackClass(type.getSuperclass());
            }

            return type;
         }
      }
   }

   private static Method getCallbackMethod(Callback callback) {
      return getCallbackMethod(findCallbackClass(callback.getClass()));
   }

   private static Method getCallbackMethod(Class cls) {
      Method[] pubMethods = cls.getDeclaredMethods();
      Method[] classMethods = cls.getMethods();
      Set pmethods = new HashSet(Arrays.asList(pubMethods));
      pmethods.retainAll(Arrays.asList(classMethods));
      Iterator i = pmethods.iterator();

      while(i.hasNext()) {
         Method m = (Method)i.next();
         if(Callback.FORBIDDEN_NAMES.contains(m.getName())) {
            i.remove();
         }
      }

      Method[] methods = (Method[])((Method[])pmethods.toArray(new Method[pmethods.size()]));
      if(methods.length == 1) {
         return checkMethod(methods[0]);
      } else {
         for(int i = 0; i < methods.length; ++i) {
            Method m = methods[i];
            if("callback".equals(m.getName())) {
               return checkMethod(m);
            }
         }

         String msg = "Callback must implement a single public method, or one public method named \'callback\'";
         throw new IllegalArgumentException(msg);
      }
   }

   private void setCallbackOptions(int options) {
      this.cbstruct.setInt((long)Pointer.SIZE, options);
   }

   public Pointer getTrampoline() {
      return this.cbstruct.getPointer(0L);
   }

   protected void finalize() {
      this.dispose();
   }

   protected synchronized void dispose() {
      if(this.cbstruct != null) {
         Native.freeNativeCallback(this.cbstruct.peer);
         this.cbstruct.peer = 0L;
         this.cbstruct = null;
      }

   }

   private Callback getCallback() {
      return (Callback)this.get();
   }

   private static Pointer getNativeFunctionPointer(Callback cb) {
      if(Proxy.isProxyClass(cb.getClass())) {
         Object handler = Proxy.getInvocationHandler(cb);
         if(handler instanceof CallbackReference.NativeFunctionHandler) {
            return ((CallbackReference.NativeFunctionHandler)handler).getPointer();
         }
      }

      return null;
   }

   public static Pointer getFunctionPointer(Callback cb) {
      return getFunctionPointer(cb, false);
   }

   private static Pointer getFunctionPointer(Callback cb, boolean direct) {
      Pointer fp = null;
      if(cb == null) {
         return null;
      } else if((fp = getNativeFunctionPointer(cb)) != null) {
         return fp;
      } else {
         int callingConvention = cb instanceof AltCallingConvention?1:0;
         Map map = callbackMap;
         synchronized(map) {
            CallbackReference cbref = (CallbackReference)map.get(cb);
            if(cbref == null) {
               cbref = new CallbackReference(cb, callingConvention, direct);
               map.put(cb, cbref);
               if(initializers.containsKey(cb)) {
                  cbref.setCallbackOptions(1);
               }
            }

            return cbref.getTrampoline();
         }
      }
   }

   private static boolean isAllowableNativeType(Class cls) {
      return cls == Void.TYPE || cls == Void.class || cls == Boolean.TYPE || cls == Boolean.class || cls == Byte.TYPE || cls == Byte.class || cls == Short.TYPE || cls == Short.class || cls == Character.TYPE || cls == Character.class || cls == Integer.TYPE || cls == Integer.class || cls == Long.TYPE || cls == Long.class || cls == Float.TYPE || cls == Float.class || cls == Double.TYPE || cls == Double.class || Structure.ByValue.class.isAssignableFrom(cls) && Structure.class.isAssignableFrom(cls) || Pointer.class.isAssignableFrom(cls);
   }

   private static Pointer getNativeString(Object value, boolean wide) {
      if(value != null) {
         NativeString ns = new NativeString(value.toString(), wide);
         allocations.put(value, ns);
         return ns.getPointer();
      } else {
         return null;
      }
   }

   static {
      try {
         PROXY_CALLBACK_METHOD = CallbackProxy.class.getMethod("callback", new Class[]{Object[].class});
      } catch (Exception var1) {
         throw new Error("Error looking up CallbackProxy.callback() method");
      }

      initializers = new WeakHashMap();
   }

   static class AttachOptions extends Structure {
      public boolean daemon;
      public boolean detach;
      public String name;
   }

   private class DefaultCallbackProxy implements CallbackProxy {
      private Method callbackMethod;
      private ToNativeConverter toNative;
      private FromNativeConverter[] fromNative;

      public DefaultCallbackProxy(Method callbackMethod, TypeMapper mapper) {
         this.callbackMethod = callbackMethod;
         Class[] argTypes = callbackMethod.getParameterTypes();
         Class returnType = callbackMethod.getReturnType();
         this.fromNative = new FromNativeConverter[argTypes.length];
         if(NativeMapped.class.isAssignableFrom(returnType)) {
            this.toNative = NativeMappedConverter.getInstance(returnType);
         } else if(mapper != null) {
            this.toNative = mapper.getToNativeConverter(returnType);
         }

         for(int i = 0; i < this.fromNative.length; ++i) {
            if(NativeMapped.class.isAssignableFrom(argTypes[i])) {
               this.fromNative[i] = new NativeMappedConverter(argTypes[i]);
            } else if(mapper != null) {
               this.fromNative[i] = mapper.getFromNativeConverter(argTypes[i]);
            }
         }

         if(!callbackMethod.isAccessible()) {
            try {
               callbackMethod.setAccessible(true);
            } catch (SecurityException var7) {
               throw new IllegalArgumentException("Callback method is inaccessible, make sure the interface is public: " + callbackMethod);
            }
         }

      }

      public Callback getCallback() {
         return CallbackReference.this.getCallback();
      }

      private Object invokeCallback(Object[] args) {
         Class[] paramTypes = this.callbackMethod.getParameterTypes();
         Object[] callbackArgs = new Object[args.length];

         for(int i = 0; i < args.length; ++i) {
            Class type = paramTypes[i];
            Object arg = args[i];
            if(this.fromNative[i] != null) {
               FromNativeContext context = new CallbackParameterContext(type, this.callbackMethod, args, i);
               callbackArgs[i] = this.fromNative[i].fromNative(arg, context);
            } else {
               callbackArgs[i] = this.convertArgument(arg, type);
            }
         }

         Object result = null;
         Callback cb = this.getCallback();
         if(cb != null) {
            try {
               result = this.convertResult(this.callbackMethod.invoke(cb, callbackArgs));
            } catch (IllegalArgumentException var8) {
               Native.getCallbackExceptionHandler().uncaughtException(cb, var8);
            } catch (IllegalAccessException var9) {
               Native.getCallbackExceptionHandler().uncaughtException(cb, var9);
            } catch (InvocationTargetException var10) {
               Native.getCallbackExceptionHandler().uncaughtException(cb, var10.getTargetException());
            }
         }

         for(int i = 0; i < callbackArgs.length; ++i) {
            if(callbackArgs[i] instanceof Structure && !(callbackArgs[i] instanceof Structure.ByValue)) {
               ((Structure)callbackArgs[i]).autoWrite();
            }
         }

         return result;
      }

      public Object callback(Object[] args) {
         try {
            return this.invokeCallback(args);
         } catch (Throwable var3) {
            Native.getCallbackExceptionHandler().uncaughtException(this.getCallback(), var3);
            return null;
         }
      }

      private Object convertArgument(Object value, Class dstType) {
         if(value instanceof Pointer) {
            if(dstType == String.class) {
               value = ((Pointer)value).getString(0L);
            } else if(dstType == WString.class) {
               value = new WString(((Pointer)value).getString(0L, true));
            } else if(dstType != String[].class && dstType != WString[].class) {
               if(Callback.class.isAssignableFrom(dstType)) {
                  CallbackReference var10000 = CallbackReference.this;
                  value = CallbackReference.getCallback(dstType, (Pointer)value);
               } else if(Structure.class.isAssignableFrom(dstType)) {
                  Structure s = Structure.newInstance(dstType);
                  if(Structure.ByValue.class.isAssignableFrom(dstType)) {
                     byte[] buf = new byte[s.size()];
                     ((Pointer)value).read(0L, (byte[])buf, 0, buf.length);
                     s.getPointer().write(0L, (byte[])buf, 0, buf.length);
                  } else {
                     s.useMemory((Pointer)value);
                  }

                  s.read();
                  value = s;
               }
            } else {
               value = ((Pointer)value).getStringArray(0L, dstType == WString[].class);
            }
         } else if((Boolean.TYPE == dstType || Boolean.class == dstType) && value instanceof Number) {
            value = Function.valueOf(((Number)value).intValue() != 0);
         }

         return value;
      }

      private Object convertResult(Object value) {
         if(this.toNative != null) {
            value = this.toNative.toNative(value, new CallbackResultContext(this.callbackMethod));
         }

         if(value == null) {
            return null;
         } else {
            Class cls = value.getClass();
            if(Structure.class.isAssignableFrom(cls)) {
               return Structure.ByValue.class.isAssignableFrom(cls)?value:((Structure)value).getPointer();
            } else if(cls != Boolean.TYPE && cls != Boolean.class) {
               if(cls != String.class && cls != WString.class) {
                  if(cls != String[].class && cls != WString.class) {
                     return Callback.class.isAssignableFrom(cls)?CallbackReference.getFunctionPointer((Callback)value):value;
                  } else {
                     StringArray sa = cls == String[].class?new StringArray((String[])((String[])value)):new StringArray((WString[])((WString[])value));
                     CallbackReference.allocations.put(value, sa);
                     return sa;
                  }
               } else {
                  return CallbackReference.getNativeString(value, cls == WString.class);
               }
            } else {
               return Boolean.TRUE.equals(value)?Function.INTEGER_TRUE:Function.INTEGER_FALSE;
            }
         }
      }

      public Class[] getParameterTypes() {
         return this.callbackMethod.getParameterTypes();
      }

      public Class getReturnType() {
         return this.callbackMethod.getReturnType();
      }
   }

   private static class NativeFunctionHandler implements InvocationHandler {
      private Function function;
      private Map options;

      public NativeFunctionHandler(Pointer address, int callingConvention, Map options) {
         this.function = new Function(address, callingConvention);
         this.options = options;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         if(Library.Handler.OBJECT_TOSTRING.equals(method)) {
            String str = "Proxy interface to " + this.function;
            Method m = (Method)this.options.get("invoking-method");
            Class cls = CallbackReference.findCallbackClass(m.getDeclaringClass());
            str = str + " (" + cls.getName() + ")";
            return str;
         } else if(Library.Handler.OBJECT_HASHCODE.equals(method)) {
            return new Integer(this.hashCode());
         } else if(Library.Handler.OBJECT_EQUALS.equals(method)) {
            Object o = args[0];
            return o != null && Proxy.isProxyClass(o.getClass())?Function.valueOf(Proxy.getInvocationHandler(o) == this):Boolean.FALSE;
         } else {
            if(Function.isVarArgs(method)) {
               args = Function.concatenateVarArgs(args);
            }

            return this.function.invoke(method.getReturnType(), args, this.options);
         }
      }

      public Pointer getPointer() {
         return this.function;
      }
   }
}
