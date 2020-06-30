package org.apache.logging.log4j.core.helpers;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class UUIDUtil {
   public static final String UUID_SEQUENCE = "org.apache.logging.log4j.uuidSequence";
   private static final String ASSIGNED_SEQUENCES = "org.apache.logging.log4j.assignedSequences";
   private static AtomicInteger count = new AtomicInteger(0);
   private static final long TYPE1 = 4096L;
   private static final byte VARIANT = -128;
   private static final int SEQUENCE_MASK = 16383;
   private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 122192928000000000L;
   private static long uuidSequence = PropertiesUtil.getProperties().getLongProperty("org.apache.logging.log4j.uuidSequence", 0L);
   private static long least;
   private static final long LOW_MASK = 4294967295L;
   private static final long MID_MASK = 281470681743360L;
   private static final long HIGH_MASK = 1152640029630136320L;
   private static final int NODE_SIZE = 8;
   private static final int SHIFT_2 = 16;
   private static final int SHIFT_4 = 32;
   private static final int SHIFT_6 = 48;
   private static final int HUNDRED_NANOS_PER_MILLI = 10000;

   public static UUID getTimeBasedUUID() {
      long time = System.currentTimeMillis() * 10000L + 122192928000000000L + (long)(count.incrementAndGet() % 10000);
      long timeLow = (time & 4294967295L) << 32;
      long timeMid = (time & 281470681743360L) >> 16;
      long timeHi = (time & 1152640029630136320L) >> 48;
      long most = timeLow | timeMid | 4096L | timeHi;
      return new UUID(most, least);
   }

   static {
      byte[] mac = null;

      try {
         InetAddress address = InetAddress.getLocalHost();

         try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            if(ni != null && !ni.isLoopback() && ni.isUp()) {
               Method method = ni.getClass().getMethod("getHardwareAddress", new Class[0]);
               if(method != null) {
                  mac = (byte[])((byte[])method.invoke(ni, new Object[0]));
               }
            }

            if(mac == null) {
               Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();

               while(enumeration.hasMoreElements() && mac == null) {
                  ni = (NetworkInterface)enumeration.nextElement();
                  if(ni != null && ni.isUp() && !ni.isLoopback()) {
                     Method method = ni.getClass().getMethod("getHardwareAddress", new Class[0]);
                     if(method != null) {
                        mac = (byte[])((byte[])method.invoke(ni, new Object[0]));
                     }
                  }
               }
            }
         } catch (Exception var20) {
            var20.printStackTrace();
         }

         if(mac == null || mac.length == 0) {
            mac = address.getAddress();
         }
      } catch (UnknownHostException var21) {
         ;
      }

      Random randomGenerator = new SecureRandom();
      if(mac == null || mac.length == 0) {
         mac = new byte[6];
         randomGenerator.nextBytes(mac);
      }

      int length = mac.length >= 6?6:mac.length;
      int index = mac.length >= 6?mac.length - 6:0;
      byte[] node = new byte[8];
      node[0] = -128;
      node[1] = 0;

      for(int i = 2; i < 8; ++i) {
         node[i] = 0;
      }

      System.arraycopy(mac, index, node, index + 2, length);
      ByteBuffer buf = ByteBuffer.wrap(node);
      long rand = uuidSequence;
      Runtime runtime = Runtime.getRuntime();
      synchronized(runtime) {
         String assigned = PropertiesUtil.getProperties().getStringProperty("org.apache.logging.log4j.assignedSequences");
         long[] sequences;
         if(assigned == null) {
            sequences = new long[0];
         } else {
            String[] array = assigned.split(",");
            sequences = new long[array.length];
            int i = 0;

            for(String value : array) {
               sequences[i] = Long.parseLong(value);
               ++i;
            }
         }

         if(rand == 0L) {
            rand = randomGenerator.nextLong();
         }

         rand = rand & 16383L;

         while(true) {
            boolean duplicate = false;

            for(long sequence : sequences) {
               if(sequence == rand) {
                  duplicate = true;
                  break;
               }
            }

            if(duplicate) {
               rand = rand + 1L & 16383L;
            }

            if(!duplicate) {
               break;
            }
         }

         assigned = assigned == null?Long.toString(rand):assigned + "," + Long.toString(rand);
         System.setProperty("org.apache.logging.log4j.assignedSequences", assigned);
      }

      least = buf.getLong() | rand << 48;
   }
}
