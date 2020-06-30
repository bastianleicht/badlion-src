package net.badlion.client.mods.misc;

import net.minecraft.client.Minecraft;

public class LegacyAnimations {
   private LegacyAnimations.AnimationMode damageAnimation = LegacyAnimations.AnimationMode.LEGACY;
   private LegacyAnimations.AnimationMode heartAnimation = LegacyAnimations.AnimationMode.LEGACY;
   private LegacyAnimations.AnimationMode enchantGlint = LegacyAnimations.AnimationMode.LEGACY;
   private LegacyAnimations.AnimationMode blockHit = LegacyAnimations.AnimationMode.LEGACY;
   private LegacyAnimations.AnimationMode itemHeld = LegacyAnimations.AnimationMode.LEGACY;
   private LegacyAnimations.AnimationMode sky = LegacyAnimations.AnimationMode.LEGACY;
   private LegacyAnimations.AnimationMode sneakingAnimation = LegacyAnimations.AnimationMode.LEGACY;
   private float eyeHeight = 1.62F;
   private float targetEyeHeight = 1.62F;

   public void setTargetEyeHeight(float targetEyeHeight) {
      this.targetEyeHeight = targetEyeHeight;
   }

   public float getEyeHeight() {
      return this.eyeHeight;
   }

   public void update() {
      if(this.sneakingAnimation.equals(LegacyAnimations.AnimationMode.LEGACY) && (double)Math.abs(this.eyeHeight - this.targetEyeHeight) > 0.005D && Math.abs(this.eyeHeight - this.targetEyeHeight) < 0.4F) {
         if(this.eyeHeight > this.targetEyeHeight) {
            this.eyeHeight = (float)((double)this.eyeHeight - (double)((this.eyeHeight - this.targetEyeHeight) / 2.3F) / Math.max(0.5D, (double)Minecraft.getDebugFPS() / 110.0D));
         } else {
            this.eyeHeight = (float)((double)this.eyeHeight + (double)((this.targetEyeHeight - this.eyeHeight) / 5.0F) / Math.max(0.5D, (double)Minecraft.getDebugFPS() / 110.0D));
         }
      } else {
         this.eyeHeight = this.targetEyeHeight;
      }

   }

   public LegacyAnimations.AnimationMode getHeartAnimation() {
      return this.heartAnimation;
   }

   public void setHeartAnimation(LegacyAnimations.AnimationMode heartAnimation) {
      this.heartAnimation = heartAnimation;
   }

   public LegacyAnimations.AnimationMode getDamageAnimation() {
      return this.damageAnimation;
   }

   public void setDamageAnimation(LegacyAnimations.AnimationMode damageAnimation) {
      this.damageAnimation = damageAnimation;
   }

   public LegacyAnimations.AnimationMode getItemHeld() {
      return this.itemHeld;
   }

   public LegacyAnimations.AnimationMode getBlockHit() {
      return this.blockHit;
   }

   public LegacyAnimations.AnimationMode getEnchantGlint() {
      return this.enchantGlint;
   }

   public void setItemHeld(LegacyAnimations.AnimationMode itemHeld) {
      this.itemHeld = itemHeld;
   }

   public void setBlockHit(LegacyAnimations.AnimationMode blockHit) {
      this.blockHit = blockHit;
   }

   public void setEnchantGlint(LegacyAnimations.AnimationMode enchantGlint) {
      this.enchantGlint = enchantGlint;
   }

   public LegacyAnimations.AnimationMode getSneakingAnimation() {
      return this.sneakingAnimation;
   }

   public void setSneakingAnimation(LegacyAnimations.AnimationMode sneakingAnimation) {
      this.sneakingAnimation = sneakingAnimation;
   }

   public static enum AnimationMode {
      LEGACY,
      CURRENT;
   }
}
