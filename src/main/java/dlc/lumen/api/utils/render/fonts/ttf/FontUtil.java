package dlc.lumen.api.utils.render.fonts.ttf;

import java.awt.Font;
import java.io.InputStream;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public class FontUtil {
   public static Font getFontFromTTF(Identifier loc, float fontSize, int fontType) {
      try {
         MinecraftClient var3 = MinecraftClient.getInstance();
         if (var3 == null) {
            return null;
         } else if (var3.getResourceManager() == null) {
            return null;
         } else {
            Optional var4 = var3.getResourceManager().getResource(loc);
            if (var4.isPresent()) {
               InputStream var5 = ((Resource)var4.get()).getInputStream();
               Font var6 = Font.createFont(fontType, var5);
               var6 = var6.deriveFont(fontSize);
               var5.close();
               return var6;
            } else {
               return null;
            }
         }
      } catch (Exception var7) {
         var7.printStackTrace();
         return null;
      }
   }
}
