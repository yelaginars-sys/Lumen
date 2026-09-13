package dlc.lumen.client.render.models;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public final class CustomModelManager {
   private static final ThreadLocal<CustomModelType> THREAD_LOCAL = new ThreadLocal<>();
   private static final Map<UUID, CustomModelType> U_UIDS = new ConcurrentHashMap<>();
   private static volatile CustomModelType type2 = CustomModelType.DEFAULT;

   private CustomModelManager() {
   }

   public static List<CustomModelType> getSelectableModels() {
      return Arrays.stream(CustomModelType.values()).filter(CustomModelType::isCustom).toList();
   }

   public static CustomModelType getSelectedModel() {
      return type2;
   }

   public static void setSelectedModel(CustomModelType type) {
      type2 = type == null ? CustomModelType.DEFAULT : type;
   }

   public static void setRemoteSelections(Map<UUID, CustomModelType> remoteSelections) {
      U_UIDS.clear();
      if (remoteSelections != null) {
         U_UIDS.putAll(remoteSelections);
      }
   }

   public static boolean shouldRenderCustomModel(PlayerEntity player) {
      return resolveActiveModel(player).isCustom();
   }

   public static CustomModelType resolveActiveModel(PlayerEntity player) {
      if (player == null) {
         return CustomModelType.DEFAULT;
      }

      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 == null || var1.player == null) {
         return CustomModelType.DEFAULT;
      }

      if (player != var1.player) {
         return U_UIDS.getOrDefault(player.getUuid(), CustomModelType.DEFAULT);
      }

      CustomModelType var2 = THREAD_LOCAL.get();
      return var2 != null ? var2 : type2;
   }

   public static boolean isPreviewActive() {
      return THREAD_LOCAL.get() != null;
   }

   public static void withPreview(CustomModelType type, Runnable action) {
      THREAD_LOCAL.set(type);

      try {
         action.run();
      } finally {
         THREAD_LOCAL.remove();
      }
   }
}
