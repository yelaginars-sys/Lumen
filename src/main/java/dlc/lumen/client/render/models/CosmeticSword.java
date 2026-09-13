package dlc.lumen.client.render.models;

import net.minecraft.util.Identifier;

public enum CosmeticSword {
   NONE("none", "Без меча", null),
   KATANA("katana", "Катана", Identifier.of("lumen", "models/swords/katana.gltf")),
   CRIMSON("crimson", "Багровый", Identifier.of("lumen", "models/swords/crimson.gltf"));

   private final String text;
   private final String text2;
   private final Identifier textureId;

   CosmeticSword(String id, String displayName, Identifier modelResource) {
      this.text = id;
      this.text2 = displayName;
      this.textureId = modelResource;
   }

   public String id() {
      return this.text;
   }

   public String displayName() {
      return this.text2;
   }

   public Identifier modelResource() {
      return this.textureId;
   }

   public boolean isCustom() {
      return this != NONE;
   }

   public static CosmeticSword fromId(String id) {
      if (id != null && !id.isBlank()) {
         for (CosmeticSword var4 : values()) {
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
