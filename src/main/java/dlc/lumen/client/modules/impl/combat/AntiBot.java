package dlc.lumen.client.modules.impl.combat;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

public class AntiBot extends Module implements QClient {
   public static AntiBot INSTANCE = new AntiBot();
   public static final List<Entity> isBot = new ArrayList<>();
   private final Set<UUID> bots = ConcurrentHashMap.newKeySet();
   private final Set<UUID> announced = ConcurrentHashMap.newKeySet();

   public AntiBot() {
      super("AntiBot", "Скрывает фальшивых игроков, появляющихся в мире", Module.ModuleCategory.COMBAT);
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.RECEIVE && event.getPacket() instanceof EntitySpawnS2CPacket spawn) {
         if (spawn.getEntityType() == EntityType.PLAYER && mc.getNetworkHandler() != null && this.isFakeProfile(spawn.getUuid())) {
            this.bots.add(spawn.getUuid());
         }
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.world != null && mc.player != null && mc.getNetworkHandler() != null) {
         List<PlayerEntity> players = new ArrayList<>(mc.world.getPlayers());
         List<Integer> removeIds = new ArrayList<>();

         for (PlayerEntity player : players) {
            if (player != null && player != mc.player && !player.isRemoved()) {
               UUID uuid = player.getUuid();
               if (this.isFunTimeNpc(player)) {
                  this.bots.add(uuid);
               }

               if (this.bots.contains(uuid)) {
                  if (!isBot.contains(player)) {
                     isBot.add(player);
                  }

                  if (this.hasFullArmor(player)) {
                     if (this.announced.add(uuid)) {
                        ChatUtils.sendMessage("Фальшивый игрок был обнаружен, и удален из мира.");
                     }

                     removeIds.add(player.getId());
                  }
               }
            }
         }

         for (int id : removeIds) {
            mc.world.removeEntity(id, RemovalReason.DISCARDED);
         }
      }
   }

   private boolean isFakeProfile(UUID uuid) {
      PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(uuid);
      boolean skin = entry != null && entry.getSkinTextures() != null && entry.getSkinTextures().textureUrl() != null;
      boolean texture = entry != null && !entry.getProfile().getProperties().get("textures").isEmpty();
      boolean ping = entry == null || entry.getLatency() == 0;
      return !skin && !texture && ping;
   }

   private boolean isFunTimeNpc(PlayerEntity player) {
      ItemStack feet = player.getEquippedStack(EquipmentSlot.FEET);
      ItemStack legs = player.getEquippedStack(EquipmentSlot.LEGS);
      ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
      ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD);
      if (feet.isEmpty() || legs.isEmpty() || chest.isEmpty() || head.isEmpty()) {
         return false;
      } else if (!feet.isEnchantable() || !legs.isEnchantable() || !chest.isEnchantable() || !head.isEnchantable()) {
         return false;
      } else if (!feet.isDamaged() && !legs.isDamaged() && !chest.isDamaged() && !head.isDamaged()) {
         if (player.getOffHandStack().getItem() != Items.AIR) {
            return false;
         } else if (player.getMainHandStack().isEmpty()) {
            return false;
         } else if (player.getHungerManager().getFoodLevel() != 20) {
            return false;
         } else {
            return this.isNpcArmor(feet.getItem()) || this.isNpcArmor(legs.getItem()) || this.isNpcArmor(chest.getItem()) || this.isNpcArmor(head.getItem());
         }
      } else {
         return false;
      }
   }

   private boolean isNpcArmor(Item item) {
      return item == Items.LEATHER_BOOTS
         || item == Items.LEATHER_LEGGINGS
         || item == Items.LEATHER_CHESTPLATE
         || item == Items.LEATHER_HELMET
         || item == Items.IRON_BOOTS
         || item == Items.IRON_LEGGINGS
         || item == Items.IRON_CHESTPLATE
         || item == Items.IRON_HELMET;
   }

   private boolean hasFullArmor(PlayerEntity player) {
      return !player.getEquippedStack(EquipmentSlot.HEAD).isEmpty()
         && !player.getEquippedStack(EquipmentSlot.CHEST).isEmpty()
         && !player.getEquippedStack(EquipmentSlot.LEGS).isEmpty()
         && !player.getEquippedStack(EquipmentSlot.FEET).isEmpty();
   }

   public static boolean checkBot(LivingEntity entity) {
      if (entity instanceof PlayerEntity player) {
         if (!INSTANCE.isEnable()) {
            return false;
         } else {
            return isBot.contains(player) || INSTANCE.bots.contains(player.getUuid());
         }
      } else {
         return false;
      }
   }

   @Override
   public void onEnable() {
      this.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.clear();
   }

   private void clear() {
      isBot.clear();
      this.bots.clear();
      this.announced.clear();
   }
}
