package net.badlion.client.mods.render.gui.elements;

import java.util.LinkedHashMap;
import net.badlion.client.Wrapper;
import net.badlion.client.mods.render.gui.Component;
import net.badlion.client.mods.render.gui.SizeableComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.lwjgl.input.Keyboard;

public class Panel extends SizeableComponent {
   private int color;
   private boolean scrollable;
   private Panel lastPanel;
   private double scrollableOffset;
   private LinkedHashMap components = new LinkedHashMap();

   public Panel(String name, int x, int y, int sizeX, int sizeY, int color, boolean visible) {
      super(name, sizeX, sizeY);
      this.setPosition(x, y);
      this.color = color;
      this.setVisible(visible);
   }

   public void setLastPanel(Panel panel) {
      this.lastPanel = panel;
   }

   public Panel getLastPanel() {
      return this.lastPanel;
   }

   public void setScrollable(boolean scrollable) {
      this.scrollable = scrollable;
   }

   public boolean isScrollable() {
      return this.scrollable;
   }

   public void addRelativeComponent(Component component, int x, int y) {
      if(component == null) {
         throw new NullPointerException("Component cannot be null!");
      } else {
         component.setX(x);
         component.setY(y);
         component.setRelative(true);
         component.setOptionalRenderScreen(this.optionalRenderScreen);
         this.addComponent(component);
      }
   }

   public void removeComponent(Component component) {
      this.components.remove(component);
   }

   public boolean removeComponentByID(String name) {
      Component component = this.getComponentByID(name);
      if(component != null) {
         this.removeComponent(component);
         return true;
      } else {
         return false;
      }
   }

   public void addComponent(Component component) {
      try {
         if(component == null) {
            throw new NullPointerException("Component cannot be null!");
         }

         String s = component.getName();
         if(this.components.containsKey(s)) {
            throw new IllegalStateException("Panel cannot contain more than one of the same component!");
         }

         if(s == null) {
            throw new NullPointerException("Component ID cannot be null!");
         }

         component.init();
         component.setOptionalRenderScreen(this.optionalRenderScreen);
         this.components.put(s, component);
      } catch (Exception var3) {
         ;
      }

   }

   public Component getComponentByID(String componentId) {
      if(componentId == null) {
         throw new NullPointerException("Component ID cannot be null!");
      } else {
         return (Component)this.components.get(componentId);
      }
   }

   public void update(Panel parent, int mouseX, int mouseY, boolean dragging) {
      double d0 = 120.0D / (double)Minecraft.getDebugFPS();
      d0 = d0 * 2.5D;
      int i = (int)((double)Wrapper.getInstance().mouse_wheel * ((double)Minecraft.getDebugFPS() / 120.0D));
      if(this.isScrollable() && mouseX > this.x && mouseX < this.x + this.sizeX && mouseY > this.y && mouseY < this.y + this.sizeY) {
         this.scrollableOffset += d0 * ((double)i / 120.0D);
      }

      if(Keyboard.isKeyDown(208)) {
         this.scrollableOffset -= d0;
      }

      if(Keyboard.isKeyDown(200)) {
         this.scrollableOffset += d0;
      }

      if(this.scrollableOffset < (double)(-8 * this.components.size())) {
         this.scrollableOffset = (double)(-8 * this.components.size());
      }

      if(this.scrollableOffset > 0.0D) {
         this.scrollableOffset = 0.0D;
      }

      for(Component component : this.components.values()) {
         if(this.optionalRenderScreen != null && component.getOptionalRenderScreen() == null) {
            component.setOptionalRenderScreen(this.optionalRenderScreen);
         }

         component.update(this, mouseX, mouseY, dragging);
      }

   }

   public void render(GuiIngame gameRenderer, int x0, int y0) {
      GuiIngame.drawRect(x0, y0, x0 + this.sizeX, y0 + this.sizeY, this.color);
      int i = 3;
      int j = this.x + 3;
      int k = this.y + 3;
      Component component = null;
      this.setScrollable(false);

      for(Component component1 : this.components.values()) {
         if(component1.isVisible()) {
            if(!component1.isRelative()) {
               if(j + component1.getSizeX() > this.x + this.getSizeX() - 3) {
                  j = this.x + 3;
                  if(component != null) {
                     k += component.getSizeY() + 3;
                  } else {
                     k += component1.getSizeY();
                  }
               }

               component1.setPosition(j, (int)((double)k + this.scrollableOffset));
               if(component1.getY() > this.y && component1.getY() + component1.getSizeY() < this.y + this.getSizeY()) {
                  component1.setVisible(true);
                  component1.render(gameRenderer, component1.getX(), component1.getY());
               } else {
                  this.setScrollable(true);
               }

               j += component1.getSizeX() + 3;
               component = component1;
            } else {
               component1.render(gameRenderer, this.x + component1.getX(), this.y + component1.getY());
            }
         }
      }

      if(!this.isScrollable()) {
         this.scrollableOffset = 0.0D;
      }

   }

   public void resetScroll() {
      this.scrollableOffset = 0.0D;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public int getColor() {
      return this.color;
   }

   public LinkedHashMap getComponents() {
      return this.components;
   }
}
