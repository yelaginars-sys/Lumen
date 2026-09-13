package dlc.lumen.client.render.figura;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

public final class BbModelThumbnail {
   private static final double LEVEL = Math.toRadians(-30.0);
   private static final double LEVEL2 = Math.toRadians(22.0);
   private static final double[] LEVEL3 = readStringOrEmpty13(new double[]{-0.35, 0.75, 0.55});
   private static final double LEVEL4 = 0.42;

   private BbModelThumbnail() {
   }

   public static int[] renderArgb(Path avatarDir, int size) {
      try {
         ArrayList<BbModelThumbnail.Tri> var2 = new ArrayList<>();
         ArrayList<Path> var3 = new ArrayList<>();

         try (Stream<Path> var4 = Files.walk(avatarDir)) {
            var4.filter(p -> p.getFileName() != null && p.getFileName().toString().toLowerCase().endsWith(".bbmodel")).forEach(var3::add);
         }

         for (Path var5 : var3) {
            try {
               readStringOrEmpty2(var5, var2);
            } catch (Throwable var8) {
            }
         }

         if (var2.isEmpty()) {
            return null;
         }

         int[] var12 = readStringOrEmpty10(var2, size);
         return readStringOrEmpty(var12, size) ? var12 : null;
      } catch (Throwable var10) {
         return null;
      }
   }

   private static boolean readStringOrEmpty(int[] argb, int size) {
      int var2 = size * size;
      boolean[] var3 = new boolean[var2];
      int var4 = 0;

      for (int var5 = 0; var5 < var2; var5++) {
         if ((argb[var5] >>> 24 & 0xFF) > 16) {
            var3[var5] = true;
            var4++;
         }
      }

      if (var4 == 0) {
         return false;
      }

      if ((double)var4 / var2 < 0.06) {
         return false;
      }

      boolean[] var14 = new boolean[var2];
      int[] var6 = new int[var2];
      int var7 = 0;

      for (int var8 = 0; var8 < var2; var8++) {
         if (var3[var8] && !var14[var8]) {
            int var9 = 0;
            int var10 = 0;
            var6[var9++] = var8;
            var14[var8] = true;

            while (var9 > 0) {
               int var11 = var6[--var9];
               var10++;
               int var12 = var11 % size;
               int var13 = var11 / size;
               if (var12 > 0 && var3[var11 - 1] && !var14[var11 - 1]) {
                  var14[var11 - 1] = true;
                  var6[var9++] = var11 - 1;
               }

               if (var12 < size - 1 && var3[var11 + 1] && !var14[var11 + 1]) {
                  var14[var11 + 1] = true;
                  var6[var9++] = var11 + 1;
               }

               if (var13 > 0 && var3[var11 - size] && !var14[var11 - size]) {
                  var14[var11 - size] = true;
                  var6[var9++] = var11 - size;
               }

               if (var13 < size - 1 && var3[var11 + size] && !var14[var11 + size]) {
                  var14[var11 + size] = true;
                  var6[var9++] = var11 + size;
               }
            }

            if (var10 > var7) {
               var7 = var10;
            }
         }
      }

      return (double)var7 / var4 >= 0.6;
   }

   private static void readStringOrEmpty2(Path bbmodel, List<BbModelThumbnail.Tri> out) throws Exception {
      String var2 = Files.readString(bbmodel);
      JsonObject var3 = JsonParser.parseString(var2).getAsJsonObject();
      HashMap var4 = new HashMap();
      if (var3.has("textures") && var3.get("textures").isJsonArray()) {
         JsonArray var5 = var3.getAsJsonArray("textures");

         for (int var6 = 0; var6 < var5.size(); var6++) {
            BufferedImage var7 = readStringOrEmpty6(var5.get(var6).getAsJsonObject(), bbmodel.getParent());
            if (var7 != null) {
               var4.put(var6, var7);
            }
         }
      }

      BufferedImage var11 = var4.isEmpty() ? null : (BufferedImage)var4.values().iterator().next();
      HashMap<String, JsonObject> var12 = new HashMap<>();
      if (var3.has("elements") && var3.get("elements").isJsonArray()) {
         for (JsonElement var8 : var3.getAsJsonArray("elements")) {
            if (var8.isJsonObject() && var8.getAsJsonObject().has("uuid")) {
               var12.put(var8.getAsJsonObject().get("uuid").getAsString(), var8.getAsJsonObject());
            }
         }
      }

      HashSet<String> var14 = new HashSet<>();
      if (var3.has("outliner") && var3.get("outliner").isJsonArray()) {
         ArrayList<double[][]> var15 = new ArrayList<>();

         for (JsonElement var10 : var3.getAsJsonArray("outliner")) {
            readStringOrEmpty3(var10, var15, var12, var14, var4, var11, out);
         }
      }

      for (Entry<String, JsonObject> var17 : var12.entrySet()) {
         if (!var14.contains(var17.getKey())) {
            readStringOrEmpty5(var17.getValue(), Collections.emptyList(), var4, var11, out);
         }
      }
   }

