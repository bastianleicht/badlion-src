package org.lwjgl.util.glu;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Quadric;

public class Disk extends Quadric {
   public void draw(float innerRadius, float outerRadius, int slices, int loops) {
      if(super.normals != 100002) {
         if(super.orientation == 100020) {
            GL11.glNormal3f(0.0F, 0.0F, 1.0F);
         } else {
            GL11.glNormal3f(0.0F, 0.0F, -1.0F);
         }
      }

      float da = 6.2831855F / (float)slices;
      float dr = (outerRadius - innerRadius) / (float)loops;
      switch(super.drawStyle) {
      case 100010:
         GL11.glBegin(0);

         for(int s = 0; s < slices; ++s) {
            float a = (float)s * da;
            float x = this.sin(a);
            float y = this.cos(a);

            for(int l = 0; l <= loops; ++l) {
               float r = innerRadius * (float)l * dr;
               GL11.glVertex2f(r * x, r * y);
            }
         }

         GL11.glEnd();
         break;
      case 100011:
         for(int l = 0; l <= loops; ++l) {
            float r = innerRadius + (float)l * dr;
            GL11.glBegin(2);

            for(int s = 0; s < slices; ++s) {
               float a = (float)s * da;
               GL11.glVertex2f(r * this.sin(a), r * this.cos(a));
            }

            GL11.glEnd();
         }

         for(int s = 0; s < slices; ++s) {
            float a = (float)s * da;
            float x = this.sin(a);
            float y = this.cos(a);
            GL11.glBegin(3);

            for(int a = 0; a <= loops; ++a) {
               float r = innerRadius + (float)a * dr;
               GL11.glVertex2f(r * x, r * y);
            }

            GL11.glEnd();
         }

         return;
      case 100012:
         float dtc = 2.0F * outerRadius;
         float r1 = innerRadius;

         for(int l = 0; l < loops; ++l) {
            float r2 = r1 + dr;
            if(super.orientation == 100020) {
               GL11.glBegin(8);

               for(int s = 0; s <= slices; ++s) {
                  float a;
                  if(s == slices) {
                     a = 0.0F;
                  } else {
                     a = (float)s * da;
                  }

                  float sa = this.sin(a);
                  float ca = this.cos(a);
                  this.TXTR_COORD(0.5F + sa * r2 / dtc, 0.5F + ca * r2 / dtc);
                  GL11.glVertex2f(r2 * sa, r2 * ca);
                  this.TXTR_COORD(0.5F + sa * r1 / dtc, 0.5F + ca * r1 / dtc);
                  GL11.glVertex2f(r1 * sa, r1 * ca);
               }

               GL11.glEnd();
            } else {
               GL11.glBegin(8);

               for(int s = slices; s >= 0; --s) {
                  float a;
                  if(s == slices) {
                     a = 0.0F;
                  } else {
                     a = (float)s * da;
                  }

                  float sa = this.sin(a);
                  float ca = this.cos(a);
                  this.TXTR_COORD(0.5F - sa * r2 / dtc, 0.5F + ca * r2 / dtc);
                  GL11.glVertex2f(r2 * sa, r2 * ca);
                  this.TXTR_COORD(0.5F - sa * r1 / dtc, 0.5F + ca * r1 / dtc);
                  GL11.glVertex2f(r1 * sa, r1 * ca);
               }

               GL11.glEnd();
            }

            r1 = r2;
         }

         return;
      case 100013:
         if((double)innerRadius != 0.0D) {
            GL11.glBegin(2);

            for(float a = 0.0F; (double)a < 6.2831854820251465D; a += da) {
               float x = innerRadius * this.sin(a);
               float y = innerRadius * this.cos(a);
               GL11.glVertex2f(x, y);
            }

            GL11.glEnd();
         }

         GL11.glBegin(2);

         for(float a = 0.0F; a < 6.2831855F; a += da) {
            float x = outerRadius * this.sin(a);
            float y = outerRadius * this.cos(a);
            GL11.glVertex2f(x, y);
         }

         GL11.glEnd();
         break;
      default:
         return;
      }

   }
}
