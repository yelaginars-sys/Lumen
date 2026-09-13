package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.client.autobuy.AutoBuyEngine;
import dlc.lumen.client.autobuy.Lot;
import dlc.lumen.client.autobuy.PriceParser;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import java.util.ArrayList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;

public class AuctionHelper extends Module {
   public static final AuctionHelper INSTANCE = new AuctionHelper();
   private final FloatSetting floatSetting = new FloatSetting("Сколько подсвечивать", 3.0F, 1.0F, 9.0F, 1.0F);

   public AuctionHelper() {
      super("AuctionHelper", "Подсвечивает самые дешёвые лоты на аукционе", Module.ModuleCategory.MISC);
      this.addSettings(this.floatSetting);
   }

   public static void render(DrawContext context, HandledScreen<?> screen, int screenX, int screenY) {
      if (INSTANCE != null && INSTANCE.isEnable()) {
         if (screen instanceof GenericContainerScreen var4 && AutoBuyEngine.isAuctionScreen(var4)) {
            GenericContainerScreenHandler var5 = var4.getScreenHandler();
            int var6 = (var5.getRows() - 1) * 9;
            ArrayList<long[]> var7 = new ArrayList<>();

            for (int var8 = 0; var8 < var6; var8++) {
               ItemStack var9 = var5.getSlot(var8).getStack();
               Lot var10 = PriceParser.parse(var8, var9);
               if (var10 != null && !var10.barrier && var10.unitPrice > 0L) {
                  var7.add(new long[]{var10.unitPrice, var8});
               }
            }

            var7.sort((a, b) -> Long.compare(a[0], b[0]));
            int var15 = Math.min((int)INSTANCE.floatSetting.getValue().floatValue(), var7.size());
            int var16 = ColorUtils.rgba(60, 230, 90, 110);
            int var17 = ColorUtils.rgba(90, 255, 120, 255);

            for (int var11 = 0; var11 < var15; var11++) {
               Slot var12 = var5.getSlot((int)var7.get(var11)[1]);
               int var13 = screenX + var12.x;
               int var14 = screenY + var12.y;
               context.fill(var13, var14, var13 + 16, var14 + 16, var16);
               context.fill(var13 - 1, var14 - 1, var13 + 17, var14, var17);
               context.fill(var13 - 1, var14 + 16, var13 + 17, var14 + 17, var17);
               context.fill(var13 - 1, var14, var13, var14 + 16, var17);
               context.fill(var13 + 16, var14, var13 + 17, var14 + 16, var17);
            }
         }
      }
   }
}
