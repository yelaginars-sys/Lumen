package dlc.lumen.client.render.models;

import net.minecraft.util.Identifier;

public enum CosmeticPet {
   NONE("none", "Без питомца", null, null, 1.0F),
   JELLIE("jellie", "Котик Jellie", helper("models/pets/jellie.obj"), helper("textures/models/pets/jellie.png"), 0.8F);

   private final String text;
   private final String text2;
   private final Identifier textureId;
   private final Identifier textureId2;
   private final float volume;

   CosmeticPet(String idStr, String displayName, Identifier obj, Identifier texture, float scale) {
      this.text = idStr;
      this.text2 = displayName;
      this.textureId = obj;
      this.textureId2 = texture;
      this.volume = scale;
   }

   private static Identifier helper(String path) {
      return Identifier.of("lumen", path);
   }

   public String id() {
      return this.text;
   }

   public String displayName() {
      return this.text2;
   }

   public Identifier obj() {
      return this.textureId;
   }

   public Identifier texture() {
      return this.textureId2;
   }

   public float scale() {
      return this.volume;
   }

   public boolean isCustom() {
      return this != NONE;
   }

   public static CosmeticPet fromId(String id) {
      if (id != null && !id.isBlank()) {
         for (CosmeticPet var4 : values()) {
            if (var4.text.equalsIgnoreCase(id) || var4.name().equalsIgnoreCase(id)) {
               return var4;
            }
         }

         return NONE;
      } else {
         return NONE;
      }
   }
}
