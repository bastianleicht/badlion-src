package org.lwjgl.openal;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EFX10;
import org.lwjgl.openal.OpenALException;

public final class EFXUtil {
   private static final int EFFECT = 1111;
   private static final int FILTER = 2222;

   public static boolean isEfxSupported() {
      if(!AL.isCreated()) {
         throw new OpenALException("OpenAL has not been created.");
      } else {
         return ALC10.alcIsExtensionPresent(AL.getDevice(), "ALC_EXT_EFX");
      }
   }

   public static boolean isEffectSupported(int effectType) {
      switch(effectType) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 32768:
         return testSupportGeneric(1111, effectType);
      default:
         throw new IllegalArgumentException("Unknown or invalid effect type: " + effectType);
      }
   }

   public static boolean isFilterSupported(int filterType) {
      switch(filterType) {
      case 0:
      case 1:
      case 2:
      case 3:
         return testSupportGeneric(2222, filterType);
      default:
         throw new IllegalArgumentException("Unknown or invalid filter type: " + filterType);
      }
   }

   private static boolean testSupportGeneric(int objectType, int typeValue) {
      switch(objectType) {
      case 1111:
      case 2222:
         boolean supported = false;
         if(isEfxSupported()) {
            AL10.alGetError();
            int testObject = 0;

            int genError;
            try {
               switch(objectType) {
               case 1111:
                  testObject = EFX10.alGenEffects();
                  break;
               case 2222:
                  testObject = EFX10.alGenFilters();
                  break;
               default:
                  throw new IllegalArgumentException("Invalid objectType: " + objectType);
               }

               genError = AL10.alGetError();
            } catch (OpenALException var9) {
               if(var9.getMessage().contains("AL_OUT_OF_MEMORY")) {
                  genError = 'ꀅ';
               } else {
                  genError = 'ꀄ';
               }
            }

            if(genError == 0) {
               AL10.alGetError();

               int setError;
               try {
                  switch(objectType) {
                  case 1111:
                     EFX10.alEffecti(testObject, '老', typeValue);
                     break;
                  case 2222:
                     EFX10.alFilteri(testObject, '老', typeValue);
                     break;
                  default:
                     throw new IllegalArgumentException("Invalid objectType: " + objectType);
                  }

                  setError = AL10.alGetError();
               } catch (OpenALException var8) {
                  setError = 'ꀃ';
               }

               if(setError == 0) {
                  supported = true;
               }

               try {
                  switch(objectType) {
                  case 1111:
                     EFX10.alDeleteEffects(testObject);
                     break;
                  case 2222:
                     EFX10.alDeleteFilters(testObject);
                     break;
                  default:
                     throw new IllegalArgumentException("Invalid objectType: " + objectType);
                  }
               } catch (OpenALException var7) {
                  ;
               }
            } else if(genError == 'ꀅ') {
               throw new OpenALException(genError);
            }
         }

         return supported;
      default:
         throw new IllegalArgumentException("Invalid objectType: " + objectType);
      }
   }
}
