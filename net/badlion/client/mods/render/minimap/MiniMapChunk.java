package net.badlion.client.mods.render.minimap;

public class MiniMapChunk {
   private int chunkX;
   private int chunkZ;
   private byte[] topLayerData = new byte[256];

   public MiniMapChunk(byte[] topLayerData, int chunkX, int chunkZ) {
      this.topLayerData = topLayerData;
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public byte[] getTopLayerData() {
      return this.topLayerData;
   }

   public void setTopLayerData(byte[] topLayerData) {
      this.topLayerData = topLayerData;
   }
}
