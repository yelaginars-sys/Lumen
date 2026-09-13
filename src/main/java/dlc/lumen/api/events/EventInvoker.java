package dlc.lumen.api.events;

import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.bots.core.BotContext;
import dlc.lumen.client.modules.Module;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;

public class EventInvoker {
   private static final ConcurrentHashMap<Class<?>, Object> listeners = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<Class<? extends Event>, List<EventInvoker.Invocation>> handlersByEvent = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<String, Long> handlerWarnTimestamps = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<String, Long> eventWarnTimestamps = new ConcurrentHashMap<>();
   private static final boolean perfDebugEnabled = Boolean.parseBoolean(System.getProperty("lumen.perf.debug", "false"));
   private static final long slowHandlerThresholdNs = Long.getLong("lumen.perf.handlerMs", 8L) * 1000000L;
   private static final long slowEventThresholdNs = Long.getLong("lumen.perf.eventMs", 18L) * 1000000L;
   private static final long warnCooldownNs = Long.getLong("lumen.perf.cooldownMs", 1000L) * 1000000L;
   private static volatile boolean needsRebuild = true;

   public static void register(Object obj) {
      listeners.putIfAbsent(obj.getClass(), obj);
      needsRebuild = true;
   }

   public static void unregister(Object obj) {
      listeners.remove(obj.getClass());
      needsRebuild = true;
   }

   public static void clean() {
      listeners.clear();
      handlersByEvent.clear();
      needsRebuild = false;
   }

   public static void invoke(Event event) throws IllegalAccessException, InvocationTargetException, InstantiationException {
      if (!BotContext.isBot()) {
         long var1 = perfDebugEnabled ? System.nanoTime() : 0L;
         if (needsRebuild) {
            rebuildCache();
         }

         List<EventInvoker.Invocation> var3 = handlersByEvent.get(event.getClass());
         if (var3 != null && !var3.isEmpty()) {
            for (EventInvoker.Invocation var5 : var3) {
               if (listeners.containsKey(var5.listener().getClass())) {
                  Method var6 = var5.method();
                  long var7 = perfDebugEnabled ? System.nanoTime() : 0L;

                  try {
                     var6.invoke(var5.listener(), event);
                  } catch (InvocationTargetException var17) {
                     handleListenerError(var5, event, var17.getCause() == null ? var17 : var17.getCause());
                  } catch (Throwable var18) {
                     handleListenerError(var5, event, var18);
                  } finally {
                     if (perfDebugEnabled) {
                        long var12 = System.nanoTime() - var7;
                        if (var12 >= slowHandlerThresholdNs) {
                           logSlowHandler(event, var5, var12);
                        }
                     }
                  }
               }
            }

            if (perfDebugEnabled) {
               long var20 = System.nanoTime() - var1;
               if (var20 >= slowEventThresholdNs) {
                  logSlowEvent(event, var20, var3.size());
               }
            }
         }
      }
   }

   public static boolean hasListeners(Class<? extends Event> eventClass) {
      if (needsRebuild) {
         rebuildCache();
      }

      List var1 = handlersByEvent.get(eventClass);
      return var1 != null && !var1.isEmpty();
   }

   private static synchronized void rebuildCache() {
      if (needsRebuild) {
         ConcurrentHashMap<Class<? extends Event>, List<EventInvoker.Invocation>> var0 = new ConcurrentHashMap<>();

         for (Object var2 : listeners.values()) {
            for (Method var6 : var2.getClass().getDeclaredMethods()) {
               if (var6.isAnnotationPresent(EventLink.class)) {
                  Class[] var7 = var6.getParameterTypes();
                  if (var7.length == 1 && Event.class.isAssignableFrom(var7[0])) {
                     Class<? extends Event> var8 = var7[0].asSubclass(Event.class);
                     var6.setAccessible(true);
                     var0.computeIfAbsent(var8, key -> new ArrayList<>())
                        .add(new EventInvoker.Invocation(var2, var6, var6.getAnnotation(EventLink.class).priority()));
                  }
               }
            }
         }

         for (List<EventInvoker.Invocation> var10 : var0.values()) {
            var10.sort((a, b) -> {
               int var2x = Integer.compare(b.priority(), a.priority());
               if (var2x != 0) {
                  return var2x;
               }

               int var3 = a.listener().getClass().getName().compareTo(b.listener().getClass().getName());
               return var3 != 0 ? var3 : a.method().getName().compareTo(b.method().getName());
            });
         }

         handlersByEvent.clear();
         handlersByEvent.putAll(var0);
         needsRebuild = false;
      }
   }

   private static void handleListenerError(EventInvoker.Invocation invocation, Event event, Throwable error) {
      if (error instanceof OutOfMemoryError var8) {
         throw var8;
      } else {
         Object var3 = invocation.listener();
         String var4 = var3.getClass().getSimpleName();
         System.err
            .println("[lumen] " + var4 + "#" + invocation.method().getName() + " упал на " + event.getClass().getSimpleName() + " — обработчик отключён");
         error.printStackTrace();
         if (var3 instanceof Module var5) {
            try {
               var5.setEnabled(false);
            } catch (Throwable var7) {
            }

            unregister(var5);
            MinecraftClient var6 = MinecraftClient.getInstance();
            if (var6 != null && var6.isOnThread()) {
               ChatUtils.sendMessage(
                  "§c"
                     + var5.getName()
                     + " выключен из-за ошибки: "
                     + error.getClass().getSimpleName()
                     + (error.getMessage() == null ? "" : " (" + error.getMessage() + ")")
               );
            }
         }
      }
   }

   private static void logSlowHandler(Event event, EventInvoker.Invocation invocation, long elapsedNanos) {
      String var4 = invocation.listener().getClass().getSimpleName();
      String var5 = invocation.method().getName();
      String var6 = event.getClass().getSimpleName();
      String var7 = "handler:" + var6 + ":" + var4 + "#" + var5;
      if (shouldLogWarning(handlerWarnTimestamps, var7)) {
         System.out.println(String.format(Locale.ROOT, "[PerfDebug] Slow handler: %s -> %s#%s took %.2f ms", var6, var4, var5, elapsedNanos / 1000000.0));
      }
   }

   private static void logSlowEvent(Event event, long elapsedNanos, int invocationCount) {
      String var4 = event.getClass().getSimpleName();
      String var5 = "event:" + var4;
      if (shouldLogWarning(eventWarnTimestamps, var5)) {
         System.out
            .println(String.format(Locale.ROOT, "[PerfDebug] Slow event: %s took %.2f ms for %d handlers", var4, elapsedNanos / 1000000.0, invocationCount));
      }
   }

   private static boolean shouldLogWarning(ConcurrentHashMap<String, Long> warnings, String key) {
      long var2 = System.nanoTime();
      Long var4 = (Long)warnings.get(key);
      if (var4 != null && var2 - var4 < warnCooldownNs) {
         return false;
      }

      warnings.put(key, var2);
      return true;
   }

   private record Invocation(Object listener, Method method, int priority) {

      private Invocation(Object listener, Method method, int priority) {
         this.listener = listener;
         this.method = method;
         this.priority = priority;
      }

      public Object listener() {
         return this.listener;
      }

      public Method method() {
         return this.method;
      }

      public int priority() {
         return this.priority;
      }
   }
}
