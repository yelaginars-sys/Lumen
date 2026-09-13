package dlc.lumen.client.render.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

final class GltfPlayerModelRenderer {
   private static final GltfPlayerModelRenderer.PoseCapture GLTF_PLAYER_MODEL_RENDERER = new GltfPlayerModelRenderer.PoseCapture();
   private static final Map<CustomModelType, GltfPlayerModelRenderer.BlockbenchGltfModel> ENUM_MAP = new EnumMap<>(CustomModelType.class);
   private static final Map<Identifier, GltfPlayerModelRenderer.BlockbenchGltfModel> IDENTIFIERS = new HashMap<>();
   private static final Set<Identifier> IDENTIFIERS2 = new HashSet<>();
   private static final Identifier TEXTURE_ID = Identifier.of("lumen", "textures/models/runtime/gltf_white");
   private static boolean flag;

   private GltfPlayerModelRenderer() {
   }

   static void renderInGui(DrawContext context, Identifier modelId, String texName, float centerX, float centerY, float boxSize, float spinDeg) {
      GltfPlayerModelRenderer.BlockbenchGltfModel var7 = ensureTexture(modelId, texName);
      if (var7 != null) {
         float var8 = boxSize / var7.maxDim();
         MatrixStack var9 = context.getMatrices();
         var9.push();
         var9.translate(centerX, centerY, 100.0);
         var9.multiplyPositionMatrix(new Matrix4f().scaling(var8, var8, -var8));
         var9.multiply(new Quaternionf().rotateZ((float) Math.PI));
         var9.multiply(new Quaternionf().rotateX((float)Math.toRadians(15.0)));
         var9.multiply(new Quaternionf().rotateY((float)Math.toRadians(spinDeg)));
         var9.translate(-var7.centerX(), -var7.centerY(), -var7.centerZ());
         DiffuseLighting.enableGuiDepthLighting();
         Immediate var10 = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
         var7.ensureTexture5(null, var9, var10, 15728880);
         var10.draw();
         var9.pop();
      }
   }

   private static GltfPlayerModelRenderer.BlockbenchGltfModel ensureTexture(Identifier modelId, String dynamicTextureName) {
      if (modelId == null) {
         return null;
      }

      synchronized (IDENTIFIERS) {
         GltfPlayerModelRenderer.BlockbenchGltfModel var3 = IDENTIFIERS.get(modelId);
         if (var3 != null) {
            return var3;
         } else if (IDENTIFIERS2.contains(modelId)) {
            return null;
         } else {
            var3 = GltfPlayerModelRenderer.BlockbenchGltfModel.ensureTexture(modelId, "cosmetic_" + dynamicTextureName);
            if (var3 == null) {
               IDENTIFIERS2.add(modelId);
               return null;
            } else {
               IDENTIFIERS.put(modelId, var3);
               return var3;
            }
         }
      }
   }

   static void renderStatic(Identifier modelId, String dynamicTextureName, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      GltfPlayerModelRenderer.BlockbenchGltfModel var5 = ensureTexture(modelId, dynamicTextureName);
      if (var5 != null) {
         var5.ensureTexture5(null, matrices, vertexConsumers, light);
      }
   }

   public static boolean render(CustomModelType type, PlayerEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      if (!type.usesGltfModel()) {
         return false;
      }

      GltfPlayerModelRenderer.BlockbenchGltfModel var5 = ensureTexture2(type);
      if (var5 == null) {
         return false;
      }

      var5.ensureTexture5(GLTF_PLAYER_MODEL_RENDERER.ensureTexture(state), matrices, vertexConsumers, light);
      return true;
   }

   private static GltfPlayerModelRenderer.BlockbenchGltfModel ensureTexture2(CustomModelType type) {
      GltfPlayerModelRenderer.BlockbenchGltfModel var1 = ENUM_MAP.get(type);
      if (var1 != null) {
         return var1;
      }

      synchronized (ENUM_MAP) {
         GltfPlayerModelRenderer.BlockbenchGltfModel var3 = ENUM_MAP.get(type);
         if (var3 != null) {
            return var3;
         }

         GltfPlayerModelRenderer.BlockbenchGltfModel var4 = GltfPlayerModelRenderer.BlockbenchGltfModel.ensureTexture(type.modelResource(), type.id());
         if (var4 != null) {
            ENUM_MAP.put(type, var4);
         }

         return var4;
      }
   }

   private static Identifier ensureTexture3(MinecraftClient client) {
      synchronized (ENUM_MAP) {
         if (flag) {
            return TEXTURE_ID;
         }

         NativeImage var2 = new NativeImage(1, 1, true);
         var2.setColorArgb(0, 0, -1);
         client.getTextureManager().registerTexture(TEXTURE_ID, new NativeImageBackedTexture(var2));
         flag = true;
         return TEXTURE_ID;
      }
   }

