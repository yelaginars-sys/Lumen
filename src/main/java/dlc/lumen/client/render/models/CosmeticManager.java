package dlc.lumen.client.render.models;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public final class CosmeticManager {
   private static volatile CosmeticSword sword2 = CosmeticSword.NONE;

   private CosmeticManager() {
   }

   public static CosmeticSword getSelectedSword() {
      return sword2;
   }

   public static void setSelectedSword(CosmeticSword sword) {
      sword2 = sword == null ? CosmeticSword.NONE : sword;
   }

   public static List<CosmeticSword> swords() {
      return Arrays.asList(CosmeticSword.values());
   }

   public static void renderSelectedSword(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      CosmeticSword var3 = sword2;
      if (var3 != null && var3.isCustom() && var3.modelResource() != null) {
         GltfPlayerModelRenderer.renderStatic(var3.modelResource(), var3.id(), matrices, vertexConsumers, light);
      }
   }

   public static void renderSwordInGui(DrawContext context, CosmeticSword sword, float centerX, float centerY, float size, float spinDeg) {
      if (sword != null && sword.isCustom() && sword.modelResource() != null) {
         GltfPlayerModelRenderer.renderInGui(context, sword.modelResource(), sword.id(), centerX, centerY, size, spinDeg);
      }
   }
}
