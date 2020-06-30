package net.badlion.client.gui.slideout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.badlion.client.Wrapper;
import net.badlion.client.gui.slideout.ImageButton;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.ModButton;
import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.gui.slideout.SelectButton;
import net.badlion.client.gui.slideout.SlideElementPage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.TextButton;

public class SlidePage {
   private List elementList = new ArrayList();
   private String name;
   private int page;
   private int pageElementIndex;
   private List pages = new ArrayList();
   private int width;
   private int height;
   private ImageButton backButton;
   private ImageButton forwardButton;
   private final int HORIZONTAL_BUFFER = 4;
   private final int VERTICAL_BUFFER = 3;

   public SlidePage(String pageName, int width, int height) {
      this.name = pageName;
      this.width = width;
      this.height = height;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getPage() {
      return this.page;
   }

   public void position() {
      this.pages.clear();
      int i = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth();
      int j = 24;
      int k = 2;
      int i1 = j + 1;
      int j1 = 0;

      SlideElementPage slideelementpage;
      int l1;
      for(slideelementpage = new SlideElementPage(); j1 < this.elementList.size(); j1 += Math.max(1, l1)) {
         int k1 = k;
         l1 = 0;

         for(int i2 = j1; i2 < this.elementList.size(); ++i2) {
            RenderElement renderelement = (RenderElement)this.elementList.get(i2);
            if(!renderelement.ignoreAutoPos) {
               if(k1 + renderelement.getWidth() + k > i || renderelement instanceof Label && ((Label)renderelement).isLineBreak() && k1 != k) {
                  break;
               }

               k1 += renderelement.getWidth() + k;
               ++l1;
               if(renderelement instanceof Label && ((Label)renderelement).isLineBreak()) {
                  break;
               }
            }
         }

         int k2 = 0;
         int i3 = k;

         for(int j2 = j1; j2 < j1 + l1; ++j2) {
            RenderElement renderelement1 = (RenderElement)this.elementList.get(j2);
            if(!renderelement1.ignoreAutoPos) {
               i3 += renderelement1.getWidth();
               if(renderelement1.getHeight() > k2) {
                  k2 = renderelement1.getHeight();
               }

               if(j2 < this.elementList.size() - 1) {
                  RenderElement renderelement2 = (RenderElement)this.elementList.get(j2 + 1);
                  if(!(renderelement2 instanceof TextButton) && !(renderelement2 instanceof ModButton) && !(renderelement2 instanceof SelectButton)) {
                     if(!(renderelement1 instanceof TextButton) && !(renderelement1 instanceof SelectButton)) {
                        if(renderelement1 instanceof ModButton) {
                           if(i3 == renderelement1.getWidth() + k) {
                              i3 += (renderelement1.getWidth() + k) * 2;
                           } else if(i3 == renderelement1.getWidth() * 2 + k) {
                              i3 += renderelement1.getWidth() + k;
                           }
                        }
                     } else if(i3 == renderelement1.getWidth() + k) {
                        i3 += renderelement1.getWidth() + k + k;
                     }
                  }
               } else if(!(renderelement1 instanceof TextButton) && !(renderelement1 instanceof SelectButton)) {
                  if(renderelement1 instanceof ModButton) {
                     if(i3 == renderelement1.getWidth() + k) {
                        i3 += (renderelement1.getWidth() + k) * 2;
                     } else if(i3 == renderelement1.getWidth() * 2 + k) {
                        i3 += renderelement1.getWidth() + k;
                     }
                  }
               } else if(i3 == renderelement1.getWidth() + k) {
                  i3 += renderelement1.getWidth() + k + k;
               }
            }
         }

         RenderElement renderelement5 = (RenderElement)this.elementList.get(j1);
         if(i1 + renderelement5.getHeight() > this.height - 24) {
            this.pages.add(slideelementpage);
            slideelementpage = new SlideElementPage();
            i1 = j + 1 + 10;
         }

         int j3 = (int)((double)(i - i3) / (double)(l1 + 1));
         int l = j3;

         for(int k3 = j1; k3 < j1 + l1; ++k3) {
            RenderElement renderelement3 = (RenderElement)this.elementList.get(k3);
            if(!renderelement3.ignoreAutoPos) {
               renderelement3.setPosition(l, i1);
               l += j3 + renderelement3.getWidth();
               slideelementpage.addElement(renderelement3);
            }
         }

         i1 += k2 + 3;
      }

      this.pages.add(slideelementpage);

      for(RenderElement renderelement4 : this.elementList) {
         if(renderelement4.ignoreAutoPos) {
            if(this.pages.size() - 1 < renderelement4.ignoreAutoPosPage) {
               for(int l2 = 0; l2 < renderelement4.ignoreAutoPosPage; ++l2) {
                  this.pages.add(new SlideElementPage());
               }
            }

            ((SlideElementPage)this.pages.get(renderelement4.ignoreAutoPosPage)).addElement(renderelement4);
         }
      }

      Iterator<SlideElementPage> iterator = this.pages.iterator();

      while(iterator.hasNext()) {
         SlideElementPage slideelementpage1 = (SlideElementPage)iterator.next();
         if(slideelementpage1.getElementList().size() == 0) {
            iterator.remove();
         } else {
            slideelementpage1.addElement(this.forwardButton);
            slideelementpage1.addElement(this.backButton);
         }
      }

      this.handleButtonColors();
   }

   public void handleButtonColors() {
      if(this.page + 1 < this.pages.size()) {
         this.forwardButton.setEnabled(true);
      } else {
         this.forwardButton.setEnabled(false);
      }

      if(this.page - 1 < 0) {
         this.backButton.setEnabled(false);
      } else {
         this.backButton.setEnabled(true);
      }

   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setPage(int page) {
      this.page = page;
   }

   public void render(double slide) {
      int i = -1;

      for(RenderElement renderelement : this.getElementList()) {
         if(renderelement.getZLevel() > i) {
            i = renderelement.getZLevel();
         }
      }

      for(int j = -1; j <= i; ++j) {
         for(RenderElement renderelement1 : this.getElementList()) {
            if(renderelement1.getZLevel() == j) {
               renderelement1.setOptionalOffset((int)slide - this.width, 0);
               renderelement1.render();
            }
         }
      }

   }

   public void update(int mouseX, int mouseY) {
      for(RenderElement renderelement : this.getElementList()) {
         renderelement.update(mouseX, mouseY);
      }

   }

   public void onClick(int mouseButton) {
      this.forwardButton.onClick(mouseButton);
      this.backButton.onClick(mouseButton);
      if(this.forwardButton.isSelected() && this.page + 1 < this.pages.size()) {
         ++this.page;
         this.handleButtonColors();
      }

      if(this.backButton.isSelected() && this.page > 0) {
         --this.page;
         this.handleButtonColors();
      }

   }

   public void addElement(RenderElement element) {
      this.elementList.add(element);
   }

   public List getAllElementList() {
      ArrayList<RenderElement> arraylist = new ArrayList();

      for(SlideElementPage slideelementpage : this.pages) {
         arraylist.addAll(slideelementpage.getElementList());
      }

      return arraylist;
   }

   public List getElementList() {
      return (List)(this.page >= this.pages.size()?(this.pages.size() == 0?new ArrayList():((SlideElementPage)this.pages.get(0)).getElementList()):((SlideElementPage)this.pages.get(this.page)).getElementList());
   }

   public void init() {
      for(RenderElement renderelement : this.elementList) {
         if(renderelement != null) {
            renderelement.init();
         }
      }

      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.backButton = new ImageButton(slideoutgui.backButtonRes, 124, 124, 0.14D);
      this.forwardButton = new ImageButton(slideoutgui.forwardButtonRes, 124, 124, 0.14D);
      this.backButton.init();
      this.forwardButton.init();
      this.backButton.setPosition(7, this.height - 23);
      this.forwardButton.setPosition(7 + this.backButton.getWidth() + 4, this.height - 23);
   }
}