   private record BlockbenchGltfModel(
      List<GltfPlayerModelRenderer.GltfNode> nodes,
      int[] sceneRoots,
      List<GltfPlayerModelRenderer.GltfMesh> meshes,
      List<GltfPlayerModelRenderer.SkinData> skins,
      List<GltfPlayerModelRenderer.GltfMaterialData> materials,
      Matrix4f[] bindWorldMatrices,
      Matrix4f[] bindWorldInverseMatrices,
      float centerX,
      float centerY,
      float centerZ,
      float maxDim
   ) {

      private BlockbenchGltfModel(
         List<GltfPlayerModelRenderer.GltfNode> nodes,
         int[] sceneRoots,
         List<GltfPlayerModelRenderer.GltfMesh> meshes,
         List<GltfPlayerModelRenderer.SkinData> skins,
         List<GltfPlayerModelRenderer.GltfMaterialData> materials,
         Matrix4f[] bindWorldMatrices,
         Matrix4f[] bindWorldInverseMatrices,
         float centerX,
         float centerY,
         float centerZ,
         float maxDim
      ) {
         this.nodes = nodes;
         this.sceneRoots = sceneRoots;
         this.meshes = meshes;
         this.skins = skins;
         this.materials = materials;
         this.bindWorldMatrices = bindWorldMatrices;
         this.bindWorldInverseMatrices = bindWorldInverseMatrices;
         this.centerX = centerX;
         this.centerY = centerY;
         this.centerZ = centerZ;
         this.maxDim = maxDim;
      }

      private static GltfPlayerModelRenderer.BlockbenchGltfModel ensureTexture(Identifier modelId, String dynamicTextureName) {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2 != null && var2.getResourceManager() != null) {
            try (
               InputStream var3 = var2.getResourceManager().open(modelId);
               InputStreamReader var4 = new InputStreamReader(var3, StandardCharsets.UTF_8);
            ) {
               JsonObject var5 = JsonParser.parseReader(var4).getAsJsonObject();
               JsonArray var6 = var5.getAsJsonArray("buffers");
               byte[] var7 = var6 != null && !var6.isEmpty()
                  ? readStringOrEmpty2(var2, modelId, var6.get(0).getAsJsonObject().get("uri").getAsString())
                  : new byte[0];
               ByteBuffer var8 = ByteBuffer.wrap(var7).order(ByteOrder.LITTLE_ENDIAN);
               JsonArray var9 = var5.getAsJsonArray("bufferViews");
               JsonArray var10 = var5.getAsJsonArray("accessors");
               JsonArray var11 = var5.getAsJsonArray("meshes");
               JsonArray var12 = var5.getAsJsonArray("nodes");
               Identifier var13 = GltfPlayerModelRenderer.ensureTexture3(var2);
               List var14 = ensureTexture2(var5, var2, modelId, dynamicTextureName);
               List var15 = ensureTexture3(var5, var14, var13);
               ArrayList var16 = new ArrayList();
               if (var11 != null) {
                  for (JsonElement var18 : var11) {
                     JsonObject var19 = var18.getAsJsonObject();
                     ArrayList var20 = new ArrayList();

                     for (JsonElement var22 : var19.getAsJsonArray("primitives")) {
                        JsonObject var23 = var22.getAsJsonObject();
                        int var24 = var23.has("mode") ? var23.get("mode").getAsInt() : 4;
                        if (var24 == 4) {
                           JsonObject var25 = var23.getAsJsonObject("attributes");
                           float[] var26 = readStringOrEmpty6(var8, var9, var10, var25.get("POSITION").getAsInt(), 3);
                           float[] var27 = var25.has("TEXCOORD_0")
                              ? readStringOrEmpty6(var8, var9, var10, var25.get("TEXCOORD_0").getAsInt(), 2)
                              : new float[var26.length / 3 * 2];
                           float[] var28 = var25.has("NORMAL")
                              ? readStringOrEmpty6(var8, var9, var10, var25.get("NORMAL").getAsInt(), 3)
                              : new float[var26.length];
                           int[] var29 = var25.has("JOINTS_0") ? readStringOrEmpty7(var8, var9, var10, var25.get("JOINTS_0").getAsInt(), 4) : new int[0];
                           float[] var30 = var25.has("WEIGHTS_0") ? readStringOrEmpty8(var8, var9, var10, var25.get("WEIGHTS_0").getAsInt(), 4) : new float[0];
                           int[] var31 = var23.has("indices")
                              ? readStringOrEmpty9(var8, var9, var10, var23.get("indices").getAsInt())
                              : readStringOrEmpty11(var26.length / 3);
                           int var32 = var23.has("material") ? var23.get("material").getAsInt() : 0;
                           var20.add(new GltfPlayerModelRenderer.GltfPrimitive(var26, var27, var28, var31, var29, var30, var32));
                        }
                     }

                     var16.add(new GltfPlayerModelRenderer.GltfMesh(var20));
                  }
               }

               ArrayList var41 = new ArrayList();
               if (var12 != null) {
                  for (JsonElement var44 : var12) {
                     JsonObject var46 = var44.getAsJsonObject();
                     var41.add(
                        new GltfPlayerModelRenderer.GltfNode(
                           var46.has("name") ? var46.get("name").getAsString() : "",
                           var46.has("mesh") ? var46.get("mesh").getAsInt() : -1,
                           var46.has("skin") ? var46.get("skin").getAsInt() : -1,
                           readIntOrZero3(var46.getAsJsonArray("children")),
                           readIntOrZero4(var46.getAsJsonArray("translation"), 0.0F, 0.0F, 0.0F),
                           readIntOrZero5(var46.getAsJsonArray("rotation")),
                           readIntOrZero4(var46.getAsJsonArray("scale"), 1.0F, 1.0F, 1.0F)
                        )
                     );
                  }
               }

               ArrayList var43 = new ArrayList();
               JsonArray var45 = var5.getAsJsonArray("skins");
               if (var45 != null) {
                  for (JsonElement var49 : var45) {
                     JsonObject var51 = var49.getAsJsonObject();
                     int[] var53 = readIntOrZero3(var51.getAsJsonArray("joints"));
                     Matrix4f[] var56 = new Matrix4f[var53.length];
                     if (var51.has("inverseBindMatrices")) {
                        float[] var59 = readStringOrEmpty6(var8, var9, var10, var51.get("inverseBindMatrices").getAsInt(), 16);

                        for (int var61 = 0; var61 < var53.length; var61++) {
                           var56[var61] = readStringOrEmpty14(var59, var61 * 16);
                        }
                     } else {
                        for (int var58 = 0; var58 < var53.length; var58++) {
                           var56[var58] = new Matrix4f().identity();
                        }
                     }

                     var43.add(new GltfPlayerModelRenderer.SkinData(var53, var56));
                  }
               }

               int[] var48 = var5.has("scenes")
                  ? readIntOrZero3(var5.getAsJsonArray("scenes").get(0).getAsJsonObject().getAsJsonArray("nodes"))
                  : new int[0];
               Matrix4f[] var50 = ensureTexture4(var41, var48, null);
               Matrix4f[] var52 = new Matrix4f[var50.length];

               for (int var54 = 0; var54 < var50.length; var54++) {
                  var52[var54] = new Matrix4f(var50[var54]).invert();
               }

               float var55 = Float.MAX_VALUE;
               float var57 = Float.MAX_VALUE;
               float var60 = Float.MAX_VALUE;
               float var62 = -Float.MAX_VALUE;
               float var63 = -Float.MAX_VALUE;
               float var64 = -Float.MAX_VALUE;

               for (int var65 = 0; var65 < var41.size(); var65++) {
                  GltfPlayerModelRenderer.GltfNode var67 = (GltfPlayerModelRenderer.GltfNode)var41.get(var65);
                  if (var67.meshIndex() >= 0 && var67.meshIndex() < var16.size() && var50[var65] != null) {
                     for (GltfPlayerModelRenderer.GltfPrimitive var71 : ((GltfPlayerModelRenderer.GltfMesh)var16.get(var67.meshIndex())).primitives()) {
                        float[] var33 = var71.positions();

                        for (byte var34 = 0; var34 + 2 < var33.length; var34 += 3) {
                           Vector4f var35 = new Vector4f(var33[var34], var33[var34 + 1], var33[var34 + 2], 1.0F);
                           var50[var65].transform(var35);
                           var55 = Math.min(var55, var35.x);
                           var62 = Math.max(var62, var35.x);
                           var57 = Math.min(var57, var35.y);
                           var63 = Math.max(var63, var35.y);
                           var60 = Math.min(var60, var35.z);
                           var64 = Math.max(var64, var35.z);
                        }
                     }
                  }
               }

               boolean var66 = var62 >= var55;
               float var68 = var66 ? (var55 + var62) * 0.5F : 0.0F;
               float var70 = var66 ? (var57 + var63) * 0.5F : 0.0F;
               float var72 = var66 ? (var60 + var64) * 0.5F : 0.0F;
               float var73 = var66 ? Math.max(var62 - var55, Math.max(var63 - var57, var64 - var60)) : 1.0F;
               return new GltfPlayerModelRenderer.BlockbenchGltfModel(
                  var41, var48, var16, var43, var15, var50, var52, var68, var70, var72, Math.max(0.001F, var73)
               );
            } catch (Exception var40) {
               var40.printStackTrace();
               return null;
            }
         } else {
            return null;
         }
      }

