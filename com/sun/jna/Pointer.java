package com.sun.jna;

import com.sun.jna.Callback;
import com.sun.jna.CallbackReference;
import com.sun.jna.FromNativeContext;
import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Platform;
import com.sun.jna.Structure;
import com.sun.jna.ToNativeContext;
import com.sun.jna.WString;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class Pointer {
   public static final int SIZE;
   public static final Pointer NULL;
   protected long peer;

   public static final Pointer createConstant(long peer) {
      return new Pointer.Opaque(peer);
   }

   public static final Pointer createConstant(int peer) {
      return new Pointer.Opaque((long)peer & -1L);
   }

   Pointer() {
   }

   public Pointer(long peer) {
      this.peer = peer;
   }

   public Pointer share(long offset) {
      return this.share(offset, 0L);
   }

   public Pointer share(long offset, long sz) {
      return offset == 0L?this:new Pointer(this.peer + offset);
   }

   public void clear(long size) {
      this.setMemory(0L, size, (byte)0);
   }

   public boolean equals(Object o) {
      return o == this?true:(o == null?false:o instanceof Pointer && ((Pointer)o).peer == this.peer);
   }

   public int hashCode() {
      return (int)((this.peer >>> 32) + (this.peer & -1L));
   }

   public long indexOf(long offset, byte value) {
      return Native.indexOf(this.peer + offset, value);
   }

   public void read(long offset, byte[] buf, int index, int length) {
      Native.read(this.peer + offset, buf, index, length);
   }

   public void read(long offset, short[] buf, int index, int length) {
      Native.read(this.peer + offset, buf, index, length);
   }

   public void read(long offset, char[] buf, int index, int length) {
      Native.read(this.peer + offset, buf, index, length);
   }

   public void read(long offset, int[] buf, int index, int length) {
      Native.read(this.peer + offset, buf, index, length);
   }

   public void read(long offset, long[] buf, int index, int length) {
      Native.read(this.peer + offset, buf, index, length);
   }

   public void read(long offset, float[] buf, int index, int length) {
      Native.read(this.peer + offset, buf, index, length);
   }

   public void read(long offset, double[] buf, int index, int length) {
      Native.read(this.peer + offset, buf, index, length);
   }

   public void read(long offset, Pointer[] buf, int index, int length) {
      for(int i = 0; i < length; ++i) {
         Pointer p = this.getPointer(offset + (long)(i * SIZE));
         Pointer oldp = buf[i + index];
         if(oldp == null || p == null || p.peer != oldp.peer) {
            buf[i + index] = p;
         }
      }

   }

   public void write(long offset, byte[] buf, int index, int length) {
      Native.write(this.peer + offset, buf, index, length);
   }

   public void write(long offset, short[] buf, int index, int length) {
      Native.write(this.peer + offset, buf, index, length);
   }

   public void write(long offset, char[] buf, int index, int length) {
      Native.write(this.peer + offset, buf, index, length);
   }

   public void write(long offset, int[] buf, int index, int length) {
      Native.write(this.peer + offset, buf, index, length);
   }

   public void write(long offset, long[] buf, int index, int length) {
      Native.write(this.peer + offset, buf, index, length);
   }

   public void write(long offset, float[] buf, int index, int length) {
      Native.write(this.peer + offset, buf, index, length);
   }

   public void write(long offset, double[] buf, int index, int length) {
      Native.write(this.peer + offset, buf, index, length);
   }

   public void write(long bOff, Pointer[] buf, int index, int length) {
      for(int i = 0; i < length; ++i) {
         this.setPointer(bOff + (long)(i * SIZE), buf[index + i]);
      }

   }

   Object getValue(long offset, Class type, Object currentValue) {
      Object result = null;
      if(Structure.class.isAssignableFrom(type)) {
         Structure s = (Structure)currentValue;
         if(Structure.ByReference.class.isAssignableFrom(type)) {
            s = Structure.updateStructureByReference(type, s, this.getPointer(offset));
         } else {
            s.useMemory(this, (int)offset);
            s.read();
         }

         result = s;
      } else if(type != Boolean.TYPE && type != Boolean.class) {
         if(type != Byte.TYPE && type != Byte.class) {
            if(type != Short.TYPE && type != Short.class) {
               if(type != Character.TYPE && type != Character.class) {
                  if(type != Integer.TYPE && type != Integer.class) {
                     if(type != Long.TYPE && type != Long.class) {
                        if(type != Float.TYPE && type != Float.class) {
                           if(type != Double.TYPE && type != Double.class) {
                              if(Pointer.class.isAssignableFrom(type)) {
                                 Pointer p = this.getPointer(offset);
                                 if(p != null) {
                                    Pointer oldp = currentValue instanceof Pointer?(Pointer)currentValue:null;
                                    if(oldp != null && p.peer == oldp.peer) {
                                       result = oldp;
                                    } else {
                                       result = p;
                                    }
                                 }
                              } else if(type == String.class) {
                                 Pointer p = this.getPointer(offset);
                                 result = p != null?p.getString(0L):null;
                              } else if(type == WString.class) {
                                 Pointer p = this.getPointer(offset);
                                 result = p != null?new WString(p.getString(0L, true)):null;
                              } else if(Callback.class.isAssignableFrom(type)) {
                                 Pointer fp = this.getPointer(offset);
                                 if(fp == null) {
                                    result = null;
                                 } else {
                                    Callback cb = (Callback)currentValue;
                                    Pointer oldfp = CallbackReference.getFunctionPointer(cb);
                                    if(!fp.equals(oldfp)) {
                                       cb = CallbackReference.getCallback(type, fp);
                                    }

                                    result = cb;
                                 }
                              } else if(Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type)) {
                                 Pointer bp = this.getPointer(offset);
                                 if(bp == null) {
                                    result = null;
                                 } else {
                                    Pointer oldbp = currentValue == null?null:Native.getDirectBufferPointer((Buffer)currentValue);
                                    if(oldbp == null || !oldbp.equals(bp)) {
                                       throw new IllegalStateException("Can\'t autogenerate a direct buffer on memory read");
                                    }

                                    result = currentValue;
                                 }
                              } else if(NativeMapped.class.isAssignableFrom(type)) {
                                 NativeMapped nm = (NativeMapped)currentValue;
                                 if(nm != null) {
                                    Object value = this.getValue(offset, nm.nativeType(), (Object)null);
                                    result = nm.fromNative(value, new FromNativeContext(type));
                                 } else {
                                    NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
                                    Object value = this.getValue(offset, tc.nativeType(), (Object)null);
                                    result = tc.fromNative(value, new FromNativeContext(type));
                                 }
                              } else {
                                 if(!type.isArray()) {
                                    throw new IllegalArgumentException("Reading \"" + type + "\" from memory is not supported");
                                 }

                                 result = currentValue;
                                 if(currentValue == null) {
                                    throw new IllegalStateException("Need an initialized array");
                                 }

                                 this.getArrayValue(offset, currentValue, type.getComponentType());
                              }
                           } else {
                              result = new Double(this.getDouble(offset));
                           }
                        } else {
                           result = new Float(this.getFloat(offset));
                        }
                     } else {
                        result = new Long(this.getLong(offset));
                     }
                  } else {
                     result = new Integer(this.getInt(offset));
                  }
               } else {
                  result = new Character(this.getChar(offset));
               }
            } else {
               result = new Short(this.getShort(offset));
            }
         } else {
            result = new Byte(this.getByte(offset));
         }
      } else {
         result = Function.valueOf(this.getInt(offset) != 0);
      }

      return result;
   }

   private void getArrayValue(long offset, Object o, Class cls) {
      int length = 0;
      length = Array.getLength(o);
      if(cls == Byte.TYPE) {
         this.read(offset, (byte[])((byte[])((byte[])o)), 0, length);
      } else if(cls == Short.TYPE) {
         this.read(offset, (short[])((short[])((short[])o)), 0, length);
      } else if(cls == Character.TYPE) {
         this.read(offset, (char[])((char[])((char[])o)), 0, length);
      } else if(cls == Integer.TYPE) {
         this.read(offset, (int[])((int[])((int[])o)), 0, length);
      } else if(cls == Long.TYPE) {
         this.read(offset, (long[])((long[])((long[])o)), 0, length);
      } else if(cls == Float.TYPE) {
         this.read(offset, (float[])((float[])((float[])o)), 0, length);
      } else if(cls == Double.TYPE) {
         this.read(offset, (double[])((double[])((double[])o)), 0, length);
      } else if(Pointer.class.isAssignableFrom(cls)) {
         this.read(offset, (Pointer[])((Pointer[])((Pointer[])o)), 0, length);
      } else if(Structure.class.isAssignableFrom(cls)) {
         Structure[] sarray = (Structure[])((Structure[])o);
         if(Structure.ByReference.class.isAssignableFrom(cls)) {
            Pointer[] parray = this.getPointerArray(offset, sarray.length);

            for(int i = 0; i < sarray.length; ++i) {
               sarray[i] = Structure.updateStructureByReference(cls, sarray[i], parray[i]);
            }
         } else {
            for(int i = 0; i < sarray.length; ++i) {
               if(sarray[i] == null) {
                  sarray[i] = Structure.newInstance(cls);
               }

               sarray[i].useMemory(this, (int)(offset + (long)(i * sarray[i].size())));
               sarray[i].read();
            }
         }
      } else {
         if(!NativeMapped.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Reading array of " + cls + " from memory not supported");
         }

         NativeMapped[] array = (NativeMapped[])((NativeMapped[])o);
         NativeMappedConverter tc = NativeMappedConverter.getInstance(cls);
         int size = Native.getNativeSize(o.getClass(), o) / array.length;

         for(int i = 0; i < array.length; ++i) {
            Object value = this.getValue(offset + (long)(size * i), tc.nativeType(), array[i]);
            array[i] = (NativeMapped)tc.fromNative(value, new FromNativeContext(cls));
         }
      }

   }

   public byte getByte(long offset) {
      return Native.getByte(this.peer + offset);
   }

   public char getChar(long offset) {
      return Native.getChar(this.peer + offset);
   }

   public short getShort(long offset) {
      return Native.getShort(this.peer + offset);
   }

   public int getInt(long offset) {
      return Native.getInt(this.peer + offset);
   }

   public long getLong(long offset) {
      return Native.getLong(this.peer + offset);
   }

   public NativeLong getNativeLong(long offset) {
      return new NativeLong(NativeLong.SIZE == 8?this.getLong(offset):(long)this.getInt(offset));
   }

   public float getFloat(long offset) {
      return Native.getFloat(this.peer + offset);
   }

   public double getDouble(long offset) {
      return Native.getDouble(this.peer + offset);
   }

   public Pointer getPointer(long offset) {
      return Native.getPointer(this.peer + offset);
   }

   public ByteBuffer getByteBuffer(long offset, long length) {
      return Native.getDirectByteBuffer(this.peer + offset, length).order(ByteOrder.nativeOrder());
   }

   public String getString(long offset, boolean wide) {
      return Native.getString(this.peer + offset, wide);
   }

   public String getString(long offset) {
      String encoding = System.getProperty("jna.encoding");
      if(encoding != null) {
         long len = this.indexOf(offset, (byte)0);
         if(len != -1L) {
            if(len > 2147483647L) {
               throw new OutOfMemoryError("String exceeds maximum length: " + len);
            }

            byte[] data = this.getByteArray(offset, (int)len);

            try {
               return new String(data, encoding);
            } catch (UnsupportedEncodingException var8) {
               ;
            }
         }
      }

      return this.getString(offset, false);
   }

   public byte[] getByteArray(long offset, int arraySize) {
      byte[] buf = new byte[arraySize];
      this.read(offset, (byte[])buf, 0, arraySize);
      return buf;
   }

   public char[] getCharArray(long offset, int arraySize) {
      char[] buf = new char[arraySize];
      this.read(offset, (char[])buf, 0, arraySize);
      return buf;
   }

   public short[] getShortArray(long offset, int arraySize) {
      short[] buf = new short[arraySize];
      this.read(offset, (short[])buf, 0, arraySize);
      return buf;
   }

   public int[] getIntArray(long offset, int arraySize) {
      int[] buf = new int[arraySize];
      this.read(offset, (int[])buf, 0, arraySize);
      return buf;
   }

   public long[] getLongArray(long offset, int arraySize) {
      long[] buf = new long[arraySize];
      this.read(offset, (long[])buf, 0, arraySize);
      return buf;
   }

   public float[] getFloatArray(long offset, int arraySize) {
      float[] buf = new float[arraySize];
      this.read(offset, (float[])buf, 0, arraySize);
      return buf;
   }

   public double[] getDoubleArray(long offset, int arraySize) {
      double[] buf = new double[arraySize];
      this.read(offset, (double[])buf, 0, arraySize);
      return buf;
   }

   public Pointer[] getPointerArray(long offset) {
      List array = new ArrayList();
      int addOffset = 0;

      for(Pointer p = this.getPointer(offset); p != null; p = this.getPointer(offset + (long)addOffset)) {
         array.add(p);
         addOffset += SIZE;
      }

      return (Pointer[])((Pointer[])array.toArray(new Pointer[array.size()]));
   }

   public Pointer[] getPointerArray(long offset, int arraySize) {
      Pointer[] buf = new Pointer[arraySize];
      this.read(offset, (Pointer[])buf, 0, arraySize);
      return buf;
   }

   public String[] getStringArray(long offset) {
      return this.getStringArray(offset, -1, false);
   }

   public String[] getStringArray(long offset, int length) {
      return this.getStringArray(offset, length, false);
   }

   public String[] getStringArray(long offset, boolean wide) {
      return this.getStringArray(offset, -1, wide);
   }

   public String[] getStringArray(long offset, int length, boolean wide) {
      List strings = new ArrayList();
      int addOffset = 0;
      Pointer p;
      if(length != -1) {
         p = this.getPointer(offset + (long)addOffset);
         int count = 0;

         while(count++ < length) {
            String s = p == null?null:p.getString(0L, wide);
            strings.add(s);
            if(count < length) {
               addOffset += SIZE;
               p = this.getPointer(offset + (long)addOffset);
            }
         }
      } else {
         while((p = this.getPointer(offset + (long)addOffset)) != null) {
            String s = p == null?null:p.getString(0L, wide);
            strings.add(s);
            addOffset += SIZE;
         }
      }

      return (String[])((String[])strings.toArray(new String[strings.size()]));
   }

   void setValue(long offset, Object value, Class type) {
      if(type != Boolean.TYPE && type != Boolean.class) {
         if(type != Byte.TYPE && type != Byte.class) {
            if(type != Short.TYPE && type != Short.class) {
               if(type != Character.TYPE && type != Character.class) {
                  if(type != Integer.TYPE && type != Integer.class) {
                     if(type != Long.TYPE && type != Long.class) {
                        if(type != Float.TYPE && type != Float.class) {
                           if(type != Double.TYPE && type != Double.class) {
                              if(type == Pointer.class) {
                                 this.setPointer(offset, (Pointer)value);
                              } else if(type == String.class) {
                                 this.setPointer(offset, (Pointer)value);
                              } else if(type == WString.class) {
                                 this.setPointer(offset, (Pointer)value);
                              } else if(Structure.class.isAssignableFrom(type)) {
                                 Structure s = (Structure)value;
                                 if(Structure.ByReference.class.isAssignableFrom(type)) {
                                    this.setPointer(offset, s == null?null:s.getPointer());
                                    if(s != null) {
                                       s.autoWrite();
                                    }
                                 } else {
                                    s.useMemory(this, (int)offset);
                                    s.write();
                                 }
                              } else if(Callback.class.isAssignableFrom(type)) {
                                 this.setPointer(offset, CallbackReference.getFunctionPointer((Callback)value));
                              } else if(Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type)) {
                                 Pointer p = value == null?null:Native.getDirectBufferPointer((Buffer)value);
                                 this.setPointer(offset, p);
                              } else if(NativeMapped.class.isAssignableFrom(type)) {
                                 NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
                                 Class nativeType = tc.nativeType();
                                 this.setValue(offset, tc.toNative(value, new ToNativeContext()), nativeType);
                              } else {
                                 if(!type.isArray()) {
                                    throw new IllegalArgumentException("Writing " + type + " to memory is not supported");
                                 }

                                 this.setArrayValue(offset, value, type.getComponentType());
                              }
                           } else {
                              this.setDouble(offset, value == null?0.0D:((Double)value).doubleValue());
                           }
                        } else {
                           this.setFloat(offset, value == null?0.0F:((Float)value).floatValue());
                        }
                     } else {
                        this.setLong(offset, value == null?0L:((Long)value).longValue());
                     }
                  } else {
                     this.setInt(offset, value == null?0:((Integer)value).intValue());
                  }
               } else {
                  this.setChar(offset, value == null?'\u0000':((Character)value).charValue());
               }
            } else {
               this.setShort(offset, value == null?0:((Short)value).shortValue());
            }
         } else {
            this.setByte(offset, value == null?0:((Byte)value).byteValue());
         }
      } else {
         this.setInt(offset, Boolean.TRUE.equals(value)?-1:0);
      }

   }

   private void setArrayValue(long offset, Object value, Class cls) {
      if(cls == Byte.TYPE) {
         byte[] buf = (byte[])((byte[])value);
         this.write(offset, (byte[])buf, 0, buf.length);
      } else if(cls == Short.TYPE) {
         short[] buf = (short[])((short[])value);
         this.write(offset, (short[])buf, 0, buf.length);
      } else if(cls == Character.TYPE) {
         char[] buf = (char[])((char[])value);
         this.write(offset, (char[])buf, 0, buf.length);
      } else if(cls == Integer.TYPE) {
         int[] buf = (int[])((int[])value);
         this.write(offset, (int[])buf, 0, buf.length);
      } else if(cls == Long.TYPE) {
         long[] buf = (long[])((long[])value);
         this.write(offset, (long[])buf, 0, buf.length);
      } else if(cls == Float.TYPE) {
         float[] buf = (float[])((float[])value);
         this.write(offset, (float[])buf, 0, buf.length);
      } else if(cls == Double.TYPE) {
         double[] buf = (double[])((double[])value);
         this.write(offset, (double[])buf, 0, buf.length);
      } else if(Pointer.class.isAssignableFrom(cls)) {
         Pointer[] buf = (Pointer[])((Pointer[])value);
         this.write(offset, (Pointer[])buf, 0, buf.length);
      } else if(Structure.class.isAssignableFrom(cls)) {
         Structure[] sbuf = (Structure[])((Structure[])value);
         if(Structure.ByReference.class.isAssignableFrom(cls)) {
            Pointer[] buf = new Pointer[sbuf.length];

            for(int i = 0; i < sbuf.length; ++i) {
               if(sbuf[i] == null) {
                  buf[i] = null;
               } else {
                  buf[i] = sbuf[i].getPointer();
                  sbuf[i].write();
               }
            }

            this.write(offset, (Pointer[])buf, 0, buf.length);
         } else {
            for(int i = 0; i < sbuf.length; ++i) {
               if(sbuf[i] == null) {
                  sbuf[i] = Structure.newInstance(cls);
               }

               sbuf[i].useMemory(this, (int)(offset + (long)(i * sbuf[i].size())));
               sbuf[i].write();
            }
         }
      } else {
         if(!NativeMapped.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Writing array of " + cls + " to memory not supported");
         }

         NativeMapped[] buf = (NativeMapped[])((NativeMapped[])value);
         NativeMappedConverter tc = NativeMappedConverter.getInstance(cls);
         Class nativeType = tc.nativeType();
         int size = Native.getNativeSize(value.getClass(), value) / buf.length;

         for(int i = 0; i < buf.length; ++i) {
            Object element = tc.toNative(buf[i], new ToNativeContext());
            this.setValue(offset + (long)(i * size), element, nativeType);
         }
      }

   }

   public void setMemory(long offset, long length, byte value) {
      Native.setMemory(this.peer + offset, length, value);
   }

   public void setByte(long offset, byte value) {
      Native.setByte(this.peer + offset, value);
   }

   public void setShort(long offset, short value) {
      Native.setShort(this.peer + offset, value);
   }

   public void setChar(long offset, char value) {
      Native.setChar(this.peer + offset, value);
   }

   public void setInt(long offset, int value) {
      Native.setInt(this.peer + offset, value);
   }

   public void setLong(long offset, long value) {
      Native.setLong(this.peer + offset, value);
   }

   public void setNativeLong(long offset, NativeLong value) {
      if(NativeLong.SIZE == 8) {
         this.setLong(offset, value.longValue());
      } else {
         this.setInt(offset, value.intValue());
      }

   }

   public void setFloat(long offset, float value) {
      Native.setFloat(this.peer + offset, value);
   }

   public void setDouble(long offset, double value) {
      Native.setDouble(this.peer + offset, value);
   }

   public void setPointer(long offset, Pointer value) {
      Native.setPointer(this.peer + offset, value != null?value.peer:0L);
   }

   public void setString(long offset, String value, boolean wide) {
      Native.setString(this.peer + offset, value, wide);
   }

   public void setString(long offset, String value) {
      byte[] data = Native.getBytes(value);
      this.write(offset, (byte[])data, 0, data.length);
      this.setByte(offset + (long)data.length, (byte)0);
   }

   public String toString() {
      return "native@0x" + Long.toHexString(this.peer);
   }

   public static long nativeValue(Pointer p) {
      return p.peer;
   }

   public static void nativeValue(Pointer p, long value) {
      p.peer = value;
   }

   static {
      if((SIZE = Native.POINTER_SIZE) == 0) {
         throw new Error("Native library not initialized");
      } else {
         NULL = null;
      }
   }

   private static class Opaque extends Pointer {
      private final String MSG;

      private Opaque(long peer) {
         super(peer);
         this.MSG = "This pointer is opaque: " + this;
      }

      public long indexOf(long offset, byte value) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void read(long bOff, byte[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void read(long bOff, char[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void read(long bOff, short[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void read(long bOff, int[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void read(long bOff, long[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void read(long bOff, float[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void read(long bOff, double[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void write(long bOff, byte[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void write(long bOff, char[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void write(long bOff, short[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void write(long bOff, int[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void write(long bOff, long[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void write(long bOff, float[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void write(long bOff, double[] buf, int index, int length) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public byte getByte(long bOff) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public char getChar(long bOff) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public short getShort(long bOff) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public int getInt(long bOff) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public long getLong(long bOff) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public float getFloat(long bOff) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public double getDouble(long bOff) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public Pointer getPointer(long bOff) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public String getString(long bOff, boolean wide) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void setByte(long bOff, byte value) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void setChar(long bOff, char value) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void setShort(long bOff, short value) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void setInt(long bOff, int value) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void setLong(long bOff, long value) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void setFloat(long bOff, float value) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void setDouble(long bOff, double value) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void setPointer(long offset, Pointer value) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public void setString(long offset, String value, boolean wide) {
         throw new UnsupportedOperationException(this.MSG);
      }

      public String toString() {
         return "opaque@0x" + Long.toHexString(this.peer);
      }
   }
}
