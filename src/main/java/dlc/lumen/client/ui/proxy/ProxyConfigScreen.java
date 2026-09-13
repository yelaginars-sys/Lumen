package dlc.lumen.client.ui.proxy;

import dlc.lumen.client.modules.impl.misc.Proxy;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ProxyConfigScreen extends Screen {
   private final Screen screen2;
   private TextFieldWidget textFieldWidget;
   private TextFieldWidget textFieldWidget2;
   private TextFieldWidget textFieldWidget3;
   private boolean flag;
   private String text2;

   public ProxyConfigScreen(Screen parent) {
      super(Text.literal("Proxy"));
      this.screen2 = parent;
   }

   @Override
   protected void init() {
      Proxy var1 = Proxy.INSTANCE;
      this.flag = var1.cfgEnabled();
      this.text2 = var1.cfgType();
      int var2 = this.width / 2;
      int var3 = var2 - 150;
      int var4 = this.height / 4;
      this.textFieldWidget = new TextFieldWidget(this.textRenderer, var3, var4 + 14, 300, 20, Text.literal("Адрес"));
      this.textFieldWidget.setMaxLength(128);
      this.textFieldWidget.setPlaceholder(Text.literal("например 1.2.3.4:1080"));
      this.textFieldWidget.setText(var1.cfgAddress());
      this.addDrawableChild(this.textFieldWidget);
      this.textFieldWidget2 = new TextFieldWidget(this.textRenderer, var3, var4 + 58, 145, 20, Text.literal("Логин"));
      this.textFieldWidget2.setMaxLength(64);
      this.textFieldWidget2.setPlaceholder(Text.literal("необязательно"));
      this.textFieldWidget2.setText(var1.cfgUser());
      this.addDrawableChild(this.textFieldWidget2);
      this.textFieldWidget3 = new TextFieldWidget(this.textRenderer, var2 + 5, var4 + 58, 145, 20, Text.literal("Пароль"));
      this.textFieldWidget3.setMaxLength(64);
      this.textFieldWidget3.setPlaceholder(Text.literal("необязательно"));
      this.textFieldWidget3.setText(var1.cfgPass());
      this.addDrawableChild(this.textFieldWidget3);
      this.addDrawableChild(ButtonWidget.builder(this.helper(), b -> {
         this.flag = !this.flag;
         b.setMessage(this.helper());
      }).dimensions(var3, var4 + 92, 145, 20).build());
      this.addDrawableChild(ButtonWidget.builder(this.helper2(), b -> {
         this.helper3();
         b.setMessage(this.helper2());
      }).dimensions(var2 + 5, var4 + 92, 145, 20).build());
      this.addDrawableChild(ButtonWidget.builder(Text.literal("Готово"), b -> {
         this.helper4();
         this.close();
      }).dimensions(var3, var4 + 122, 145, 20).build());
      this.addDrawableChild(ButtonWidget.builder(Text.literal("Отмена"), b -> this.close()).dimensions(var2 + 5, var4 + 122, 145, 20).build());
   }

   private Text helper() {
      return Text.literal("Прокси: " + (this.flag ? "Вкл" : "Выкл"));
   }

   private Text helper2() {
      return Text.literal("Тип: " + this.text2);
   }

   private void helper3() {
      List var1 = Proxy.INSTANCE.cfgTypes();
      if (!var1.isEmpty()) {
         int var2 = var1.indexOf(this.text2);
         this.text2 = (String)var1.get((var2 + 1) % var1.size());
      }
   }

   private void helper4() {
      Proxy var1 = Proxy.INSTANCE;
      var1.cfgSetAddress(this.textFieldWidget.getText().trim());
      var1.cfgSetUser(this.textFieldWidget2.getText().trim());
      var1.cfgSetPass(this.textFieldWidget3.getText());
      var1.cfgSetType(this.text2);
      var1.cfgSetEnabled(this.flag);
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      int var5 = this.width / 2;
      int var6 = var5 - 150;
      int var7 = this.height / 4;
      context.drawCenteredTextWithShadow(this.textRenderer, this.title, var5, var7 - 22, 16777215);
      context.drawTextWithShadow(this.textRenderer, Text.literal("Адрес прокси (host:port)"), var6, var7 + 2, 16777215);
      context.drawTextWithShadow(this.textRenderer, Text.literal("Логин"), var6, var7 + 46, 12632256);
      context.drawTextWithShadow(this.textRenderer, Text.literal("Пароль"), var5 + 5, var7 + 46, 12632256);
      context.drawCenteredTextWithShadow(
         this.textRenderer, Text.literal("форматы: host:port  •  user:pass@host:port  •  socks5://host:port"), var5, var7 - 10, 8421504
      );
   }

   @Override
   public void close() {
      if (this.client != null) {
         this.client.setScreen(this.screen2);
      }
   }
}
