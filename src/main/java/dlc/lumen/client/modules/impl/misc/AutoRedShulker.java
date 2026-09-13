package dlc.lumen.client.modules.impl.misc;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dlc.lumen.Lumen;
import dlc.lumen.api.commands.impl.RCTCommand;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.baritone.BaritoneControl;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;

public class AutoRedShulker extends Module {
   public static final AutoRedShulker INSTANCE = new AutoRedShulker();
   private static final BlockPos BLOCK_POS = new BlockPos(-38, 88, 26);
   private static final int INDEX = 2;
   private static final int INDEX2 = 74;
   private static final long TIMESTAMP = 800L;
   private static final long TIMESTAMP2 = 100L;
   private static final long TIMESTAMP3 = 5000L;
   private static final long TIMESTAMP4 = 8000L;
   private AutoRedShulker.State state = AutoRedShulker.State.SPAWN;
   private long timestamp = 0L;
   private int index = 0;
   private long timestamp2 = 0L;

   public AutoRedShulker() {
      super("AutoRedShulker", "Сканит красный шалкер прямо со спавна", Module.ModuleCategory.MISC);
   }

   @Override
   public void onEnable() {
      this.index = 0;
      this.timestamp2 = 0L;
      this.updateState(AutoRedShulker.State.SPAWN);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      BaritoneControl.stop();
      super.onDisable();
   }

   private void updateState(AutoRedShulker.State next) {
      this.state = next;
      this.timestamp = System.currentTimeMillis();
      this.timestamp2 = System.currentTimeMillis();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.getNetworkHandler() != null) {
         long var2 = System.currentTimeMillis();
         if (!RCTCommand.RUNNING || this.state == AutoRedShulker.State.WAIT_RCT) {
            switch (this.state) {
               case SPAWN:
                  ChatUtils.sendMessage("§b[AutoRedShulker] Телепорт на спавн...");
                  this.updateState3("spawn");
                  this.updateState(AutoRedShulker.State.WAIT_LOAD);
                  break;
               case WAIT_LOAD:
                  if (var2 - this.timestamp < 800L) {
                     return;
                  }

                  if (var2 - this.timestamp2 >= 100L) {
                     this.timestamp2 = var2;
                     if (this.checkState()) {
                        ChatUtils.sendMessage("§a[AutoRedShulker] Сканирую анархию #" + this.index + "...");
                        this.updateState(AutoRedShulker.State.SCAN);
                     } else if (var2 - this.timestamp > 8000L) {
                        ChatUtils.sendMessage("§7[AutoRedShulker] Чанк не прогрузился за 8с — скип #" + this.index);
                        this.updateState(AutoRedShulker.State.NEXT_ANARCHY);
                     }
                  }
                  break;
               case SCAN:
                  if (var2 - this.timestamp2 >= 100L) {
                     this.timestamp2 = var2;
                     Block var5 = this.computeBlock();
                     if (var5 == Blocks.RED_SHULKER_BOX) {
                        ChatUtils.sendMessage("§4§l[AutoRedShulker] КРАСНЫЙ ШАЛКЕР НАЙДЕН! Анархия #" + this.index);
                        ChatUtils.sendMessage("§a[AutoRedShulker] Бегу к шалкеру...");
                        BaritoneControl.gotoPos(BLOCK_POS);
                        this.updateState(AutoRedShulker.State.DONE);
                        this.setEnabled(false);
                     } else if (var5 == Blocks.PURPLE_SHULKER_BOX) {
                        ChatUtils.sendMessage("§5[AutoRedShulker] Пурпурный шалкер — скип #" + this.index);
                        this.updateState(AutoRedShulker.State.NEXT_ANARCHY);
                     } else {
                        ChatUtils.sendMessage("§8[AutoRedShulker] Нет красного шалкера — скип #" + this.index);
                        this.updateState(AutoRedShulker.State.NEXT_ANARCHY);
                     }
                  }
                  break;
               case NEXT_ANARCHY:
                  int var4 = this.resolveInt();
                  ChatUtils.sendMessage("§e[AutoRedShulker] Перехожу на анархию #" + var4 + "...");
                  this.updateState2(var4);
                  this.updateState(AutoRedShulker.State.WAIT_RCT);
                  break;
               case WAIT_RCT:
                  if (!RCTCommand.RUNNING && var2 - this.timestamp > 200L) {
                     this.updateState(AutoRedShulker.State.SPAWN);
                  } else if (var2 - this.timestamp > 5000L) {
                     ChatUtils.sendMessage("§c[AutoRedShulker] RCT timeout — пробую следующую");
                     this.updateState(AutoRedShulker.State.SPAWN);
                  }
               case DONE:
            }
         }
      }
   }

   private boolean checkState() {
      return mc.world.isChunkLoaded(BLOCK_POS.getX() >> 4, BLOCK_POS.getZ() >> 4);
   }

   private int resolveInt() {
      this.index = this.index % 74 + 1;
      return this.index;
   }

   private void updateState2(int number) {
      try {
         Lumen.INSTANCE.commandStorage.getDispatcher().execute("rct " + number, Lumen.INSTANCE.commandStorage.getSource());
      } catch (CommandSyntaxException var3) {
         ChatUtils.sendMessage("§c[AutoRedShulker] .rct не удался: " + var3.getMessage());
         if (this.state == AutoRedShulker.State.WAIT_RCT) {
            this.updateState(AutoRedShulker.State.SPAWN);
         }
      }
   }

   private void updateState3(String command) {
      if (mc.getNetworkHandler() != null) {
         mc.getNetworkHandler().sendChatCommand(command);
      }
   }

   private Block computeBlock() {
      if (!mc.world.isChunkLoaded(BLOCK_POS.getX() >> 4, BLOCK_POS.getZ() >> 4)) {
         return Blocks.AIR;
      }

      Mutable var1 = new Mutable();

      for (int var2 = -2; var2 <= 2; var2++) {
         for (int var3 = -2; var3 <= 2; var3++) {
            for (int var4 = -2; var4 <= 2; var4++) {
               var1.set(BLOCK_POS.getX() + var2, BLOCK_POS.getY() + var3, BLOCK_POS.getZ() + var4);
               Block var5 = mc.world.getBlockState(var1).getBlock();
               if (var5 == Blocks.RED_SHULKER_BOX) {
                  return var5;
               }

               if (var5 == Blocks.PURPLE_SHULKER_BOX) {
                  return var5;
               }
            }
         }
      }

      return mc.world.getBlockState(BLOCK_POS).getBlock();
   }

   private enum State {
      SPAWN,
      WAIT_LOAD,
      SCAN,
      NEXT_ANARCHY,
      WAIT_RCT,
      DONE;
   }
}
