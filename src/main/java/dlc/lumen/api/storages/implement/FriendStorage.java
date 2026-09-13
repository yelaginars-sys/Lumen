package dlc.lumen.api.storages.implement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FriendStorage {
   private final List<String> friend = new ArrayList<>();
   private final Map<String, String> customName = new LinkedHashMap<>();
   private final Map<String, String> modelId = new LinkedHashMap<>();

   public List<String> getFriends() {
      return new ArrayList<>(this.friend);
   }

   public void add(String friend) {
      if (friend != null && !friend.isEmpty() && !this.friend.contains(friend)) {
         this.friend.add(friend);
      }
   }

   public void remove(String friend) {
      this.friend.remove(friend);
      this.customName.remove(friend);
      this.modelId.remove(friend);
   }

   public void clear() {
      this.friend.clear();
      this.customName.clear();
      this.modelId.clear();
   }

   public boolean isFriend(String friend) {
      return this.friend.contains(friend);
   }

   public boolean isEmpty() {
      return this.friend.isEmpty();
   }

   public String getCustomName(String friend) {
      String var2 = this.customName.get(friend);
      return var2 != null && !var2.isBlank() ? var2 : null;
   }

   public void setCustomName(String friend, String customName) {
      if (this.isFriend(friend)) {
         if (customName != null && !customName.isBlank()) {
            this.customName.put(friend, customName);
         } else {
            this.customName.remove(friend);
         }
      }
   }

   public String getModelId(String friend) {
      String var2 = this.modelId.get(friend);
      return var2 != null && !var2.isBlank() ? var2 : null;
   }

   public void setModelId(String friend, String modelId) {
      if (this.isFriend(friend)) {
         if (modelId != null && !modelId.isBlank()) {
            this.modelId.put(friend, modelId);
         } else {
            this.modelId.remove(friend);
         }
      }
   }
}
