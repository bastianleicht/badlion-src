package com.mojang.realmsclient.gui;

import net.minecraft.realms.RealmsButton;

public interface GuiCallback {
   void tick();

   void buttonClicked(RealmsButton var1);
}