   private static void readStringOrEmpty3(
      JsonElement node,
      List<double[][]> chain,
      Map<String, JsonObject> byUuid,
      Set<String> done,
      Map<Integer, BufferedImage> textures,
      BufferedImage anyTex,
      List<BbModelThumbnail.Tri> out
   ) {
      if (node.isJsonPrimitive()) {
         JsonObject var10 = (JsonObject)byUuid.get(node.getAsString());
         if (var10 != null && readStringOrEmpty4(var10) && done.add(node.getAsString())) {
            readStringOrEmpty5(var10, chain, textures, anyTex, out);
         }
      } else if (node.isJsonObject()) {
         JsonObject var7 = node.getAsJsonObject();
         if (readStringOrEmpty4(var7)) {
            chain.add(new double[][]{readStringOrEmpty12(var7, "origin", 0.0, 0.0, 0.0), readStringOrEmpty12(var7, "rotation", 0.0, 0.0, 0.0)});
            if (var7.has("children") && var7.get("children").isJsonArray()) {
               for (JsonElement var9 : var7.getAsJsonArray("children")) {
                  readStringOrEmpty3(var9, chain, byUuid, done, textures, anyTex, out);
               }
            }

            chain.remove(chain.size() - 1);
         }
      }
   }

   private static boolean readStringOrEmpty4(JsonObject o) {
      return !o.has("visibility") || o.get("visibility").isJsonNull() || o.get("visibility").getAsBoolean();
   }

   private static void readStringOrEmpty5(
      JsonObject e, List<double[][]> groupChain, Map<Integer, BufferedImage> textures, BufferedImage anyTex, List<BbModelThumbnail.Tri> out
   ) {
      ArrayList var5 = new ArrayList();
      var5.add(new double[][]{readStringOrEmpty12(e, "origin", 0.0, 0.0, 0.0), readStringOrEmpty12(e, "rotation", 0.0, 0.0, 0.0)});

      for (int var6 = groupChain.size() - 1; var6 >= 0; var6--) {
         var5.add((double[][])groupChain.get(var6));
      }

      if (e.has("vertices")) {
         readStringOrEmpty7(e, textures, anyTex, var5, out);
      } else if (e.has("from") && e.has("to")) {
         readStringOrEmpty8(e, textures, anyTex, var5, out);
      }
   }

   private static BufferedImage readStringOrEmpty6(JsonObject t, Path dir) {
      try {
         if (t.has("source") && !t.get("source").isJsonNull()) {
            String var2 = t.get("source").getAsString();
            if (var2.startsWith("data:")) {
               int var3 = var2.indexOf(44);
               if (var3 >= 0) {
                  byte[] var13 = Base64.getDecoder().decode(var2.substring(var3 + 1).trim());
                  return ImageIO.read(new ByteArrayInputStream(var13));
               }
            }
         }

         for (String var5 : new String[]{"relative_path", "name"}) {
            if (t.has(var5) && !t.get(var5).isJsonNull()) {
               String var6 = t.get(var5).getAsString().replace('\\', '/');
               Path var7 = dir.resolve(var6).normalize();
               if (Files.isRegularFile(var7)) {
                  return ImageIO.read(var7.toFile());
               }

               String var8 = var6.contains("/") ? var6.substring(var6.lastIndexOf(47) + 1) : var6;
               Path var9 = dir.resolve(var8);
               if (Files.isRegularFile(var9)) {
                  return ImageIO.read(var9.toFile());
               }
            }
         }
      } catch (Throwable var10) {
      }

      return null;
   }

