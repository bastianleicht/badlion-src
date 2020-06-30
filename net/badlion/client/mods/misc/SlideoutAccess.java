package net.badlion.client.mods.misc;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.gui.BoxedCoord;

public class SlideoutAccess extends RenderMod {
   private SlideoutGUI instance;

   public SlideoutAccess() {
      super("SlideoutAccess", 0, 0, 0, 0);
      this.zIndex = 101;
      this.defaultTopLeftBox = new BoxedCoord(0, 0, 0.0D, 0.0D);
      this.defaultCenterBox = new BoxedCoord(0, 0, 0.0D, 0.0D);
      this.defaultBottomRightBox = new BoxedCoord(0, 0, 0.0D, 0.0D);
   }

   public int getKey() {
      return Wrapper.getInstance().getActiveModProfile().getSlideoutKey();
   }

   public SlideoutGUI getSlideoutInstance() {
      if(this.instance == null) {
         this.instance = new SlideoutGUI();
         this.instance.init();
      }

      return this.instance;
   }

   public void init() {
      this.instance = new SlideoutGUI();
      this.instance.init();
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.GUI_KEY_PRESS);
      this.registerEvent(EventType.KEY_PRESS);
      this.registerEvent(EventType.GUI_CLICK_MOUSE);
      super.init();
   }

   public void onEvent(Event e) {
      this.getSlideoutInstance().passEvent(e);
      super.onEvent(e);
   }

   public void setKey(int key) {
      Wrapper.getInstance().getActiveModProfile().setSlideoutKey(key);
   }
}
