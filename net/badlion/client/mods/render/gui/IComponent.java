package net.badlion.client.mods.render.gui;

import net.badlion.client.mods.render.gui.elements.Panel;
import net.minecraft.client.gui.GuiIngame;

public interface IComponent {
   void render(GuiIngame var1, int var2, int var3);

   void init();

   void update(Panel var1, int var2, int var3, boolean var4);
}
