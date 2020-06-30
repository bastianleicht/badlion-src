package net.badlion.client.mods.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.GUIClickMouse;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Image;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.gui.slideout.SelectButton;
import net.badlion.client.gui.slideout.SimpleButton;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.mods.Mod;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.util.ColorUtil;
import net.badlion.client.util.ImageDimension;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ChangeColorMod extends Mod {
   private String parent;
   private ModColor currentColor;
   private transient Map renderElements = new HashMap();
   private transient Image backButton;
   private transient SimpleButton selectAll;
   private transient SimpleButton deselectAll;
   private transient SimpleButton apply;
   private transient SimpleButton preview;

   public ChangeColorMod() {
      super("Change Colors");
      this.iconDimension = new ImageDimension(111, 78);
   }

   public void init() {
      this.registerEvent(EventType.MOTION_UPDATE);
      this.registerEvent(EventType.GUI_CLICK_MOUSE);
      this.setFontOffset(0.083D);
      this.offsetX = -2;
      super.init();
   }

   public void reset() {
      super.reset();
   }

   public void createCogMenu() {
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth(), Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label("Change Colors", -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() - 25, 5));
      if(this.currentColor != null) {
         this.preview = new SimpleButton("", this.currentColor.getColorInt(), this.currentColor.getColorInt(), this.currentColor.getColorInt());
      } else {
         this.preview = new SimpleButton("", -1, -1, -1);
      }

      this.preview.setSize(60, 20);
      this.slideCogMenu.addElement(this.preview);

      for(Mod mod : Wrapper.getInstance().getActiveModProfile().getAllMods()) {
         if(mod.getSlideCogMenu() != null && mod.getSlideCogMenu().getAllElementList() != null) {
            boolean flag = false;

            for(RenderElement renderelement : mod.getSlideCogMenu().getAllElementList()) {
               if(renderelement instanceof ColorPicker) {
                  flag = true;
                  break;
               }
            }

            if(flag) {
               this.slideCogMenu.addElement(new Label(mod.getDisplayName(), -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));

               for(RenderElement renderelement1 : mod.getSlideCogMenu().getAllElementList()) {
                  if(renderelement1 instanceof ColorPicker) {
                     MutableBoolean mutableboolean = new MutableBoolean(false);
                     this.renderElements.put((ColorPicker)renderelement1, mutableboolean);
                     this.slideCogMenu.addElement(new SelectButton(((ColorPicker)renderelement1).getName().replace(" Color", ""), mutableboolean, 1.0D));
                  }
               }
            }
         }
      }

      this.backButton = new Image(Mod.backButtonRes, 124, 124, 0.2D);
      this.backButton.ignoreAutoPos(0);
      this.selectAll = new SimpleButton("Select All", -12303292, -11184811, -1, true, 10);
      this.selectAll.ignoreAutoPos(0);
      this.selectAll.setPosition(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() - 30 - 42 - 37 - 8, Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutHeight() - (this.selectAll.getHeight() + 6));
      this.selectAll.init();
      this.deselectAll = new SimpleButton("Deselect All", -12303292, -11184811, -1, true, 10);
      this.deselectAll.ignoreAutoPos(0);
      this.deselectAll.setPosition(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() - 30 - 42 - 5, Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutHeight() - (this.deselectAll.getHeight() + 6));
      this.deselectAll.setSize(40, this.deselectAll.getHeight());
      this.deselectAll.setTextScale(0.8D);
      this.deselectAll.init();
      this.apply = new SimpleButton("Apply", -12303292, -11184811, -1, true, 10);
      this.apply.ignoreAutoPos(0);
      this.apply.setPosition(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() - 29, Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutHeight() - (this.apply.getHeight() + 6));
      this.apply.setSize(28, this.apply.getHeight());
      this.apply.setTextScale(0.8D);
      this.apply.init();
      int i = 10;
      int j = 30;
      this.backButton.setPosition(i, j);
      this.backButton.setColorOffset(0.8D, 0.8D, 0.8D);
      this.slideCogMenu.addElement(this.backButton);
      this.slideCogMenu.addElement(this.selectAll);
      this.slideCogMenu.addElement(this.deselectAll);
      this.slideCogMenu.addElement(this.apply);
      this.slideCogMenu.init();
      this.slideCogMenu.position();
      this.loadedCogMenu = true;
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
                  Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().setPage(this.parent);
               }
            }

            if(this.apply != null) {
               this.apply.update(i1, j1);
               this.apply.onClick(((GUIClickMouse)e).getMouseButton());
               if(this.apply.isSelected()) {
                  for(Entry<ColorPicker, MutableBoolean> entry : this.renderElements.entrySet()) {
                     if(((MutableBoolean)entry.getValue()).getValue().booleanValue()) {
                        ((ColorPicker)entry.getKey()).setColor(ColorUtil.getIntFromColor(this.currentColor.getColor()));
                     }
                  }

                  Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().setPage(this.parent);
               }
            }

            if(this.selectAll != null) {
               this.selectAll.update(i1, j1);
               this.selectAll.onClick(((GUIClickMouse)e).getMouseButton());
               if(this.selectAll.isSelected()) {
                  for(MutableBoolean mutableboolean : this.renderElements.values()) {
                     mutableboolean.setValue(true);
                  }
               }

               this.deselectAll.update(i1, j1);
               this.deselectAll.onClick(((GUIClickMouse)e).getMouseButton());
               if(this.deselectAll.isSelected()) {
                  for(MutableBoolean mutableboolean1 : this.renderElements.values()) {
                     mutableboolean1.setValue(false);
                  }
               }
            }
         }
      }

      super.onEvent(e);
   }

   public void beforeOpen(ModColor currentColor, String parent) {
      this.currentColor = currentColor;
      this.preview.setColor(currentColor.getColorInt());
      this.preview.setHoverColor(currentColor.getColorInt());
      this.parent = parent;
   }
}
