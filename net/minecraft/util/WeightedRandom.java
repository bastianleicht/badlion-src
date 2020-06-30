package net.minecraft.util;

import java.util.Collection;
import java.util.Random;

public class WeightedRandom {
   public static int getTotalWeight(Collection collection) {
      int i = 0;

      for(WeightedRandom.Item weightedrandom$item : collection) {
         i += weightedrandom$item.itemWeight;
      }

      return i;
   }

   public static WeightedRandom.Item getRandomItem(Random random, Collection collection, int totalWeight) {
      if(totalWeight <= 0) {
         throw new IllegalArgumentException();
      } else {
         int i = random.nextInt(totalWeight);
         return getRandomItem(collection, i);
      }
   }

   public static WeightedRandom.Item getRandomItem(Collection collection, int weight) {
      for(T t : collection) {
         weight -= t.itemWeight;
         if(weight < 0) {
            return t;
         }
      }

      return null;
   }

   public static WeightedRandom.Item getRandomItem(Random random, Collection collection) {
      return getRandomItem(random, collection, getTotalWeight(collection));
   }

   public static class Item {
      protected int itemWeight;

      public Item(int itemWeightIn) {
         this.itemWeight = itemWeightIn;
      }
   }
}
