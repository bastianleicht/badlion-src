package net.badlion.client.mods;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.GUIClickMouse;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.gui.slideout.Image;
import net.badlion.client.gui.slideout.SimpleButton;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.mods.IMod;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Mod implements IMod {
   protected static transient ResourceLocation backButtonRes = new ResourceLocation("textures/slideout/cogwheel/back-button.svg_large.png");
   protected transient Minecraft gameInstance;
   protected transient String name;
   protected transient String displayName;
   private transient boolean forceDisabled = false;
   private boolean favorite;
   private boolean enabled = true;
   protected int zIndex = 100;
   private double fontOffset;
   protected transient SlidePage slideCogMenu;
   protected transient boolean loadedCogMenu;
   protected transient ImageDimension iconDimension;
   public transient int offsetX;
   public transient int offsetY;
   private transient Image backButton;
   private transient SimpleButton reset;
   private transient SimpleButton modStatus;
   private transient boolean resetConfirmation;

   public Mod(String name, boolean enableDefault) {
      this.name = name;
      this.enabled = enableDefault;
   }

   public Mod(String name) {
      this.name = name;
      this.enabled = true;
   }

   public void init() {
      Wrapper.getInstance().getActiveModProfile().registerEvent(this, EventType.GUI_CLICK_MOUSE);
      Wrapper.getInstance().getActiveModProfile().registerEvent(this, EventType.MOTION_UPDATE);
      this.gameInstance = Minecraft.getMinecraft();
   }

   public void registerEvent(EventType eventType) {
      Wrapper.getInstance().getActiveModProfile().registerEvent(this, eventType);
   }

   public void handleDisallowedMods(Wrapper.DisallowedMods disallowedMods) {
      if(disallowedMods != null) {
         if(disallowedMods.isDisabled()) {
            this.forceDisabled = true;
         }
      } else {
         this.forceDisabled = false;
      }

   }

   public void setZIndex(int zindex) {
      this.zIndex = zindex;
   }

   public int getZIndex() {
      return this.zIndex;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
      this.updateModStatus();
   }

   public void toggle() {
      this.setEnabled(!this.isEnabled());
   }

   public void createCogMenu() {
      if(this.slideCogMenu != null) {
         this.backButton = new Image(backButtonRes, 124, 124, 0.2D);
         this.backButton.ignoreAutoPos(0);
         this.reset = new SimpleButton("Reset Mod", -12303292, -11184811, -1);
         this.reset.ignoreAutoPos(0);
         this.reset.setPosition(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() / 2 + 20, Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutHeight() - (this.reset.getHeight() + 6));
         this.reset.setSize(56, this.reset.getHeight() + 1);
         this.modStatus = new SimpleButton("", -1, -1, -1);
         this.modStatus.ignoreAutoPos(0);
         this.modStatus.setPosition(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() / 2 - 2 - 28, Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutHeight() - (this.modStatus.getHeight() + 6));
         this.modStatus.setSize(41, this.modStatus.getHeight() + 1);
         this.updateModStatus();
         this.modStatus.init();
         int i = 10;
         int j = 30;
         this.backButton.setPosition(i, j);
         this.backButton.setColorOffset(0.8D, 0.8D, 0.8D);
         this.slideCogMenu.addElement(this.backButton);
         this.slideCogMenu.addElement(this.reset);
         this.slideCogMenu.addElement(this.modStatus);
         this.slideCogMenu.init();
         this.slideCogMenu.position();
         this.loadedCogMenu = true;
      }

   }

   public void updateModStatus() {
      if(this.modStatus != null) {
         this.modStatus.setText(this.isEnabled()?"Enabled":"Disabled");
         int i = this.isEnabled()?-14963585:-2338240;
         this.modStatus.setColor(i);
         this.modStatus.setHoverColor(i);
      }

   }

   public SlidePage getSlideCogMenu() {
      return this.slideCogMenu;
   }

   public boolean hasSlideCogMenu() {
      return this.slideCogMenu != null;
   }

   public void reset() {
      Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().initPages();
   }

   public String getName() {
      return this.name;
   }

   public String getDisplayName() {
      return this.displayName == null?this.name:this.displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public void onEvent(Event e) {
      if(this.slideCogMenu != null && this.slideCogMenu.getPage() == 0 && this.isPageOpen()) {
         if(this.backButton != null && e instanceof MotionUpdate) {
            int i = Wrapper.getInstance().getMouseX();
            int j = Wrapper.getInstance().getMouseY();
            this.backButton.setColorOffset(0.75D, 0.75D, 0.75D);
            int k = this.backButton.getWidth();
            int l = this.backButton.getHeight();
            if(i > this.backButton.getX() && (double)i < (double)this.backButton.getX() + (double)k * this.backButton.getScale() && j > this.backButton.getY() && (double)j < (double)this.backButton.getY() + (double)l * this.backButton.getScale()) {
               this.backButton.setColorOffset(1.0D, 1.0D, 1.0D);
            }
         }

         if(e instanceof GUIClickMouse) {
            int i1 = Wrapper.getInstance().getMouseX();
            int j1 = Wrapper.getInstance().getMouseY();
            if(this.backButton != null) {
               int k1 = this.backButton.getWidth();
               int l1 = this.backButton.getHeight();
               if(i1 > this.backButton.getX() && (double)i1 < (double)this.backButton.getX() + (double)k1 * this.backButton.getScale() && j1 > this.backButton.getY() && (double)j1 < (double)this.backButton.getY() + (double)l1 * this.backButton.getScale()) {
                  Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().setPage("modPage");
                  e.setCancelled(true);
               }
            }

            if(this.reset != null) {
               this.reset.update(i1, j1);
               this.reset.onClick(((GUIClickMouse)e).getMouseButton());
               if(this.reset.isSelected()) {
                  if(!this.resetConfirmation) {
                     this.reset.setText("Are you sure?");
                     this.reset.setSize(64, this.reset.getHeight());
                     this.reset.setPosition(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() / 2 + 16, Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutHeight() - (this.reset.getHeight() + 5));
                     this.resetConfirmation = true;
                  } else {
                     this.reset();
                     this.reset.setText("Reset Mod");
                     this.reset.setSize(56, this.reset.getHeight());
                     this.resetConfirmation = false;
                  }
               }
            }

            if(this.modStatus != null) {
               this.modStatus.update(i1, j1);
               this.modStatus.onClick(((GUIClickMouse)e).getMouseButton());
               if(this.modStatus.isSelected()) {
                  this.setEnabled(!this.enabled);
               }
            }
         }
      }

   }

   public ImageDimension getIconDimension() {
      return this.iconDimension;
   }

   public void setFontOffset(double fontOffset) {
      this.fontOffset = fontOffset;
   }

   public double getFontOffset() {
      return this.fontOffset;
   }

   public boolean isFavorite() {
      return this.favorite;
   }

   public void setFavorite(boolean favorite) {
      this.favorite = favorite;
   }

   public boolean isPageOpen() {
      return this.name.equals(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSelectedPage());
   }

   public boolean isForceDisabled() {
      return this.forceDisabled;
   }
}
