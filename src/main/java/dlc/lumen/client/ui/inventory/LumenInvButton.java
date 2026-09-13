package dlc.lumen.client.ui.inventory;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.Selectable.SelectionType;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;

public class LumenInvButton implements Element, Drawable, Selectable {
   private static final int WIDTH = 78;
   private static final int HEIGHT = 15;

   private final String label;
   private final Runnable action;
   private int x;
   private int y;
   private boolean hovered;
   private boolean focused;

   public LumenInvButton(String label, Runnable action, int x, int y) {
      this.label = label;
      this.action = action;
      this.x = x;
      this.y = y;
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      this.hovered = mouseX >= this.x && mouseX < this.x + WIDTH && mouseY >= this.y && mouseY < this.y + HEIGHT;
      MatrixStack matrices = context.getMatrices();
      int background = this.hovered ? ColorUtils.rgba(30, 32, 40, 235) : ColorUtils.rgba(17, 18, 23, 210);
      RenderUtils.drawRoundedRect(matrices, this.x, this.y, WIDTH, HEIGHT, 4.0F, background);
      Font font = Fonts.getFont("inter_medium", 13);
      if (font != null) {
         float textX = this.x + 8.0F;
         float textY = this.y + (HEIGHT - font.getHeight()) / 2.0F;
         int color = this.hovered ? ColorUtils.getThemeColor() : ColorUtils.clientText();
         font.drawString(matrices, this.label, textX, textY, color);
      }
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && this.hovered) {
         this.action.run();
         return true;
      }

      return false;
   }

   @Override
   public boolean isMouseOver(double mouseX, double mouseY) {
      return mouseX >= this.x && mouseX < this.x + WIDTH && mouseY >= this.y && mouseY < this.y + HEIGHT;
   }

   @Override
   public SelectionType getType() {
      return this.hovered ? SelectionType.HOVERED : SelectionType.NONE;
   }

   @Override
   public void appendNarrations(NarrationMessageBuilder builder) {
   }

   @Override
   public boolean isFocused() {
      return this.focused;
   }

   @Override
   public void setFocused(boolean focused) {
      this.focused = focused;
   }
}
