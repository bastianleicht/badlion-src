package org.lwjgl.util.glu;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Quadric;

public class Sphere extends Quadric {
   public void draw(float radius, int slices, int stacks) {
      boolean normals = super.normals != 100002;
      float nsign;
      if(super.orientation == 100021) {
         nsign = -1.0F;
      } else {
         nsign = 1.0F;
      }

      float drho = 3.1415927F / (float)stacks;
      float dtheta = 6.2831855F / (float)slices;
      if(super.drawStyle == 100012) {
         if(!super.textureFlag) {
            GL11.glBegin(6);
            GL11.glNormal3f(0.0F, 0.0F, 1.0F);
            GL11.glVertex3f(0.0F, 0.0F, nsign * radius);

            for(int j = 0; j <= slices; ++j) {
               float theta = j == slices?0.0F:(float)j * dtheta;
               float x = -this.sin(theta) * this.sin(drho);
               float y = this.cos(theta) * this.sin(drho);
               float z = nsign * this.cos(drho);
               if(normals) {
                  GL11.glNormal3f(x * nsign, y * nsign, z * nsign);
               }

               GL11.glVertex3f(x * radius, y * radius, z * radius);
            }

            GL11.glEnd();
         }

         float ds = 1.0F / (float)slices;
         float dt = 1.0F / (float)stacks;
         float t = 1.0F;
         int imin;
         int imax;
         if(super.textureFlag) {
            imin = 0;
            imax = stacks;
         } else {
            imin = 1;
            imax = stacks - 1;
         }

         for(int i = imin; i < imax; ++i) {
            float rho = (float)i * drho;
            GL11.glBegin(8);
            float s = 0.0F;

            for(int j = 0; j <= slices; ++j) {
               float theta = j == slices?0.0F:(float)j * dtheta;
               float x = -this.sin(theta) * this.sin(rho);
               float y = this.cos(theta) * this.sin(rho);
               float z = nsign * this.cos(rho);
               if(normals) {
                  GL11.glNormal3f(x * nsign, y * nsign, z * nsign);
               }

               this.TXTR_COORD(s, t);
               GL11.glVertex3f(x * radius, y * radius, z * radius);
               x = -this.sin(theta) * this.sin(rho + drho);
               y = this.cos(theta) * this.sin(rho + drho);
               z = nsign * this.cos(rho + drho);
               if(normals) {
                  GL11.glNormal3f(x * nsign, y * nsign, z * nsign);
               }

               this.TXTR_COORD(s, t - dt);
               s += ds;
               GL11.glVertex3f(x * radius, y * radius, z * radius);
            }

            GL11.glEnd();
            t -= dt;
         }

         if(!super.textureFlag) {
            GL11.glBegin(6);
            GL11.glNormal3f(0.0F, 0.0F, -1.0F);
            GL11.glVertex3f(0.0F, 0.0F, -radius * nsign);
            float rho = 3.1415927F - drho;
            float s = 1.0F;

            for(int j = slices; j >= 0; --j) {
               float theta = j == slices?0.0F:(float)j * dtheta;
               float x = -this.sin(theta) * this.sin(rho);
               float y = this.cos(theta) * this.sin(rho);
               float z = nsign * this.cos(rho);
               if(normals) {
                  GL11.glNormal3f(x * nsign, y * nsign, z * nsign);
               }

               s -= ds;
               GL11.glVertex3f(x * radius, y * radius, z * radius);
            }

            GL11.glEnd();
         }
      } else if(super.drawStyle != 100011 && super.drawStyle != 100013) {
         if(super.drawStyle == 100010) {
            GL11.glBegin(0);
            if(normals) {
               GL11.glNormal3f(0.0F, 0.0F, nsign);
            }

            GL11.glVertex3f(0.0F, 0.0F, radius);
            if(normals) {
               GL11.glNormal3f(0.0F, 0.0F, -nsign);
            }

            GL11.glVertex3f(0.0F, 0.0F, -radius);

            for(int i = 1; i < stacks - 1; ++i) {
               float rho = (float)i * drho;

               for(int j = 0; j < slices; ++j) {
                  float theta = (float)j * dtheta;
                  float x = this.cos(theta) * this.sin(rho);
                  float y = this.sin(theta) * this.sin(rho);
                  float z = this.cos(rho);
                  if(normals) {
                     GL11.glNormal3f(x * nsign, y * nsign, z * nsign);
                  }

                  GL11.glVertex3f(x * radius, y * radius, z * radius);
               }
            }

            GL11.glEnd();
         }
      } else {
         for(int i = 1; i < stacks; ++i) {
            float rho = (float)i * drho;
            GL11.glBegin(2);

            for(int j = 0; j < slices; ++j) {
               float theta = (float)j * dtheta;
               float x = this.cos(theta) * this.sin(rho);
               float y = this.sin(theta) * this.sin(rho);
               float z = this.cos(rho);
               if(normals) {
                  GL11.glNormal3f(x * nsign, y * nsign, z * nsign);
               }

               GL11.glVertex3f(x * radius, y * radius, z * radius);
            }

            GL11.glEnd();
         }

         for(int j = 0; j < slices; ++j) {
            float theta = (float)j * dtheta;
            GL11.glBegin(3);

            for(int i = 0; i <= stacks; ++i) {
               float rho = (float)i * drho;
               float x = this.cos(theta) * this.sin(rho);
               float y = this.sin(theta) * this.sin(rho);
               float z = this.cos(rho);
               if(normals) {
                  GL11.glNormal3f(x * nsign, y * nsign, z * nsign);
               }

               GL11.glVertex3f(x * radius, y * radius, z * radius);
            }

            GL11.glEnd();
         }
      }

   }
}
