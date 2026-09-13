package dlc.lumen.client.modules;

import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.notification.NotificationManager;
import dlc.lumen.client.modules.settings.Setting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Generated;

public abstract class Module implements QClient {
   private String displayName;
   private String displayDescription;
   private int key2;
   private Module.ModuleCategory moduleCategory;
   private boolean open;
   private boolean enable2;
   private final List<Setting> settings2 = new ArrayList<>();
   private final AnimationUtils animka = new AnimationUtils(60.0F, 11.0F, Easings.LINEAR);
   private final AnimationUtils arrayAnimka = new AnimationUtils(0.0F, 11.0F, Easings.LINEAR);

   public Module(String name, String description, Module.ModuleCategory category) {
      this.displayName = name;
      this.displayDescription = description;
      this.moduleCategory = category;
      this.key2 = -1;
   }

   public Module(String name, Module.ModuleCategory category) {
      this.displayName = name;
      this.displayDescription = "NULLABLE";
      this.moduleCategory = category;
      this.key2 = -1;
   }

   public void onEnable() {
      this.enable2 = true;
      EventInvoker.register(this);
      this.animka.update(1.0F);
      NotificationManager.push(this.displayName, this.moduleCategory.getIcons(), true);
   }

   public void onDisable() {
      this.enable2 = false;
      EventInvoker.unregister(this);
      this.animka.update(0.0F);
      NotificationManager.push(this.displayName, this.moduleCategory.getIcons(), false);
   }

   public void toggle() {
      this.enable2 = !this.enable2;
      if (this.enable2) {
         this.onEnable();
      } else {
         this.onDisable();
      }
   }

   public void setEnabled(boolean state) {
      boolean var2 = this.enable2;
      this.enable2 = state;

      try {
         if (state) {
            this.onEnable();
         } else if (var2) {
            this.onDisable();
         }
      } catch (Exception var4) {
         this.enable2 = false;
         this.onDisable();
      }
   }

   public void addSettings(Setting... settings) {
      if (settings != null && settings.length != 0) {
         Arrays.stream(settings).filter(Objects::nonNull).forEach(this.settings2::add);
      }
   }

   public String getDisplayName() {
      return Lumen.INSTANCE.localizationStorage == null ? this.displayName : Lumen.INSTANCE.localizationStorage.translate(this.displayName);
   }

   public String getDisplayDescription() {
      return Lumen.INSTANCE.localizationStorage == null ? this.displayDescription : Lumen.INSTANCE.localizationStorage.translate(this.displayDescription);
   }

   @Generated
   public String getName() {
      return this.displayName;
   }

   @Generated
   public String getDescription() {
      return this.displayDescription;
   }

   @Generated
   public int getKey() {
      return this.key2;
   }

   @Generated
   public Module.ModuleCategory getCategory() {
      return this.moduleCategory;
   }

   @Generated
   public boolean isOpen() {
      return this.open;
   }

   @Generated
   public boolean isEnable() {
      return this.enable2;
   }

   @Generated
   public List<Setting> getSettings() {
      return this.settings2;
   }

   @Generated
   public AnimationUtils getAnimka() {
      return this.animka;
   }

   @Generated
   public AnimationUtils getArrayAnimka() {
      return this.arrayAnimka;
   }

   @Generated
   public void setName(String name) {
      this.displayName = name;
   }

   @Generated
   public void setDescription(String description) {
      this.displayDescription = description;
   }

   @Generated
   public void setKey(int key) {
      this.key2 = key;
   }

   @Generated
   public void setCategory(Module.ModuleCategory category) {
      this.moduleCategory = category;
   }

   @Generated
   public void setOpen(boolean isOpen) {
      this.open = isOpen;
   }

   @Generated
   public void setEnable(boolean enable) {
      this.enable2 = enable;
   }

   public enum ModuleCategory {
      COMBAT("Combat", "b"),
      MOVEMENT("Movement", "c"),
      RENDER("Render", "d"),
      MODELS("Models", "f"),
      MISC("Misc", "h"),
      PLAYER("Player", "e"),
      AUTOBUY("AutoBuy", "j");

      private final String displayName;
      private final String displayDescription;

      @Generated
      ModuleCategory(final String name, final String icons) {
         this.displayName = name;
         this.displayDescription = icons;
      }

      @Generated
      public String getName() {
         return this.displayName;
      }

      @Generated
      public String getIcons() {
         return this.displayDescription;
      }
   }
}
