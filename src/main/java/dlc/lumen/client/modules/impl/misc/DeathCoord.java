package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DeathCoord extends Module {
   public static DeathCoord INSTANCE = new DeathCoord();
   private final BooleanSetting booleanSetting = new BooleanSetting("Копировать в буфер", true);
   private BlockPos blockPos = null;
   private boolean flag = false;

   public DeathCoord() {
      super("DeathCoord", "Показывает координаты смерти", Module.ModuleCategory.MISC);
      this.addSettings(this.booleanSetting);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.flag = false;
      this.blockPos = null;
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (mc.player.getHealth() <= 0.0F && !this.flag) {
            this.flag = true;
            this.blockPos = mc.player.getBlockPos();
            String var2 = "X: " + this.blockPos.getX() + " Y: " + this.blockPos.getY() + " Z: " + this.blockPos.getZ();
            String var3 = this.resolveString();
            String var4 = "§cВы умерли! §f" + var2 + " §7(" + var3 + ")";
            mc.player.sendMessage(Text.literal(var4), false);
            if (this.booleanSetting.isState()) {
               mc.keyboard.setClipboard(this.blockPos.getX() + " " + this.blockPos.getY() + " " + this.blockPos.getZ());
            }
         }

         if (mc.player.getHealth() > 0.0F && this.flag) {
            this.flag = false;
         }
      }
   }

   private String resolveString() {
      if (mc.world == null) {
         return "Unknown";
      } else {
         String var1 = mc.world.getRegistryKey().getValue().toString();
         if (var1.contains("overworld")) {
            return "Overworld";
         } else if (var1.contains("nether")) {
            return "Nether";
         } else {
            return var1.contains("end") ? "End" : var1;
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.flag = false;
      this.blockPos = null;
   }
}
