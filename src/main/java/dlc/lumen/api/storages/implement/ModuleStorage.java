package dlc.lumen.api.storages.implement;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.Module;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class ModuleStorage implements QClient {
   private List<Module> modules = new ArrayList<>();

    public ModuleStorage() {
       this.loadModules();
    }

    private void loadModules() {
      ModuleClass.INSTANCE.initialize();
      ObjectArrayList var1 = ModuleClass.INSTANCE.getObject();
      if (var1 != null) {
         this.modules.addAll(var1);
      }
   }

   public Module getModule(String name) {
      for (Module var3 : this.modules) {
         if (var3.getName().equalsIgnoreCase(name)) {
            return var3;
         }
      }

      return null;
   }

   public <T extends Module> T getModule(String name, Class<T> moduleClass) {
      Module var3 = this.getModule(name);
      return (T)(moduleClass.isInstance(var3) ? var3 : null);
   }

   public <T extends Module> T getModule(Class<T> moduleClass) {
      for (Module var3 : this.modules) {
         if (moduleClass.isInstance(var3)) {
            return (T)var3;
         }
      }

      return null;
   }

   public List<Module> getModules() {
      return this.modules;
   }

   @Generated
   public void setModules(List<Module> modules) {
      this.modules = modules;
   }
}
