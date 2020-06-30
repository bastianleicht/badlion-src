package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public interface IReloadableResourceManager extends IResourceManager {
   void reloadResources(List var1);

   void registerReloadListener(IResourceManagerReloadListener var1);
}