   private static void readStringOrEmpty7(
      JsonObject e, Map<Integer, BufferedImage> textures, BufferedImage anyTex, List<double[][]> ops, List<BbModelThumbnail.Tri> out
   ) {
      HashMap var5 = new HashMap();

      for (Entry var7 : e.getAsJsonObject("vertices").entrySet()) {
         JsonArray var8 = ((JsonElement)var7.getValue()).getAsJsonArray();
         double[] var9 = new double[]{var8.get(0).getAsDouble(), var8.get(1).getAsDouble(), var8.get(2).getAsDouble()};
         var5.put((String)var7.getKey(), readStringOrEmpty11(var9, ops));
      }

      if (e.has("faces")) {
         for (Entry var27 : e.getAsJsonObject("faces").entrySet()) {
            JsonObject var28 = ((JsonElement)var27.getValue()).getAsJsonObject();
            if (var28.has("vertices")) {
               JsonArray var29 = var28.getAsJsonArray("vertices");
               BufferedImage var10 = readStringOrEmpty9(var28, textures, anyTex);
               double var11 = var10 != null ? var10.getWidth() : 16.0;
               double var13 = var10 != null ? var10.getHeight() : 16.0;
               JsonObject var15 = var28.has("uv") ? var28.getAsJsonObject("uv") : null;
               int var16 = var29.size();
               String[] var17 = new String[var16];
               double[][] var18 = new double[var16][];
               double[][] var19 = new double[var16][];

               for (int var20 = 0; var20 < var16; var20++) {
                  var17[var20] = var29.get(var20).getAsString();
                  var18[var20] = (double[])var5.get(var17[var20]);
                  double var21 = 0.0;
                  double var23 = 0.0;
                  if (var15 != null && var15.has(var17[var20])) {
                     JsonArray var25 = var15.getAsJsonArray(var17[var20]);
                     var21 = var25.get(0).getAsDouble() / var11;
                     var23 = var25.get(1).getAsDouble() / var13;
                  }

                  var19[var20] = new double[]{var21, var23};
               }

               for (int var30 = 1; var30 + 1 < var16; var30++) {
                  if (var18[0] != null && var18[var30] != null && var18[var30 + 1] != null) {
                     out.add(new BbModelThumbnail.Tri(var18[0], var18[var30], var18[var30 + 1], var19[0], var19[var30], var19[var30 + 1], var10));
                  }
               }
            }
         }
      }
   }

   private static void readStringOrEmpty8(
      JsonObject e, Map<Integer, BufferedImage> textures, BufferedImage anyTex, List<double[][]> ops, List<BbModelThumbnail.Tri> out
   ) {
      double[] var5 = readStringOrEmpty12(e, "from", 0.0, 0.0, 0.0);
      double[] var6 = readStringOrEmpty12(e, "to", 0.0, 0.0, 0.0);
      double var7 = var5[0];
      double var9 = var5[1];
      double var11 = var5[2];
      double var13 = var6[0];
      double var15 = var6[1];
      double var17 = var6[2];
      double[] var19 = readStringOrEmpty11(new double[]{var7, var9, var11}, ops);
      double[] var20 = readStringOrEmpty11(new double[]{var7, var9, var17}, ops);
      double[] var21 = readStringOrEmpty11(new double[]{var7, var15, var11}, ops);
      double[] var22 = readStringOrEmpty11(new double[]{var7, var15, var17}, ops);
      double[] var23 = readStringOrEmpty11(new double[]{var13, var9, var11}, ops);
      double[] var24 = readStringOrEmpty11(new double[]{var13, var9, var17}, ops);
      double[] var25 = readStringOrEmpty11(new double[]{var13, var15, var11}, ops);
      double[] var26 = readStringOrEmpty11(new double[]{var13, var15, var17}, ops);
      if (e.has("faces")) {
         JsonObject var27 = e.getAsJsonObject("faces");
         readIntOrZero(var27, "north", var25, var21, var19, var23, textures, anyTex, out);
         readIntOrZero(var27, "south", var22, var26, var24, var20, textures, anyTex, out);
         readIntOrZero(var27, "west", var22, var21, var19, var20, textures, anyTex, out);
         readIntOrZero(var27, "east", var25, var26, var24, var23, textures, anyTex, out);
         readIntOrZero(var27, "up", var21, var25, var26, var22, textures, anyTex, out);
         readIntOrZero(var27, "down", var20, var24, var23, var19, textures, anyTex, out);
      }
   }

