package net.badlion.client.config;

public class BetterframesConfig {
   private BetterframesConfig.BFRenderType renderImprovements = BetterframesConfig.BFRenderType.EXPERIMENTAL;
   private boolean weather = true;
   private boolean reducedChunkUpdates = true;
   private boolean fogEnabled = true;
   private boolean worldSwitchDirtScreen = true;

   public boolean isRenderImprovements() {
      return this.renderImprovements == BetterframesConfig.BFRenderType.EXPERIMENTAL;
   }

   public BetterframesConfig.BFRenderType getRenderImprovements() {
      return this.renderImprovements;
   }

   public void setRenderImprovements(boolean enabled) {
      this.setRenderImprovements(enabled?BetterframesConfig.BFRenderType.EXPERIMENTAL:BetterframesConfig.BFRenderType.DEFAULT);
   }

   public void setRenderImprovements(BetterframesConfig.BFRenderType renderImprovements) {
      this.renderImprovements = renderImprovements;
   }

   public boolean isWeather() {
      return this.weather;
   }

   public void setWeather(boolean weather) {
      this.weather = weather;
   }

   public boolean isReducedChunkUpdates() {
      return this.reducedChunkUpdates;
   }

   public void setReducedChunkUpdates(boolean reducedChunkUpdates) {
      this.reducedChunkUpdates = reducedChunkUpdates;
   }

   public boolean isFogEnabled() {
      return this.fogEnabled;
   }

   public void setFogEnabled(boolean fogEnabled) {
      this.fogEnabled = fogEnabled;
   }

   public boolean isWorldSwitchDirtScreen() {
      return this.worldSwitchDirtScreen;
   }

   public void setWorldSwitchDirtScreen(boolean worldSwitchDirtScreen) {
      this.worldSwitchDirtScreen = worldSwitchDirtScreen;
   }

   private static enum BFRenderType {
      EXPERIMENTAL,
      DEFAULT;
   }
}
