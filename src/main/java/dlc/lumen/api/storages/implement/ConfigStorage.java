package dlc.lumen.api.storages.implement;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dlc.lumen.Lumen;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.cmd.macro.Macro;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.render.ClickGuiTheme;
import dlc.lumen.client.modules.impl.render.Interface;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import dlc.lumen.client.modules.impl.render.base.implement.TargetHud;
import dlc.lumen.client.modules.settings.Setting;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import dlc.lumen.client.render.models.CustomModelManager;
import dlc.lumen.client.render.models.CustomModelType;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map.Entry;

public class ConfigStorage {
   public String currentConfig = "default";
   private static final String text = ".win";
   private static final String[] textArray = new String[]{".win", ".lumen"};

   public ConfigStorage() {
      this.init();
      Runtime.getRuntime().addShutdownHook(new Thread(this::saveAll));
   }

   private void init() {
      try {
         this.loadGlobals();
         this.loadConfig(this.currentConfig);
      } catch (Exception var2) {
         var2.printStackTrace(System.err);
      }
   }

   private void saveAll() {
      try {
         this.saveGlobals();
         this.saveConfig(this.currentConfig);
      } catch (Exception var2) {
         var2.printStackTrace(System.err);
      }
   }

   public void saveConfig(String config) throws Exception {
      File var2 = this.getConfig(config);
      JsonObject var3 = new JsonObject();
      var3.add("config", new JsonPrimitive(config));
      var3.add("theme", new JsonPrimitive(Lumen.INSTANCE.themeStorage.getThemes().name()));
      var3.add("language", new JsonPrimitive(Lumen.INSTANCE.localizationStorage.getLanguage().name()));
      var3.add("modules", this.buildModulesJson());
      var3.add("draggables", this.buildDraggablesJson());
      var3.add("hud", this.buildHudJson());
      var3.add("clickGui", this.buildClickGuiJson());
      var3.add("customTheme", this.buildCustomThemeJson());
      var3.add("clientColors", this.buildClientColorsJson());
      var3.add("playerModel", this.buildPlayerModelJson());

      try (OutputStreamWriter var4 = new OutputStreamWriter(new FileOutputStream(var2, false), StandardCharsets.UTF_8)) {
         var4.write(new GsonBuilder().setPrettyPrinting().create().toJson(var3));
      }

      this.currentConfig = config;
   }

   public void loadConfig(String config) throws Exception {
      File var2 = this.getConfig2(config);
      if (var2 != null && var2.exists()) {
         JsonObject var3;
         try (
            InputStream var4 = Files.newInputStream(var2.toPath());
            InputStreamReader var5 = new InputStreamReader(var4, StandardCharsets.UTF_8);
         ) {
            var3 = JsonParser.parseReader(var5).getAsJsonObject();
         }

         if (var3.has("theme")) {
            String var14 = var3.get("theme").getAsString();

            for (ThemeStorage.Themes var8 : ThemeStorage.Themes.values()) {
               if (var8.name().equals(var14)) {
                  Lumen.INSTANCE.themeStorage.setThemes(var8);
                  break;
               }
            }
         }

         if (var3.has("language")) {
            try {
               Lumen.INSTANCE.localizationStorage.setLanguage(LocalizationStorage.Language.valueOf(var3.get("language").getAsString()));
            } catch (Exception var11) {
            }
         }

         if (var3.has("draggables")) {
            this.handleDraggables(var3.get("draggables").getAsJsonObject());
         }

         if (var3.has("clickGui")) {
            this.handleClickGui(var3.get("clickGui").getAsJsonObject());
         }

         if (var3.has("customTheme")) {
            this.handleCustomTheme(var3.get("customTheme").getAsJsonObject());
         }

         if (var3.has("clientColors")) {
            this.handleColors(var3.get("clientColors").getAsJsonObject());
         }

         if (var3.has("modules")) {
            this.handleModules(var3.get("modules").getAsJsonObject());
         }

         if (var3.has("hud")) {
            this.handleHud(var3.get("hud").getAsJsonObject());
         }

         if (var3.has("playerModel")) {
            this.handleAction(var3.get("playerModel").getAsJsonObject());
         }

         this.currentConfig = config;
      }
   }

