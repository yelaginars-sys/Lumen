package dlc.lumen.api.storages.implement;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class StaffStorage {
   private final List<String> staff = new ArrayList<>();

   public void add(String friend) {
      if (!friend.isEmpty()) {
         this.staff.add(friend);
      }
   }

   public void remove(String friend) {
      this.staff.remove(friend);
   }

   public void clear() {
      this.staff.clear();
   }

   public boolean isStaff(String friend) {
      return this.staff.contains(friend);
   }

   public boolean isEmpty() {
      return this.staff.isEmpty();
   }

   @Generated
   public List<String> getStaffs() {
      return this.staff;
   }
}
