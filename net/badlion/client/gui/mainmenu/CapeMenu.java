package net.badlion.client.gui.mainmenu;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class CapeMenu {
   private int rotate = 0;
   private ModelBiped biped = new ModelBiped(0.0F);
   private ModelRenderer bipedCloak;
   private ModelRenderer bipedCloakShoulders;
   private ResourceLocation cape = new ResourceLocation("textures/capes/cape_2.png");

   public CapeMenu(GuiMainMenu guiMainMenu) {
      this.bipedCloak = new ModelRenderer(this.biped, 0, 0);
      this.bipedCloak.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, 0.0F);
      this.bipedCloakShoulders = new ModelRenderer(this.biped, 0, 17);
      this.bipedCloakShoulders.addBox(-5.0F, -1.0F, -2.0F, 2, 1, 5, 0.0F);
      this.bipedCloakShoulders.addBox(3.0F, -1.0F, -2.0F, 2, 1, 5, 0.0F);
   }

   public void render(int mouseX, int mouseY) {
   }
}
