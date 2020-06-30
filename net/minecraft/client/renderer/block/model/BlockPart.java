package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class BlockPart {
   public final Vector3f positionFrom;
   public final Vector3f positionTo;
   public final Map mapFaces;
   public final BlockPartRotation partRotation;
   public final boolean shade;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing;

   public BlockPart(Vector3f positionFromIn, Vector3f positionToIn, Map mapFacesIn, BlockPartRotation partRotationIn, boolean shadeIn) {
      this.positionFrom = positionFromIn;
      this.positionTo = positionToIn;
      this.mapFaces = mapFacesIn;
      this.partRotation = partRotationIn;
      this.shade = shadeIn;
      this.setDefaultUvs();
   }

   private void setDefaultUvs() {
      for(Entry<EnumFacing, BlockPartFace> entry : this.mapFaces.entrySet()) {
         float[] afloat = this.getFaceUvs((EnumFacing)entry.getKey());
         ((BlockPartFace)entry.getValue()).blockFaceUV.setUvs(afloat);
      }

   }

   private float[] getFaceUvs(EnumFacing p_178236_1_) {
      float[] afloat;
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[p_178236_1_.ordinal()]) {
      case 1:
      case 2:
         afloat = new float[]{this.positionFrom.x, this.positionFrom.z, this.positionTo.x, this.positionTo.z};
         break;
      case 3:
      case 4:
         afloat = new float[]{this.positionFrom.x, 16.0F - this.positionTo.y, this.positionTo.x, 16.0F - this.positionFrom.y};
         break;
      case 5:
      case 6:
         afloat = new float[]{this.positionFrom.z, 16.0F - this.positionTo.y, this.positionTo.z, 16.0F - this.positionFrom.y};
         break;
      default:
         throw new NullPointerException();
      }

      return afloat;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$util$EnumFacing;
      if($SWITCH_TABLE$net$minecraft$util$EnumFacing != null) {
         return var10000;
      } else {
         int[] var0 = new int[EnumFacing.values().length];

         try {
            var0[EnumFacing.DOWN.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[EnumFacing.EAST.ordinal()] = 6;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[EnumFacing.NORTH.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[EnumFacing.SOUTH.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[EnumFacing.UP.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[EnumFacing.WEST.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$util$EnumFacing = var0;
         return var0;
      }
   }

   static class Deserializer implements JsonDeserializer {
      public BlockPart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Vector3f vector3f = this.parsePositionFrom(jsonobject);
         Vector3f vector3f1 = this.parsePositionTo(jsonobject);
         BlockPartRotation blockpartrotation = this.parseRotation(jsonobject);
         Map<EnumFacing, BlockPartFace> map = this.parseFacesCheck(p_deserialize_3_, jsonobject);
         if(jsonobject.has("shade") && !JsonUtils.isBoolean(jsonobject, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         } else {
            boolean flag = JsonUtils.getBoolean(jsonobject, "shade", true);
            return new BlockPart(vector3f, vector3f1, map, blockpartrotation, flag);
         }
      }

      private BlockPartRotation parseRotation(JsonObject p_178256_1_) {
         BlockPartRotation blockpartrotation = null;
         if(p_178256_1_.has("rotation")) {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_178256_1_, "rotation");
            Vector3f vector3f = this.parsePosition(jsonobject, "origin");
            vector3f.scale(0.0625F);
            EnumFacing.Axis enumfacing$axis = this.parseAxis(jsonobject);
            float f = this.parseAngle(jsonobject);
            boolean flag = JsonUtils.getBoolean(jsonobject, "rescale", false);
            blockpartrotation = new BlockPartRotation(vector3f, enumfacing$axis, f, flag);
         }

         return blockpartrotation;
      }

      private float parseAngle(JsonObject p_178255_1_) {
         float f = JsonUtils.getFloat(p_178255_1_, "angle");
         if(f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return f;
         }
      }

      private EnumFacing.Axis parseAxis(JsonObject p_178252_1_) {
         String s = JsonUtils.getString(p_178252_1_, "axis");
         EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.byName(s.toLowerCase());
         if(enumfacing$axis == null) {
            throw new JsonParseException("Invalid rotation axis: " + s);
         } else {
            return enumfacing$axis;
         }
      }

      private Map parseFacesCheck(JsonDeserializationContext p_178250_1_, JsonObject p_178250_2_) {
         Map<EnumFacing, BlockPartFace> map = this.parseFaces(p_178250_1_, p_178250_2_);
         if(map.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return map;
         }
      }

      private Map parseFaces(JsonDeserializationContext p_178253_1_, JsonObject p_178253_2_) {
         Map<EnumFacing, BlockPartFace> map = Maps.newEnumMap(EnumFacing.class);
         JsonObject jsonobject = JsonUtils.getJsonObject(p_178253_2_, "faces");

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            EnumFacing enumfacing = this.parseEnumFacing((String)entry.getKey());
            map.put(enumfacing, (BlockPartFace)p_178253_1_.deserialize((JsonElement)entry.getValue(), BlockPartFace.class));
         }

         return map;
      }

      private EnumFacing parseEnumFacing(String name) {
         EnumFacing enumfacing = EnumFacing.byName(name);
         if(enumfacing == null) {
            throw new JsonParseException("Unknown facing: " + name);
         } else {
            return enumfacing;
         }
      }

      private Vector3f parsePositionTo(JsonObject p_178247_1_) {
         Vector3f vector3f = this.parsePosition(p_178247_1_, "to");
         if(vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
            return vector3f;
         } else {
            throw new JsonParseException("\'to\' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private Vector3f parsePositionFrom(JsonObject p_178249_1_) {
         Vector3f vector3f = this.parsePosition(p_178249_1_, "from");
         if(vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
            return vector3f;
         } else {
            throw new JsonParseException("\'from\' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private Vector3f parsePosition(JsonObject p_178251_1_, String p_178251_2_) {
         JsonArray jsonarray = JsonUtils.getJsonArray(p_178251_1_, p_178251_2_);
         if(jsonarray.size() != 3) {
            throw new JsonParseException("Expected 3 " + p_178251_2_ + " values, found: " + jsonarray.size());
         } else {
            float[] afloat = new float[3];

            for(int i = 0; i < afloat.length; ++i) {
               afloat[i] = JsonUtils.getFloat(jsonarray.get(i), p_178251_2_ + "[" + i + "]");
            }

            return new Vector3f(afloat[0], afloat[1], afloat[2]);
         }
      }
   }
}