      private static List<Identifier> ensureTexture2(JsonObject root, MinecraftClient client, Identifier modelId, String dynamicTextureName) throws IOException {
         JsonArray var4 = root.getAsJsonArray("images");
         ArrayList var5 = new ArrayList();
         if (var4 != null && !var4.isEmpty()) {
            for (int var6 = 0; var6 < var4.size(); var6++) {
               String var7 = var4.get(var6).getAsJsonObject().get("uri").getAsString();
               byte[] var8 = readStringOrEmpty2(client, modelId, var7);

               try (ByteArrayInputStream var9 = new ByteArrayInputStream(var8)) {
                  NativeImage var10 = NativeImage.read(var9);
                  NativeImageBackedTexture var11 = new NativeImageBackedTexture(var10);
                  Identifier var12 = Identifier.of("lumen", "textures/models/runtime/" + dynamicTextureName + "_" + var6);
                  client.getTextureManager().registerTexture(var12, var11);
                  var5.add(var12);
               }
            }

            return var5;
         } else {
            return var5;
         }
      }

      private static List<GltfPlayerModelRenderer.GltfMaterialData> ensureTexture3(JsonObject root, List<Identifier> textureIds, Identifier fallbackTextureId) {
         ArrayList var3 = new ArrayList();
         JsonArray var4 = root.getAsJsonArray("textures");
         int[] var5 = readIntOrZero(var4);
         JsonArray var6 = root.getAsJsonArray("materials");
         if (var6 != null && !var6.isEmpty()) {
            for (JsonElement var8 : var6) {
               JsonObject var9 = var8.getAsJsonObject();
               JsonObject var10 = var9.has("pbrMetallicRoughness") ? var9.getAsJsonObject("pbrMetallicRoughness") : null;
               float[] var11 = readIntOrZero2(var10 != null ? var10.getAsJsonArray("baseColorFactor") : null);
               Identifier var12 = fallbackTextureId;
               if (var10 != null && var10.has("baseColorTexture")) {
                  JsonObject var13 = var10.getAsJsonObject("baseColorTexture");
                  int var14 = var13.get("index").getAsInt();
                  if (var14 >= 0 && var14 < var5.length) {
                     int var15 = var5[var14];
                     if (var15 >= 0 && var15 < textureIds.size()) {
                        var12 = (Identifier)textureIds.get(var15);
                     }
                  }
               }

               var3.add(
                  new GltfPlayerModelRenderer.GltfMaterialData(
                     var12, readStringOrEmpty(var11[0]), readStringOrEmpty(var11[1]), readStringOrEmpty(var11[2]), readStringOrEmpty(var11[3])
                  )
               );
            }

            return var3;
         } else {
            var3.add(GltfPlayerModelRenderer.GltfMaterialData.ensureTexture(fallbackTextureId));
            return var3;
         }
      }

      private static int[] readIntOrZero(JsonArray texturesJson) {
         if (texturesJson != null && !texturesJson.isEmpty()) {
            int[] var1 = new int[texturesJson.size()];

            for (int var2 = 0; var2 < texturesJson.size(); var2++) {
               JsonObject var3 = texturesJson.get(var2).getAsJsonObject();
               var1[var2] = var3.has("source") ? var3.get("source").getAsInt() : -1;
            }

            return var1;
         } else {
            return new int[0];
         }
      }

      private static float[] readIntOrZero2(JsonArray factorJson) {
         return factorJson != null && !factorJson.isEmpty()
            ? new float[]{
               factorJson.get(0).getAsFloat(),
               factorJson.get(1).getAsFloat(),
               factorJson.get(2).getAsFloat(),
               factorJson.size() > 3 ? factorJson.get(3).getAsFloat() : 1.0F
            }
            : new float[]{1.0F, 1.0F, 1.0F, 1.0F};
      }

      private static int readStringOrEmpty(float value) {
         return Math.max(0, Math.min(255, Math.round(value * 255.0F)));
      }

      private static byte[] readStringOrEmpty2(MinecraftClient client, Identifier modelId, String uri) throws IOException {
         if (uri.startsWith("data:")) {
            return readStringOrEmpty3(uri);
         }

         Identifier var3 = readStringOrEmpty4(modelId, uri);

         try (InputStream var4 = client.getResourceManager().open(var3)) {
            return var4.readAllBytes();
         }
      }

      private static byte[] readStringOrEmpty3(String uri) {
         int var1 = uri.indexOf(44);
         if (var1 < 0) {
            throw new IllegalArgumentException("Expected embedded data URI");
         } else {
            return Base64.getDecoder().decode(uri.substring(var1 + 1));
         }
      }

      private static Identifier readStringOrEmpty4(Identifier base, String relativePath) {
         String var2 = base.getPath();
         int var3 = var2.lastIndexOf(47);
         String var4 = var3 < 0 ? "" : var2.substring(0, var3 + 1);
         return Identifier.of(base.getNamespace(), readStringOrEmpty5(var4 + relativePath));
      }

      private static String readStringOrEmpty5(String path) {
         String var1 = path.replace('\\', '/');
         ArrayDeque var2 = new ArrayDeque();

         for (String var6 : var1.split("/")) {
            if (!var6.isEmpty() && !".".equals(var6)) {
               if ("..".equals(var6)) {
                  if (!var2.isEmpty()) {
                     var2.removeLast();
                  }
               } else {
                  var2.addLast(var6);
               }
            }
         }

         return String.join("/", var2);
      }

      private static float[] readStringOrEmpty6(ByteBuffer source, JsonArray bufferViewsJson, JsonArray accessorsJson, int accessorIndex, int expectedComponents) {
         JsonObject var5 = accessorsJson.get(accessorIndex).getAsJsonObject();
         int var6 = readStringOrEmpty12(var5.get("type").getAsString());
         if (var6 != expectedComponents) {
            throw new IllegalStateException("Unexpected accessor component count");
         }

         JsonObject var7 = bufferViewsJson.get(var5.get("bufferView").getAsInt()).getAsJsonObject();
         ByteBuffer var8 = readStringOrEmpty10(source, var7, var5);
         int var9 = var5.get("count").getAsInt();
         int var10 = var7.has("byteStride") ? var7.get("byteStride").getAsInt() : var6 * 4;
         float[] var11 = new float[var9 * var6];

         for (int var12 = 0; var12 < var9; var12++) {
            int var13 = var12 * var10;

            for (int var14 = 0; var14 < var6; var14++) {
               var11[var12 * var6 + var14] = var8.getFloat(var13 + var14 * 4);
            }
         }

         return var11;
      }

