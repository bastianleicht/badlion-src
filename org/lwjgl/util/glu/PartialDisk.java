package org.lwjgl.util.glu;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Quadric;

public class PartialDisk extends Quadric {
   private static final int CACHE_SIZE = 240;

   public void draw(float innerRadius, float outerRadius, int slices, int loops, float startAngle, float sweepAngle) {
      float[] sinCache = new float[240];
      float[] cosCache = new float[240];
      float texLow = 0.0F;
      float texHigh = 0.0F;
      if(slices >= 240) {
         slices = 239;
      }

      if(slices >= 2 && loops >= 1 && outerRadius > 0.0F && innerRadius >= 0.0F && innerRadius <= outerRadius) {
         if(sweepAngle < -360.0F) {
            sweepAngle = 360.0F;
         }

         if(sweepAngle > 360.0F) {
            sweepAngle = 360.0F;
         }

         if(sweepAngle < 0.0F) {
            startAngle += sweepAngle;
            sweepAngle = -sweepAngle;
         }

         int slices2;
         if(sweepAngle == 360.0F) {
            slices2 = slices;
         } else {
            slices2 = slices + 1;
         }

         float deltaRadius = outerRadius - innerRadius;
         float angleOffset = startAngle / 180.0F * 3.1415927F;

         for(int i = 0; i <= slices; ++i) {
            float angle = angleOffset + 3.1415927F * sweepAngle / 180.0F * (float)i / (float)slices;
            sinCache[i] = this.sin(angle);
            cosCache[i] = this.cos(angle);
         }

         if(sweepAngle == 360.0F) {
            sinCache[slices] = sinCache[0];
            cosCache[slices] = cosCache[0];
         }

         switch(super.normals) {
         case 100000:
         case 100001:
            if(super.orientation == 100020) {
               GL11.glNormal3f(0.0F, 0.0F, 1.0F);
            } else {
               GL11.glNormal3f(0.0F, 0.0F, -1.0F);
            }
         case 100002:
         }

         switch(super.drawStyle) {
         case 100010:
            GL11.glBegin(0);

            for(int var30 = 0; var30 < slices2; ++var30) {
               float sintemp = sinCache[var30];
               float costemp = cosCache[var30];

               for(int var35 = 0; var35 <= loops; ++var35) {
                  float var45 = outerRadius - deltaRadius * ((float)var35 / (float)loops);
                  if(super.textureFlag) {
                     texLow = var45 / outerRadius / 2.0F;
                     GL11.glTexCoord2f(texLow * sinCache[var30] + 0.5F, texLow * cosCache[var30] + 0.5F);
                  }

                  GL11.glVertex3f(var45 * sintemp, var45 * costemp, 0.0F);
               }
            }

            GL11.glEnd();
            break;
         case 100011:
            if(innerRadius == outerRadius) {
               GL11.glBegin(3);

               for(int var27 = 0; var27 <= slices; ++var27) {
                  if(super.textureFlag) {
                     GL11.glTexCoord2f(sinCache[var27] / 2.0F + 0.5F, cosCache[var27] / 2.0F + 0.5F);
                  }

                  GL11.glVertex3f(innerRadius * sinCache[var27], innerRadius * cosCache[var27], 0.0F);
               }

               GL11.glEnd();
               break;
            } else {
               for(int j = 0; j <= loops; ++j) {
                  float radiusLow = outerRadius - deltaRadius * ((float)j / (float)loops);
                  if(super.textureFlag) {
                     texLow = radiusLow / outerRadius / 2.0F;
                  }

                  GL11.glBegin(3);

                  for(int var28 = 0; var28 <= slices; ++var28) {
                     if(super.textureFlag) {
                        GL11.glTexCoord2f(texLow * sinCache[var28] + 0.5F, texLow * cosCache[var28] + 0.5F);
                     }

                     GL11.glVertex3f(radiusLow * sinCache[var28], radiusLow * cosCache[var28], 0.0F);
                  }

                  GL11.glEnd();
               }

               for(int var29 = 0; var29 < slices2; ++var29) {
                  float sintemp = sinCache[var29];
                  float costemp = cosCache[var29];
                  GL11.glBegin(3);

                  for(int var34 = 0; var34 <= loops; ++var34) {
                     float radiusLow = outerRadius - deltaRadius * ((float)var34 / (float)loops);
                     if(super.textureFlag) {
                        texLow = radiusLow / outerRadius / 2.0F;
                     }

                     if(super.textureFlag) {
                        GL11.glTexCoord2f(texLow * sinCache[var29] + 0.5F, texLow * cosCache[var29] + 0.5F);
                     }

                     GL11.glVertex3f(radiusLow * sintemp, radiusLow * costemp, 0.0F);
                  }

                  GL11.glEnd();
               }

               return;
            }
         case 100012:
            int finish;
            if(innerRadius != 0.0F) {
               finish = loops;
            } else {
               finish = loops - 1;
               GL11.glBegin(6);
               if(super.textureFlag) {
                  GL11.glTexCoord2f(0.5F, 0.5F);
               }

               GL11.glVertex3f(0.0F, 0.0F, 0.0F);
               float radiusLow = outerRadius - deltaRadius * ((float)(loops - 1) / (float)loops);
               if(super.textureFlag) {
                  texLow = radiusLow / outerRadius / 2.0F;
               }

               if(super.orientation == 100020) {
                  for(int var24 = slices; var24 >= 0; --var24) {
                     if(super.textureFlag) {
                        GL11.glTexCoord2f(texLow * sinCache[var24] + 0.5F, texLow * cosCache[var24] + 0.5F);
                     }

                     GL11.glVertex3f(radiusLow * sinCache[var24], radiusLow * cosCache[var24], 0.0F);
                  }
               } else {
                  for(int var25 = 0; var25 <= slices; ++var25) {
                     if(super.textureFlag) {
                        GL11.glTexCoord2f(texLow * sinCache[var25] + 0.5F, texLow * cosCache[var25] + 0.5F);
                     }

                     GL11.glVertex3f(radiusLow * sinCache[var25], radiusLow * cosCache[var25], 0.0F);
                  }
               }

               GL11.glEnd();
            }

            for(int j = 0; j < finish; ++j) {
               float radiusLow = outerRadius - deltaRadius * ((float)j / (float)loops);
               float radiusHigh = outerRadius - deltaRadius * ((float)(j + 1) / (float)loops);
               if(super.textureFlag) {
                  texLow = radiusLow / outerRadius / 2.0F;
                  texHigh = radiusHigh / outerRadius / 2.0F;
               }

               GL11.glBegin(8);

               for(int var26 = 0; var26 <= slices; ++var26) {
                  if(super.orientation == 100020) {
                     if(super.textureFlag) {
                        GL11.glTexCoord2f(texLow * sinCache[var26] + 0.5F, texLow * cosCache[var26] + 0.5F);
                     }

                     GL11.glVertex3f(radiusLow * sinCache[var26], radiusLow * cosCache[var26], 0.0F);
                     if(super.textureFlag) {
                        GL11.glTexCoord2f(texHigh * sinCache[var26] + 0.5F, texHigh * cosCache[var26] + 0.5F);
                     }

                     GL11.glVertex3f(radiusHigh * sinCache[var26], radiusHigh * cosCache[var26], 0.0F);
                  } else {
                     if(super.textureFlag) {
                        GL11.glTexCoord2f(texHigh * sinCache[var26] + 0.5F, texHigh * cosCache[var26] + 0.5F);
                     }

                     GL11.glVertex3f(radiusHigh * sinCache[var26], radiusHigh * cosCache[var26], 0.0F);
                     if(super.textureFlag) {
                        GL11.glTexCoord2f(texLow * sinCache[var26] + 0.5F, texLow * cosCache[var26] + 0.5F);
                     }

                     GL11.glVertex3f(radiusLow * sinCache[var26], radiusLow * cosCache[var26], 0.0F);
                  }
               }

               GL11.glEnd();
            }

            return;
         case 100013:
            if(sweepAngle < 360.0F) {
               for(int var22 = 0; var22 <= slices; var22 += slices) {
                  float sintemp = sinCache[var22];
                  float costemp = cosCache[var22];
                  GL11.glBegin(3);

                  for(int j = 0; j <= loops; ++j) {
                     float radiusLow = outerRadius - deltaRadius * ((float)j / (float)loops);
                     if(super.textureFlag) {
                        texLow = radiusLow / outerRadius / 2.0F;
                        GL11.glTexCoord2f(texLow * sinCache[var22] + 0.5F, texLow * cosCache[var22] + 0.5F);
                     }

                     GL11.glVertex3f(radiusLow * sintemp, radiusLow * costemp, 0.0F);
                  }

                  GL11.glEnd();
               }
            }

            for(int j = 0; j <= loops; j += loops) {
               float radiusLow = outerRadius - deltaRadius * ((float)j / (float)loops);
               if(super.textureFlag) {
                  texLow = radiusLow / outerRadius / 2.0F;
               }

               GL11.glBegin(3);

               for(int var23 = 0; var23 <= slices; ++var23) {
                  if(super.textureFlag) {
                     GL11.glTexCoord2f(texLow * sinCache[var23] + 0.5F, texLow * cosCache[var23] + 0.5F);
                  }

                  GL11.glVertex3f(radiusLow * sinCache[var23], radiusLow * cosCache[var23], 0.0F);
               }

               GL11.glEnd();
               if(innerRadius == outerRadius) {
                  break;
               }
            }
         }

      } else {
         System.err.println("PartialDisk: GLU_INVALID_VALUE");
      }
   }
}
