package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.win32.StdCallLibrary;

public interface Sspi extends StdCallLibrary {
   int MAX_TOKEN_SIZE = 12288;
   int SECPKG_CRED_INBOUND = 1;
   int SECPKG_CRED_OUTBOUND = 2;
   int SECURITY_NATIVE_DREP = 16;
   int ISC_REQ_ALLOCATE_MEMORY = 256;
   int ISC_REQ_CONFIDENTIALITY = 16;
   int ISC_REQ_CONNECTION = 2048;
   int ISC_REQ_DELEGATE = 1;
   int ISC_REQ_EXTENDED_ERROR = 16384;
   int ISC_REQ_INTEGRITY = 65536;
   int ISC_REQ_MUTUAL_AUTH = 2;
   int ISC_REQ_REPLAY_DETECT = 4;
   int ISC_REQ_SEQUENCE_DETECT = 8;
   int ISC_REQ_STREAM = 32768;
   int SECBUFFER_VERSION = 0;
   int SECBUFFER_EMPTY = 0;
   int SECBUFFER_DATA = 1;
   int SECBUFFER_TOKEN = 2;

   public static class CredHandle extends Sspi.SecHandle {
   }

   public static class CtxtHandle extends Sspi.SecHandle {
   }

   public static class PSecHandle extends Structure {
      public Sspi.SecHandle.ByReference secHandle;

      public PSecHandle() {
      }

      public PSecHandle(Sspi.SecHandle h) {
         super(h.getPointer());
         this.read();
      }

      public static class ByReference extends Sspi.PSecHandle implements Structure.ByReference {
      }
   }

   public static class PSecPkgInfo extends Structure {
      public Sspi.SecPkgInfo.ByReference pPkgInfo;

      public Sspi.SecPkgInfo.ByReference[] toArray(int size) {
         return (Sspi.SecPkgInfo.ByReference[])((Sspi.SecPkgInfo.ByReference[])this.pPkgInfo.toArray(size));
      }

      public static class ByReference extends Sspi.PSecPkgInfo implements Structure.ByReference {
      }
   }

   public static class SECURITY_INTEGER extends Structure {
      public NativeLong dwLower = new NativeLong(0L);
      public NativeLong dwUpper = new NativeLong(0L);
   }

   public static class SecBuffer extends Structure {
      public NativeLong cbBuffer;
      public NativeLong BufferType;
      public Pointer pvBuffer;

      public SecBuffer() {
         this.cbBuffer = new NativeLong(0L);
         this.pvBuffer = null;
         this.BufferType = new NativeLong(0L);
      }

      public SecBuffer(int type, int size) {
         this.cbBuffer = new NativeLong((long)size);
         this.pvBuffer = new Memory((long)size);
         this.BufferType = new NativeLong((long)type);
         this.allocateMemory();
      }

      public SecBuffer(int type, byte[] token) {
         this.cbBuffer = new NativeLong((long)token.length);
         this.pvBuffer = new Memory((long)token.length);
         this.pvBuffer.write(0L, (byte[])token, 0, token.length);
         this.BufferType = new NativeLong((long)type);
         this.allocateMemory();
      }

      public byte[] getBytes() {
         return this.pvBuffer.getByteArray(0L, this.cbBuffer.intValue());
      }

      public static class ByReference extends Sspi.SecBuffer implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(int type, int size) {
            super(type, size);
         }

         public ByReference(int type, byte[] token) {
            super(type, token);
         }

         public byte[] getBytes() {
            return super.getBytes();
         }
      }
   }

   public static class SecBufferDesc extends Structure {
      public NativeLong ulVersion = new NativeLong(0L);
      public NativeLong cBuffers = new NativeLong(1L);
      public Sspi.SecBuffer.ByReference[] pBuffers;

      public SecBufferDesc() {
         Sspi.SecBuffer.ByReference secBuffer = new Sspi.SecBuffer.ByReference();
         this.pBuffers = (Sspi.SecBuffer.ByReference[])((Sspi.SecBuffer.ByReference[])secBuffer.toArray(1));
         this.allocateMemory();
      }

      public SecBufferDesc(int type, byte[] token) {
         Sspi.SecBuffer.ByReference secBuffer = new Sspi.SecBuffer.ByReference(type, token);
         this.pBuffers = (Sspi.SecBuffer.ByReference[])((Sspi.SecBuffer.ByReference[])secBuffer.toArray(1));
         this.allocateMemory();
      }

      public SecBufferDesc(int type, int tokenSize) {
         Sspi.SecBuffer.ByReference secBuffer = new Sspi.SecBuffer.ByReference(type, tokenSize);
         this.pBuffers = (Sspi.SecBuffer.ByReference[])((Sspi.SecBuffer.ByReference[])secBuffer.toArray(1));
         this.allocateMemory();
      }

      public byte[] getBytes() {
         if(this.pBuffers != null && this.cBuffers != null) {
            if(this.cBuffers.intValue() == 1) {
               return this.pBuffers[0].getBytes();
            } else {
               throw new RuntimeException("cBuffers > 1");
            }
         } else {
            throw new RuntimeException("pBuffers | cBuffers");
         }
      }
   }

   public static class SecHandle extends Structure {
      public Pointer dwLower = null;
      public Pointer dwUpper = null;

      public boolean isNull() {
         return this.dwLower == null && this.dwUpper == null;
      }

      public static class ByReference extends Sspi.SecHandle implements Structure.ByReference {
      }
   }

   public static class SecPkgInfo extends Structure {
      public NativeLong fCapabilities = new NativeLong(0L);
      public short wVersion = 1;
      public short wRPCID = 0;
      public NativeLong cbMaxToken = new NativeLong(0L);
      public WString Name;
      public WString Comment;

      public static class ByReference extends Sspi.SecPkgInfo implements Structure.ByReference {
      }
   }

   public static class TimeStamp extends Sspi.SECURITY_INTEGER {
   }
}