      private static int[] readStringOrEmpty7(ByteBuffer source, JsonArray bufferViewsJson, JsonArray accessorsJson, int accessorIndex, int expectedComponents) {
         JsonObject var5 = accessorsJson.get(accessorIndex).getAsJsonObject();
         int var6 = readStringOrEmpty12(var5.get("type").getAsString());
         if (var6 != expectedComponents) {
            throw new IllegalStateException("Unexpected accessor component count");
         }

         JsonObject var7 = bufferViewsJson.get(var5.get("bufferView").getAsInt()).getAsJsonObject();
         ByteBuffer var8 = readStringOrEmpty10(source, var7, var5);
         int var9 = var5.get("count").getAsInt();
         int var10 = var5.get("componentType").getAsInt();
         int var11 = readStringOrEmpty13(var10);
         int var12 = var7.has("byteStride") ? var7.get("byteStride").getAsInt() : var6 * var11;
         int[] var13 = new int[var9 * var6];

         for (int var14 = 0; var14 < var9; var14++) {
            int var15 = var14 * var12;

            for (int var16 = 0; var16 < var6; var16++) {
               int var17 = var15 + var16 * var11;

               var13[var14 * var6 + var16] = switch (var10) {
                  case 5121 -> Byte.toUnsignedInt(var8.get(var17));
                  case 5123 -> Short.toUnsignedInt(var8.getShort(var17));
                  default -> throw new IllegalStateException("Unsupported joint component type: " + var10);
               };
            }
         }

         return var13;
      }

      private static float[] readStringOrEmpty8(ByteBuffer source, JsonArray bufferViewsJson, JsonArray accessorsJson, int accessorIndex, int expectedComponents) {
         JsonObject var5 = accessorsJson.get(accessorIndex).getAsJsonObject();
         int var6 = readStringOrEmpty12(var5.get("type").getAsString());
         if (var6 != expectedComponents) {
            throw new IllegalStateException("Unexpected accessor component count");
         }

         JsonObject var7 = bufferViewsJson.get(var5.get("bufferView").getAsInt()).getAsJsonObject();
         ByteBuffer var8 = readStringOrEmpty10(source, var7, var5);
         int var9 = var5.get("count").getAsInt();
         int var10 = var5.get("componentType").getAsInt();
         int var11 = readStringOrEmpty13(var10);
         int var12 = var7.has("byteStride") ? var7.get("byteStride").getAsInt() : var6 * var11;
         boolean var13 = var5.has("normalized") && var5.get("normalized").getAsBoolean();
         float[] var14 = new float[var9 * var6];

         for (int var15 = 0; var15 < var9; var15++) {
            int var16 = var15 * var12;

            for (int var17 = 0; var17 < var6; var17++) {
               int var18 = var16 + var17 * var11;

               var14[var15 * var6 + var17] = switch (var10) {
                  case 5121 -> {
                     int var20 = Byte.toUnsignedInt(var8.get(var18));
                     yield var13 ? var20 / 255.0F : var20;
                  }
                  case 5123 -> {
                     int var19 = Short.toUnsignedInt(var8.getShort(var18));
                     yield var13 ? var19 / 65535.0F : var19;
                  }
                  case 5126 -> var8.getFloat(var18);
                  default -> throw new IllegalStateException("Unsupported weight component type: " + var10);
               };
            }
         }

         return var14;
      }

      private static int[] readStringOrEmpty9(ByteBuffer source, JsonArray bufferViewsJson, JsonArray accessorsJson, int accessorIndex) {
         JsonObject var4 = accessorsJson.get(accessorIndex).getAsJsonObject();
         JsonObject var5 = bufferViewsJson.get(var4.get("bufferView").getAsInt()).getAsJsonObject();
         ByteBuffer var6 = readStringOrEmpty10(source, var5, var4);
         int var7 = var4.get("count").getAsInt();
         int var8 = var4.get("componentType").getAsInt();
         int var9 = readStringOrEmpty13(var8);
         int[] var10 = new int[var7];

         for (int var11 = 0; var11 < var7; var11++) {
            int var12 = var11 * var9;

            var10[var11] = switch (var8) {
               case 5121 -> Byte.toUnsignedInt(var6.get(var12));
               default -> throw new IllegalStateException("Unsupported index component type: " + var8);
               case 5123 -> Short.toUnsignedInt(var6.getShort(var12));
               case 5125 -> var6.getInt(var12);
            };
         }

         return var10;
      }

      private static ByteBuffer readStringOrEmpty10(ByteBuffer source, JsonObject bufferView, JsonObject accessor) {
         int var3 = bufferView.has("byteOffset") ? bufferView.get("byteOffset").getAsInt() : 0;
         int var4 = accessor.has("byteOffset") ? accessor.get("byteOffset").getAsInt() : 0;
         ByteBuffer var5 = source.duplicate().order(ByteOrder.LITTLE_ENDIAN);
         var5.position(var3 + var4);
         return var5.slice().order(ByteOrder.LITTLE_ENDIAN);
      }

      private static int[] readStringOrEmpty11(int vertexCount) {
         int[] var1 = new int[vertexCount];
         int var2 = 0;

         while (var2 < vertexCount) {
            var1[var2] = var2++;
         }

         return var1;
      }

      private static int[] readIntOrZero3(JsonArray array) {
         if (array != null && !array.isEmpty()) {
            int[] var1 = new int[array.size()];

            for (int var2 = 0; var2 < array.size(); var2++) {
               var1[var2] = array.get(var2).getAsInt();
            }

            return var1;
         } else {
            return new int[0];
         }
      }

