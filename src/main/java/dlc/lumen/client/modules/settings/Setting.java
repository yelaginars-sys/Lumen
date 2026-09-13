package dlc.lumen.client.modules.settings;

import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import java.awt.Color;
import java.util.function.Supplier;
import lombok.Generated;

public abstract class Setting implements QClient {
   private final String text;
   public Supplier<Boolean> visible = () -> true;
   public Color color = Color.WHITE;

   public Setting(String name) {
      this.text = name;
   }

   public Boolean visible() {
      return this.visible.get();
   }

   public String displayName() {
      return Lumen.INSTANCE.localizationStorage == null ? this.text : Lumen.INSTANCE.localizationStorage.translate(this.text);
   }

   @Generated
   public String name() {
      return this.text;
   }

   @Generated
   public Color color() {
      return this.color;
   }
}
