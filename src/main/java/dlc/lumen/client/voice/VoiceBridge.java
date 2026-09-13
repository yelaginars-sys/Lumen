package dlc.lumen.client.voice;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class VoiceBridge {
   private static final String CLIENT_MANAGER_CLASS = "de.maxhenkel.voicechat.voice.client.ClientManager";
   private static final String PLAYER_STATE_MANAGER_CLASS = "de.maxhenkel.voicechat.voice.client.ClientPlayerStateManager";
   private static final String VOICECHAT_CLIENT_CLASS = "de.maxhenkel.voicechat.VoicechatClient";
   private static final String MICROPHONE_MANAGER_CLASS = "de.maxhenkel.voicechat.voice.client.microphone.MicrophoneManager";
   private static final String SOUND_MANAGER_CLASS = "de.maxhenkel.voicechat.voice.client.SoundManager";
   private static final String ACTIVATION_TYPE_CLASS = "de.maxhenkel.voicechat.voice.client.MicrophoneActivationType";
   private static Boolean presenceCached;
   private static final Map<String, Method> methodCache = new ConcurrentHashMap<>();
   private static final Map<String, Field> fieldCache = new ConcurrentHashMap<>();

   private VoiceBridge() {
   }

   public static boolean isPresent() {
      if (presenceCached == null) {
         try {
            Class.forName("de.maxhenkel.voicechat.voice.client.ClientManager");
            presenceCached = Boolean.TRUE;
         } catch (Throwable var1) {
            presenceCached = Boolean.FALSE;
         }
      }

      return presenceCached;
   }

   public static boolean hasVoice(UUID uuid) {
      if (uuid != null && isPresent()) {
         try {
            Object var1 = getPlayerStateManager();
            if (var1 == null) {
               return false;
            }

            Object var2 = invokeCached(var1, "getState", new Class[]{UUID.class}, uuid);
            return var2 == null ? false : !(invokeCached(var1, "isPlayerDisconnected", new Class[]{UUID.class}, uuid) instanceof Boolean var4 && var4);
         } catch (Throwable var5) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static Object getPlayerStateManager() {
      try {
         Class var0 = Class.forName("de.maxhenkel.voicechat.voice.client.ClientManager");
         Method var1 = var0.getMethod("getPlayerStateManager");
         return var1.invoke(null);
      } catch (Throwable var2) {
         return null;
      }
   }

   private static Object getClientConfig() {
      try {
         Field var0 = resolveFieldCached("de.maxhenkel.voicechat.VoicechatClient", "CLIENT_CONFIG");
         return var0 == null ? null : var0.get(null);
      } catch (Throwable var1) {
         return null;
      }
   }

   private static Object getConfigEntry(String fieldName) {
      try {
         Object var1 = getClientConfig();
         if (var1 == null) {
            return null;
         }

         Field var2 = var1.getClass().getField(fieldName);
         return var2.get(var1);
      } catch (Throwable var3) {
         return null;
      }
   }

   private static Object getConfigValue(String fieldName) {
      try {
         Object var1 = getConfigEntry(fieldName);
         return var1 == null ? null : var1.getClass().getMethod("get").invoke(var1);
      } catch (Throwable var2) {
         return null;
      }
   }

   private static void setConfigValue(String fieldName, Object value) {
      try {
         Object var2 = getConfigEntry(fieldName);
         if (var2 == null || value == null) {
            return;
         }

         Method var3 = findMethodByArgCount(var2.getClass(), "set", 1);
         Method var4 = findMethodByArgCount(var2.getClass(), "save", 0);
         if (var3 == null) {
            return;
         }

         var3.setAccessible(true);
         var3.invoke(var2, value);
         if (var4 != null) {
            var4.setAccessible(true);
            var4.invoke(var2);
         }
      } catch (Throwable var5) {
      }
   }

   public static String getMicrophone() {
      Object var0 = getConfigValue("microphone");
      return var0 == null ? "" : var0.toString();
   }

   public static void setMicrophone(String name) {
      setConfigValue("microphone", name == null ? "" : name);
   }

   public static String getSpeaker() {
      Object var0 = getConfigValue("speaker");
      return var0 == null ? "" : var0.toString();
   }

   public static void setSpeaker(String name) {
      setConfigValue("speaker", name == null ? "" : name);
   }

   public static List<String> getMicrophones() {
      return listStaticStrings("de.maxhenkel.voicechat.voice.client.microphone.MicrophoneManager", "deviceNames");
   }

   public static List<String> getSpeakers() {
      return listStaticStrings("de.maxhenkel.voicechat.voice.client.SoundManager", "getAllSpeakers");
   }

   private static List<String> listStaticStrings(String className, String staticMethod) {
      ArrayList var2 = new ArrayList();
      if (!isPresent()) {
         return var2;
      }

      try {
         Class var3 = Class.forName(className);
         Object var4 = var3.getMethod(staticMethod).invoke(null);
         if (var4 instanceof List) {
            for (Object var7 : (List)var4) {
               if (var7 != null) {
                  var2.add(var7.toString());
               }
            }
         }
      } catch (Throwable var8) {
      }

      return var2;
   }

   public static double getVoiceChatVolume() {
      return toDoubleOrDefault(getConfigValue("voiceChatVolume"), 1.0);
   }

   public static void setVoiceChatVolume(double v) {
      setConfigValue("voiceChatVolume", v);
   }

   public static double getMicrophoneGain() {
      return toDoubleOrDefault(getConfigValue("microphoneGain"), 1.0);
   }

   public static void setMicrophoneGain(double v) {
      setConfigValue("microphoneGain", v);
   }

   public static double getActivationThreshold() {
      return toDoubleOrDefault(getConfigValue("voiceActivationThreshold"), -50.0);
   }

   public static void setActivationThreshold(double v) {
      setConfigValue("voiceActivationThreshold", v);
   }

   public static boolean isDenoiser() {
      return toBooleanOrDefault(getConfigValue("denoiser"), false);
   }

   public static void setDenoiser(boolean v) {
      setConfigValue("denoiser", v);
   }

   public static boolean isMuted() {
      return toBooleanOrDefault(getConfigValue("muted"), false);
   }

   public static boolean isDisabled() {
      return toBooleanOrDefault(getConfigValue("disabled"), false);
   }

   public static void setMuted(boolean v) {
      if (!tryManagerBooleanSetter("setMuted", v)) {
         setConfigValue("muted", v);
      }
   }

   public static void setDisabled(boolean v) {
      if (!tryManagerBooleanSetter("setDisabled", v)) {
         setConfigValue("disabled", v);
      }
   }

   private static boolean tryManagerBooleanSetter(String method, boolean v) {
      try {
         Object var2 = getPlayerStateManager();
         if (var2 == null) {
            return false;
         }

         Method var3 = findMethodByArgCount(var2.getClass(), method, 1);
         if (var3 == null) {
            return false;
         }

         var3.setAccessible(true);
         var3.invoke(var2, v);
         return true;
      } catch (Throwable var4) {
         return false;
      }
   }

   public static boolean isPushToTalk() {
      Object var0 = getConfigValue("microphoneActivationType");
      return var0 != null && "PTT".equals(var0.toString());
   }

   public static void setPushToTalk(boolean ptt) {
      try {
         Class var1 = Class.forName("de.maxhenkel.voicechat.voice.client.MicrophoneActivationType");
         Enum var2 = Enum.valueOf(var1, ptt ? "PTT" : "VOICE");
         setConfigValue("microphoneActivationType", var2);
      } catch (Throwable var3) {
      }
   }

   private static double toDoubleOrDefault(Object o, double def) {
      return o instanceof Number var3 ? var3.doubleValue() : def;
   }

   private static boolean toBooleanOrDefault(Object o, boolean def) {
      return o instanceof Boolean var2 ? var2 : def;
   }

   private static Object invokeCached(Object target, String name, Class<?>[] sig, Object... args) {
      try {
         String var4 = target.getClass().getName() + "#" + name + "/" + sig.length;
         Method var5 = methodCache.get(var4);
         if (var5 == null) {
            var5 = target.getClass().getMethod(name, sig);
            var5.setAccessible(true);
            methodCache.put(var4, var5);
         }

         return var5.invoke(target, args);
      } catch (Throwable var6) {
         return null;
      }
   }

   private static Field resolveFieldCached(String className, String fieldName) {
      String var2 = className + "#" + fieldName;
      Field var3 = fieldCache.get(var2);
      if (var3 != null) {
         return var3;
      }

      try {
         var3 = Class.forName(className).getField(fieldName);
         var3.setAccessible(true);
         fieldCache.put(var2, var3);
         return var3;
      } catch (Throwable var5) {
         return null;
      }
   }

   private static Method findMethodByArgCount(Class<?> c, String name, int argCount) {
      for (Method var6 : c.getMethods()) {
         if (var6.getName().equals(name) && var6.getParameterCount() == argCount) {
            return var6;
         }
      }

      for (Method var10 : c.getDeclaredMethods()) {
         if (var10.getName().equals(name) && var10.getParameterCount() == argCount) {
            return var10;
         }
      }

      return null;
   }
}
