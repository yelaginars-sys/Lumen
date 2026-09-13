package dlc.lumen.api.utils.namespaced;

import net.minecraft.util.Identifier;

public class Namespaced {
   public static Identifier of(String path) {
      return Identifier.of("lumen", path);
   }
}
