package dlc.lumen.api.utils.render;

import dlc.lumen.api.QClient;
import lombok.Generated;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class ShaderUtils implements QClient {
   public static final ShaderProgramKey playerOutline = getOutline("outline", "outline", VertexFormats.POSITION);
   public static final ShaderProgramKey blurFog = getOutline("kawase_down", "kawase_down", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey roundedRect = getOutline("rect", "rounded_rect", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey roundedRectOutline = getOutline("rect", "rounded_rect_outline", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey ringArc = getOutline("ring_arc", "ring_arc", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey roundedTexture = getOutline("texture", "texture_rect", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey liquidGlass = getOutline("liquidglass", "liquid", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey hudLiquidGlass = getOutline("liquidglass", "liquid_hud", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey kawaseDown = getOutline("kawase_down", "kawase_down", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey kawaseUp = getOutline("kawase_up", "kawase_up", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey gradientRect = getOutline("gradient_rect", "gradient", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shadowRect = getOutline("shadow_rect", "shadow", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey shadow6Rect = getOutline("shadow6", "shadow", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey fontsMsdf = getOutline("fonts", "fonts", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey face = getOutline("face", "face", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey gradient6Rect = getOutline("gradient6", "gradient", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey sonar = getOutline("sonar", "sonar", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey scanEffect = getOutline("sonar", "scan_effect", VertexFormats.POSITION_TEXTURE);
   public static final ShaderProgramKey blockOverlay = getOutline("blockoverlay", "block_overlay", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey blockOverlayNebula = getOutline("blockoverlay", "block_overlay_nebula", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey chamsFill = getOutline("chams", "chams_fill", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey chamsGlass = getOutline("chams", "chams_glass", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey chamsComposite = getOutline("chams", "chams_composite", VertexFormats.POSITION_TEXTURE);
   public static final ShaderProgramKey chamsGlitch = getOutline("chams", "chams_glitch", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey chamsMarble = getOutline("chams", "chams_marble", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsMaskDiff = getOutline("hands", "hands_mask_diff", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsOverlay = getOutline("hands", "hands_overlay", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsGlow = getOutline("hands", "hands_glow", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsKawaseDown = getOutline("hands", "hands_kawase_down", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsKawaseUp = getOutline("hands", "hands_kawase_up", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsTrail = getOutline("hands", "hands_trail", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsTrailComposite = getOutline("hands", "hands_trail_composite", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsNebula = getOutline("hands", "hands_nebula", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsSlime = getOutline("hands", "hands_slime", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsFire = getOutline("hands", "hands_fire", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsFireComposite = getOutline("hands", "hands_fire_composite", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey trailsGhost = getOutline("trails", "trails_ghost", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey viralJumpSpace = getOutline("viraljump", "viraljump_space", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey viralJumpSlime = getOutline("viraljump", "viraljump_slime", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey viralJumpVector = getOutline("viraljump", "viraljump_vector", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderEspGlow = getOutline("shaderesp", "glow", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderEspFill = getOutline("shaderesp", "fill", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderEspDown = getOutline("shaderesp", "esp_down", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderEspUp = getOutline("shaderesp", "esp_up", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderEspCombine = getOutline("shaderesp", "esp_combine", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey fogBlur = getOutline("fog", "fog_blur", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey sky = getOutline("sky", "sky", VertexFormats.POSITION_TEXTURE);
   public static final ShaderProgramKey lightning = getOutline("lightning", "lightning", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey mainMenuBackground = getOutline("mainmenu", "background", VertexFormats.POSITION_TEXTURE);

   private static ShaderProgramKey getOutline(String shaderNamePackage, String shaderName, VertexFormat vertexFormat) {
      return new ShaderProgramKey(Identifier.of("lumen", "core/" + shaderNamePackage + "/" + shaderName), vertexFormat, Defines.EMPTY);
   }

   @Generated
   private ShaderUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
