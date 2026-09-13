package dlc.lumen.client.bots.core.behavior;

import java.lang.reflect.Field;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class OfflineConnectIdentity implements ConnectIdentity {
   @Override
   public void beforeConnect() {
      try {
         Class var1 = Class.forName("ru.fiw.proxyserver.ProxyServer");
         Field var2 = var1.getField("proxyMenuButton");
         if (var2.get(null) == null) {
            var2.set(null, ButtonWidget.builder(Text.empty(), button -> {}).build());
         }
      } catch (Throwable var3) {
      }
   }
}
