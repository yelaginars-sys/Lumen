package dlc.lumen.client.render.figura;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventTickPost;
import java.util.Locale;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public enum FiguraKeybindGuard implements QClient {
   INSTANCE;

   @EventLink
   public void onTick(EventTickPost event) {
      if (mc.options != null && mc.options.allKeys != null) {
         boolean var2 = false;

         for (KeyBinding var6 : mc.options.allKeys) {
            if (var6 != null && !var6.isUnbound()) {
               String var7 = var6.getTranslationKey();
               if (var7 != null && var7.toLowerCase(Locale.ROOT).contains("figura")) {
                  var6.setBoundKey(InputUtil.UNKNOWN_KEY);
                  var2 = true;
               }
            }
         }

         if (var2) {
            KeyBinding.updateKeysByCode();
         }
      }
   }
}
