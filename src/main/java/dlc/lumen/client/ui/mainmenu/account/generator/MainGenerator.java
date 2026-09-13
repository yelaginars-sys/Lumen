package dlc.lumen.client.ui.mainmenu.account.generator;

import java.util.List;
import java.util.Random;

public final class MainGenerator {
   private static final Random RANDOM = new Random();
   private static final List<String> STRINGS = List.of(
      "utonem_",
      "nfkvi",
      "avamiss",
      "fozix1337",
      "fanatik11",
      "kare_towerz",
      "Bosikomoe",
      "abs0lutikov",
      "energy_noboost",
      "elenmay",
      "macc",
      "javlin",
      "zxcrep",
      "Melisahvh",
      "ridiska1234",
      "GlimmerStar",
      "BillyBoss",
      "Kelss",
      "quix1e",
      "Reaqwem",
      "W1lddFlame",
      "Tyngar",
      "SanyaHF",
      "Woltreks",
      "AsikK1",
      "Emill1999",
      "NoMercyEzz",
      "Starusty"
   );
   private static final List<String> STRINGS2 = List.of("Nova", "Pixel", "Lime", "Quartz", "Echo", "Velvet", "Aero", "Cinder");
   private static final List<String> STRINGS3 = List.of("Fox", "Bloom", "Wave", "Mint", "Leaf", "Core", "Shift", "Star");

   private MainGenerator() {
   }

   public static String generate() {
      String var0;
      if (RANDOM.nextBoolean()) {
         var0 = STRINGS.get(RANDOM.nextInt(STRINGS.size()));
      } else {
         var0 = STRINGS2.get(RANDOM.nextInt(STRINGS2.size())) + STRINGS3.get(RANDOM.nextInt(STRINGS3.size()));
      }

      if (RANDOM.nextBoolean()) {
         var0 = var0 + RANDOM.nextInt(10, 999);
      }

      return var0.length() > 16 ? var0.substring(0, 16) : var0;
   }
}
