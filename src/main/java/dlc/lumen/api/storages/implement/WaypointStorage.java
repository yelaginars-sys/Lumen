package dlc.lumen.api.storages.implement;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.utils.cmd.waypoint.Waypoint;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;

public class WaypointStorage implements QClient {
   private final List<Waypoint> empty = new CopyOnWriteArrayList<>();

   public WaypointStorage() {
      EventInvoker.register(this);
   }

   public void add(Waypoint waypoint) {
      if (waypoint != null) {
         this.removeByName(waypoint.getName());
         this.empty.add(waypoint);
      }
   }

   public void set(Waypoint waypoint) {
      this.add(waypoint);
   }

   public boolean removeByName(String name) {
      return name == null ? false : this.empty.removeIf(wp -> wp.getName() != null && wp.getName().equalsIgnoreCase(name));
   }

   public void clear() {
      this.empty.clear();
   }

   public boolean isEmpty() {
      return this.empty.isEmpty();
   }

   @Generated
   public List<Waypoint> getWaypoints() {
      return this.empty;
   }
}
