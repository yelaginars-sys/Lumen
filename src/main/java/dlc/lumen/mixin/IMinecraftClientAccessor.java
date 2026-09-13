package dlc.lumen.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface IMinecraftClientAccessor {
   @Mutable
   @Accessor("session")
   void setSession(Session var1);

   @Mutable
   @Accessor("itemUseCooldown")
   void setItemUseCooldown(int var1);

   @Mutable
   @Accessor("framebuffer")
   void lumen$setFramebuffer(Framebuffer var1);

   @Invoker("doAttack")
   boolean lumen$doAttack();

   @Invoker("doItemUse")
   void lumen$doItemUse();

   @Accessor("attackCooldown")
   int lumen$getAttackCooldown();

   @Accessor("attackCooldown")
   void lumen$setAttackCooldown(int var1);
}
