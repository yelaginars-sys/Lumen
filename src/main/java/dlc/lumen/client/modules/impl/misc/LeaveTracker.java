package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.Module;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class LeaveTracker extends Module {
   public static LeaveTracker INSTANCE = new LeaveTracker();
   private final Map<UUID, LeaveTracker.TrackedPlayer> uUIDs = new HashMap<>();
   private ClientWorld clientWorld;
   private boolean flag;

   public LeaveTracker() {
      super("LeaveTracker", "Пишет координаты ливнутых игроков из прогрузки", Module.ModuleCategory.MISC);
   }

   @Override
   public void onDisable() {
      this.uUIDs.clear();
      this.flag = false;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (mc.world != this.clientWorld) {
            this.clientWorld = mc.world;
            this.uUIDs.clear();
            this.flag = false;
         }

         if (!this.flag) {
            this.helper();
            this.flag = true;
         } else {
            HashSet var2 = new HashSet();

            for (PlayerEntity var4 : mc.world.getPlayers()) {
               if (var4 != mc.player && var4.isAlive()) {
                  UUID var5 = var4.getUuid();
                  var2.add(var5);
                  this.uUIDs.put(var5, new LeaveTracker.TrackedPlayer(var4.getName().getString(), var4.getBlockPos()));
               }
            }

            Iterator var8 = this.uUIDs.entrySet().iterator();

            while (var8.hasNext()) {
               Entry var9 = (Entry)var8.next();
               if (!var2.contains(var9.getKey())) {
                  LeaveTracker.TrackedPlayer var10 = (LeaveTracker.TrackedPlayer)var9.getValue();
                  double var6 = mc.player.squaredDistanceTo(var10.pos().getX(), var10.pos().getY(), var10.pos().getZ());
                  if (var6 < 65536.0) {
                     ChatUtils.sendMessage(
                        Formatting.GRAY
                           + var10.name()
                           + Formatting.WHITE
                           + " ливнул на "
                           + Formatting.GRAY
                           + var10.pos().getX()
                           + " "
                           + var10.pos().getY()
                           + " "
                           + var10.pos().getZ()
                     );
                  }

                  var8.remove();
               }
            }
         }
      }
   }

   private void helper() {
      this.uUIDs.clear();

      for (PlayerEntity var2 : mc.world.getPlayers()) {
         if (var2 != mc.player && var2.isAlive()) {
            this.uUIDs.put(var2.getUuid(), new LeaveTracker.TrackedPlayer(var2.getName().getString(), var2.getBlockPos()));
         }
      }
   }

   private record TrackedPlayer(String name, BlockPos pos) {

      private TrackedPlayer(String name, BlockPos pos) {
         this.name = name;
         this.pos = pos;
      }

      public String name() {
         return this.name;
      }

      public BlockPos pos() {
         return this.pos;
      }
   }
}
