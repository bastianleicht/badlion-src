package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import javax.annotation.Nullable;

@Beta
public final class Funnels {
   public static Funnel byteArrayFunnel() {
      return Funnels.ByteArrayFunnel.INSTANCE;
   }

   public static Funnel unencodedCharsFunnel() {
      return Funnels.UnencodedCharsFunnel.INSTANCE;
   }

   public static Funnel stringFunnel(Charset charset) {
      return new Funnels.StringCharsetFunnel(charset);
   }

   public static Funnel integerFunnel() {
      return Funnels.IntegerFunnel.INSTANCE;
   }

   public static Funnel sequentialFunnel(Funnel elementFunnel) {
      return new Funnels.SequentialFunnel(elementFunnel);
   }

   public static Funnel longFunnel() {
      return Funnels.LongFunnel.INSTANCE;
   }

   public static OutputStream asOutputStream(PrimitiveSink sink) {
      return new Funnels.SinkAsStream(sink);
   }

   private static enum ByteArrayFunnel implements Funnel {
      INSTANCE;

      public void funnel(byte[] from, PrimitiveSink into) {
         into.putBytes(from);
      }

      public String toString() {
         return "Funnels.byteArrayFunnel()";
      }
   }

   private static enum IntegerFunnel implements Funnel {
      INSTANCE;

      public void funnel(Integer from, PrimitiveSink into) {
         into.putInt(from.intValue());
      }

      public String toString() {
         return "Funnels.integerFunnel()";
      }
   }

   private static enum LongFunnel implements Funnel {
      INSTANCE;

      public void funnel(Long from, PrimitiveSink into) {
         into.putLong(from.longValue());
      }

      public String toString() {
         return "Funnels.longFunnel()";
      }
   }

   private static class SequentialFunnel implements Funnel, Serializable {
      private final Funnel elementFunnel;

      SequentialFunnel(Funnel elementFunnel) {
         this.elementFunnel = (Funnel)Preconditions.checkNotNull(elementFunnel);
      }

      public void funnel(Iterable from, PrimitiveSink into) {
         for(E e : from) {
            this.elementFunnel.funnel(e, into);
         }

      }

      public String toString() {
         return "Funnels.sequentialFunnel(" + this.elementFunnel + ")";
      }

      public boolean equals(@Nullable Object o) {
         if(o instanceof Funnels.SequentialFunnel) {
            Funnels.SequentialFunnel<?> funnel = (Funnels.SequentialFunnel)o;
            return this.elementFunnel.equals(funnel.elementFunnel);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Funnels.SequentialFunnel.class.hashCode() ^ this.elementFunnel.hashCode();
      }
   }

   private static class SinkAsStream extends OutputStream {
      final PrimitiveSink sink;

      SinkAsStream(PrimitiveSink sink) {
         this.sink = (PrimitiveSink)Preconditions.checkNotNull(sink);
      }

      public void write(int b) {
         this.sink.putByte((byte)b);
      }

      public void write(byte[] bytes) {
         this.sink.putBytes(bytes);
      }

      public void write(byte[] bytes, int off, int len) {
         this.sink.putBytes(bytes, off, len);
      }

      public String toString() {
         return "Funnels.asOutputStream(" + this.sink + ")";
      }
   }

   private static class StringCharsetFunnel implements Funnel, Serializable {
      private final Charset charset;

      StringCharsetFunnel(Charset charset) {
         this.charset = (Charset)Preconditions.checkNotNull(charset);
      }

      public void funnel(CharSequence from, PrimitiveSink into) {
         into.putString(from, this.charset);
      }

      public String toString() {
         return "Funnels.stringFunnel(" + this.charset.name() + ")";
      }

      public boolean equals(@Nullable Object o) {
         if(o instanceof Funnels.StringCharsetFunnel) {
            Funnels.StringCharsetFunnel funnel = (Funnels.StringCharsetFunnel)o;
            return this.charset.equals(funnel.charset);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Funnels.StringCharsetFunnel.class.hashCode() ^ this.charset.hashCode();
      }

      Object writeReplace() {
         return new Funnels.StringCharsetFunnel.SerializedForm(this.charset);
      }

      private static class SerializedForm implements Serializable {
         private final String charsetCanonicalName;
         private static final long serialVersionUID = 0L;

         SerializedForm(Charset charset) {
            this.charsetCanonicalName = charset.name();
         }

         private Object readResolve() {
            return Funnels.stringFunnel(Charset.forName(this.charsetCanonicalName));
         }
      }
   }

   private static enum UnencodedCharsFunnel implements Funnel {
      INSTANCE;

      public void funnel(CharSequence from, PrimitiveSink into) {
         into.putUnencodedChars(from);
      }

      public String toString() {
         return "Funnels.unencodedCharsFunnel()";
      }
   }
}
