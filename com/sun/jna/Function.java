package com.sun.jna;

import com.sun.jna.Callback;
import com.sun.jna.CallbackReference;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.FunctionParameterContext;
import com.sun.jna.FunctionResultContext;
import com.sun.jna.Memory;
import com.sun.jna.MethodParameterContext;
import com.sun.jna.MethodResultContext;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.NativeString;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.ToNativeContext;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class Function extends Pointer {
   public static final int MAX_NARGS = 256;
   public static final int C_CONVENTION = 0;
   public static final int ALT_CONVENTION = 1;
   private static final int MASK_CC = 3;
   public static final int THROW_LAST_ERROR = 4;
   static final Integer INTEGER_TRUE = new Integer(-1);
   static final Integer INTEGER_FALSE = new Integer(0);
   private NativeLibrary library;
   private final String functionName;
   int callFlags;
   final Map options;
   static final String OPTION_INVOKING_METHOD = "invoking-method";

   public static Function getFunction(String libraryName, String functionName) {
      return NativeLibrary.getInstance(libraryName).getFunction(functionName);
   }

   public static Function getFunction(String libraryName, String functionName, int callFlags) {
      return NativeLibrary.getInstance(libraryName).getFunction(functionName, callFlags);
   }

   public static Function getFunction(Pointer p) {
      return getFunction(p, 0);
   }

   public static Function getFunction(Pointer p, int callFlags) {
      return new Function(p, callFlags);
   }

   Function(NativeLibrary library, String functionName, int callFlags) {
      this.checkCallingConvention(callFlags & 3);
      if(functionName == null) {
         throw new NullPointerException("Function name must not be null");
      } else {
         this.library = library;
         this.functionName = functionName;
         this.callFlags = callFlags;
         this.options = library.options;

         try {
            this.peer = library.getSymbolAddress(functionName);
         } catch (UnsatisfiedLinkError var5) {
            throw new UnsatisfiedLinkError("Error looking up function \'" + functionName + "\': " + var5.getMessage());
         }
      }
   }

   Function(Pointer functionAddress, int callFlags) {
      this.checkCallingConvention(callFlags & 3);
      if(functionAddress != null && functionAddress.peer != 0L) {
         this.functionName = functionAddress.toString();
         this.callFlags = callFlags;
         this.peer = functionAddress.peer;
         this.options = Collections.EMPTY_MAP;
      } else {
         throw new NullPointerException("Function address may not be null");
      }
   }

   private void checkCallingConvention(int convention) throws IllegalArgumentException {
      switch(convention) {
      case 0:
      case 1:
         return;
      default:
         throw new IllegalArgumentException("Unrecognized calling convention: " + convention);
      }
   }

   public String getName() {
      return this.functionName;
   }

   public int getCallingConvention() {
      return this.callFlags & 3;
   }

   public Object invoke(Class returnType, Object[] inArgs) {
      return this.invoke(returnType, inArgs, this.options);
   }

   public Object invoke(Class returnType, Object[] inArgs, Map options) {
      Object[] args = new Object[0];
      if(inArgs != null) {
         if(inArgs.length > 256) {
            throw new UnsupportedOperationException("Maximum argument count is 256");
         }

         args = new Object[inArgs.length];
         System.arraycopy(inArgs, 0, args, 0, args.length);
      }

      TypeMapper mapper = (TypeMapper)options.get("type-mapper");
      Method invokingMethod = (Method)options.get("invoking-method");
      boolean allowObjects = Boolean.TRUE.equals(options.get("allow-objects"));

      for(int i = 0; i < args.length; ++i) {
         args[i] = this.convertArgument(args, i, invokingMethod, mapper, allowObjects);
      }

      Class nativeType = returnType;
      FromNativeConverter resultConverter = null;
      if(NativeMapped.class.isAssignableFrom(returnType)) {
         NativeMappedConverter tc = NativeMappedConverter.getInstance(returnType);
         resultConverter = tc;
         nativeType = tc.nativeType();
      } else if(mapper != null) {
         resultConverter = mapper.getFromNativeConverter(returnType);
         if(resultConverter != null) {
            nativeType = resultConverter.nativeType();
         }
      }

      Object result = this.invoke(args, nativeType, allowObjects);
      if(resultConverter != null) {
         FromNativeContext context;
         if(invokingMethod != null) {
            context = new MethodResultContext(returnType, this, inArgs, invokingMethod);
         } else {
            context = new FunctionResultContext(returnType, this, inArgs);
         }

         result = resultConverter.fromNative(result, context);
      }

      if(inArgs != null) {
         for(int i = 0; i < inArgs.length; ++i) {
            Object inArg = inArgs[i];
            if(inArg != null) {
               if(inArg instanceof Structure) {
                  if(!(inArg instanceof Structure.ByValue)) {
                     ((Structure)inArg).autoRead();
                  }
               } else if(args[i] instanceof Function.PostCallRead) {
                  ((Function.PostCallRead)args[i]).read();
                  if(args[i] instanceof Function.PointerArray) {
                     Function.PointerArray array = (Function.PointerArray)args[i];
                     if(Structure.ByReference[].class.isAssignableFrom(inArg.getClass())) {
                        Class type = inArg.getClass().getComponentType();
                        Structure[] ss = (Structure[])((Structure[])inArg);

                        for(int si = 0; si < ss.length; ++si) {
                           Pointer p = array.getPointer((long)(Pointer.SIZE * si));
                           ss[si] = Structure.updateStructureByReference(type, ss[si], p);
                        }
                     }
                  }
               } else if(Structure[].class.isAssignableFrom(inArg.getClass())) {
                  Structure.autoRead((Structure[])((Structure[])inArg));
               }
            }
         }
      }

      return result;
   }

   Object invoke(Object[] args, Class returnType, boolean allowObjects) {
      Object result = null;
      if(returnType != null && returnType != Void.TYPE && returnType != Void.class) {
         if(returnType != Boolean.TYPE && returnType != Boolean.class) {
            if(returnType != Byte.TYPE && returnType != Byte.class) {
               if(returnType != Short.TYPE && returnType != Short.class) {
                  if(returnType != Character.TYPE && returnType != Character.class) {
                     if(returnType != Integer.TYPE && returnType != Integer.class) {
                        if(returnType != Long.TYPE && returnType != Long.class) {
                           if(returnType != Float.TYPE && returnType != Float.class) {
                              if(returnType != Double.TYPE && returnType != Double.class) {
                                 if(returnType == String.class) {
                                    result = this.invokeString(this.callFlags, args, false);
                                 } else if(returnType == WString.class) {
                                    String s = this.invokeString(this.callFlags, args, true);
                                    if(s != null) {
                                       result = new WString(s);
                                    }
                                 } else {
                                    if(Pointer.class.isAssignableFrom(returnType)) {
                                       return this.invokePointer(this.callFlags, args);
                                    }

                                    if(Structure.class.isAssignableFrom(returnType)) {
                                       if(Structure.ByValue.class.isAssignableFrom(returnType)) {
                                          Structure s = Native.invokeStructure(this.peer, this.callFlags, args, Structure.newInstance(returnType));
                                          s.autoRead();
                                          result = s;
                                       } else {
                                          result = this.invokePointer(this.callFlags, args);
                                          if(result != null) {
                                             Structure s = Structure.newInstance(returnType);
                                             s.useMemory((Pointer)result);
                                             s.autoRead();
                                             result = s;
                                          }
                                       }
                                    } else if(Callback.class.isAssignableFrom(returnType)) {
                                       result = this.invokePointer(this.callFlags, args);
                                       if(result != null) {
                                          result = CallbackReference.getCallback(returnType, (Pointer)result);
                                       }
                                    } else if(returnType == String[].class) {
                                       Pointer p = this.invokePointer(this.callFlags, args);
                                       if(p != null) {
                                          result = p.getStringArray(0L);
                                       }
                                    } else if(returnType == WString[].class) {
                                       Pointer p = this.invokePointer(this.callFlags, args);
                                       if(p != null) {
                                          String[] arr = p.getStringArray(0L, true);
                                          WString[] warr = new WString[arr.length];

                                          for(int i = 0; i < arr.length; ++i) {
                                             warr[i] = new WString(arr[i]);
                                          }

                                          result = warr;
                                       }
                                    } else if(returnType == Pointer[].class) {
                                       Pointer p = this.invokePointer(this.callFlags, args);
                                       if(p != null) {
                                          result = p.getPointerArray(0L);
                                       }
                                    } else {
                                       if(!allowObjects) {
                                          throw new IllegalArgumentException("Unsupported return type " + returnType + " in function " + this.getName());
                                       }

                                       result = Native.invokeObject(this.peer, this.callFlags, args);
                                       if(result != null && !returnType.isAssignableFrom(result.getClass())) {
                                          throw new ClassCastException("Return type " + returnType + " does not match result " + result.getClass());
                                       }
                                    }
                                 }
                              } else {
                                 result = new Double(Native.invokeDouble(this.peer, this.callFlags, args));
                              }
                           } else {
                              result = new Float(Native.invokeFloat(this.peer, this.callFlags, args));
                           }
                        } else {
                           result = new Long(Native.invokeLong(this.peer, this.callFlags, args));
                        }
                     } else {
                        result = new Integer(Native.invokeInt(this.peer, this.callFlags, args));
                     }
                  } else {
                     result = new Character((char)Native.invokeInt(this.peer, this.callFlags, args));
                  }
               } else {
                  result = new Short((short)Native.invokeInt(this.peer, this.callFlags, args));
               }
            } else {
               result = new Byte((byte)Native.invokeInt(this.peer, this.callFlags, args));
            }
         } else {
            result = valueOf(Native.invokeInt(this.peer, this.callFlags, args) != 0);
         }
      } else {
         Native.invokeVoid(this.peer, this.callFlags, args);
         result = null;
      }

      return result;
   }

   private Pointer invokePointer(int callFlags, Object[] args) {
      long ptr = Native.invokePointer(this.peer, callFlags, args);
      return ptr == 0L?null:new Pointer(ptr);
   }

   private Object convertArgument(Object[] args, int index, Method invokingMethod, TypeMapper mapper, boolean allowObjects) {
      Object arg = args[index];
      if(arg != null) {
         Class type = arg.getClass();
         ToNativeConverter converter = null;
         if(NativeMapped.class.isAssignableFrom(type)) {
            converter = NativeMappedConverter.getInstance(type);
         } else if(mapper != null) {
            converter = mapper.getToNativeConverter(type);
         }

         if(converter != null) {
            ToNativeContext context;
            if(invokingMethod != null) {
               context = new MethodParameterContext(this, args, index, invokingMethod);
            } else {
               context = new FunctionParameterContext(this, args, index);
            }

            arg = converter.toNative(arg, context);
         }
      }

      if(arg != null && !this.isPrimitiveArray(arg.getClass())) {
         Class argClass = arg.getClass();
         if(arg instanceof Structure) {
            Structure struct = (Structure)arg;
            struct.autoWrite();
            if(struct instanceof Structure.ByValue) {
               Class ptype = struct.getClass();
               if(invokingMethod != null) {
                  Class[] ptypes = invokingMethod.getParameterTypes();
                  if(isVarArgs(invokingMethod)) {
                     if(index < ptypes.length - 1) {
                        ptype = ptypes[index];
                     } else {
                        Class etype = ptypes[ptypes.length - 1].getComponentType();
                        if(etype != Object.class) {
                           ptype = etype;
                        }
                     }
                  } else {
                     ptype = ptypes[index];
                  }
               }

               if(Structure.ByValue.class.isAssignableFrom(ptype)) {
                  return struct;
               }
            }

            return struct.getPointer();
         } else if(arg instanceof Callback) {
            return CallbackReference.getFunctionPointer((Callback)arg);
         } else if(arg instanceof String) {
            return (new NativeString((String)arg, false)).getPointer();
         } else if(arg instanceof WString) {
            return (new NativeString(arg.toString(), true)).getPointer();
         } else if(arg instanceof Boolean) {
            return Boolean.TRUE.equals(arg)?INTEGER_TRUE:INTEGER_FALSE;
         } else if(String[].class == argClass) {
            return new StringArray((String[])((String[])arg));
         } else if(WString[].class == argClass) {
            return new StringArray((WString[])((WString[])arg));
         } else if(Pointer[].class == argClass) {
            return new Function.PointerArray((Pointer[])((Pointer[])arg));
         } else if(NativeMapped[].class.isAssignableFrom(argClass)) {
            return new Function.NativeMappedArray((NativeMapped[])((NativeMapped[])arg));
         } else if(Structure[].class.isAssignableFrom(argClass)) {
            Structure[] ss = (Structure[])((Structure[])arg);
            Class type = argClass.getComponentType();
            boolean byRef = Structure.ByReference.class.isAssignableFrom(type);
            if(byRef) {
               Pointer[] pointers = new Pointer[ss.length + 1];

               for(int i = 0; i < ss.length; ++i) {
                  pointers[i] = ss[i] != null?ss[i].getPointer():null;
               }

               return new Function.PointerArray(pointers);
            } else if(ss.length == 0) {
               throw new IllegalArgumentException("Structure array must have non-zero length");
            } else if(ss[0] == null) {
               Structure.newInstance(type).toArray(ss);
               return ss[0].getPointer();
            } else {
               Structure.autoWrite(ss);
               return ss[0].getPointer();
            }
         } else if(argClass.isArray()) {
            throw new IllegalArgumentException("Unsupported array argument type: " + argClass.getComponentType());
         } else if(allowObjects) {
            return arg;
         } else if(!Native.isSupportedNativeType(arg.getClass())) {
            throw new IllegalArgumentException("Unsupported argument type " + arg.getClass().getName() + " at parameter " + index + " of function " + this.getName());
         } else {
            return arg;
         }
      } else {
         return arg;
      }
   }

   private boolean isPrimitiveArray(Class argClass) {
      return argClass.isArray() && argClass.getComponentType().isPrimitive();
   }

   public void invoke(Object[] args) {
      this.invoke(Void.class, args);
   }

   private String invokeString(int callFlags, Object[] args, boolean wide) {
      Pointer ptr = this.invokePointer(callFlags, args);
      String s = null;
      if(ptr != null) {
         if(wide) {
            s = ptr.getString(0L, wide);
         } else {
            s = ptr.getString(0L);
         }
      }

      return s;
   }

   public String toString() {
      return this.library != null?"native function " + this.functionName + "(" + this.library.getName() + ")@0x" + Long.toHexString(this.peer):"native function@0x" + Long.toHexString(this.peer);
   }

   public Object invokeObject(Object[] args) {
      return this.invoke(Object.class, args);
   }

   public Pointer invokePointer(Object[] args) {
      return (Pointer)this.invoke(Pointer.class, args);
   }

   public String invokeString(Object[] args, boolean wide) {
      Object o = this.invoke(wide?WString.class:String.class, args);
      return o != null?o.toString():null;
   }

   public int invokeInt(Object[] args) {
      return ((Integer)this.invoke(Integer.class, args)).intValue();
   }

   public long invokeLong(Object[] args) {
      return ((Long)this.invoke(Long.class, args)).longValue();
   }

   public float invokeFloat(Object[] args) {
      return ((Float)this.invoke(Float.class, args)).floatValue();
   }

   public double invokeDouble(Object[] args) {
      return ((Double)this.invoke(Double.class, args)).doubleValue();
   }

   public void invokeVoid(Object[] args) {
      this.invoke(Void.class, args);
   }

   public boolean equals(Object o) {
      if(o == this) {
         return true;
      } else if(o == null) {
         return false;
      } else if(o.getClass() != this.getClass()) {
         return false;
      } else {
         Function other = (Function)o;
         return other.callFlags == this.callFlags && other.options.equals(this.options) && other.peer == this.peer;
      }
   }

   public int hashCode() {
      return this.callFlags + this.options.hashCode() + super.hashCode();
   }

   static Object[] concatenateVarArgs(Object[] inArgs) {
      if(inArgs != null && inArgs.length > 0) {
         Object lastArg = inArgs[inArgs.length - 1];
         Class argType = lastArg != null?lastArg.getClass():null;
         if(argType != null && argType.isArray()) {
            Object[] varArgs = (Object[])((Object[])lastArg);
            Object[] fullArgs = new Object[inArgs.length + varArgs.length];
            System.arraycopy(inArgs, 0, fullArgs, 0, inArgs.length - 1);
            System.arraycopy(varArgs, 0, fullArgs, inArgs.length - 1, varArgs.length);
            fullArgs[fullArgs.length - 1] = null;
            inArgs = fullArgs;
         }
      }

      return inArgs;
   }

   static boolean isVarArgs(Method m) {
      try {
         Method v = m.getClass().getMethod("isVarArgs", new Class[0]);
         return Boolean.TRUE.equals(v.invoke(m, new Object[0]));
      } catch (SecurityException var2) {
         ;
      } catch (NoSuchMethodException var3) {
         ;
      } catch (IllegalArgumentException var4) {
         ;
      } catch (IllegalAccessException var5) {
         ;
      } catch (InvocationTargetException var6) {
         ;
      }

      return false;
   }

   static Boolean valueOf(boolean b) {
      return b?Boolean.TRUE:Boolean.FALSE;
   }

   private static class NativeMappedArray extends Memory implements Function.PostCallRead {
      private final NativeMapped[] original;

      public NativeMappedArray(NativeMapped[] arg) {
         super((long)Native.getNativeSize(arg.getClass(), arg));
         this.original = arg;
         this.setValue(0L, this.original, this.original.getClass());
      }

      public void read() {
         this.getValue(0L, this.original.getClass(), this.original);
      }
   }

   private static class PointerArray extends Memory implements Function.PostCallRead {
      private final Pointer[] original;

      public PointerArray(Pointer[] arg) {
         super((long)(Pointer.SIZE * (arg.length + 1)));
         this.original = arg;

         for(int i = 0; i < arg.length; ++i) {
            this.setPointer((long)(i * Pointer.SIZE), arg[i]);
         }

         this.setPointer((long)(Pointer.SIZE * arg.length), (Pointer)null);
      }

      public void read() {
         this.read(0L, this.original, 0, this.original.length);
      }
   }

   public interface PostCallRead {
      void read();
   }
}