   private static void readIntOrZero(
      JsonObject faces,
      String name,
      double[] tl,
      double[] tr,
      double[] br,
      double[] bl,
      Map<Integer, BufferedImage> textures,
      BufferedImage anyTex,
      List<BbModelThumbnail.Tri> out
   ) {
      if (faces.has(name) && faces.get(name).isJsonObject()) {
         JsonObject var9 = faces.getAsJsonObject(name);
         BufferedImage var10 = readStringOrEmpty9(var9, textures, anyTex);
         double var11 = var10 != null ? var10.getWidth() : 16.0;
         double var13 = var10 != null ? var10.getHeight() : 16.0;
         double var15 = 0.0;
         double var17 = 0.0;
         double var19 = 1.0;
         double var21 = 1.0;
         if (var9.has("uv") && var9.get("uv").isJsonArray()) {
            JsonArray var23 = var9.getAsJsonArray("uv");
            var15 = var23.get(0).getAsDouble() / var11;
            var17 = var23.get(1).getAsDouble() / var13;
            var19 = var23.get(2).getAsDouble() / var11;
            var21 = var23.get(3).getAsDouble() / var13;
         }

         double[] var27 = new double[]{var15, var17};
         double[] var24 = new double[]{var19, var17};
         double[] var25 = new double[]{var19, var21};
         double[] var26 = new double[]{var15, var21};
         out.add(new BbModelThumbnail.Tri(tl, tr, br, var27, var24, var25, var10));
         out.add(new BbModelThumbnail.Tri(tl, br, bl, var27, var25, var26, var10));
      }
   }

   private static BufferedImage readStringOrEmpty9(JsonObject f, Map<Integer, BufferedImage> textures, BufferedImage anyTex) {
      if (f.has("texture") && f.get("texture").isJsonPrimitive()) {
         try {
            BufferedImage var3 = (BufferedImage)textures.get(f.get("texture").getAsInt());
            if (var3 != null) {
               return var3;
            }
         } catch (Throwable var4) {
         }
      }

      return anyTex;
   }

   private static int[] readStringOrEmpty10(List<BbModelThumbnail.Tri> tris, int size) {
      double var2 = 1.0E9;
      double var4 = 1.0E9;
      double var6 = 1.0E9;
      double var8 = -1.0E9;
      double var10 = -1.0E9;
      double var12 = -1.0E9;

      for (BbModelThumbnail.Tri var15 : tris) {
         for (double[] var19 : new double[][]{var15.a, var15.b, var15.c}) {
            double[] var20 = helper2(var19);
            var2 = Math.min(var2, var20[0]);
            var8 = Math.max(var8, var20[0]);
            var4 = Math.min(var4, var20[1]);
            var10 = Math.max(var10, var20[1]);
            var6 = Math.min(var6, var20[2]);
            var12 = Math.max(var12, var20[2]);
         }
      }

      double var50 = Math.max(1.0E-6, var8 - var2);
      double var51 = Math.max(1.0E-6, var10 - var4);
      double var52 = 0.08 * size;
      double var53 = (size - 2.0 * var52) / Math.max(var50, var51);
      double var22 = (var2 + var8) / 2.0;
      double var24 = (var4 + var10) / 2.0;
      double var26 = size / 2.0;
      int[] var28 = new int[size * size];
      double[] var29 = new double[size * size];
      Arrays.fill(var29, Double.POSITIVE_INFINITY);

      for (BbModelThumbnail.Tri var31 : tris) {
         double[] var32 = helper2(var31.a);
         double[] var33 = helper2(var31.b);
         double[] var34 = helper2(var31.c);
         double var35 = var26 + (var32[0] - var22) * var53;
         double var37 = var26 - (var32[1] - var24) * var53;
         double var39 = var26 + (var33[0] - var22) * var53;
         double var41 = var26 - (var33[1] - var24) * var53;
         double var43 = var26 + (var34[0] - var22) * var53;
         double var45 = var26 - (var34[1] - var24) * var53;
         double[] var47 = readStringOrEmpty13(helper5(helper4(var33, var32), helper4(var34, var32)));
         double var48 = 0.42 + 0.5800000000000001 * Math.abs(helper6(var47, LEVEL3));
         helper(
            var28, var29, size, var35, var37, var32[2], var31.ua, var39, var41, var33[2], var31.ub, var43, var45, var34[2], var31.uc, var31.tex, var48
         );
      }

      return var28;
   }

