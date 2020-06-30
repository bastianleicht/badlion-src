package net.minecraft.world;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.LockCode;

public interface ILockableContainer extends IInventory, IInteractionObject {
   boolean isLocked();

   void setLockCode(LockCode var1);

   LockCode getLockCode();
}
