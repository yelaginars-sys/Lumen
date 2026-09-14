package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventTickPre;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class NoWeb extends Module {
   public static final NoWeb INSTANCE = new NoWeb();
   private final ModeSetting modeSetting = new ModeSetting("Мод", "NoWeb ReallyWorld", "NoWeb ReallyWorld");

   public NoWeb() {
      super("NoWeb", "Убирает замедление от паутины", Module.ModuleCategory.MOVEMENT);
      this.addSettings(this.modeSetting);
   }

   @EventLink
   public void onClientTick(EventTickPre event) {
      if (mc.player == null || mc.world == null || !this.isEnable()) {
         return;
      }

      if (this.modeSetting.is("NoWeb ReallyWorld") && this.isInWeb(mc.player.getBoundingBox())) {
         Vec3d var2 = mc.player.getVelocity();
         mc.player.setVelocity(var2.x, 0.0, var2.z);
         if (mc.options.jumpKey.isPressed()) {
            mc.player.setVelocity(var2.x, 0.9F, var2.z);
         }

         if (mc.options.sneakKey.isPressed()) {
            mc.player.setVelocity(var2.x, -0.9F, var2.z);
         }

         this.applyInputMovement(0.21);
      }
   }

   private boolean isInWeb(Box box) {
      for (BlockPos var3 : BlockPos.iterate(
         MathHelper.floor(box.minX),
         MathHelper.floor(box.minY),
         MathHelper.floor(box.minZ),
         MathHelper.floor(box.maxX),
         MathHelper.floor(box.maxY),
         MathHelper.floor(box.maxZ)
      )) {
         if (mc.world.getBlockState(var3).isOf(Blocks.COBWEB)) {
            return true;
         }
      }

      return false;
   }

   private void applyInputMovement(double speed) {
      float var3 = mc.player.getYaw();
      float var4 = 0.0F;
      float var5 = 0.0F;
      if (mc.options.forwardKey.isPressed()) {
         var4++;
      }

      if (mc.options.backKey.isPressed()) {
         var4--;
      }

      if (mc.options.rightKey.isPressed()) {
         var5++;
      }

      if (mc.options.leftKey.isPressed()) {
         var5--;
      }

      if (var4 != 0.0F || var5 != 0.0F) {
         double var6 = var4 * var4 + var5 * var5;
         if (var6 > 1.0) {
            double var8 = Math.sqrt(var6);
            var4 /= (float)var8;
            var5 /= (float)var8;
         }

         double var10 = Math.toRadians(var3);
         double var12 = Math.sin(var10);
         double var14 = Math.cos(var10);
         double var16 = (var5 * var14 - var4 * var12) * speed;
         double var18 = (var4 * var14 + var5 * var12) * speed;
         Vec3d var20 = mc.player.getVelocity();
         mc.player.setVelocity(var16, var20.y, var18);
      }
   }
}
