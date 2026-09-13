package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.social.GlobalSocialManager;
import dlc.lumen.client.voice.call.AudioDevices;
import dlc.lumen.client.voice.call.AudioSelfTest;
import dlc.lumen.client.voice.call.CallAudio;
import dlc.lumen.client.voice.call.PortalMedia;
import dlc.lumen.client.voice.call.VoiceCallManager;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import net.minecraft.command.CommandSource;

public class VoiceCommand extends Command {
   public VoiceCommand() {
      super("voice");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.executes(
                                                            context -> {
                                                               handleAction();
                                                               return 1;
                                                            }
                                                         ))
                                                         .then(
                                                            this.literal("invite")
                                                               .then(this.arg("nickname", StringArgumentType.string()).suggests((context, suggestions) -> {
                                                                  String var2 = GlobalSocialManager.INSTANCE.getNickname();

                                                                  for (String var4 : GlobalSocialManager.INSTANCE.getKnownNicknames()) {
                                                                     if (!var4.equalsIgnoreCase(var2)
                                                                        && var4.toLowerCase().startsWith(suggestions.getRemainingLowerCase())) {
                                                                        suggestions.suggest(var4);
                                                                     }
                                                                  }

                                                                  return suggestions.buildFuture();
                                                               }).executes(context -> {
                                                                  VoiceCallManager.INSTANCE.invite(StringArgumentType.getString(context, "nickname"));
                                                                  return 1;
                                                               }))
                                                         ))
                                                      .then(this.literal("accept").executes(context -> {
                                                         VoiceCallManager.INSTANCE.accept();
                                                         return 1;
                                                      })))
                                                   .then(this.literal("reject").executes(context -> {
                                                      VoiceCallManager.INSTANCE.reject();
                                                      return 1;
                                                   })))
                                                .then(this.literal("hangup").executes(context -> {
                                                   VoiceCallManager.INSTANCE.hangup();
                                                   return 1;
                                                })))
                                             .then(this.literal("mute").executes(context -> {
                                                PortalMedia var1 = VoiceCallManager.INSTANCE.getMedia();
                                                var1.setMuted(!var1.isMuted());
                                                ChatUtils.sendMessage(var1.isMuted() ? "Микрофон выключен." : "Микрофон включён.");
                                                return 1;
                                             })))
                                          .then(this.literal("deafen").executes(context -> {
                                             PortalMedia var1 = VoiceCallManager.INSTANCE.getMedia();
                                             var1.setDeafened(!var1.isDeafened());
                                             ChatUtils.sendMessage(var1.isDeafened() ? "Звук собеседника выключен." : "Звук собеседника включён.");
                                             return 1;
                                          })))
                                       .then(this.literal("video").then(this.arg("enabled", BoolArgumentType.bool()).executes(context -> {
                                          boolean var1 = BoolArgumentType.getBool(context, "enabled");
                                          VoiceCallManager.INSTANCE.getMedia().setVideoEnabled(var1);
                                          ChatUtils.sendMessage(var1 ? "Трансляция экрана включена." : "Трансляция экрана выключена.");
                                          return 1;
                                       }))))
                                    .then(this.literal("fps").then(this.arg("value", IntegerArgumentType.integer(5, 60)).executes(context -> {
                                       int var1 = IntegerArgumentType.getInteger(context, "value");
                                       VoiceCallManager.INSTANCE.getMedia().setTargetFps(var1);
                                       ChatUtils.sendMessage("Целевой FPS видео: " + var1);
                                       return 1;
                                    }))))
                                 .then(this.literal("quality").then(this.arg("level", IntegerArgumentType.integer(0, 3)).executes(context -> {
                                    PortalMedia var1 = VoiceCallManager.INSTANCE.getMedia();
                                    var1.setResolutionIndex(IntegerArgumentType.getInteger(context, "level"));
                                    ChatUtils.sendMessage("Разрешение видео: " + var1.getResolutionLabel());
                                    return 1;
                                 }))))
                              .then(this.literal("volume").then(this.arg("percent", IntegerArgumentType.integer(0, 100)).executes(context -> {
                                 int var1 = IntegerArgumentType.getInteger(context, "percent");
                                 VoiceCallManager.INSTANCE.getMedia().setVolume(var1 / 100.0F);
                                 ChatUtils.sendMessage("Громкость собеседника: " + var1 + "%");
                                 return 1;
                              }))))
                           .then(this.literal("unmuteguard").executes(context -> {
                              VoiceCallManager.INSTANCE.getMedia().resetSafety();
                              ChatUtils.sendMessage("Защита слуха сброшена — звук снова пропускается.");
                              return 1;
                           })))
                        .then(this.literal("devices").executes(context -> {
                           handleAction2();
                           return 1;
                        })))
                     .then(this.literal("mic").then(this.arg("index", IntegerArgumentType.integer(0, 63)).executes(context -> {
                        int var1 = IntegerArgumentType.getInteger(context, "index");
                        boolean var2 = AudioDevices.selectInput(CallAudio.format(), var1);
                        ChatUtils.sendMessage(var2 ? "Микрофон: " + AudioDevices.getInputName() : "Нет входа с номером " + var1 + ". Список: .voice devices");
                        return 1;
                     }))))
                  .then(this.literal("speaker").then(this.arg("index", IntegerArgumentType.integer(0, 63)).executes(context -> {
                     int var1 = IntegerArgumentType.getInteger(context, "index");
                     boolean var2 = AudioDevices.selectOutput(CallAudio.format(), var1);
                     ChatUtils.sendMessage(var2 ? "Динамик: " + AudioDevices.getOutputName() : "Нет выхода с номером " + var1 + ". Список: .voice devices");
                     return 1;
                  }))))
               .then(this.literal("test").executes(context -> {
                  AudioSelfTest.run();
                  return 1;
               })))
            .then(this.literal("status").executes(context -> {
               handleAction3();
               return 1;
            })))
         .then(this.literal("help").executes(context -> {
            handleAction();
            return 1;
         }));
   }

   private static void handleAction() {
      ChatUtils.sendMessage(".voice invite <ник> - позвонить другу");
      ChatUtils.sendMessage(".voice accept / reject - ответить на входящий");
      ChatUtils.sendMessage(".voice hangup - завершить звонок");
      ChatUtils.sendMessage("Панель звонка - HUD-элемент \"Звонок\" (кнопки кликабельны в чате)");
      ChatUtils.sendMessage(".voice mute / deafen - микрофон и звук собеседника");
      ChatUtils.sendMessage(".voice video <true|false> - трансляция своего экрана");
      ChatUtils.sendMessage(".voice fps <5-60> / .voice quality <0-3> - качество потока");
      ChatUtils.sendMessage(".voice volume <0-100> - громкость собеседника");
      ChatUtils.sendMessage(".voice devices - список устройств звука");
      ChatUtils.sendMessage(".voice mic <номер> / .voice speaker <номер> - выбрать устройство");
      ChatUtils.sendMessage(".voice test - проверить микрофон и кодек без сети");
      ChatUtils.sendMessage(".voice status - состояние звонка");
   }

   private static void handleAction2() {
      AudioFormat var0 = CallAudio.format();
      List var1 = AudioDevices.listInputs(var0);
      List var2 = AudioDevices.listOutputs(var0);
      ChatUtils.sendMessage("Входы (.voice mic <номер>):");
      if (var1.isEmpty()) {
         ChatUtils.sendMessage("  подходящих входов не найдено");
      }

      for (int var3 = 0; var3 < var1.size(); var3++) {
         ChatUtils.sendMessage("  " + var3 + " - " + (String)var1.get(var3));
      }

      ChatUtils.sendMessage("Выходы (.voice speaker <номер>):");
      if (var2.isEmpty()) {
         ChatUtils.sendMessage("  подходящих выходов не найдено");
      }

      for (int var4 = 0; var4 < var2.size(); var4++) {
         ChatUtils.sendMessage("  " + var4 + " - " + (String)var2.get(var4));
      }

      ChatUtils.sendMessage("Сейчас: микрофон " + AudioDevices.getInputName() + ", динамик " + AudioDevices.getOutputName());
   }

   private static void handleAction3() {
      VoiceCallManager var0 = VoiceCallManager.INSTANCE;
      PortalMedia var1 = var0.getMedia();
      if (!var0.isBusy()) {
         ChatUtils.sendMessage("Активного звонка нет. Позвонить: .voice invite <ник>");
      } else {
         ChatUtils.sendMessage("Состояние: " + resolveState(var0.getState()) + " · " + var0.getPeerNickname());
         if (var0.isActive()) {
            ChatUtils.sendMessage("Длительность: " + var0.getCallSeconds() + " с · " + (var1.isDirectPath() ? "прямое соединение" : "через релей"));
            ChatUtils.sendMessage("Голос: " + var1.getAudioBackendLabel() + (var1.isMuted() ? " (микрофон выключен)" : ""));
            ChatUtils.sendMessage("Пакеты: " + var1.getAudioStats());
            String var2 = var1.getCompatibilityProblem();
            if (!var2.isBlank()) {
               ChatUtils.sendMessage("ПРОБЛЕМА СОВМЕСТИМОСТИ: " + var2);
            }

            ChatUtils.sendMessage(
               "Видео: "
                  + (
                     var1.isVideoEnabled()
                        ? var1.getResolutionLabel() + " · " + var1.getEncodedFps() + " FPS · " + var1.getLastFrameBytes() / 1024 + " КБ/кадр"
                        : "выключено"
                  )
            );
         }

         String var3 = var0.getLastError();
         if (!var3.isBlank()) {
            ChatUtils.sendMessage("Последняя ошибка: " + var3);
         }
      }
   }

   private static String resolveState(VoiceCallManager.State state) {
      return switch (state) {
         case OUTGOING -> "исходящий";
         case INCOMING -> "входящий";
         case ACTIVE -> "разговор";
         case ENDED -> "завершён";
         case IDLE -> "нет звонка";
      };
   }
}
