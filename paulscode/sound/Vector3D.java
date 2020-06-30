package paulscode.sound;

public class Vector3D {
   public float x;
   public float y;
   public float z;

   public Vector3D() {
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = 0.0F;
   }

   public Vector3D(float nx, float ny, float nz) {
      this.x = nx;
      this.y = ny;
      this.z = nz;
   }

   public Vector3D clone() {
      return new Vector3D(this.x, this.y, this.z);
   }

   public Vector3D cross(Vector3D A, Vector3D B) {
      return new Vector3D(A.y * B.z - B.y * A.z, A.z * B.x - B.z * A.x, A.x * B.y - B.x * A.y);
   }

   public Vector3D cross(Vector3D B) {
      return new Vector3D(this.y * B.z - B.y * this.z, this.z * B.x - B.z * this.x, this.x * B.y - B.x * this.y);
   }

   public float dot(Vector3D A, Vector3D B) {
      return A.x * B.x + A.y * B.y + A.z * B.z;
   }

   public float dot(Vector3D B) {
      return this.x * B.x + this.y * B.y + this.z * B.z;
   }

   public Vector3D add(Vector3D A, Vector3D B) {
      return new Vector3D(A.x + B.x, A.y + B.y, A.z + B.z);
   }

   public Vector3D add(Vector3D B) {
      return new Vector3D(this.x + B.x, this.y + B.y, this.z + B.z);
   }

   public Vector3D subtract(Vector3D A, Vector3D B) {
      return new Vector3D(A.x - B.x, A.y - B.y, A.z - B.z);
   }

   public Vector3D subtract(Vector3D B) {
      return new Vector3D(this.x - B.x, this.y - B.y, this.z - B.z);
   }

   public float length() {
      return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
   }

   public void normalize() {
      double t = Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
      this.x = (float)((double)this.x / t);
      this.y = (float)((double)this.y / t);
      this.z = (float)((double)this.z / t);
   }

   public String toString() {
      return "Vector3D (" + this.x + ", " + this.y + ", " + this.z + ")";
   }
}
