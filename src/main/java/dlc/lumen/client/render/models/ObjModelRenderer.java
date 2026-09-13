package dlc.lumen.client.render.models;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class ObjModelRenderer {
   private static final Map<Identifier, ObjModelRenderer.ObjModel> IDENTIFIERS = new HashMap<>();
   private static final Set<Identifier> IDENTIFIERS2 = new HashSet<>();

   private ObjModelRenderer() {
   }

   public static void render(Identifier objId, Identifier textureId, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      ObjModelRenderer.ObjModel var5 = helper(objId, textureId);
      if (var5 != null) {
         var5.helper(matrices, vertexConsumers, light);
      }
   }

   public static void renderInGui(DrawContext context, Identifier objId, Identifier textureId, float centerX, float centerY, float size, float spinDeg) {
      MatrixStack var7 = context.getMatrices();
      var7.push();
      var7.translate(centerX, centerY, 100.0);
      var7.multiplyPositionMatrix(new Matrix4f().scaling(size, -size, size));
      var7.multiply(new Quaternionf().rotateX((float)Math.toRadians(15.0)));
      var7.multiply(new Quaternionf().rotateY((float)Math.toRadians(spinDeg)));
      DiffuseLighting.enableGuiDepthLighting();
      Immediate var8 = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
      render(objId, textureId, var7, var8, 15728880);
      var8.draw();
      var7.pop();
   }

   private static ObjModelRenderer.ObjModel helper(Identifier objId, Identifier textureId) {
      synchronized (IDENTIFIERS) {
         ObjModelRenderer.ObjModel var3 = IDENTIFIERS.get(objId);
         if (var3 != null) {
            return var3;
         } else if (IDENTIFIERS2.contains(objId)) {
            return null;
         } else {
            ObjModelRenderer.ObjModel var4 = helper2(objId, textureId);
            if (var4 == null) {
               IDENTIFIERS2.add(objId);
               return null;
            } else {
               IDENTIFIERS.put(objId, var4);
               return var4;
            }
         }
      }
   }

   private static ObjModelRenderer.ObjModel helper2(Identifier objId, Identifier textureId) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null && var2.getResourceManager() != null) {
         ArrayList var3 = new ArrayList();
         ArrayList var4 = new ArrayList();
         ArrayList var5 = new ArrayList();
         ArrayList var6 = new ArrayList();
         ArrayList var7 = new ArrayList();
         ArrayList var8 = new ArrayList();

         String var11;
         try (
            InputStream var9 = var2.getResourceManager().open(objId);
            BufferedReader var10 = new BufferedReader(new InputStreamReader(var9, StandardCharsets.UTF_8));
         ) {
            while ((var11 = var10.readLine()) != null) {
               var11 = var11.trim();
               if (!var11.isEmpty() && var11.charAt(0) != '#') {
                  String[] var12 = var11.split("\\s+");
                  switch (var12[0]) {
                     case "v":
                        var3.add(new float[]{Float.parseFloat(var12[1]) / 16.0F, Float.parseFloat(var12[2]) / 16.0F, Float.parseFloat(var12[3]) / 16.0F});
                        break;
                     case "vt":
                        var4.add(new float[]{Float.parseFloat(var12[1]), 1.0F - Float.parseFloat(var12[2])});
                        break;
                     case "vn":
                        var5.add(new float[]{Float.parseFloat(var12[1]), Float.parseFloat(var12[2]), Float.parseFloat(var12[3])});
                        break;
                     case "f":
                        int var15 = var12.length - 1;

                        for (int var16 = 1; var16 + 1 < var15; var16++) {
                           helper3(var12[1], var3, var4, var5, var6, var7, var8);
                           helper3(var12[1 + var16], var3, var4, var5, var6, var7, var8);
                           helper3(var12[2 + var16], var3, var4, var5, var6, var7, var8);
                        }
                  }
               }
            }
         } catch (Exception var21) {
            return null;
         }

         if (var6.isEmpty()) {
            return null;
         }

         float[] var22 = helper5(var6);
         float var23 = Float.MAX_VALUE;

         for (byte var25 = 1; var25 < var22.length; var25 += 3) {
            if (var22[var25] < var23) {
               var23 = var22[var25];
            }
         }

         return new ObjModelRenderer.ObjModel(var22, helper5(var7), helper5(var8), textureId, var23);
      } else {
         return null;
      }
   }

   public static float modelMinY(Identifier objId, Identifier textureId) {
      ObjModelRenderer.ObjModel var2 = helper(objId, textureId);
      return var2 == null ? 0.0F : var2.minY();
   }

   private static void helper3(
      String token, List<float[]> verts, List<float[]> uvs, List<float[]> norms, List<Float> pos, List<Float> tex, List<Float> nrm
   ) {
      String[] var7 = token.split("/");
      int var8 = helper4(var7[0], verts.size());
      float[] var9 = var8 >= 0 && var8 < verts.size() ? (float[])verts.get(var8) : new float[]{0.0F, 0.0F, 0.0F};
      pos.add(var9[0]);
      pos.add(var9[1]);
      pos.add(var9[2]);
      if (var7.length > 1 && !var7[1].isEmpty()) {
         int var10 = helper4(var7[1], uvs.size());
         float[] var11 = var10 >= 0 && var10 < uvs.size() ? (float[])uvs.get(var10) : new float[]{0.0F, 0.0F};
         tex.add(var11[0]);
         tex.add(var11[1]);
      } else {
         tex.add(0.0F);
         tex.add(0.0F);
      }

      if (var7.length > 2 && !var7[2].isEmpty()) {
         int var12 = helper4(var7[2], norms.size());
         float[] var13 = var12 >= 0 && var12 < norms.size() ? (float[])norms.get(var12) : new float[]{0.0F, 1.0F, 0.0F};
         nrm.add(var13[0]);
         nrm.add(var13[1]);
         nrm.add(var13[2]);
      } else {
         nrm.add(0.0F);
         nrm.add(1.0F);
         nrm.add(0.0F);
      }
   }

   private static int helper4(String s, int size) {
      int var2 = Integer.parseInt(s);
      return var2 < 0 ? size + var2 : var2 - 1;
   }

   private static float[] helper5(List<Float> list) {
      float[] var1 = new float[list.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = (Float)list.get(var2);
      }

      return var1;
   }

   private record ObjModel(float[] pos, float[] uv, float[] norm, Identifier texture, float minY) {

      private ObjModel(float[] pos, float[] uv, float[] norm, Identifier texture, float minY) {
         this.pos = pos;
         this.uv = uv;
         this.norm = norm;
         this.texture = texture;
         this.minY = minY;
      }

      private void helper(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
         Entry var4 = matrices.peek();
         Matrix4f var5 = var4.getPositionMatrix();
         VertexConsumer var6 = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(this.texture));
         int var7 = this.pos.length / 3;

         for (byte var8 = 0; var8 + 2 < var7; var8 += 3) {
            this.helper2(var6, var5, var4, light, var8);
            this.helper2(var6, var5, var4, light, var8 + 1);
            this.helper2(var6, var5, var4, light, var8 + 2);
            this.helper2(var6, var5, var4, light, var8 + 2);
         }
      }

      private void helper2(VertexConsumer consumer, Matrix4f positionMatrix, Entry entry, int light, int corner) {
         int var6 = corner * 3;
         int var7 = corner * 2;
         consumer.vertex(positionMatrix, this.pos[var6], this.pos[var6 + 1], this.pos[var6 + 2])
            .color(255, 255, 255, 255)
            .texture(this.uv[var7], this.uv[var7 + 1])
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal(entry, this.norm[var6], this.norm[var6 + 1], this.norm[var6 + 2]);
      }

      public float[] pos() {
         return this.pos;
      }

      public float[] uv() {
         return this.uv;
      }

      public float[] norm() {
         return this.norm;
      }

      public Identifier texture() {
         return this.texture;
      }

      public float minY() {
         return this.minY;
      }
   }
}
