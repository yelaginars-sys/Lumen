package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;
import lombok.Generated;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class EventRender extends Event {
   public static class Default extends Event {
      private final DrawContext context;
      private final float partialTicks;

      @Generated
      public DrawContext getContext() {
         return this.context;
      }

      @Generated
      public float getPartialTicks() {
         return this.partialTicks;
      }

      @Generated
      public Default(DrawContext context, float partialTicks) {
         this.context = context;
         this.partialTicks = partialTicks;
      }
   }

   public static class Game extends Event {
      private final WorldRenderer context;
      private final MatrixStack partialTicks;
      private final Matrix4f projectionMatrix;
      private final Camera camera;
      private final float partialTicks2;
      private final long finishTimeNano;

      @Generated
      public WorldRenderer getContext() {
         return this.context;
      }

      @Generated
      public MatrixStack getMatrix() {
         return this.partialTicks;
      }

      @Generated
      public Matrix4f getProjectionMatrix() {
         return this.projectionMatrix;
      }

      @Generated
      public Camera getCamera() {
         return this.camera;
      }

      @Generated
      public float getPartialTicks() {
         return this.partialTicks2;
      }

      @Generated
      public long getFinishTimeNano() {
         return this.finishTimeNano;
      }

      @Generated
      public Game(WorldRenderer context, MatrixStack matrix, Matrix4f projectionMatrix, Camera camera, float partialTicks, long finishTimeNano) {
         this.context = context;
         this.partialTicks = matrix;
         this.projectionMatrix = projectionMatrix;
         this.camera = camera;
         this.partialTicks2 = partialTicks;
         this.finishTimeNano = finishTimeNano;
      }
   }

   public static class World extends Event {
      private final Window context;
      private final float partialTicks;
      private final Matrix4f projectionMatrix;
      private final MatrixStack camera;

      @Generated
      public Window getScaledResolution() {
         return this.context;
      }

      @Generated
      public float getPartialTicks() {
         return this.partialTicks;
      }

      @Generated
      public Matrix4f getMatrix() {
         return this.projectionMatrix;
      }

      @Generated
      public MatrixStack getMatrixStack() {
         return this.camera;
      }

      @Generated
      public World(Window scaledResolution, float partialTicks, Matrix4f matrix, MatrixStack matrixStack) {
         this.context = scaledResolution;
         this.partialTicks = partialTicks;
         this.projectionMatrix = matrix;
         this.camera = matrixStack;
      }
   }
}
