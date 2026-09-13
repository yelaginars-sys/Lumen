package dlc.lumen.client.render.figura;

import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventTickPost;
import dlc.lumen.client.social.GlobalSocialManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;

public enum FriendModelManager implements QClient {
   INSTANCE;

   private static final String AVATAR_MANAGER_CLASS_NAME = "org.figuramc.figura.avatar.AvatarManager";
   private static final String USER_DATA_CLASS_NAME = "org.figuramc.figura.avatar.UserData";
   private static final String LOCAL_AVATAR_LOADER_CLASS_NAME = "org.figuramc.figura.avatar.local.LocalAvatarLoader";
   private boolean flag;
   private boolean flag2;
   private Method method;
   private Method method2;
   private Constructor<?> constructor;
   private Class<?> class2;
   private Field field;
   private Field field2;
   private final Map<UUID, String> uUIDs = new HashMap<>();
   private ClientWorld clientWorld;
   private int index;

   @EventLink
   public void onTick(EventTickPost event) {
      if (FiguraBridge.isFiguraPresent()) {
         ClientWorld var2 = mc.world;
         if (var2 != null && mc.player != null && mc.getNetworkHandler() != null) {
            if (var2 != this.clientWorld) {
               this.clientWorld = var2;
               this.uUIDs.clear();
            }

            if (++this.index >= 5) {
               this.index = 0;
               this.helper();
            }
         } else {
            this.clientWorld = null;
         }
      }
   }

   private void helper() {
      if (this.helper5()) {
         HashMap<UUID, String> var1 = new HashMap<>();

         for (PlayerListEntry var3 : mc.getNetworkHandler().getPlayerList()) {
            if (var3 != null && var3.getProfile() != null) {
               String var4 = var3.getProfile().getName();
               UUID var5 = var3.getProfile().getId();
               if (var4 != null && var5 != null) {
                  String var6 = GlobalSocialManager.INSTANCE.getPartyFiguraModelId(var5);
                  if (var6 == null && Lumen.INSTANCE.friendStorage.isFriend(var4)) {
                     var6 = Lumen.INSTANCE.friendStorage.getModelId(var4);
                  }

                  if (var6 != null) {
                     var1.put(var5, var6);
                  }
               }
            }
         }

         for (Entry<UUID, String> var8 : var1.entrySet()) {
            String var9 = this.uUIDs.get(var8.getKey());
            if (!var8.getValue().equals(var9) && this.helper3(var8.getKey(), var8.getValue())) {
               this.uUIDs.put(var8.getKey(), var8.getValue());
            }
         }

         this.uUIDs.keySet().removeIf(uuid -> {
            if (!var1.containsKey(uuid)) {
               this.helper4(uuid);
               return true;
            } else {
               return false;
            }
         });
      }
   }

   public void syncFriend(String name) {
      if (name != null && FiguraBridge.isFiguraPresent() && mc.getNetworkHandler() != null) {
         UUID var2 = this.helper2(name);
         if (var2 != null) {
            String var3 = Lumen.INSTANCE.friendStorage.isFriend(name) ? Lumen.INSTANCE.friendStorage.getModelId(name) : null;
            if (var3 == null) {
               this.helper4(var2);
               this.uUIDs.remove(var2);
            } else if (this.helper3(var2, var3)) {
               this.uUIDs.put(var2, var3);
            }
         }
      }
   }

   private UUID helper2(String name) {
      for (PlayerListEntry var3 : mc.getNetworkHandler().getPlayerList()) {
         if (var3 != null && var3.getProfile() != null && name.equals(var3.getProfile().getName())) {
            return var3.getProfile().getId();
         }
      }

      return null;
   }

   private boolean helper3(UUID uuid, String modelId) {
      try {
         Path var3 = FiguraBridge.avatarDir();
         if (var3 == null) {
            return false;
         }

         var3 = var3.toAbsolutePath().normalize();
         Path var4 = var3.resolve(modelId).normalize();
         if (var4.startsWith(var3) && Files.isDirectory(var4)) {
            this.method.invoke(null, uuid);
            ((Set)this.field2.get(null)).add(uuid);
            Map var5 = (Map)this.field.get(null);
            Object var6 = var5.get(uuid);
            if (var6 == null || !this.class2.isInstance(var6)) {
               var6 = this.constructor.newInstance(uuid);
               var5.put(uuid, var6);
            }

            this.method2.invoke(null, var4, var6);
            return true;
         } else {
            return false;
         }
      } catch (Throwable var7) {
         return false;
      }
   }

   private void helper4(UUID uuid) {
      try {
         this.method.invoke(null, uuid);
         ((Set)this.field2.get(null)).remove(uuid);
      } catch (Throwable var3) {
      }
   }

   private boolean helper5() {
      if (this.flag) {
         return true;
      }

      if (this.flag2) {
         return false;
      }

      try {
         ClassLoader var1 = FriendModelManager.class.getClassLoader();
         Class var2 = Class.forName("org.figuramc.figura.avatar.AvatarManager", true, var1);
         this.class2 = Class.forName("org.figuramc.figura.avatar.UserData", true, var1);
         Class var3 = Class.forName("org.figuramc.figura.avatar.local.LocalAvatarLoader", true, var1);
         this.method = var2.getMethod("clearAvatars", UUID.class);
         this.method2 = var3.getMethod("loadAvatar", Path.class, this.class2);
         this.constructor = this.class2.getConstructor(UUID.class);
         this.field = var2.getDeclaredField("LOADED_USERS");
         this.field.setAccessible(true);
         this.field2 = var2.getDeclaredField("FETCHED_USERS");
         this.field2.setAccessible(true);
         this.flag = true;
         return true;
      } catch (Throwable var4) {
         this.flag2 = true;
         return false;
      }
   }
}
