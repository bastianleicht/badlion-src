package net.badlion.client.mods.render.minimap;

import java.util.HashMap;
import java.util.Map;
import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Dropdown;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.ShowDirection;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.mods.render.gui.BoxedCoord;
import net.badlion.client.mods.render.minimap.MiniMapChunk;
import net.badlion.client.mods.render.minimap.MiniMapData;
import net.badlion.client.util.ColorUtil;
import net.badlion.client.util.ImageDimension;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.opengl.GL11;

public class MiniMap extends RenderMod {
   private transient Map chunkCache = new HashMap();
   private transient ResourceLocation background = new ResourceLocation("textures/map/background.png");
   private transient ResourceLocation circle = new ResourceLocation("textures/map/default-circle.png");
   private transient int currentZoomLevel = 2;
   private transient MiniMapData[] mapDatas = new MiniMapData[5];
   private transient double currentPlayerPosX;
   private transient double currentPlayerPosZ;
   private transient DynamicTexture dynamicTexture;
   private transient ResourceLocation resourceLocation;
   private transient int[] textureData;
   private transient int textureWidth = 128;
   private ModColor backgroundColor = new ModColor(-13289665);
   private ModColor borderColor = new ModColor(-13289665);
   private ModColor directionColor = new ModColor(-13289665);
   private MutableBoolean directions = new MutableBoolean(true);
   private MutableBoolean fancyFont = new MutableBoolean(false);
   private MiniMap.Type selected = MiniMap.Type.FLAT;
   private transient Dropdown typeDropdown;

   public MiniMap() {
      super("MiniMap", 0, 0, 102, 102, true);
      this.iconDimension = new ImageDimension(88, 76);
      this.mapDatas[0] = new MiniMapData(6);
      this.mapDatas[1] = new MiniMapData(4);
      this.mapDatas[2] = new MiniMapData(3);
      this.mapDatas[3] = new MiniMapData(2);
      this.mapDatas[4] = new MiniMapData(1);
      this.defaultTopLeftBox = new BoxedCoord(0, 0, 0.0D, 0.0D);
      this.defaultCenterBox = new BoxedCoord(1, 2, 0.36666666666666664D, 0.4148148148148148D);
      this.defaultBottomRightBox = new BoxedCoord(2, 4, 0.75D, 0.8888888888888888D);
   }

