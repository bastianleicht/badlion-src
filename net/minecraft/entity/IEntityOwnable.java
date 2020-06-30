package net.minecraft.entity;

import net.minecraft.entity.Entity;

public interface IEntityOwnable {
   String getOwnerId();

   Entity getOwner();
}
