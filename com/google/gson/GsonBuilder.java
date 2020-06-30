package com.google.gson;

import com.google.gson.DefaultDateTypeAdapter;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.TreeTypeAdapter;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GsonBuilder {
   private Excluder excluder = Excluder.DEFAULT;
   private LongSerializationPolicy longSerializationPolicy = LongSerializationPolicy.DEFAULT;
   private FieldNamingStrategy fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
   private final Map instanceCreators = new HashMap();
   private final List factories = new ArrayList();
   private final List hierarchyFactories = new ArrayList();
   private boolean serializeNulls;
   private String datePattern;
   private int dateStyle = 2;
   private int timeStyle = 2;
   private boolean complexMapKeySerialization;
   private boolean serializeSpecialFloatingPointValues;
   private boolean escapeHtmlChars = true;
   private boolean prettyPrinting;
   private boolean generateNonExecutableJson;

   public GsonBuilder setVersion(double ignoreVersionsAfter) {
      this.excluder = this.excluder.withVersion(ignoreVersionsAfter);
      return this;
   }

   public GsonBuilder excludeFieldsWithModifiers(int... modifiers) {
      this.excluder = this.excluder.withModifiers(modifiers);
      return this;
   }

   public GsonBuilder generateNonExecutableJson() {
      this.generateNonExecutableJson = true;
      return this;
   }

   public GsonBuilder excludeFieldsWithoutExposeAnnotation() {
      this.excluder = this.excluder.excludeFieldsWithoutExposeAnnotation();
      return this;
   }

   public GsonBuilder serializeNulls() {
      this.serializeNulls = true;
      return this;
   }

   public GsonBuilder enableComplexMapKeySerialization() {
      this.complexMapKeySerialization = true;
      return this;
   }

   public GsonBuilder disableInnerClassSerialization() {
      this.excluder = this.excluder.disableInnerClassSerialization();
      return this;
   }

   public GsonBuilder setLongSerializationPolicy(LongSerializationPolicy serializationPolicy) {
      this.longSerializationPolicy = serializationPolicy;
      return this;
   }

   public GsonBuilder setFieldNamingPolicy(FieldNamingPolicy namingConvention) {
      this.fieldNamingPolicy = namingConvention;
      return this;
   }

   public GsonBuilder setFieldNamingStrategy(FieldNamingStrategy fieldNamingStrategy) {
      this.fieldNamingPolicy = fieldNamingStrategy;
      return this;
   }

   public GsonBuilder setExclusionStrategies(ExclusionStrategy... strategies) {
      for(ExclusionStrategy strategy : strategies) {
         this.excluder = this.excluder.withExclusionStrategy(strategy, true, true);
      }

      return this;
   }

   public GsonBuilder addSerializationExclusionStrategy(ExclusionStrategy strategy) {
      this.excluder = this.excluder.withExclusionStrategy(strategy, true, false);
      return this;
   }

   public GsonBuilder addDeserializationExclusionStrategy(ExclusionStrategy strategy) {
      this.excluder = this.excluder.withExclusionStrategy(strategy, false, true);
      return this;
   }

   public GsonBuilder setPrettyPrinting() {
      this.prettyPrinting = true;
      return this;
   }

   public GsonBuilder disableHtmlEscaping() {
      this.escapeHtmlChars = false;
      return this;
   }

   public GsonBuilder setDateFormat(String pattern) {
      this.datePattern = pattern;
      return this;
   }

   public GsonBuilder setDateFormat(int style) {
      this.dateStyle = style;
      this.datePattern = null;
      return this;
   }

   public GsonBuilder setDateFormat(int dateStyle, int timeStyle) {
      this.dateStyle = dateStyle;
      this.timeStyle = timeStyle;
      this.datePattern = null;
      return this;
   }

   public GsonBuilder registerTypeAdapter(Type type, Object typeAdapter) {
      $Gson$Preconditions.checkArgument(typeAdapter instanceof JsonSerializer || typeAdapter instanceof JsonDeserializer || typeAdapter instanceof InstanceCreator || typeAdapter instanceof TypeAdapter);
      if(typeAdapter instanceof InstanceCreator) {
         this.instanceCreators.put(type, (InstanceCreator)typeAdapter);
      }

      if(typeAdapter instanceof JsonSerializer || typeAdapter instanceof JsonDeserializer) {
         TypeToken<?> typeToken = TypeToken.get(type);
         this.factories.add(TreeTypeAdapter.newFactoryWithMatchRawType(typeToken, typeAdapter));
      }

      if(typeAdapter instanceof TypeAdapter) {
         this.factories.add(TypeAdapters.newFactory(TypeToken.get(type), (TypeAdapter)typeAdapter));
      }

      return this;
   }

   public GsonBuilder registerTypeAdapterFactory(TypeAdapterFactory factory) {
      this.factories.add(factory);
      return this;
   }

   public GsonBuilder registerTypeHierarchyAdapter(Class baseType, Object typeAdapter) {
      $Gson$Preconditions.checkArgument(typeAdapter instanceof JsonSerializer || typeAdapter instanceof JsonDeserializer || typeAdapter instanceof TypeAdapter);
      if(typeAdapter instanceof JsonDeserializer || typeAdapter instanceof JsonSerializer) {
         this.hierarchyFactories.add(0, TreeTypeAdapter.newTypeHierarchyFactory(baseType, typeAdapter));
      }

      if(typeAdapter instanceof TypeAdapter) {
         this.factories.add(TypeAdapters.newTypeHierarchyFactory(baseType, (TypeAdapter)typeAdapter));
      }

      return this;
   }

   public GsonBuilder serializeSpecialFloatingPointValues() {
      this.serializeSpecialFloatingPointValues = true;
      return this;
   }

   public Gson create() {
      List<TypeAdapterFactory> factories = new ArrayList();
      factories.addAll(this.factories);
      Collections.reverse(factories);
      factories.addAll(this.hierarchyFactories);
      this.addTypeAdaptersForDate(this.datePattern, this.dateStyle, this.timeStyle, factories);
      return new Gson(this.excluder, this.fieldNamingPolicy, this.instanceCreators, this.serializeNulls, this.complexMapKeySerialization, this.generateNonExecutableJson, this.escapeHtmlChars, this.prettyPrinting, this.serializeSpecialFloatingPointValues, this.longSerializationPolicy, factories);
   }

   private void addTypeAdaptersForDate(String datePattern, int dateStyle, int timeStyle, List factories) {
      DefaultDateTypeAdapter dateTypeAdapter;
      if(datePattern != null && !"".equals(datePattern.trim())) {
         dateTypeAdapter = new DefaultDateTypeAdapter(datePattern);
      } else {
         if(dateStyle == 2 || timeStyle == 2) {
            return;
         }

         dateTypeAdapter = new DefaultDateTypeAdapter(dateStyle, timeStyle);
      }

      factories.add(TreeTypeAdapter.newFactory(TypeToken.get(Date.class), dateTypeAdapter));
      factories.add(TreeTypeAdapter.newFactory(TypeToken.get(Timestamp.class), dateTypeAdapter));
      factories.add(TreeTypeAdapter.newFactory(TypeToken.get(java.sql.Date.class), dateTypeAdapter));
   }
}
