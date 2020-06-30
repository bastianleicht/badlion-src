package net.badlion.client.mods.render;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.mods.Mod;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.AxisAlignedBB;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.util.Color;

public class Hitboxes extends Mod {
   private MutableBoolean animalHitBoxesEnabled = new MutableBoolean(true);
   private MutableBoolean itemDropHitboxesEnabled = new MutableBoolean(true);
   private MutableBoolean monsterHitboxesEnabled = new MutableBoolean(true);
   private MutableBoolean playerHitboxesEnabled = new MutableBoolean(true);
   private MutableBoolean projectileHitboxesEnabled = new MutableBoolean(true);
   private transient TextButton animalHitBoxesButton;
   private transient TextButton itemDropHitboxesButton;
   private transient TextButton monsterHitboxesButton;
   private transient TextButton playerHitboxesButton;
   private transient TextButton projectileHitboxesButton;
   private ModColor mainHitboxColor = new ModColor(-1);
   private ModColor animalHitboxColor = new ModColor(-1);
   private ModColor itemDropHitboxColor = new ModColor(-1);
   private ModColor monsterHitboxColor = new ModColor(-1);
   private ModColor playerHitboxColor = new ModColor(-1);
   private ModColor projectileHitboxColor = new ModColor(-1);

   public Hitboxes() {
      super("Hitboxes", false);
      this.iconDimension = new ImageDimension(111, 78);
   }

   public void init() {
      this.registerEvent(EventType.MOTION_UPDATE);
      this.setFontOffset(0.083D);
      this.offsetX = -2;
      super.init();
   }