   private static void helper(
      int[] argb,
      double[] zbuf,
      int size,
      double ax,
      double ay,
      double az,
      double[] ua,
      double bx,
      double by,
      double bz,
      double[] ub,
      double cx,
      double cy,
      double cz,
      double[] uc,
      BufferedImage tex,
      double shade
   ) {
      int var27 = (int)Math.floor(Math.min(ax, Math.min(bx, cx)));
      int var28 = (int)Math.ceil(Math.max(ax, Math.max(bx, cx)));
      int var29 = (int)Math.floor(Math.min(ay, Math.min(by, cy)));
      int var30 = (int)Math.ceil(Math.max(ay, Math.max(by, cy)));
      var27 = Math.max(0, var27);
      var29 = Math.max(0, var29);
      var28 = Math.min(size - 1, var28);
      var30 = Math.min(size - 1, var30);
      double var31 = (bx - ax) * (cy - ay) - (by - ay) * (cx - ax);
      if (!(Math.abs(var31) < 1.0E-9)) {
         for (int var33 = var29; var33 <= var30; var33++) {
            for (int var34 = var27; var34 <= var28; var34++) {
               double var35 = var34 + 0.5;
               double var37 = var33 + 0.5;
               double var39 = ((bx - var35) * (cy - var37) - (by - var37) * (cx - var35)) / var31;
               double var41 = ((cx - var35) * (ay - var37) - (cy - var37) * (ax - var35)) / var31;
               double var43 = 1.0 - var39 - var41;
               if (!(var39 < 0.0) && !(var41 < 0.0) && !(var43 < 0.0)) {
                  double var45 = var39 * az + var41 * bz + var43 * cz;
                  int var47 = var33 * size + var34;
                  if (!(var45 >= zbuf[var47])) {
                     int var49 = 255;
                     int var48;
                     if (tex != null) {
                        double var50 = var39 * ua[0] + var41 * ub[0] + var43 * uc[0];
                        double var52 = var39 * ua[1] + var41 * ub[1] + var43 * uc[1];
                        int var54 = helper7((int)Math.floor(var50 * tex.getWidth()), 0, tex.getWidth() - 1);
                        int var55 = helper7((int)Math.floor(var52 * tex.getHeight()), 0, tex.getHeight() - 1);
                        int var56 = tex.getRGB(var54, var55);
                        var49 = var56 >>> 24 & 0xFF;
                        if (var49 < 16) {
                           continue;
                        }

                        var48 = var56 & 16777215;
                     } else {
                        var48 = 11579576;
                     }

                     int var61 = helper7((int)((var48 >> 16 & 0xFF) * shade), 0, 255);
                     int var51 = helper7((int)((var48 >> 8 & 0xFF) * shade), 0, 255);
                     int var62 = helper7((int)((var48 & 0xFF) * shade), 0, 255);
                     zbuf[var47] = var45;
                     argb[var47] = var49 << 24 | var61 << 16 | var51 << 8 | var62;
                  }
               }
            }
         }
      }
   }

