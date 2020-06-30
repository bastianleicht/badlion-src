package net.badlion.client.gui;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JFrame;

public class ExternalUIFrame extends JFrame {
   private Image backgroundImage;

   public ExternalUIFrame(String name) {
      super(name);
   }

   public void setBackgroundImage(Image image) {
      this.backgroundImage = image;
   }

   public void paintComponents(Graphics g) {
      if(this.backgroundImage != null) {
         g.drawImage(this.backgroundImage, 0, 0, this);
      }

   }
}
