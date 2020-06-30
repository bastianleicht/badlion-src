package paulscode.sound;

import paulscode.sound.Vector3D;

public class ListenerData {
   public Vector3D position;
   public Vector3D lookAt;
   public Vector3D up;
   public Vector3D velocity;
   public float angle = 0.0F;

   public ListenerData() {
      this.position = new Vector3D(0.0F, 0.0F, 0.0F);
      this.lookAt = new Vector3D(0.0F, 0.0F, -1.0F);
      this.up = new Vector3D(0.0F, 1.0F, 0.0F);
      this.velocity = new Vector3D(0.0F, 0.0F, 0.0F);
      this.angle = 0.0F;
   }

   public ListenerData(float pX, float pY, float pZ, float lX, float lY, float lZ, float uX, float uY, float uZ, float a) {
      this.position = new Vector3D(pX, pY, pZ);
      this.lookAt = new Vector3D(lX, lY, lZ);
      this.up = new Vector3D(uX, uY, uZ);
      this.velocity = new Vector3D(0.0F, 0.0F, 0.0F);
      this.angle = a;
   }

   public ListenerData(Vector3D p, Vector3D l, Vector3D u, float a) {
      this.position = p.clone();
      this.lookAt = l.clone();
      this.up = u.clone();
      this.velocity = new Vector3D(0.0F, 0.0F, 0.0F);
      this.angle = a;
   }

   public void setData(float pX, float pY, float pZ, float lX, float lY, float lZ, float uX, float uY, float uZ, float a) {
      this.position.x = pX;
      this.position.y = pY;
      this.position.z = pZ;
      this.lookAt.x = lX;
      this.lookAt.y = lY;
      this.lookAt.z = lZ;
      this.up.x = uX;
      this.up.y = uY;
      this.up.z = uZ;
      this.angle = a;
   }

   public void setData(Vector3D p, Vector3D l, Vector3D u, float a) {
      this.position.x = p.x;
      this.position.y = p.y;
      this.position.z = p.z;
      this.lookAt.x = l.x;
      this.lookAt.y = l.y;
      this.lookAt.z = l.z;
      this.up.x = u.x;
      this.up.y = u.y;
      this.up.z = u.z;
      this.angle = a;
   }

   public void setData(ListenerData l) {
      this.position.x = l.position.x;
      this.position.y = l.position.y;
      this.position.z = l.position.z;
      this.lookAt.x = l.lookAt.x;
      this.lookAt.y = l.lookAt.y;
      this.lookAt.z = l.lookAt.z;
      this.up.x = l.up.x;
      this.up.y = l.up.y;
      this.up.z = l.up.z;
      this.angle = l.angle;
   }

   public void setPosition(float x, float y, float z) {
      this.position.x = x;
      this.position.y = y;
      this.position.z = z;
   }

   public void setPosition(Vector3D p) {
      this.position.x = p.x;
      this.position.y = p.y;
      this.position.z = p.z;
   }

   public void setOrientation(float lX, float lY, float lZ, float uX, float uY, float uZ) {
      this.lookAt.x = lX;
      this.lookAt.y = lY;
      this.lookAt.z = lZ;
      this.up.x = uX;
      this.up.y = uY;
      this.up.z = uZ;
   }

   public void setOrientation(Vector3D l, Vector3D u) {
      this.lookAt.x = l.x;
      this.lookAt.y = l.y;
      this.lookAt.z = l.z;
      this.up.x = u.x;
      this.up.y = u.y;
      this.up.z = u.z;
   }

   public void setVelocity(Vector3D v) {
      this.velocity.x = v.x;
      this.velocity.y = v.y;
      this.velocity.z = v.z;
   }

   public void setVelocity(float x, float y, float z) {
      this.velocity.x = x;
      this.velocity.y = y;
      this.velocity.z = z;
   }

   public void setAngle(float a) {
      this.angle = a;
      this.lookAt.x = -1.0F * (float)Math.sin((double)this.angle);
      this.lookAt.z = -1.0F * (float)Math.cos((double)this.angle);
   }
}
