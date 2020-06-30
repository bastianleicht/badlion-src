package net.badlion.client.mods.render.minimap;

import net.badlion.client.mods.render.minimap.MiniMapChunk;

public class MiniMapData {
   private final int height;
   private final int width;
   int posXCenter = 0;
   int posZCenter = 0;
   int posXStart = 0;
   int posZStart = 0;
   byte[] blockData;

   public MiniMapData(int chunkRadius) {
      this.height = (1 + chunkRadius * 2) * 16;
      this.width = (1 + chunkRadius * 2) * 16;
      this.blockData = new byte[this.height * this.width];
   }

   public void fillInChunkData(MiniMapChunk miniMapChunk) {
      int i = Math.abs((miniMapChunk.getChunkX() << 4) - this.posXStart);
      int j = Math.abs((miniMapChunk.getChunkZ() << 4) - this.posZStart);
      int k = i + j;
      int l = 0;

      byte[] var9;
      for(byte b0 : var9 = miniMapChunk.getTopLayerData()) {
         if(l != 0 && l % 16 == 0) {
            k += this.width - 16;
         }

         this.blockData[k] = b0;
         ++l;
      }

   }

   public byte[] getBlockData() {
      return this.blockData;
   }
}