   public void reset() {
      this.animalHitBoxesButton.setEnabled(true);
      this.itemDropHitboxesButton.setEnabled(true);
      this.monsterHitboxesButton.setEnabled(true);
      this.playerHitboxesButton.setEnabled(true);
      this.projectileHitboxesButton.setEnabled(true);
      this.mainHitboxColor = new ModColor(-1);
      this.animalHitboxColor = new ModColor(-1);
      this.itemDropHitboxColor = new ModColor(-1);
      this.monsterHitboxColor = new ModColor(-1);
      this.playerHitboxColor = new ModColor(-1);
      this.projectileHitboxColor = new ModColor(-1);
      super.reset();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 8));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(new ColorPicker("Main Hitbox Color", this.mainHitboxColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Animal Hitbox Color", this.animalHitboxColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Item Drop Hitbox Color", this.itemDropHitboxColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Monster Hitbox Color", this.monsterHitboxColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Player Hitbox Color", this.playerHitboxColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Projectile Hitbox Color", this.projectileHitboxColor, 0.13D));
      this.animalHitBoxesButton = new TextButton("Animal Hitboxes", this.animalHitBoxesEnabled, 1.0D);
      this.itemDropHitboxesButton = new TextButton("Item Drop Hitboxes", this.itemDropHitboxesEnabled, 1.0D);
      this.monsterHitboxesButton = new TextButton("Monster Hitboxes", this.monsterHitboxesEnabled, 1.0D);
      this.playerHitboxesButton = new TextButton("Player Hitboxes", this.playerHitboxesEnabled, 1.0D);
      this.projectileHitboxesButton = new TextButton("Projectile Hitboxes", this.projectileHitboxesEnabled, 1.0D);
      this.slideCogMenu.addElement(this.animalHitBoxesButton);
      this.slideCogMenu.addElement(this.itemDropHitboxesButton);
      this.slideCogMenu.addElement(this.monsterHitboxesButton);
      this.slideCogMenu.addElement(this.playerHitboxesButton);
      this.slideCogMenu.addElement(this.projectileHitboxesButton);
      this.mainHitboxColor.init();
      this.animalHitboxColor.init();
      this.itemDropHitboxColor.init();
      this.monsterHitboxColor.init();
      this.playerHitboxColor.init();
      this.projectileHitboxColor.init();
      super.createCogMenu();
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate) {
         this.mainHitboxColor.tickColor();
         this.animalHitboxColor.tickColor();
         this.itemDropHitboxColor.tickColor();
         this.monsterHitboxColor.tickColor();
         this.playerHitboxColor.tickColor();
         this.projectileHitboxColor.tickColor();
      }

      super.onEvent(e);
   }

   public static boolean isHitboxEnabled(Entity entity) {
      if(entity instanceof EntityPlayer) {
         if(!Wrapper.getInstance().getActiveModProfile().getHitboxes().isPlayerHitboxesEnabled()) {
            return false;
         }
      } else if(entity instanceof EntityItem) {
         if(!Wrapper.getInstance().getActiveModProfile().getHitboxes().isItemDropHitboxesEnabled()) {
            return false;
         }
      } else if(!(entity instanceof IProjectile) && !(entity instanceof EntityFishHook)) {
         if(entity instanceof EntityMob) {
            if(!Wrapper.getInstance().getActiveModProfile().getHitboxes().isMonsterHitboxesEnabled()) {
               return false;
            }
         } else if(entity instanceof EntityLiving) {
            if(!Wrapper.getInstance().getActiveModProfile().getHitboxes().isAnimalHitBoxesEnabled()) {
               return false;
            }

            if(entity instanceof EntityHorse) {
               EntityHorse entityhorse = (EntityHorse)entity;
               if(entityhorse.getGrowingAge() < -1000) {
                  return false;
               }
            }
         }
      } else if(!Wrapper.getInstance().getActiveModProfile().getHitboxes().isProjectileHitboxesEnabled()) {
         return false;
      }

      return true;
   }

   public static Color renderHitboxes(Entity entity) {
      Color color;
      if(entity instanceof EntityPlayer) {
         color = Wrapper.getInstance().getActiveModProfile().getHitboxes().getPlayerHitboxColor().getColor();
      } else if(entity instanceof EntityItem) {
         color = Wrapper.getInstance().getActiveModProfile().getHitboxes().getItemDropHitboxColor().getColor();
      } else if(!(entity instanceof IProjectile) && !(entity instanceof EntityFishHook)) {
         if(entity instanceof EntityMob) {
            color = Wrapper.getInstance().getActiveModProfile().getHitboxes().getMonsterHitboxColor().getColor();
         } else if(entity instanceof EntityLiving) {
            color = Wrapper.getInstance().getActiveModProfile().getHitboxes().getAnimalHitboxColor().getColor();
         } else {
            color = Wrapper.getInstance().getActiveModProfile().getHitboxes().getMainHitboxColor().getColor();
         }
      } else {
         color = Wrapper.getInstance().getActiveModProfile().getHitboxes().getProjectileHitboxColor().getColor();
      }

      return color;
   }

   public static void renderHitbox(Entity entityIn, double x, double y, double z) {
      Color color = renderHitboxes(entityIn);
      if(isHitboxEnabled(entityIn)) {
         GlStateManager.depthMask(false);
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox();
         AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - entityIn.posX + x, axisalignedbb.minY - entityIn.posY + y, axisalignedbb.minZ - entityIn.posZ + z, axisalignedbb.maxX - entityIn.posX + x, axisalignedbb.maxY - entityIn.posY + y, axisalignedbb.maxZ - entityIn.posZ + z);
         RenderGlobal.func_181563_a(axisalignedbb1, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
         GlStateManager.enableTexture2D();
         GlStateManager.enableLighting();
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }

   }

   public boolean isAnimalHitBoxesEnabled() {
      return this.animalHitBoxesEnabled.booleanValue();
   }

   public boolean isItemDropHitboxesEnabled() {
      return this.itemDropHitboxesEnabled.booleanValue();
   }

   public boolean isMonsterHitboxesEnabled() {
      return this.monsterHitboxesEnabled.booleanValue();
   }

   public boolean isPlayerHitboxesEnabled() {
      return this.playerHitboxesEnabled.booleanValue();
   }

   public boolean isProjectileHitboxesEnabled() {
      return this.projectileHitboxesEnabled.booleanValue();
   }

   public ModColor getMainHitboxColor() {
      return this.mainHitboxColor;
   }

   public ModColor getAnimalHitboxColor() {
      return this.animalHitboxColor;
   }

   public ModColor getItemDropHitboxColor() {
      return this.itemDropHitboxColor;
   }

   public ModColor getMonsterHitboxColor() {
      return this.monsterHitboxColor;
   }

   public ModColor getPlayerHitboxColor() {
      return this.playerHitboxColor;
   }

   public ModColor getProjectileHitboxColor() {
      return this.projectileHitboxColor;
   }
}
