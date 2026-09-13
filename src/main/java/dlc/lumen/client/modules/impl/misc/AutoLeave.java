package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

public class AutoLeave extends Module {
   public static final AutoLeave INSTANCE = new AutoLeave();
   private static final Set<String> value = new HashSet<>(
      Arrays.asList("supp", "mod", "der", "adm", "wne", "curat", "dev", "yt", "мод", "помо", "адм", "владе", "курато", "сапп", "ютуб", "стажер", "сотрудник")
   );
   private final FloatSetting floatSetting = new FloatSetting("Дистанция срабатывания", 5.0F, 3.0F, 50.0F, 1.0F);
   private final ListSetting value2 = new ListSetting(
      "Выходить если замечен", new BooleanSetting("Игрок", true), new BooleanSetting("Модератор", false)
   );
   private final ModeSetting modeSetting = new ModeSetting("Тип выхода", "В мейн меню", "В мейн меню", "/hub", "/home", "/spawn");
   private final BooleanSetting booleanSetting = new BooleanSetting("Выключать баритон", false);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Выключать после выхода", true);
   private int index;

   public AutoLeave() {
      super("AutoLeave", "Выходит с сервера, когда замечает поблизости игрока", Module.ModuleCategory.MISC);
      this.addSettings(this.floatSetting, this.value2, this.modeSetting, this.booleanSetting, this.booleanSetting2);
   }

   @Override
   public void onEnable() {
      this.index = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.index = 0;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (this.index > 0) {
            this.index--;
         } else {
            float var2 = this.floatSetting.get();

            for (PlayerEntity var4 : mc.world.getPlayers()) {
               if (var4 != null && var4 != mc.player && mc.player.distanceTo(var4) <= var2 && this.checkCondition(var4)) {
                  this.updateState();
                  break;
               }
            }
         }
      }
   }

   private boolean checkCondition(PlayerEntity player) {
      return this.checkCondition2(player) ? this.value2.is("Модератор") : this.value2.is("Игрок");
   }

   private boolean checkCondition2(PlayerEntity player) {
      if (player == null) {
         return false;
      }

      String var2 = player.getName().getString();
      if (Lumen.INSTANCE != null && Lumen.INSTANCE.staffStorage != null && Lumen.INSTANCE.staffStorage.isStaff(var2)) {
         return true;
      }

      Team var3 = player.getScoreboardTeam();
      if (var3 == null) {
         return false;
      }

      String var4 = var3.getPrefix().getString().toLowerCase(Locale.ROOT);

      for (String var6 : value) {
         if (var4.contains(var6)) {
            return true;
         }
      }

      return false;
   }

   private void updateState() {
      this.updateState2();
      switch (this.modeSetting.getCurrent()) {
         case "В мейн меню":
            this.updateState3();
            break;
         case "/hub":
            this.updateState4("hub");
            break;
         case "/home":
            this.updateState4("home home");
            break;
         case "/spawn":
            this.updateState4("spawn");
      }
   }

   private void updateState2() {
      if (this.booleanSetting.isState() && mc.getNetworkHandler() != null) {
         mc.getNetworkHandler().sendChatMessage("#stop");
      }
   }

   private void updateState3() {
      if (mc.getNetworkHandler() == null) {
         ChatUtils.sendMessage("Модуль не работает в одиночном мире");
      } else {
         mc.getNetworkHandler().getConnection().disconnect(Text.literal("AutoLeave"));
         if (this.booleanSetting2.isState()) {
            this.toggle();
         }
      }
   }

   private void updateState4(String command) {
      if (mc.getNetworkHandler() == null) {
         ChatUtils.sendMessage("AutoLeave нельзя использовать в одиночной игре!");
      } else {
         mc.getNetworkHandler().sendChatCommand(command);
         this.index = this.booleanSetting2.isState() ? 10 : 30;
         if (this.booleanSetting2.isState()) {
            this.toggle();
         }
      }
   }
}
