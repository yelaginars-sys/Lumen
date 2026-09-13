package dlc.lumen.client.autobuy;

public class Marker {
   public Marker.Type type;
   public String key;
   public String value;

   public Marker() {
   }

   public Marker(Marker.Type type, String key, String value) {
      this.type = type;
      this.key = key;
      this.value = value;
   }

   public static Marker pbv(String key, String value) {
      return new Marker(Marker.Type.PBV, key, value);
   }

   public static Marker customEnch(String key) {
      return new Marker(Marker.Type.CUSTOM_ENCH, key, null);
   }

   public static Marker customData(String key, String value) {
      return new Marker(Marker.Type.CUSTOM_DATA, key, value);
   }

   @Override
   public String toString() {
      return this.type + "{" + this.key + (this.value == null ? "" : "=" + this.value) + "}";
   }

   public enum Type {
      PBV,
      CUSTOM_ENCH,
      CUSTOM_DATA;
   }
}
