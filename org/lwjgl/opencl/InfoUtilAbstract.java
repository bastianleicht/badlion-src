package org.lwjgl.opencl;

import java.nio.ByteBuffer;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.APIUtil;
import org.lwjgl.opencl.CLObject;
import org.lwjgl.opencl.InfoUtil;

abstract class InfoUtilAbstract implements InfoUtil {
   protected abstract int getInfo(CLObject var1, int var2, ByteBuffer var3, PointerBuffer var4);

   protected int getInfoSizeArraySize(CLObject object, int param_name) {
      throw new UnsupportedOperationException();
   }

   protected PointerBuffer getSizesBuffer(CLObject object, int param_name) {
      int size = this.getInfoSizeArraySize(object, param_name);
      PointerBuffer buffer = APIUtil.getBufferPointer(size);
      buffer.limit(size);
      this.getInfo(object, param_name, buffer.getBuffer(), (PointerBuffer)null);
      return buffer;
   }

   public int getInfoInt(CLObject object, int param_name) {
      object.checkValid();
      ByteBuffer buffer = APIUtil.getBufferByte(4);
      this.getInfo(object, param_name, buffer, (PointerBuffer)null);
      return buffer.getInt(0);
   }

   public long getInfoSize(CLObject object, int param_name) {
      object.checkValid();
      PointerBuffer buffer = APIUtil.getBufferPointer();
      this.getInfo(object, param_name, buffer.getBuffer(), (PointerBuffer)null);
      return buffer.get(0);
   }

   public long[] getInfoSizeArray(CLObject object, int param_name) {
      object.checkValid();
      int size = this.getInfoSizeArraySize(object, param_name);
      PointerBuffer buffer = APIUtil.getBufferPointer(size);
      this.getInfo(object, param_name, buffer.getBuffer(), (PointerBuffer)null);
      long[] array = new long[size];

      for(int i = 0; i < size; ++i) {
         array[i] = buffer.get(i);
      }

      return array;
   }

   public long getInfoLong(CLObject object, int param_name) {
      object.checkValid();
      ByteBuffer buffer = APIUtil.getBufferByte(8);
      this.getInfo(object, param_name, buffer, (PointerBuffer)null);
      return buffer.getLong(0);
   }

   public String getInfoString(CLObject object, int param_name) {
      object.checkValid();
      int bytes = this.getSizeRet(object, param_name);
      if(bytes <= 1) {
         return null;
      } else {
         ByteBuffer buffer = APIUtil.getBufferByte(bytes);
         this.getInfo(object, param_name, buffer, (PointerBuffer)null);
         buffer.limit(bytes - 1);
         return APIUtil.getString(buffer);
      }
   }

   protected final int getSizeRet(CLObject object, int param_name) {
      PointerBuffer bytes = APIUtil.getBufferPointer();
      int errcode = this.getInfo(object, param_name, (ByteBuffer)null, bytes);
      if(errcode != 0) {
         throw new IllegalArgumentException("Invalid parameter specified: " + LWJGLUtil.toHexString(param_name));
      } else {
         return (int)bytes.get(0);
      }
   }
}
