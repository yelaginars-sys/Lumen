package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventAttackEntity;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;

public class PvpSafe extends Module {
   public static final PvpSafe INSTANCE = new PvpSafe();
   private final FloatSetting floatSetting = new FloatSetting("Время боя", 15.0F, 1.0F, 60.0F, 1.0F);
   private final TextSetting textSetting = new TextSetting("Команды", "hub", 128);
   private long timestamp = -1L;
   private long timestamp2 = -1L;

   public PvpSafe() {
      super("PvpSafe", "Не даёт ливнуть в кт с серва", Module.ModuleCategory.MISC);
      this.addSettings(this.floatSetting, this.textSetting);
   }

   @Override
   public void onEnable() {
      this.timestamp = -1L;
      this.timestamp2 = -1L;
      super.onEnable();
   }

   @EventLink
   public void onAttack(EventAttackEntity event) {
      if (event.getTarget() instanceof PlayerEntity var2 && var2 != mc.player) {
         this.timestamp = System.currentTimeMillis();
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null) {
         if (mc.player.hurtTime == mc.player.maxHurtTime) {
            DamageSource var2 = mc.player.getRecentDamageSource();
            if (var2 != null && var2.getAttacker() instanceof PlayerEntity var3 && var3 != mc.player) {
               this.timestamp = System.currentTimeMillis();
            }
         }
      }
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.SEND) {
         if (event.getPacket() instanceof CommandExecutionC2SPacket var2) {
            if (this.helper()) {
               String var4 = var2.command().toLowerCase();
               if (this.helper2(var4)) {
                  event.cancel();
                  this.helper3();
               }
            }
         }
      }
   }

   private boolean helper() {
      return this.timestamp != -1L && System.currentTimeMillis() - this.timestamp < (long)(this.floatSetting.get() * 1000.0F);
   }

   private boolean helper2(String command) {
      String var2 = this.textSetting.get();
      if (var2 != null && !var2.isBlank()) {
         for (String var6 : var2.split(",")) {
            String var7 = var6.trim().toLowerCase();
            if (!var7.isEmpty() && command.contains(var7)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void helper3() {
      long var1 = System.currentTimeMillis();
      if (this.timestamp2 == -1L || var1 - this.timestamp2 >= 1000L) {
         this.timestamp2 = var1;
         long var3 = (long)(this.floatSetting.get() * 1000.0F) - (var1 - this.timestamp);
         ChatUtils.sendMessage("Команда заблокирована: бой ещё " + Math.max(1L, var3 / 1000L) + " с");
      }
   }
}