   public void init() {
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      super.init();
      this.dynamicTexture = new DynamicTexture(this.textureWidth, this.textureWidth);
      this.textureData = this.dynamicTexture.getTextureData();
      this.resourceLocation = this.gameInstance.getTextureManager().getDynamicTextureLocation("map/test", this.dynamicTexture);

      for(int i = 0; i < this.textureData.length; ++i) {
         this.textureData[i] = 0;
      }

      this.circle = new ResourceLocation("textures/map/" + this.selected.name().toLowerCase() + ".png");
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(this.typeDropdown = new Dropdown(new String[]{"Flat", "Fancy", "Dots"}, this.selected.ordinal(), 0.19D));
      this.slideCogMenu.addElement(new ColorPicker("Background Color", this.backgroundColor, 0.13D, true));
      this.slideCogMenu.addElement(new ColorPicker("Border Color", this.borderColor, 0.13D, true));
      this.slideCogMenu.addElement(new ColorPicker("Directions Color", this.directionColor, 0.13D, true));
      this.slideCogMenu.addElement(new TextButton("Compass Directions", this.directions, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Fancy Font", this.fancyFont, 1.0D));
      this.backgroundColor.init();
      this.borderColor.init();
      this.directionColor.init();
      super.createCogMenu();
   }

   public void reset() {
      this.offsetX = 0;
      this.offsetY = 0;
      this.backgroundColor = new ModColor(-13289665);
      this.borderColor = new ModColor(-13289665);
      this.directionColor = new ModColor(-13289665);
      this.selected = MiniMap.Type.FLAT;
      this.directions.setValue(true);
      this.fancyFont.setValue(false);
      super.reset();
   }

   public void generateChunk(byte[] chunkTopData, int chunkX, int chunkZ) {
      ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(chunkX, chunkZ);
      if(this.chunkCache.containsKey(chunkcoordintpair)) {
         ((MiniMapChunk)this.chunkCache.get(chunkcoordintpair)).setTopLayerData(chunkTopData);
      } else {
         this.chunkCache.put(chunkcoordintpair, new MiniMapChunk(chunkTopData, chunkX, chunkZ));
      }

   }

   public void onEvent(Event e) {
      if(this.isEnabled()) {
         if(e instanceof MotionUpdate) {
            if(this.loadedCogMenu) {
               MiniMap.Type minimap$type = this.selected;
               this.selected = MiniMap.Type.valueOf(this.typeDropdown.getValue().toUpperCase());
               if(!this.selected.equals(minimap$type)) {
                  this.circle = new ResourceLocation("textures/map/" + this.selected.name().toLowerCase() + ".png");
               }
            }

            this.backgroundColor.tickColor();
            this.borderColor.tickColor();
            this.directionColor.tickColor();
            int i2 = (int)this.currentPlayerPosX >> 4;
            int i = (int)this.currentPlayerPosZ >> 4;
            this.currentPlayerPosX = this.gameInstance.thePlayer.posX;
            this.currentPlayerPosZ = this.gameInstance.thePlayer.posZ;
            int j = this.textureWidth;
            int k = (int)this.currentPlayerPosX >> 4;
            int l = (int)this.currentPlayerPosZ >> 4;
            if(i2 != k || i != l) {
               for(int i1 = 0; i1 < this.textureData.length; ++i1) {
                  this.textureData[i1] = 0;
               }

               for(int i3 = 0; i3 < 7; ++i3) {
                  for(int j1 = 0; j1 < 7; ++j1) {
                     ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(((int)this.currentPlayerPosX >> 4) - 3 + i3, ((int)this.currentPlayerPosZ >> 4) - 3 + j1);
                     if(this.chunkCache.containsKey(chunkcoordintpair)) {
                        MiniMapChunk minimapchunk = (MiniMapChunk)this.chunkCache.get(chunkcoordintpair);
                        this.fillInChunkData(minimapchunk, i3 * 16, j1 * 16 * j);
                     }
                  }
               }

               this.dynamicTexture.updateDynamicTexture();
            }
         } else if(e instanceof RenderGame) {
            this.beginRender();
            int j2 = 0;
            int k2 = 0;
            float f3 = (float)(this.currentPlayerPosX - (double)(((int)this.currentPlayerPosX >> 4) - 3 << 4));
            float f4 = (float)(this.currentPlayerPosZ - (double)(((int)this.currentPlayerPosZ >> 4) - 3 << 4));
            int l2 = 48;
            float f5 = 0.0078125F;
            float f6 = 0.0078125F;
            int j3 = 3;
            GL11.glPushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            ColorUtil.bindColor(this.backgroundColor.getColor());
            this.gameInstance.getTextureManager().bindTexture(this.background);
            Gui.drawModalRectWithCustomSizedTexture(j2, k2, 0.0F, 0.0F, l2 * 2 + j3 * 2, l2 * 2 + j3 * 2, (float)(l2 * 2 + j3 * 2), (float)(l2 * 2 + j3 * 2));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((float)(j2 + l2 + j3), (float)(k2 + l2 + j3), 0.0F);
            GL11.glRotatef(180.0F - this.gameInstance.thePlayer.rotationYaw, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-f3, -f4, 0.0F);
            this.gameInstance.getTextureManager().bindTexture(this.resourceLocation);
            GL11.glBegin(9);

            for(int k3 = 360; k3 >= 0; k3 -= 10) {
               float f = (float)Math.toRadians((double)k3);
               float f1 = (float)(Math.cos((double)f) * 0.38D + (double)(f5 * f3));
               float f2 = (float)(Math.sin((double)f) * 0.38D + (double)(f6 * f4));
               GL11.glTexCoord2f(f1, f2);
               GL11.glVertex2f((float)(Math.cos((double)f) * (double)l2) + f3, (float)(Math.sin((double)f) * (double)l2) + f4);
            }

            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            ColorUtil.bindColor(this.borderColor.getColor());
            this.gameInstance.getTextureManager().bindTexture(this.circle);
            Gui.drawModalRectWithCustomSizedTexture(j2, k2, 0.0F, 0.0F, l2 * 2 + j3 * 2, l2 * 2 + j3 * 2, (float)(l2 * 2 + j3 * 2), (float)(l2 * 2 + j3 * 2));
            GL11.glPopMatrix();
            if(this.directions.isTrue()) {
               int l3 = MathHelper.floor_float(180.0F - this.gameInstance.thePlayer.rotationYaw - 90.0F);

               ShowDirection.Direction[] var14;
               for(ShowDirection.Direction showdirection$direction : var14 = new ShowDirection.Direction[]{ShowDirection.Direction.N, ShowDirection.Direction.E, ShowDirection.Direction.S, ShowDirection.Direction.W}) {
                  int k1 = (int)(Math.cos(Math.toRadians((double)l3)) * (double)l2);
                  int l1 = (int)(Math.sin(Math.toRadians((double)l3)) * (double)l2);
                  if(!this.fancyFont.isTrue()) {
                     GL11.glPushMatrix();
                     this.gameInstance.fontRendererObj.drawString(showdirection$direction.name(), j2 + l2 + k1, k2 + l2 + l1, this.directionColor.getColorInt());
                     GL11.glPopMatrix();
                  } else {
                     GL11.glPushMatrix();
                     ColorUtil.bindColor(this.directionColor.getColor());
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(j2 + l2 + k1, k2 + l2 - 3 + l1, showdirection$direction.name(), 12, BadlionFontRenderer.FontType.TITLE);
                     GL11.glPopMatrix();
                  }

                  l3 += 90;
               }
            }

            GL11.glPushMatrix();
            GL11.glTranslatef((float)(j2 + l2 + j3), (float)(k2 + l2 + j3), 0.0F);
            GL11.glTranslatef(-f3, -f4, 0.0F);
            GL11.glDisable(3553);
            GL11.glBegin(4);
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
            GL11.glVertex2f(f3, f4 - 2.0F);
            GL11.glVertex2f(f3 - 2.0F, f4 + 1.0F);
            GL11.glVertex2f(f3 + 2.0F, f4 + 1.0F);
            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glPopMatrix();
            this.endRender();
         }
      }

      super.onEvent(e);
   }

   private void fillInChunkData(MiniMapChunk miniMapChunk, int posXStart, int posZStart) {
      int i = this.textureWidth;
      int j = posXStart + posZStart;
      int k = 0;

      byte[] var10;
      for(byte b0 : var10 = miniMapChunk.getTopLayerData()) {
         if(k != 0 && k % 16 == 0) {
            j += i - 16;
         }

         int l = b0 & 255;
         if(l / 4 == 0) {
            this.textureData[j] = (j + j / 16 & 1) * 8 + 16 << 24;
         } else {
            this.textureData[j] = MapColor.mapColorArray[l / 4].func_151643_b(l & 3);
         }

         ++j;
         ++k;
      }

   }

   public void clearMinimapChunks() {
      this.chunkCache.clear();
   }

   static enum Type {
      FLAT,
      FANCY,
      DOTS;
   }
}