   public void saveGlobals() throws Exception {
      File var1 = this.createFile();
      JsonObject var2 = new JsonObject();
      var2.add("config", new JsonPrimitive(this.currentConfig));
      var2.add("theme", new JsonPrimitive(Lumen.INSTANCE.themeStorage.getThemes().name()));
      var2.add("language", new JsonPrimitive(Lumen.INSTANCE.localizationStorage.getLanguage().name()));
      var2.add("draggables", this.buildDraggablesJson());
      var2.add("hud", this.buildHudJson());
      var2.add("clickGui", this.buildClickGuiJson());
      var2.add("customTheme", this.buildCustomThemeJson());
      var2.add("clientColors", this.buildClientColorsJson());
      var2.add("playerModel", this.buildPlayerModelJson());
      JsonArray var3 = new JsonArray();

      for (String var5 : Lumen.INSTANCE.friendStorage.getFriends()) {
         JsonObject var6 = new JsonObject();
         var6.addProperty("name", var5);
         String var7 = Lumen.INSTANCE.friendStorage.getCustomName(var5);
         if (var7 != null) {
            var6.addProperty("customName", var7);
         }

         String var8 = Lumen.INSTANCE.friendStorage.getModelId(var5);
         if (var8 != null) {
            var6.addProperty("modelId", var8);
         }

         var3.add(var6);
      }

      var2.add("friends", var3);
      JsonArray var11 = new JsonArray();
      Lumen.INSTANCE.staffStorage.getStaffs().forEach(var11::add);
      var2.add("staffs", var11);
      JsonArray var12 = new JsonArray();
      Lumen.INSTANCE.macroStorage.getMacros().forEach(macro -> {
         JsonObject var2x = new JsonObject();
         var2x.addProperty("name", macro.getName());
         var2x.addProperty("command", macro.getCommand());
         var2x.addProperty("key", macro.getBind().getKey());
         var12.add(var2x);
      });
      var2.add("macros", var12);

      try (OutputStreamWriter var13 = new OutputStreamWriter(new FileOutputStream(var1, false), StandardCharsets.UTF_8)) {
         var13.write(new GsonBuilder().setPrettyPrinting().create().toJson(var2));
      }
   }