   private static double[] helper2(double[] p) {
      double var1 = Math.cos(LEVEL);
      double var3 = Math.sin(LEVEL);
      double var5 = p[0] * var1 + p[2] * var3;
      double var7 = -p[0] * var3 + p[2] * var1;
      double var9 = p[1];
      double var11 = Math.cos(LEVEL2);
      double var13 = Math.sin(LEVEL2);
      double var15 = var9 * var11 - var7 * var13;
      double var17 = var9 * var13 + var7 * var11;
      return new double[]{var5, var15, var17};
   }

   private static double[] readStringOrEmpty11(double[] p, List<double[][]> ops) {
      double[] var2 = p;

      for (double[][] var4 : ops) {
         var2 = helper3(var2, var4[0], var4[1]);
      }

      return var2;
   }

   private static double[] helper3(double[] p, double[] origin, double[] rot) {
      if (rot[0] == 0.0 && rot[1] == 0.0 && rot[2] == 0.0) {
         return p;
      }

      double var3 = p[0] - origin[0];
      double var5 = p[1] - origin[1];
      double var7 = p[2] - origin[2];
      double var9 = Math.toRadians(rot[0]);
      double var11 = Math.toRadians(rot[1]);
      double var13 = Math.toRadians(rot[2]);
      double var15 = Math.cos(var9);
      double var17 = Math.sin(var9);
      double var19 = var5 * var15 - var7 * var17;
      double var21 = var5 * var17 + var7 * var15;
      var5 = var19;
      var7 = var21;
      double var23 = Math.cos(var11);
      double var25 = Math.sin(var11);
      double var27 = var3 * var23 + var7 * var25;
      double var29 = -var3 * var25 + var7 * var23;
      var3 = var27;
      var7 = var29;
      double var31 = Math.cos(var13);
      double var33 = Math.sin(var13);
      double var35 = var3 * var31 - var5 * var33;
      double var37 = var3 * var33 + var5 * var31;
      var3 = var35;
      var5 = var37;
      return new double[]{var3 + origin[0], var5 + origin[1], var7 + origin[2]};
   }

   private static double[] readStringOrEmpty12(JsonObject o, String key, double dx, double dy, double dz) {
      if (o.has(key) && o.get(key).isJsonArray()) {
         JsonArray var8 = o.getAsJsonArray(key);
         if (var8.size() >= 3) {
            return new double[]{var8.get(0).getAsDouble(), var8.get(1).getAsDouble(), var8.get(2).getAsDouble()};
         }
      }

      return new double[]{dx, dy, dz};
   }

   private static double[] helper4(double[] a, double[] b) {
      return new double[]{a[0] - b[0], a[1] - b[1], a[2] - b[2]};
   }

   private static double[] helper5(double[] a, double[] b) {
      return new double[]{a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]};
   }

   private static double helper6(double[] a, double[] b) {
      return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
   }

   private static double[] readStringOrEmpty13(double[] a) {
      double var1 = Math.sqrt(helper6(a, a));
      return var1 < 1.0E-9 ? new double[]{0.0, 0.0, 1.0} : new double[]{a[0] / var1, a[1] / var1, a[2] / var1};
   }

   private static int helper7(int v, int lo, int hi) {
      return v < lo ? lo : (v > hi ? hi : v);
   }

   public static void main(String[] args) throws Exception {
      int var1 = args.length >= 3 ? Integer.parseInt(args[2]) : 128;
      int[] var2 = renderArgb(Path.of(args[0]), var1);
      if (var2 == null) {
         System.out.println("NULL (нечего рендерить): " + args[0]);
      } else {
         BufferedImage var3 = new BufferedImage(var1, var1, 2);
         var3.setRGB(0, 0, var1, var1, var2, 0, var1);
         ImageIO.write(var3, "png", Path.of(args[1]).toFile());
         System.out.println("wrote " + args[1]);
      }
   }

   private static final class Tri {
      final double[] a;
      final double[] b;
      final double[] c;
      final double[] ua;
      final double[] ub;
      final double[] uc;
      final BufferedImage tex;

      Tri(double[] a, double[] b, double[] c, double[] ua, double[] ub, double[] uc, BufferedImage tex) {
         this.a = a;
         this.b = b;
         this.c = c;
         this.ua = ua;
         this.ub = ub;
         this.uc = uc;
         this.tex = tex;
      }
   }
}
