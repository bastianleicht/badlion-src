package net.badlion.client.gui.slideout;

import java.util.ArrayList;
import java.util.List;
import net.badlion.client.gui.slideout.RenderElement;

public class SlideElementPage {
   private List elementList = new ArrayList();

   public void addElement(RenderElement e) {
      this.elementList.add(e);
   }

   public RenderElement getElement(int index) {
      return (RenderElement)this.elementList.get(index);
   }

   public List getElementList() {
      return this.elementList;
   }
}