      private static Vector3f readIntOrZero4(JsonArray array, float x, float y, float z) {
         return array != null && !array.isEmpty()
            ? new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat())
            : new Vector3f(x, y, z);
      }

      private static Quaternionf readIntOrZero5(JsonArray array) {
         return array != null && !array.isEmpty()
            ? new Quaternionf(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat())
            : new Quaternionf();
      }

      private static int readStringOrEmpty12(String type) {
         return switch (type) {
            case "SCALAR" -> 1;
            case "VEC2" -> 2;
            case "VEC3" -> 3;
            case "VEC4" -> 4;
            case "MAT4" -> 16;
            default -> throw new IllegalStateException("Unsupported accessor type: " + type);
         };
      }

      private static int readStringOrEmpty13(int componentType) {
         return switch (componentType) {
            case 5121 -> 1;
            default -> throw new IllegalStateException("Unsupported component type: " + componentType);
            case 5123 -> 2;
            case 5125, 5126 -> 4;
         };
      }

      private static Matrix4f readStringOrEmpty14(float[] values, int offset) {
         return new Matrix4f()
            .set(
               values[offset],
               values[offset + 1],
               values[offset + 2],
               values[offset + 3],
               values[offset + 4],
               values[offset + 5],
               values[offset + 6],
               values[offset + 7],
               values[offset + 8],
               values[offset + 9],
               values[offset + 10],
               values[offset + 11],
               values[offset + 12],
               values[offset + 13],
               values[offset + 14],
               values[offset + 15]
            );
      }

      private static Matrix4f[] ensureTexture4(List<GltfPlayerModelRenderer.GltfNode> nodes, int[] sceneRoots, GltfPlayerModelRenderer.PlayerPose pose) {
         Matrix4f[] var3 = new Matrix4f[nodes.size()];

         for (int var7 : sceneRoots) {
            helper(nodes, var7, new Matrix4f().identity(), pose, var3);
         }

         return var3;
      }

      private static void helper(
         List<GltfPlayerModelRenderer.GltfNode> nodes, int nodeIndex, Matrix4f parentWorld, GltfPlayerModelRenderer.PlayerPose pose, Matrix4f[] worldMatrices
      ) {
         GltfPlayerModelRenderer.GltfNode var5 = (GltfPlayerModelRenderer.GltfNode)nodes.get(nodeIndex);
         Matrix4f var6 = new Matrix4f(parentWorld).mul(var5.ensureTexture(helper2(var5.name(), pose)));
         worldMatrices[nodeIndex] = var6;

         for (int var10 : var5.children()) {
            helper(nodes, var10, var6, pose, worldMatrices);
         }
      }

      private static Quaternionf helper2(String nodeName, GltfPlayerModelRenderer.PlayerPose pose) {
         if (pose != null && nodeName != null && !nodeName.isBlank()) {
            GltfPlayerModelRenderer.PartPose var2 = switch (nodeName) {
               case "Head" -> pose.head();
               case "Torso" -> pose.body();
               case "LeftArm" -> pose.leftArm();
               case "RightArm" -> pose.rightArm();
               case "LeftLeg" -> pose.leftLeg();
               case "RightLeg" -> pose.rightLeg();
               default -> null;
            };
            if (var2 != null) {
               return var2.readIntOrZero();
            }

            String var5 = nodeName.toLowerCase(Locale.ROOT);
            GltfPlayerModelRenderer.PartPose var6 = null;
            if (var5.startsWith("bip_head")) {
               var6 = pose.head();
            } else if (var5.startsWith("bip_neck")) {
               var6 = pose.head().ensureTexture2(0.45F);
            } else if (var5.startsWith("bip_pelvis")) {
               var6 = pose.body().ensureTexture3(0.2F, 0.35F, 0.2F);
            } else if (var5.startsWith("bip_spine_0")) {
               var6 = pose.body().ensureTexture3(0.35F, 0.35F, 0.25F);
            } else if (var5.startsWith("bip_spine_1")) {
               var6 = pose.body().ensureTexture3(0.45F, 0.45F, 0.35F);
            } else if (var5.startsWith("bip_upperarm_l")) {
               var6 = pose.leftArm();
            } else if (var5.startsWith("bip_lowerarm_l")) {
               var6 = pose.leftArm().ensureTexture3(0.18F, 0.06F, 0.08F);
            } else if (var5.startsWith("bip_hand_l")) {
               var6 = pose.leftArm().ensureTexture3(0.08F, 0.04F, 0.04F);
            } else if (var5.startsWith("bip_upperarm_r")) {
               var6 = pose.rightArm();
            } else if (var5.startsWith("bip_lowerarm_r")) {
               var6 = pose.rightArm().ensureTexture3(0.18F, 0.06F, 0.08F);
            } else if (var5.startsWith("bip_hand_r")) {
               var6 = pose.rightArm().ensureTexture3(0.08F, 0.04F, 0.04F);
            } else if (var5.startsWith("bip_hip_l")) {
               var6 = pose.leftLeg().ensureTexture3(1.0F, 0.3F, 0.1F);
            } else if (var5.startsWith("bip_knee_l")) {
               var6 = pose.leftLeg().ensureTexture3(0.12F, 0.0F, 0.0F);
            } else if (var5.startsWith("bip_foot_l")) {
               var6 = pose.leftLeg().ensureTexture3(-0.08F, 0.0F, 0.0F);
            } else if (var5.startsWith("bip_hip_r")) {
               var6 = pose.rightLeg().ensureTexture3(1.0F, 0.3F, 0.1F);
            } else if (var5.startsWith("bip_knee_r")) {
               var6 = pose.rightLeg().ensureTexture3(0.12F, 0.0F, 0.0F);
            } else if (var5.startsWith("bip_foot_r")) {
               var6 = pose.rightLeg().ensureTexture3(-0.08F, 0.0F, 0.0F);
            } else if (var5.startsWith("headx")) {
               var6 = pose.head();
            } else if (var5.startsWith("neckx")) {
               var6 = pose.head().ensureTexture2(0.45F);
            } else if (var5.startsWith("rootx")) {
               var6 = pose.body().ensureTexture3(0.12F, 0.18F, 0.08F);
            } else if (var5.startsWith("spine_01x")) {
               var6 = pose.body().ensureTexture3(0.35F, 0.35F, 0.25F);
            } else if (var5.startsWith("spine_02x")) {
               var6 = pose.body().ensureTexture3(0.45F, 0.45F, 0.35F);
            } else if (var5.startsWith("shoulderl")) {
               var6 = pose.leftArm().ensureTexture3(0.45F, 0.35F, 0.25F);
            } else if (var5.startsWith("arm_stretchl")) {
               var6 = pose.leftArm();
            } else if (var5.startsWith("arm_twistl")) {
               var6 = pose.leftArm().ensureTexture3(0.22F, 0.08F, 0.08F);
            } else if (var5.startsWith("forearm_stretchl")) {
               var6 = pose.leftArm().ensureTexture3(0.18F, 0.06F, 0.08F);
            } else if (var5.startsWith("forearm_twistl")) {
               var6 = pose.leftArm().ensureTexture3(0.1F, 0.04F, 0.05F);
            } else if (var5.startsWith("handl")) {
               var6 = pose.leftArm().ensureTexture3(0.08F, 0.04F, 0.04F);
            } else if (var5.startsWith("shoulderr")) {
               var6 = pose.rightArm().ensureTexture3(0.45F, 0.35F, 0.25F);
            } else if (var5.startsWith("arm_stretchr")) {
               var6 = pose.rightArm();
            } else if (var5.startsWith("arm_twistr")) {
               var6 = pose.rightArm().ensureTexture3(0.22F, 0.08F, 0.08F);
            } else if (var5.startsWith("forearm_stretchr")) {
               var6 = pose.rightArm().ensureTexture3(0.18F, 0.06F, 0.08F);
            } else if (var5.startsWith("forearm_twistr")) {
               var6 = pose.rightArm().ensureTexture3(0.1F, 0.04F, 0.05F);
            } else if (var5.startsWith("handr")) {
               var6 = pose.rightArm().ensureTexture3(0.08F, 0.04F, 0.04F);
            } else if (var5.startsWith("thigh_stretchl")) {
               var6 = pose.leftLeg().ensureTexture3(1.0F, 0.3F, 0.1F);
            } else if (var5.startsWith("thigh_twistl")) {
               var6 = pose.leftLeg().ensureTexture3(0.22F, 0.08F, 0.05F);
            } else if (var5.startsWith("leg_stretchl")) {
               var6 = pose.leftLeg().ensureTexture3(0.12F, 0.0F, 0.0F);
            } else if (var5.startsWith("leg_twistl")) {
               var6 = pose.leftLeg().ensureTexture3(0.08F, 0.0F, 0.0F);
            } else if (var5.startsWith("footl")) {
               var6 = pose.leftLeg().ensureTexture3(-0.08F, 0.0F, 0.0F);
            } else if (var5.startsWith("toes_01l")) {
               var6 = pose.leftLeg().ensureTexture3(-0.12F, 0.0F, 0.0F);
            } else if (var5.startsWith("thigh_stretchr")) {
               var6 = pose.rightLeg().ensureTexture3(1.0F, 0.3F, 0.1F);
            } else if (var5.startsWith("thigh_twistr")) {
               var6 = pose.rightLeg().ensureTexture3(0.22F, 0.08F, 0.05F);
            } else if (var5.startsWith("leg_stretchr")) {
               var6 = pose.rightLeg().ensureTexture3(0.12F, 0.0F, 0.0F);
            } else if (var5.startsWith("leg_twistr")) {
               var6 = pose.rightLeg().ensureTexture3(0.08F, 0.0F, 0.0F);
            } else if (var5.startsWith("footr")) {
               var6 = pose.rightLeg().ensureTexture3(-0.08F, 0.0F, 0.0F);
            } else if (var5.startsWith("toes_01r")) {
               var6 = pose.rightLeg().ensureTexture3(-0.12F, 0.0F, 0.0F);
            }

            return var6 == null ? null : var6.readIntOrZero();
         } else {
            return null;
         }
      }

      private void ensureTexture5(GltfPlayerModelRenderer.PlayerPose pose, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
         Matrix4f[] var5 = ensureTexture4(this.nodes, this.sceneRoots, pose);

         for (int var9 : this.sceneRoots) {
            this.helper3(var9, pose, var5, matrices, vertexConsumers, light);
         }
      }

      private void helper3(
         int nodeIndex,
         GltfPlayerModelRenderer.PlayerPose pose,
         Matrix4f[] animatedWorldMatrices,
         MatrixStack matrices,
         VertexConsumerProvider vertexConsumers,
         int light
      ) {
         GltfPlayerModelRenderer.GltfNode var7 = this.nodes.get(nodeIndex);
         matrices.push();
         this.helper4(var7, pose, matrices);
         if (var7.meshIndex() >= 0 && var7.meshIndex() < this.meshes.size()) {
            GltfPlayerModelRenderer.SkinData var8 = var7.skinIndex() >= 0 && var7.skinIndex() < this.skins.size()
               ? this.skins.get(var7.skinIndex())
               : null;
            this.meshes
               .get(var7.meshIndex())
               .ensureTexture(
                  nodeIndex, var8, animatedWorldMatrices, this.bindWorldMatrices, this.bindWorldInverseMatrices, matrices, vertexConsumers, this.materials, light
               );
         }

         for (int var11 : var7.children()) {
            this.helper3(var11, pose, animatedWorldMatrices, matrices, vertexConsumers, light);
         }

         matrices.pop();
      }

      private void helper4(GltfPlayerModelRenderer.GltfNode node, GltfPlayerModelRenderer.PlayerPose pose, MatrixStack matrices) {
         matrices.translate(node.translation().x, node.translation().y, node.translation().z);
         matrices.multiply(node.rotation());
         matrices.scale(node.scale().x, node.scale().y, node.scale().z);
         Quaternionf var4 = helper2(node.name(), pose);
         if (var4 != null) {
            matrices.multiply(var4);
         }
      }

      public List<GltfPlayerModelRenderer.GltfNode> nodes() {
         return this.nodes;
      }

      public int[] sceneRoots() {
         return this.sceneRoots;
      }

      public List<GltfPlayerModelRenderer.GltfMesh> meshes() {
         return this.meshes;
      }

      public List<GltfPlayerModelRenderer.SkinData> skins() {
         return this.skins;
      }

      public List<GltfPlayerModelRenderer.GltfMaterialData> materials() {
         return this.materials;
      }

      public Matrix4f[] bindWorldMatrices() {
         return this.bindWorldMatrices;
      }

      public Matrix4f[] bindWorldInverseMatrices() {
         return this.bindWorldInverseMatrices;
      }

      public float centerX() {
         return this.centerX;
      }

      public float centerY() {
         return this.centerY;
      }

      public float centerZ() {
         return this.centerZ;
      }

      public float maxDim() {
         return this.maxDim;
      }
   }

   private record GltfMaterialData(Identifier textureId, int red, int green, int blue, int alpha) {

      private GltfMaterialData(Identifier textureId, int red, int green, int blue, int alpha) {
         this.textureId = textureId;
         this.red = red;
         this.green = green;
         this.blue = blue;
         this.alpha = alpha;
      }

      private static GltfPlayerModelRenderer.GltfMaterialData ensureTexture(Identifier fallbackTextureId) {
         return new GltfPlayerModelRenderer.GltfMaterialData(fallbackTextureId, 255, 255, 255, 255);
      }

      public Identifier textureId() {
         return this.textureId;
      }

      public int red() {
         return this.red;
      }

      public int green() {
         return this.green;
      }

      public int blue() {
         return this.blue;
      }

      public int alpha() {
         return this.alpha;
      }
   }

   private record GltfMesh(List<GltfPlayerModelRenderer.GltfPrimitive> primitives) {

      private GltfMesh(List<GltfPlayerModelRenderer.GltfPrimitive> primitives) {
         this.primitives = primitives;
      }

      private void ensureTexture(
         int nodeIndex,
         GltfPlayerModelRenderer.SkinData skin,
         Matrix4f[] animatedWorldMatrices,
         Matrix4f[] bindWorldMatrices,
         Matrix4f[] bindWorldInverseMatrices,
         MatrixStack matrices,
         VertexConsumerProvider vertexConsumers,
         List<GltfPlayerModelRenderer.GltfMaterialData> materials,
         int light
      ) {
         for (GltfPlayerModelRenderer.GltfPrimitive var11 : this.primitives) {
            var11.ensureTexture(
               nodeIndex, skin, animatedWorldMatrices, bindWorldMatrices, bindWorldInverseMatrices, matrices, vertexConsumers, materials, light
            );
         }
      }

      public List<GltfPlayerModelRenderer.GltfPrimitive> primitives() {
         return this.primitives;
      }
   }

   private record GltfNode(String name, int meshIndex, int skinIndex, int[] children, Vector3f translation, Quaternionf rotation, Vector3f scale) {

      private GltfNode(String name, int meshIndex, int skinIndex, int[] children, Vector3f translation, Quaternionf rotation, Vector3f scale) {
         this.name = name;
         this.meshIndex = meshIndex;
         this.skinIndex = skinIndex;
         this.children = children;
         this.translation = translation;
         this.rotation = rotation;
         this.scale = scale;
      }

      private Matrix4f ensureTexture(Quaternionf animatedRotation) {
         Matrix4f var2 = new Matrix4f().translate(this.translation).rotate(this.rotation).scale(this.scale);
         if (animatedRotation != null) {
            var2.rotate(animatedRotation);
         }

         return var2;
      }

      public String name() {
         return this.name;
      }

      public int meshIndex() {
         return this.meshIndex;
      }

      public int skinIndex() {
         return this.skinIndex;
      }

      public int[] children() {
         return this.children;
      }

      public Vector3f translation() {
         return this.translation;
      }

      public Quaternionf rotation() {
         return this.rotation;
      }

      public Vector3f scale() {
         return this.scale;
      }
   }

   private record GltfPrimitive(float[] positions, float[] texCoords, float[] normals, int[] indices, int[] joints, float[] weights, int materialIndex) {

      private GltfPrimitive(float[] positions, float[] texCoords, float[] normals, int[] indices, int[] joints, float[] weights, int materialIndex) {
         this.positions = positions;
         this.texCoords = texCoords;
         this.normals = normals;
         this.indices = indices;
         this.joints = joints;
         this.weights = weights;
         this.materialIndex = materialIndex;
      }

      private void ensureTexture(
         int nodeIndex,
         GltfPlayerModelRenderer.SkinData skin,
         Matrix4f[] animatedWorldMatrices,
         Matrix4f[] bindWorldMatrices,
         Matrix4f[] bindWorldInverseMatrices,
         MatrixStack matrices,
         VertexConsumerProvider vertexConsumers,
         List<GltfPlayerModelRenderer.GltfMaterialData> materials,
         int light
      ) {
         Entry var10 = matrices.peek();
         Matrix4f var11 = var10.getPositionMatrix();
         GltfPlayerModelRenderer.GltfMaterialData var12 = materials.isEmpty()
            ? GltfPlayerModelRenderer.GltfMaterialData.ensureTexture(GltfPlayerModelRenderer.TEXTURE_ID)
            : (GltfPlayerModelRenderer.GltfMaterialData)materials.get(Math.max(0, Math.min(this.materialIndex, materials.size() - 1)));
         VertexConsumer var13 = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(var12.textureId()));

         for (byte var14 = 0; var14 + 2 < this.indices.length; var14 += 3) {
            int var15 = this.indices[var14];
            int var16 = this.indices[var14 + 1];
            int var17 = this.indices[var14 + 2];
            Vector3f var18 = this.ensureTexture3(var15, nodeIndex, skin, animatedWorldMatrices, bindWorldMatrices, bindWorldInverseMatrices);
            Vector3f var19 = this.ensureTexture3(var16, nodeIndex, skin, animatedWorldMatrices, bindWorldMatrices, bindWorldInverseMatrices);
            Vector3f var20 = this.ensureTexture3(var17, nodeIndex, skin, animatedWorldMatrices, bindWorldMatrices, bindWorldInverseMatrices);
            Vector3f var21 = this.ensureTexture2(var18, var19, var20);
            this.readIntOrZero2(
               var13, var12, var10, var11, light, var15, var18, var21, nodeIndex, skin, animatedWorldMatrices, bindWorldMatrices, bindWorldInverseMatrices
            );
            this.readIntOrZero2(
               var13, var12, var10, var11, light, var16, var19, var21, nodeIndex, skin, animatedWorldMatrices, bindWorldMatrices, bindWorldInverseMatrices
            );
            this.readIntOrZero2(
               var13, var12, var10, var11, light, var17, var20, var21, nodeIndex, skin, animatedWorldMatrices, bindWorldMatrices, bindWorldInverseMatrices
            );
            this.readIntOrZero2(
               var13, var12, var10, var11, light, var17, var20, var21, nodeIndex, skin, animatedWorldMatrices, bindWorldMatrices, bindWorldInverseMatrices
            );
         }
      }

      private Vector3f ensureTexture2(Vector3f first, Vector3f second, Vector3f third) {
         Vector3f var4 = second.sub(first, new Vector3f());
         Vector3f var5 = third.sub(first, new Vector3f());
         return var4.cross(var5, new Vector3f()).normalize();
      }

      private Vector3f ensureTexture3(
         int index,
         int nodeIndex,
         GltfPlayerModelRenderer.SkinData skin,
         Matrix4f[] animatedWorldMatrices,
         Matrix4f[] bindWorldMatrices,
         Matrix4f[] bindWorldInverseMatrices
      ) {
         int var7 = index * 3;
         float var8 = this.positions[var7];
         float var9 = this.positions[var7 + 1];
         float var10 = this.positions[var7 + 2];
         if (skin != null && this.joints.length >= index * 4 + 4 && this.weights.length >= index * 4 + 4) {
            Matrix4f var11 = bindWorldMatrices[nodeIndex];
            Matrix4f var12 = bindWorldInverseMatrices[nodeIndex];
            Vector4f var13 = new Vector4f(var8, var9, var10, 1.0F);
            var11.transform(var13);
            Vector4f var14 = new Vector4f();
            float var15 = 0.0F;
            int var16 = index * 4;

            for (int var17 = 0; var17 < 4; var17++) {
               float var18 = this.weights[var16 + var17];
               if (!(var18 <= 0.0F)) {
                  int var19 = this.joints[var16 + var17];
                  if (var19 >= 0 && var19 < skin.joints().length && var19 < skin.inverseBindMatrices().length) {
                     int var20 = skin.joints()[var19];
                     Matrix4f var21 = new Matrix4f(animatedWorldMatrices[var20]).mul(skin.inverseBindMatrices()[var19]);
                     Vector4f var22 = new Vector4f(var13);
                     var21.transform(var22);
                     var14.add(var22.mul(var18));
                     var15 += var18;
                  }
               }
            }

            if (var15 <= 0.0F) {
               return new Vector3f(var8, var9, var10);
            }

            if (var15 != 1.0F) {
               var14.div(var15);
            }

            var12.transform(var14);
            return new Vector3f(var14.x, var14.y, var14.z);
         } else {
            return new Vector3f(var8, var9, var10);
         }
      }

      private Vector3f readIntOrZero(
         int index,
         Vector3f fallbackNormal,
         int nodeIndex,
         GltfPlayerModelRenderer.SkinData skin,
         Matrix4f[] animatedWorldMatrices,
         Matrix4f[] bindWorldMatrices,
         Matrix4f[] bindWorldInverseMatrices
      ) {
         int var8 = index * 3;
         if (this.normals.length < var8 + 3) {
            return fallbackNormal;
         }

         Vector3f var9 = new Vector3f(this.normals[var8], this.normals[var8 + 1], this.normals[var8 + 2]);
         if (skin != null && this.joints.length >= index * 4 + 4 && this.weights.length >= index * 4 + 4) {
            Matrix4f var10 = bindWorldMatrices[nodeIndex];
            Matrix4f var11 = bindWorldInverseMatrices[nodeIndex];
            Vector3f var12 = new Vector3f(var9);
            var10.transformDirection(var12);
            Vector3f var13 = new Vector3f();
            float var14 = 0.0F;
            int var15 = index * 4;

            for (int var16 = 0; var16 < 4; var16++) {
               float var17 = this.weights[var15 + var16];
               if (!(var17 <= 0.0F)) {
                  int var18 = this.joints[var15 + var16];
                  if (var18 >= 0 && var18 < skin.joints().length && var18 < skin.inverseBindMatrices().length) {
                     int var19 = skin.joints()[var18];
                     Matrix4f var20 = new Matrix4f(animatedWorldMatrices[var19]).mul(skin.inverseBindMatrices()[var18]);
                     Vector3f var21 = new Vector3f(var12);
                     var20.transformDirection(var21);
                     var13.add(var21.mul(var17));
                     var14 += var17;
                  }
               }
            }

            if (var14 <= 0.0F) {
               return fallbackNormal;
            }

            var11.transformDirection(var13);
            return var13.normalize();
         } else {
            return var9.normalize();
         }
      }

      private void readIntOrZero2(
         VertexConsumer consumer,
         GltfPlayerModelRenderer.GltfMaterialData material,
         Entry entry,
         Matrix4f positionMatrix,
         int light,
         int index,
         Vector3f position,
         Vector3f fallbackNormal,
         int nodeIndex,
         GltfPlayerModelRenderer.SkinData skin,
         Matrix4f[] animatedWorldMatrices,
         Matrix4f[] bindWorldMatrices,
         Matrix4f[] bindWorldInverseMatrices
      ) {
         int var14 = index * 2;
         Vector3f var15 = this.readIntOrZero(index, fallbackNormal, nodeIndex, skin, animatedWorldMatrices, bindWorldMatrices, bindWorldInverseMatrices);
         float var16 = this.texCoords.length >= var14 + 2 ? this.texCoords[var14] : 0.0F;
         float var17 = this.texCoords.length >= var14 + 2 ? this.texCoords[var14 + 1] : 0.0F;
         consumer.vertex(positionMatrix, position.x, position.y, position.z)
            .color(material.red(), material.green(), material.blue(), material.alpha())
            .texture(var16, var17)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal(entry, var15.x, var15.y, var15.z);
      }

      public float[] positions() {
         return this.positions;
      }

      public float[] texCoords() {
         return this.texCoords;
      }

      public float[] normals() {
         return this.normals;
      }

      public int[] indices() {
         return this.indices;
      }

      public int[] joints() {
         return this.joints;
      }

      public float[] weights() {
         return this.weights;
      }

      public int materialIndex() {
         return this.materialIndex;
      }
   }

   private record PartPose(float pitch, float yaw, float roll) {

      private PartPose(float pitch, float yaw, float roll) {
         this.pitch = pitch;
         this.yaw = yaw;
         this.roll = roll;
      }

      private static GltfPlayerModelRenderer.PartPose ensureTexture(ModelPart part) {
         return new GltfPlayerModelRenderer.PartPose(part.pitch, part.yaw, part.roll);
      }

      private GltfPlayerModelRenderer.PartPose ensureTexture2(float factor) {
         return this.ensureTexture3(factor, factor, factor);
      }

      private GltfPlayerModelRenderer.PartPose ensureTexture3(float pitchFactor, float yawFactor, float rollFactor) {
         return new GltfPlayerModelRenderer.PartPose(this.pitch * pitchFactor, this.yaw * yawFactor, this.roll * rollFactor);
      }

      private Quaternionf readIntOrZero() {
         return new Quaternionf().rotationZYX(this.roll, this.yaw, this.pitch);
      }

      public float pitch() {
         return this.pitch;
      }

      public float yaw() {
         return this.yaw;
      }

      public float roll() {
         return this.roll;
      }
   }

   private record PlayerPose(
      GltfPlayerModelRenderer.PartPose head,
      GltfPlayerModelRenderer.PartPose body,
      GltfPlayerModelRenderer.PartPose leftArm,
      GltfPlayerModelRenderer.PartPose rightArm,
      GltfPlayerModelRenderer.PartPose leftLeg,
      GltfPlayerModelRenderer.PartPose rightLeg
   ) {

      private PlayerPose(
         GltfPlayerModelRenderer.PartPose head,
         GltfPlayerModelRenderer.PartPose body,
         GltfPlayerModelRenderer.PartPose leftArm,
         GltfPlayerModelRenderer.PartPose rightArm,
         GltfPlayerModelRenderer.PartPose leftLeg,
         GltfPlayerModelRenderer.PartPose rightLeg
      ) {
         this.head = head;
         this.body = body;
         this.leftArm = leftArm;
         this.rightArm = rightArm;
         this.leftLeg = leftLeg;
         this.rightLeg = rightLeg;
      }

      public GltfPlayerModelRenderer.PartPose head() {
         return this.head;
      }

      public GltfPlayerModelRenderer.PartPose body() {
         return this.body;
      }

      public GltfPlayerModelRenderer.PartPose leftArm() {
         return this.leftArm;
      }

      public GltfPlayerModelRenderer.PartPose rightArm() {
         return this.rightArm;
      }

      public GltfPlayerModelRenderer.PartPose leftLeg() {
         return this.leftLeg;
      }

      public GltfPlayerModelRenderer.PartPose rightLeg() {
         return this.rightLeg;
      }
   }

   private static final class PoseCapture {
      private final PlayerEntityModel GLTF_PLAYER_MODEL_RENDERER = new PlayerEntityModel(
         TexturedModelData.of(PlayerEntityModel.getTexturedModelData(new Dilation(0.0F), false), 64, 64).createModel(), false
      );

      private GltfPlayerModelRenderer.PlayerPose ensureTexture(PlayerEntityRenderState state) {
         this.GLTF_PLAYER_MODEL_RENDERER.setAngles(state);
         return new GltfPlayerModelRenderer.PlayerPose(
            GltfPlayerModelRenderer.PartPose.ensureTexture(this.GLTF_PLAYER_MODEL_RENDERER.head),
            GltfPlayerModelRenderer.PartPose.ensureTexture(this.GLTF_PLAYER_MODEL_RENDERER.body),
            GltfPlayerModelRenderer.PartPose.ensureTexture(this.GLTF_PLAYER_MODEL_RENDERER.leftArm),
            GltfPlayerModelRenderer.PartPose.ensureTexture(this.GLTF_PLAYER_MODEL_RENDERER.rightArm),
            GltfPlayerModelRenderer.PartPose.ensureTexture(this.GLTF_PLAYER_MODEL_RENDERER.leftLeg),
            GltfPlayerModelRenderer.PartPose.ensureTexture(this.GLTF_PLAYER_MODEL_RENDERER.rightLeg)
         );
      }
   }

   private record SkinData(int[] joints, Matrix4f[] inverseBindMatrices) {

      private SkinData(int[] joints, Matrix4f[] inverseBindMatrices) {
         this.joints = joints;
         this.inverseBindMatrices = inverseBindMatrices;
      }

      public int[] joints() {
         return this.joints;
      }

      public Matrix4f[] inverseBindMatrices() {
         return this.inverseBindMatrices;
      }
   }
}