   public void loadGlobals() throws Exception {
      File var1 = this.getFile();
      if (var1 != null && var1.exists()) {
         JsonObject var2;
         try (
            InputStream var3 = Files.newInputStream(var1.toPath());
            InputStreamReader var4 = new InputStreamReader(var3, StandardCharsets.UTF_8);
         ) {
            var2 = JsonParser.parseReader(var4).getAsJsonObject();
         }

         if (var2.has("config")) {
            this.currentConfig = var2.get("config").getAsString();
         }

         if (var2.has("theme")) {
            String var16 = var2.get("theme").getAsString();

            for (ThemeStorage.Themes var7 : ThemeStorage.Themes.values()) {
               if (var7.name().equals(var16)) {
                  Lumen.INSTANCE.themeStorage.setThemes(var7);
                  break;
               }
            }
         }

         if (var2.has("language")) {
            try {
               Lumen.INSTANCE.localizationStorage.setLanguage(LocalizationStorage.Language.valueOf(var2.get("language").getAsString()));
            } catch (Exception var12) {
            }
         }

         if (var2.has("draggables")) {
            this.handleDraggables(var2.get("draggables").getAsJsonObject());
         }

         if (var2.has("clickGui")) {
            this.handleClickGui(var2.get("clickGui").getAsJsonObject());
         }

         if (var2.has("customTheme")) {
            this.handleCustomTheme(var2.get("customTheme").getAsJsonObject());
         }

         if (var2.has("clientColors")) {
            this.handleColors(var2.get("clientColors").getAsJsonObject());
         }

         if (var2.has("hud")) {
            this.handleHud(var2.get("hud").getAsJsonObject());
         }

         if (var2.has("playerModel")) {
            this.handleAction(var2.get("playerModel").getAsJsonObject());
         }

         if (var2.has("friends")) {
            for (JsonElement var21 : var2.get("friends").getAsJsonArray()) {
               try {
                  if (var21.isJsonObject()) {
                     JsonObject var24 = var21.getAsJsonObject();
                     if (var24.has("name")) {
                        String var27 = var24.get("name").getAsString();
                        if (!var27.isBlank()) {
                           if (!Lumen.INSTANCE.friendStorage.isFriend(var27)) {
                              Lumen.INSTANCE.friendStorage.add(var27);
                           }

                           if (var24.has("customName")) {
                              Lumen.INSTANCE.friendStorage.setCustomName(var27, var24.get("customName").getAsString());
                           }

                           if (var24.has("modelId")) {
                              Lumen.INSTANCE.friendStorage.setModelId(var27, var24.get("modelId").getAsString());
                           }
                        }
                     }
                  } else {
                     String var25 = var21.getAsString();
                     if (!Lumen.INSTANCE.friendStorage.isFriend(var25)) {
                        Lumen.INSTANCE.friendStorage.add(var25);
                     }
                  }
               } catch (Exception var11) {
               }
            }
         }

         if (var2.has("staffs")) {
            for (JsonElement var22 : var2.get("staffs").getAsJsonArray()) {
               if (!Lumen.INSTANCE.staffStorage.isStaff(var22.getAsString())) {
                  Lumen.INSTANCE.staffStorage.add(var22.getAsString());
               }
            }
         }

         if (var2.has("macros")) {
            for (JsonElement var23 : var2.get("macros").getAsJsonArray()) {
               try {
                  String var26;
                  String var28;
                  int var29;
                  if (var23.isJsonObject()) {
                     JsonObject var8 = var23.getAsJsonObject();
                     var26 = var8.has("name") ? var8.get("name").getAsString() : "";
                     var28 = var8.has("command") ? var8.get("command").getAsString() : "";
                     var29 = var8.has("key") ? var8.get("key").getAsInt() : -1;
                  } else {
                     String[] var30 = var23.getAsString().split(":", 3);
                     if (var30.length < 3) {
                        continue;
                     }

                     var26 = var30[0];
                     var28 = var30[1];
                     var29 = Integer.parseInt(var30[2]);
                  }

                  if (!var26.isBlank() && Lumen.INSTANCE.macroStorage.getMacro(var26) == null) {
                     Lumen.INSTANCE.macroStorage.add(new Macro(var26, var28, new BindSetting("bind", var29)));
                  }
               } catch (Exception var15) {
               }
            }
         }
      }
   }

   private JsonObject buildModulesJson() {
      JsonObject var1 = new JsonObject();
      ObjectListIterator var2 = ModuleClass.INSTANCE.getObject().iterator();

      while (var2.hasNext()) {
         Module var3 = (Module)var2.next();

         try {
            JsonObject var4 = new JsonObject();
            var4.add("toggled", new JsonPrimitive(var3.isEnable()));
            var4.add("bind", new JsonPrimitive(var3.getKey()));
            JsonObject var5 = new JsonObject();

            for (Setting var7 : var3.getSettings()) {
               try {
                  if (var7 instanceof BooleanSetting var8) {
                     var5.add(var7.name(), new JsonPrimitive(var8.isState()));
                     if (var8.getKey() != -1) {
                        var5.add(var7.name() + "@bind", new JsonPrimitive(var8.getKey()));
                     }
                  } else if (var7 instanceof FloatSetting var9) {
                     var5.add(var7.name(), new JsonPrimitive(var9.getValue().floatValue()));
                  } else if (var7 instanceof ModeSetting var10) {
                     var5.add(var7.name(), new JsonPrimitive(var10.getCurrent()));
                  } else if (var7 instanceof TextSetting var11) {
                     var5.add(var7.name(), new JsonPrimitive(var11.get()));
                  } else if (var7 instanceof BindSetting var12) {
                     var5.add(var7.name(), new JsonPrimitive(var12.getKey()));
                  } else if (var7 instanceof ListSetting var13) {
                     JsonObject var14 = new JsonObject();

                     for (BooleanSetting var16 : var13.getSettings()) {
                        var14.add(var16.name(), new JsonPrimitive(var16.isState()));
                     }

                     var5.add(var13.name(), var14);
                  }
               } catch (Exception var17) {
               }
            }

            var4.add("settings", var5);
            var1.add(var3.getName(), var4);
         } catch (Exception var18) {
         }
      }

      return var1;
   }

