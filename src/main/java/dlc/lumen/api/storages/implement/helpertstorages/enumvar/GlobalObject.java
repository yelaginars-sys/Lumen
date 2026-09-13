package dlc.lumen.api.storages.implement.helpertstorages.enumvar;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Generated;

public class GlobalObject<T> {
   private final ObjectArrayList<T> object = new ObjectArrayList();

   @Generated
   public ObjectArrayList<T> getObject() {
      return this.object;
   }
}
