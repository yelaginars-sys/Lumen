package dlc.lumen.api.utils.cmd.waypoint;

import lombok.Generated;

public class Waypoint {
   private double x;
   private double y;
   private double z;
   private String name;

   @Generated
   public Waypoint(double x, double y, double z, String name) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.name = name;
   }

   @Generated
   public double getX() {
      return this.x;
   }

   @Generated
   public double getY() {
      return this.y;
   }

   @Generated
   public double getZ() {
      return this.z;
   }

   @Generated
   public String getName() {
      return this.name;
   }
}