   private void handleModules(JsonObject modules) {
      LinkedHashMap<Module, Boolean> var2 = new LinkedHashMap<>();
      ObjectListIterator var3 = ModuleClass.INSTANCE.getObject().iterator();

      while (var3.hasNext()) {
         Module var4 = (Module)var3.next();

         try {
            JsonObject var5 = modules.has(var4.getName()) ? modules.get(var4.getName()).getAsJsonObject() : null;
            boolean var6 = var5 != null && var5.has("toggled") && var5.get("toggled").getAsBoolean();
            var2.put(var4, var6);
            if (var4.isEnable()) {
               var4.setEnabled(false);
            }
         } catch (Exception var22) {
            var2.put(var4, false);
         }
      }

      var3 = ModuleClass.INSTANCE.getObject().iterator();

      while (var3.hasNext()) {
         Module var25 = (Module)var3.next();

         try {
            if (modules.has(var25.getName())) {
               JsonObject var27 = modules.get(var25.getName()).getAsJsonObject();
               if (var27.has("bind")) {
                  var25.setKey(var27.get("bind").getAsInt());
               }

               if (var27.has("settings")) {
                  JsonObject var28 = var27.get("settings").getAsJsonObject();

                  for (Setting var8 : var25.getSettings()) {
                     try {
                        if (var28.has(var8.name())) {
                           JsonElement var9 = var28.get(var8.name());
                           if (var8 instanceof BooleanSetting var10) {
                              var10.setState(var9.getAsBoolean());
                              if (var28.has(var8.name() + "@bind")) {
                                 var10.setKey(var28.get(var8.name() + "@bind").getAsInt());
                              }
                           } else if (var8 instanceof FloatSetting var11) {
                              var11.setValue(var9.getAsFloat());
                           } else if (var8 instanceof ModeSetting var12) {
                              var12.set(var9.getAsString());
                           } else if (var8 instanceof TextSetting var13) {
                              var13.setText(var9.getAsString());
                           } else if (var8 instanceof BindSetting var14) {
                              var14.setKey(var9.getAsInt());
                           } else if (var8 instanceof ListSetting var15) {
                              JsonObject var16 = var9.getAsJsonObject();

                              for (BooleanSetting var18 : var15.getSettings()) {
                                 if (var16.has(var18.name())) {
                                    var18.setState(var16.get(var18.name()).getAsBoolean());
                                 }
                              }
                           }
                        }
                     } catch (Exception var20) {
                     }
                  }
               }
            }
         } catch (Exception var21) {
         }
      }

      for (Entry var26 : var2.entrySet()) {
         try {
            ((Module)var26.getKey()).setEnabled((Boolean)var26.getValue());
         } catch (Exception var19) {
         }
      }
   }

   private JsonObject buildHudJson() {
      JsonObject var1 = new JsonObject();
      Interface var2 = ModuleClass.interfaceModule;
      if (var2 == null) {
         return var1;
      }

      for (Entry var4 : var2.getConfigurableHudElements().entrySet()) {
         InterfaceProcessing var5 = (InterfaceProcessing)var4.getValue();
         if (var5 != null) {
            JsonObject var6 = new JsonObject();
            var6.add("unusualRectType", new JsonPrimitive(var5.isUnusualRectType()));
            if (var5 instanceof TargetHud var7) {
               var6.add("headParticlesEnabled", new JsonPrimitive(var7.isHeadParticlesEnabled()));
            }

            var1.add((String)var4.getKey(), var6);
         }
      }

      return var1;
   }

   private void handleHud(JsonObject hud) {
      Interface var2 = ModuleClass.interfaceModule;
      if (var2 != null) {
         for (Entry var4 : var2.getConfigurableHudElements().entrySet()) {
            if (hud.has((String)var4.getKey())) {
               try {
                  JsonObject var5 = hud.get((String)var4.getKey()).getAsJsonObject();
                  InterfaceProcessing var6 = (InterfaceProcessing)var4.getValue();
                  if (var5.has("unusualRectType")) {
                     var6.setUnusualRectType(var5.get("unusualRectType").getAsBoolean());
                  }

                  if (var6 instanceof TargetHud var7 && var5.has("headParticlesEnabled")) {
                     var7.setHeadParticlesEnabled(var5.get("headParticlesEnabled").getAsBoolean());
                  }
               } catch (Exception var8) {
               }
            }
         }
      }
   }

