package dlc.lumen.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.cmd.waypoint.Waypoint;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.impl.render.Arrows;
import dlc.lumen.client.modules.impl.render.Points;
import dlc.lumen.client.modules.impl.render.Trajectories;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import dlc.lumen.client.social.GlobalSocialManager;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldTagRenderer implements QClient {
   public static final WorldTagRenderer INSTANCE = new WorldTagRenderer();
   private static final Identifier TEXTURE_ID = Identifier.of("lumen", "textures/arrows/arrow.png");
   private static final Identifier TEXTURE_ID2 = Identifier.of("lumen", "textures/arrows/arr.png");
   private static final Identifier TEXTURE_ID3 = Identifier.of("lumen", "textures/arrows/gps.png");
   private static final int INDEX = -1;
   private static final int INDEX2 = 11;
   private static final int INDEX3 = 32;
   private static final int INDEX4 = 20;
   private final Matrix4f matrix4f = new Matrix4f();
   private final Quaternionf quaternionf = new Quaternionf();
   private Vec3d vec3d = Vec3d.ZERO;
   private int index;
   private int index2;
   private boolean flag;
   private final Vector3f vector3f = new Vector3f();
   private final Vector4f vector4f = new Vector4f();
   private final float[] volume = new float[2];

   private WorldTagRenderer() {
   }

   @EventLink(priority = 100)
   public void onRender3D(Event3DRender event) {
      this.flag = true;
      this.matrix4f.set(event.getProjectionMatrix());
      this.vec3d = event.getCamera().getPos();
      this.quaternionf.set(event.getCamera().getRotation()).conjugate();
      this.index = mc.getWindow().getScaledWidth();
      this.index2 = mc.getWindow().getScaledHeight();
   }

   @EventLink(priority = 100)
   public void onRender2D(EventRender.Default event) {
      if (this.flag && mc.world != null && mc.player != null) {
         List<Trajectories.TrapRenderInfo> var2 = Trajectories.INSTANCE.getActiveTrapRenderInfo();
         List<Trajectories.ImpactRenderInfo> var3 = Trajectories.INSTANCE.isEnable() ? Trajectories.INSTANCE.getImpactRenderInfo() : Collections.emptyList();
         List<Waypoint> var4 = Lumen.INSTANCE.waypointStorage != null ? Lumen.INSTANCE.waypointStorage.getWaypoints() : Collections.emptyList();
         List<GlobalSocialManager.PartyPoint> var5 = Points.INSTANCE.isEnable() ? GlobalSocialManager.INSTANCE.getVisiblePoints() : Collections.emptyList();
         List<GlobalSocialManager.PartyMemberSnapshot> var6 = Arrows.INSTANCE.isEnable() ? GlobalSocialManager.INSTANCE.getVisiblePartyMembers() : Collections.emptyList();
         List<GlobalSocialManager.PartyMemberSnapshot> var7 = Arrows.INSTANCE.isEnable() ? GlobalSocialManager.INSTANCE.getAllPartyMembers() : Collections.emptyList();
         if (!var2.isEmpty() || !var3.isEmpty() || !var4.isEmpty() || !var5.isEmpty() || !var6.isEmpty() || !var7.isEmpty()) {
            this.helper2(event, var2);
            this.helper4(event, var3);

            for (Waypoint var9 : var4) {
               this.helper6(event, var9);
            }

            this.helper7(event, var5);
            this.helper9(event, var6);
            this.helper11(event, var7);
         }
      }
   }

   private boolean helper(Vec3d pos, double yOffset) {
      this.vector3f
         .set((float)(pos.x - this.vec3d.x), (float)(pos.y + yOffset - this.vec3d.y), (float)(pos.z - this.vec3d.z));
      this.vector3f.rotate(this.quaternionf);
      this.vector4f.set(this.vector3f.x, this.vector3f.y, this.vector3f.z, 1.0F);
      this.matrix4f.transform(this.vector4f);
      float var4 = this.vector4f.w;
      if (var4 <= 1.0E-5F) {
         return false;
      } else {
         float var5 = (this.vector4f.x / var4 * 0.5F + 0.5F) * this.index;
         float var6 = (1.0F - (this.vector4f.y / var4 * 0.5F + 0.5F)) * this.index2;
         if (!Float.isNaN(var5) && !Float.isNaN(var6) && !Float.isInfinite(var5) && !Float.isInfinite(var6)) {
            this.volume[0] = var5;
            this.volume[1] = var6;
            return true;
         } else {
            return false;
         }
      }
   }

   private void helper2(EventRender.Default event, List<Trajectories.TrapRenderInfo> trapTags) {
      if (trapTags != null && !trapTags.isEmpty()) {
         Font var3 = Fonts.getFont("sf_regular", 11);
         if (var3 != null) {
            int var4 = 0;

            for (Trajectories.TrapRenderInfo var6 : trapTags) {
               if (var4 >= 32) {
                  break;
               }

               if (!(mc.player.squaredDistanceTo(var6.center()) > 16384.0) && this.helper(var6.center(), 1.1)) {
                  this.helper3(event, var3, var6, this.volume[0], this.volume[1]);
                  var4++;
               }
            }
         }
      }
   }

   private void helper3(EventRender.Default event, Font font, Trajectories.TrapRenderInfo trapInfo, float anchorX, float anchorY) {
      MatrixStack var6 = event.getContext().getMatrices();
      String var7 = this.helper17(trapInfo.secondsLeft());
      float var8 = 10.0F;
      float var9 = 3.0F;
      float var10 = font.getStringWidth(trapInfo.displayName());
      float var11 = font.getStringWidth(var7);
      float var12 = var8 + var9 + var10 + var9 + var11 + 9.0F;
      float var13 = 14.0F;
      float var14 = anchorX - var12 * 0.5F;
      float var15 = anchorY - var13 - 2.0F;
      InterfaceProcessing.drawHudBg(var6, var14 - 2.0F, var15 - 0.5F, var12 + 4.0F, var13 - 2.0F);
      this.helper14(event, new ItemStack(trapInfo.iconItem()), var14 + 2.0F, var15 + 0.3F, 0.62F);
      font.drawString(var6, trapInfo.displayName(), var14 + 2.0F + var8 + var9, var15 + 4.4F, -1);
      font.drawString(var6, var7, var14 + 2.0F + var8 + var9 + var10 + var9, var15 + 4.4F, ColorUtils.rgba(190, 190, 190, 255));
   }

   private void helper4(EventRender.Default event, List<Trajectories.ImpactRenderInfo> impacts) {
      if (impacts != null && !impacts.isEmpty()) {
         Font var3 = Fonts.getFont("sf_regular", 11);
         if (var3 != null) {
            int var4 = 0;

            for (Trajectories.ImpactRenderInfo var6 : impacts) {
               if (var4 >= 32) {
                  break;
               }

               if (!(mc.player.squaredDistanceTo(var6.pos()) > 16384.0) && this.helper(var6.pos(), 0.6)) {
                  this.helper5(event, var3, var6, this.volume[0], this.volume[1]);
                  var4++;
               }
            }
         }
      }
   }

   private void helper5(EventRender.Default event, Font font, Trajectories.ImpactRenderInfo impact, float anchorX, float anchorY) {
      MatrixStack var6 = event.getContext().getMatrices();
      ItemStack var7 = new ItemStack(impact.icon());
      String var8 = var7.getName().getString();
      String var9 = this.helper15(impact.pos());
      String var10 = this.helper17(impact.seconds());
      float var11 = 10.0F;
      float var12 = 3.0F;
      float var13 = font.getStringWidth(var8);
      float var14 = font.getStringWidth(var9);
      float var15 = font.getStringWidth(var10);
      float var16 = var11 + var12 + var13 + var12 + var14 + var12 + var15 + 9.0F;
      float var17 = 14.0F;
      float var18 = anchorX - var16 * 0.5F;
      float var19 = anchorY - var17 - 2.0F;
      InterfaceProcessing.drawHudBg(var6, var18 - 2.0F, var19 - 0.5F, var16 + 4.0F, var17 - 2.0F);
      this.helper14(event, var7, var18 + 2.0F, var19 + 0.3F, 0.62F);
      float var20 = var18 + 2.0F + var11 + var12;
      float var21 = var19 + 4.4F;
      font.drawString(var6, var8, var20, var21, -1);
      var20 += var13 + var12;
      font.drawString(var6, var9, var20, var21, ColorUtils.getThemeColor());
      var20 += var14 + var12;
      font.drawString(var6, var10, var20, var21, ColorUtils.rgba(190, 190, 190, 255));
   }

   private void helper6(EventRender.Default event, Waypoint waypoint) {
      if (waypoint != null) {
         Font var3 = Fonts.getFont("sf_regular", 11);
         if (var3 != null) {
            Vec3d var4 = new Vec3d(waypoint.getX() + 0.5, waypoint.getY() + 1.0, waypoint.getZ() + 0.5);
            if (this.helper(var4, 0.6)) {
               MatrixStack var5 = event.getContext().getMatrices();
               ItemStack var6 = new ItemStack(Items.COMPASS);
               String var7 = waypoint.getName() != null && !waypoint.getName().isBlank() ? waypoint.getName() : "GPS";
               String var8 = this.helper15(var4);
               float var9 = 10.0F;
               float var10 = 3.0F;
               float var11 = var3.getStringWidth(var7);
               float var12 = var3.getStringWidth(var8);
               float var13 = var9 + var10 + var11 + var10 + var12 + 9.0F;
               float var14 = 14.0F;
               float var15 = this.volume[0] - var13 * 0.5F;
               float var16 = this.volume[1] - var14 - 2.0F;
               InterfaceProcessing.drawHudBg(var5, var15 - 2.0F, var16 - 0.5F, var13 + 4.0F, var14 - 2.0F);
               this.helper14(event, var6, var15 + 2.0F, var16 + 0.3F, 0.62F);
               float var17 = var15 + 2.0F + var9 + var10;
               float var18 = var16 + 4.4F;
               var3.drawString(var5, var7, var17, var18, -1);
               var17 += var11 + var10;
               var3.drawString(var5, var8, var17, var18, ColorUtils.getThemeColor());
            }
         }
      }
   }

   private void helper7(EventRender.Default event, List<GlobalSocialManager.PartyPoint> points) {
      if (points != null && !points.isEmpty()) {
         Font var3 = Fonts.getFont("sf_regular", 11);
         if (var3 != null) {
            int var4 = 0;

            for (GlobalSocialManager.PartyPoint var6 : points) {
               if (var4 >= 20) {
                  break;
               }

               Vec3d var7 = new Vec3d(var6.x(), var6.y(), var6.z());
               if (!(mc.player.squaredDistanceTo(var7) > 65536.0) && this.helper(var7, 1.05)) {
                  this.helper8(event, var3, var6, var7, this.volume[0], this.volume[1]);
                  var4++;
               }
            }
         }
      }
   }

   private void helper8(EventRender.Default event, Font font, GlobalSocialManager.PartyPoint point, Vec3d pos, float anchorX, float anchorY) {
      MatrixStack var7 = event.getContext().getMatrices();
      String var8 = point.ownerNickname() != null && !point.ownerNickname().isBlank() ? point.ownerNickname() + " • point" : "Point";
      String var9 = this.helper15(pos);
      float var10 = 10.0F;
      float var11 = 3.0F;
      float var12 = font.getStringWidth(var8);
      float var13 = font.getStringWidth(var9);
      float var14 = var10 + var11 + var12 + var11 + var13 + 9.0F;
      float var15 = 14.0F;
      float var16 = anchorX - var14 * 0.5F;
      float var17 = anchorY - var15 - 2.0F;
      InterfaceProcessing.drawHudBg(var7, var16 - 2.0F, var17 - 0.5F, var14 + 4.0F, var15 - 2.0F);
      this.helper12(
         var7, TEXTURE_ID3, var16 + 2.0F, var17 + 0.4F, var10, point.local() ? ColorUtils.getThemeColor() : ColorUtils.rgba(255, 255, 255, 230)
      );
      float var18 = var16 + 2.0F + var10 + var11;
      float var19 = var17 + 4.4F;
      font.drawString(var7, var8, var18, var19, point.local() ? ColorUtils.getThemeColor() : -1);
      var18 += var12 + var11;
      font.drawString(var7, var9, var18, var19, ColorUtils.rgba(190, 190, 190, 255));
   }

   private void helper9(EventRender.Default event, List<GlobalSocialManager.PartyMemberSnapshot> members) {
      if (members != null && !members.isEmpty()) {
         Font var3 = Fonts.getFont("sf_regular", 11);
         if (var3 != null) {
            int var4 = 0;

            for (GlobalSocialManager.PartyMemberSnapshot var6 : members) {
               if (var4 >= 20) {
                  break;
               }

               Vec3d var7 = GlobalSocialManager.INSTANCE.getRenderPosition(var6);
               if (var7 != null && this.helper(var7, 2.15) && this.helper13(this.volume[0], this.volume[1], 28.0F)) {
                  this.helper10(event, var3, var6, var7, this.volume[0], this.volume[1]);
                  var4++;
               }
            }
         }
      }
   }

   private void helper10(EventRender.Default event, Font font, GlobalSocialManager.PartyMemberSnapshot member, Vec3d pos, float anchorX, float anchorY) {
      MatrixStack var7 = event.getContext().getMatrices();
      String var8 = member.nickname().equalsIgnoreCase(member.mcName()) ? member.nickname() : member.nickname() + " [" + member.mcName() + "]";
      String var9 = this.helper15(pos);
      float var10 = 10.0F;
      float var11 = 3.0F;
      float var12 = font.getStringWidth(var8);
      float var13 = font.getStringWidth(var9);
      float var14 = var10 + var11 + var12 + var11 + var13 + 9.0F;
      float var15 = 14.0F;
      float var16 = anchorX - var14 * 0.5F;
      float var17 = anchorY - var15 - 2.0F;
      InterfaceProcessing.drawHudBg(var7, var16 - 2.0F, var17 - 0.5F, var14 + 4.0F, var15 - 2.0F);
      this.helper12(var7, TEXTURE_ID2, var16 + 2.0F, var17 + 0.5F, var10, ColorUtils.getThemeColor());
      float var18 = var16 + 2.0F + var10 + var11;
      float var19 = var17 + 4.4F;
      font.drawString(var7, var8, var18, var19, -1);
      var18 += var12 + var11;
      font.drawString(var7, var9, var18, var19, ColorUtils.getThemeColor());
   }

   private void helper11(EventRender.Default event, List<GlobalSocialManager.PartyMemberSnapshot> members) {
      if (members != null && !members.isEmpty()) {
         Font var3 = Fonts.getFont("sf_regular", 11);
         if (var3 != null) {
            MatrixStack var4 = event.getContext().getMatrices();
            float var5 = this.index * 0.5F;
            float var6 = this.index2 * 0.5F;
            float var7 = Math.min(this.index, this.index2) * 0.34F;
            boolean var8 = Arrows.INSTANCE.showsCrossWorld();

            for (GlobalSocialManager.PartyMemberSnapshot var10 : members) {
               boolean var11 = GlobalSocialManager.INSTANCE.isSameWorldAs(var10);
               if (var11 || var8) {
                  Vec3d var12 = var11 ? GlobalSocialManager.INSTANCE.getRenderPosition(var10) : new Vec3d(var10.x(), var10.y(), var10.z());
                  if (var12 != null
                     && (!var11 || !this.helper(var12, 2.15) || !this.helper13(this.volume[0], this.volume[1], 28.0F))) {
                     double var13 = var12.x - mc.player.getX();
                     double var15 = var12.z - mc.player.getZ();
                     if (!(var13 * var13 + var15 * var15 < 1.0)) {
                        float var17 = (float)Math.toDegrees(Math.atan2(var13, var15));
                        float var18 = MathHelper.wrapDegrees(var17 - mc.player.getYaw());
                        double var19 = Math.toRadians(var18);
                        float var21 = var5 + (float)Math.sin(var19) * var7;
                        float var22 = var6 - (float)Math.cos(var19) * var7;
                        int var23 = var11 ? ColorUtils.getThemeColor() : ColorUtils.rgba(180, 180, 180, 230);
                        var4.push();
                        var4.translate(var21, var22, 0.0F);
                        var4.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var18));
                        RenderUtils.drawImage(var4, TEXTURE_ID, -11.0F, -11.0F, 22.0F, 22.0F, var23);
                        var4.pop();
                        var3.drawCenteredString(var4, var10.nickname(), var21, var22 + 11.0F, -1);
                        String var24 = var11 ? this.helper15(var12) : this.helper16(var12);
                        var3.drawCenteredString(var4, var24, var21, var22 + 20.0F, ColorUtils.rgba(190, 190, 190, 255));
                     }
                  }
               }
            }
         }
      }
   }

   private void helper12(MatrixStack matrices, Identifier texture, float x, float y, float size, int color) {
      RenderUtils.drawImage(matrices, texture, x, y, size, size, color);
   }

   private boolean helper13(float x, float y, float padding) {
      return x >= padding && x <= this.index - padding && y >= padding && y <= this.index2 - padding;
   }

   private void helper14(EventRender.Default event, ItemStack stack, float x, float y, float scale) {
      MatrixStack var6 = event.getContext().getMatrices();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      var6.push();
      var6.translate(x, y, 0.0F);
      var6.scale(scale, scale, 1.0F);
      event.getContext().drawItem(stack, 0, 0);
      var6.pop();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
   }

   private String helper15(Vec3d pos) {
      double var2 = Math.sqrt(mc.player.squaredDistanceTo(pos));
      return Math.round(var2) + "м";
   }

   private String helper16(Vec3d pos) {
      return Math.round(pos.x) + " " + Math.round(pos.y) + " " + Math.round(pos.z);
   }

   private String helper17(float secondsLeft) {
      int var2 = Math.max(0, Math.round(secondsLeft * 10.0F));
      return var2 / 10 + "." + Math.abs(var2 % 10) + "с";
   }
}
