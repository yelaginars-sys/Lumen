package dlc.lumen.mixin;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerMoveC2SPacket.class)
public interface IPlayerMoveC2SPacketAccessor {
   @Accessor("x")
   void setX(double var1);

   @Accessor("y")
   void setY(double var1);

   @Accessor("z")
   void setZ(double var1);

   @Mutable
   @Accessor("horizontalCollision")
   void setHorizontalCollision(boolean var1);
}