   private JsonObject buildDraggablesJson() {
      JsonObject var1 = new JsonObject();

      for (Draggable var3 : DragStorage.draggables.values()) {
         JsonObject var4 = new JsonObject();
         var4.add("x", new JsonPrimitive(var3.getX()));
         var4.add("y", new JsonPrimitive(var3.getY()));
         var4.add("scaleX", new JsonPrimitive(var3.getScaleX()));
         var4.add("scaleY", new JsonPrimitive(var3.getScaleY()));
         var1.add(var3.getName(), var4);
      }

      return var1;
   }

   private void handleDraggables(JsonObject draggables) {
      for (String var3 : draggables.keySet()) {
         Draggable var4 = DragStorage.draggables.get(var3);
         if (var4 != null) {
            JsonObject var5 = draggables.get(var3).getAsJsonObject();
            if (var5.has("x")) {
               var4.setX(var5.get("x").getAsFloat());
            }

            if (var5.has("y")) {
               var4.setY(var5.get("y").getAsFloat());
            }

            if (var5.has("scaleX")) {
               var4.setScaleX(var5.get("scaleX").getAsFloat());
            }

            if (var5.has("scaleY")) {
               var4.setScaleY(var5.get("scaleY").getAsFloat());
            }
         }
      }
   }

   private JsonObject buildClickGuiJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("selectedModel", CustomModelManager.getSelectedModel().id());
      var1.addProperty("guiOpacity", ClickGuiTheme.opacity());
      var1.addProperty("guiBlur", ClickGuiTheme.blurMul());
      var1.addProperty("guiRadius", ClickGuiTheme.radiusMul());
      var1.addProperty("guiDensity", ClickGuiTheme.density());
      var1.addProperty("guiGlass", ClickGuiTheme.glass());
      var1.addProperty("guiHudGlass", ClickGuiTheme.hudGlass());
      var1.addProperty("guiShadow", ClickGuiTheme.shadowEnabled());
      var1.addProperty("guiAnimations", ClickGuiTheme.animationsEnabled());
      return var1;
   }

   private void handleClickGui(JsonObject clickGui) {
      if (clickGui.has("selectedModel")) {
         CustomModelManager.setSelectedModel(CustomModelType.fromId(clickGui.get("selectedModel").getAsString()));
      }

      if (clickGui.has("guiOpacity")) {
         ClickGuiTheme.setOpacity(clickGui.get("guiOpacity").getAsFloat());
      }

      if (clickGui.has("guiBlur")) {
         ClickGuiTheme.setBlur(clickGui.get("guiBlur").getAsFloat());
      }

      if (clickGui.has("guiRadius")) {
         ClickGuiTheme.setRadiusMul(clickGui.get("guiRadius").getAsFloat());
      }

      if (clickGui.has("guiDensity")) {
         ClickGuiTheme.setDensity(clickGui.get("guiDensity").getAsFloat());
      }

      if (clickGui.has("guiGlass")) {
         ClickGuiTheme.setGlass(clickGui.get("guiGlass").getAsBoolean());
      }

      ClickGuiTheme.setHudGlass(false);
      if (clickGui.has("guiShadow")) {
         ClickGuiTheme.setShadow(clickGui.get("guiShadow").getAsBoolean());
      }

      if (clickGui.has("guiAnimations")) {
         ClickGuiTheme.setAnimations(clickGui.get("guiAnimations").getAsBoolean());
      }
   }

   private JsonObject buildCustomThemeJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("animated", Lumen.INSTANCE.themeStorage.isCustomAnimated());
      var1.addProperty("primary", Lumen.INSTANCE.themeStorage.getCustomPrimaryColor());
      var1.addProperty("secondary", Lumen.INSTANCE.themeStorage.getCustomSecondaryColor());
      return var1;
   }

   private JsonObject buildClientColorsJson() {
      JsonObject var1 = new JsonObject();

      for (ClientColors.Slot var5 : ClientColors.Slot.values()) {
         JsonObject var6 = new JsonObject();
         var6.addProperty("color", ClientColors.raw(var5));
         var6.addProperty("rainbow", ClientColors.rainbow(var5));
         var1.add(var5.name(), var6);
      }

      return var1;
   }

   private void handleColors(JsonObject colors) {
      for (ClientColors.Slot var5 : ClientColors.Slot.values()) {
         if (colors.has(var5.name())) {
            try {
               JsonObject var6 = colors.get(var5.name()).getAsJsonObject();
               if (var6.has("color")) {
                  ClientColors.set(var5, var6.get("color").getAsInt());
               }

               if (var6.has("rainbow")) {
                  ClientColors.setRainbow(var5, var6.get("rainbow").getAsBoolean());
               }
            } catch (Exception var7) {
            }
         }
      }
   }

   private JsonObject buildPlayerModelJson() {
      JsonObject var1 = new JsonObject();
      CustomModelType var2 = CustomModelManager.getSelectedModel();
      var1.addProperty("gltf", var2 == null ? "default" : var2.id());
      return var1;
   }

   private void handleAction(JsonObject obj) {
      if (obj != null) {
         if (obj.has("gltf")) {
            CustomModelManager.setSelectedModel(CustomModelType.fromId(obj.get("gltf").getAsString()));
         }
      }
   }

   private void handleCustomTheme(JsonObject customTheme) {
      if (customTheme.has("animated")) {
         Lumen.INSTANCE.themeStorage.setCustomAnimated(customTheme.get("animated").getAsBoolean());
      }

      if (customTheme.has("primary")) {
         Lumen.INSTANCE.themeStorage.setCustomPrimaryColor(customTheme.get("primary").getAsInt());
      }

      if (customTheme.has("secondary")) {
         Lumen.INSTANCE.themeStorage.setCustomSecondaryColor(customTheme.get("secondary").getAsInt());
      }
   }

   public boolean deleteConfig(String config) {
      if (config != null && !config.isBlank() && !config.equals(this.currentConfig)) {
         boolean var2 = false;
         File var3 = this.getConfig(config);
         if (var3.exists() && var3.delete()) {
            var2 = true;
         }

         for (String var7 : textArray) {
            File var8 = new File(Lumen.INSTANCE.configsDir, config + var7);
            if (var8.exists() && var8.delete()) {
               var2 = true;
            }
         }

         return var2;
      } else {
         return false;
      }
   }

   public Set<String> getAvailableConfigs() {
      LinkedHashSet var1 = new LinkedHashSet();
      File[] var2 = Lumen.INSTANCE.configsDir.listFiles();
      if (var2 == null) {
         return var1;
      }

      for (File var6 : var2) {
         String var7 = var6.getName();
         if (var7.toLowerCase().endsWith(".win")) {
            var1.add(var7.substring(0, var7.length() - ".win".length()));
         } else {
            for (String var11 : textArray) {
               if (var7.toLowerCase().endsWith(var11)) {
                  var1.add(var7.substring(0, var7.length() - var11.length()));
                  break;
               }
            }
         }
      }

      return var1;
   }

   private File getConfig(String config) {
      return new File(Lumen.INSTANCE.configsDir, config + ".win");
   }

   private File getConfig2(String config) {
      File var2 = this.getConfig(config);
      if (var2.exists()) {
         return var2;
      }

      for (String var6 : textArray) {
         File var7 = new File(Lumen.INSTANCE.configsDir, config + var6);
         if (var7.exists()) {
            return var7;
         }
      }

      return var2;
   }

   private File createFile() {
      return new File(Lumen.INSTANCE.globalsDir, "globals.win");
   }

   private File getFile() {
      File var1 = this.createFile();
      if (var1.exists()) {
         return var1;
      }

      for (String var5 : textArray) {
         File var6 = new File(Lumen.INSTANCE.globalsDir, "globals" + var5);
         if (var6.exists()) {
            return var6;
         }
      }

      return var1;
   }
}
