package net.java.games.input;

import net.java.games.input.Component;

public interface Rumbler {
   void rumble(float var1);

   String getAxisName();

   Component.Identifier getAxisIdentifier();
}
