package dlc.lumen.client.render.models;

import net.minecraft.util.Identifier;

public enum CustomModelType {
   DEFAULT("default", "Default", null, null, 1.0F, 1.0F, 1.0F, 0.0F, 34.0F, -8.0F),
   AMOGUS("amogus", "Amogus", Identifier.of("lumen", "textures/models/amogus.png"), null, 1.0F, 1.0F, 1.0F, -0.5F, 34.0F, -14.0F),
   RABBIT("rabbit", "Crazy Rabbit", Identifier.of("lumen", "textures/models/rabbit.png"), null, 1.25F, 1.25F, 1.25F, -0.3F, 32.0F, -10.0F),
   WHITE_DEMON("white_demon", "White Demon", Identifier.of("lumen", "textures/models/whitedemon.png"), null, 1.0F, 1.0F, 1.0F, 0.0F, 34.0F, -8.0F),
   RED_DEMON("red_demon", "Red Demon", Identifier.of("lumen", "textures/models/reddemon.png"), null, 1.0F, 1.0F, 1.0F, 0.0F, 34.0F, -8.0F),
   FREDDY("freddy", "Freddy Bear", Identifier.of("lumen", "textures/models/freddy.png"), null, 0.75F, 0.65F, 0.75F, 0.85F, 28.0F, 4.0F),
   TETO("teto", "Teto", null, Identifier.of("lumen", "models/teto.gltf"), 0.85F, 0.85F, 0.85F, 0.02F, 34.0F, -8.0F);

   private final String text;
   private final String text2;
   private final Identifier textureId;
   private final Identifier textureId2;
   private final float volume;
   private final float volume2;
   private final float volume3;
   private final float volume4;
   private final float volume5;
   private final float volume6;

   CustomModelType(
      String id,
      String displayName,
      Identifier texture,
      Identifier modelResource,
      float renderScaleX,
      float renderScaleY,
      float renderScaleZ,
      float renderOffsetY,
      float previewSize,
      float previewLookYOffset
   ) {
      this.text = id;
      this.text2 = displayName;
      this.textureId = texture;
      this.textureId2 = modelResource;
      this.volume = renderScaleX;
      this.volume2 = renderScaleY;
      this.volume3 = renderScaleZ;
      this.volume4 = renderOffsetY;
      this.volume5 = previewSize;
      this.volume6 = previewLookYOffset;
   }

   public String id() {
      return this.text;
   }

   public String displayName() {
      return this.text2;
   }

   public Identifier texture() {
      return this.textureId;
   }

   public Identifier modelResource() {
      return this.textureId2;
   }

   public float renderScaleX() {
      return this.volume;
   }

   public float renderScaleY() {
      return this.volume2;
   }

   public float renderScaleZ() {
      return this.volume3;
   }

   public float renderOffsetY() {
      return this.volume4;
   }

   public float previewSize() {
      return this.volume5;
   }

   public float previewLookYOffset() {
      return this.volume6;
   }

   public boolean isCustom() {
      return this != DEFAULT;
   }

   public boolean usesStaticTextureModel() {
      return this.textureId != null;
   }

   public boolean usesGltfModel() {
      return this.textureId2 != null;
   }

   public static CustomModelType fromId(String id) {
      if (id != null && !id.isBlank()) {
         for (CustomModelType var4 : values()) {
            if (var4.text.equalsIgnoreCase(id) || var4.name().equalsIgnoreCase(id)) {
               return var4;
            }
         }

         return DEFAULT;
      } else {
         return DEFAULT;
      }
   }
}
