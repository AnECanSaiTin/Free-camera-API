package cn.anecansaitin.freecameraapi.client.listener;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.common.animation.CameraPoint;
import cn.anecansaitin.freecameraapi.common.animation.GlobalCameraTrack;
import cn.anecansaitin.freecameraapi.common.animation.PointInterpolationType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Math;
import org.joml.Vector3f;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class RenderGlobalTrack {
    public static boolean render = true;
    public static String trackName;
    public static GlobalCameraTrack track;

    static {
        track = new GlobalCameraTrack();
        track.add(0, new CameraPoint(new Vector3f(1, 56, 3), new Vector3f(), 70, PointInterpolationType.LINEAR));
        track.add(1, new CameraPoint(new Vector3f(3, 56, 5), new Vector3f(), 70, PointInterpolationType.LINEAR));
        track.add(3, new CameraPoint(new Vector3f(7, 56, 8), new Vector3f(), 70, PointInterpolationType.LINEAR));
        track.add(2, new CameraPoint(new Vector3f(5, 56, 0), new Vector3f(), 70, PointInterpolationType.LINEAR));

        track.add(4, new CameraPoint(new Vector3f(1, 58, 3), new Vector3f(), 70, PointInterpolationType.SMOOTH));
        track.add(5, new CameraPoint(new Vector3f(3, 58, 5), new Vector3f(), 70, PointInterpolationType.SMOOTH));
        track.add(6, new CameraPoint(new Vector3f(5, 58, 0), new Vector3f(), 70, PointInterpolationType.SMOOTH));
        track.add(7, new CameraPoint(new Vector3f(7, 58, 8), new Vector3f(), 70, PointInterpolationType.SMOOTH));

        track.add(8, new CameraPoint(new Vector3f(1, 59, 3), new Vector3f(), 70, PointInterpolationType.STEP));
        track.add(9, new CameraPoint(new Vector3f(3, 59, 5), new Vector3f(), 70, PointInterpolationType.STEP));
        track.add(10, new CameraPoint(new Vector3f(5, 59, 0), new Vector3f(), 70, PointInterpolationType.STEP));
        track.add(11, new CameraPoint(new Vector3f(7, 59, 8), new Vector3f(), 70, PointInterpolationType.STEP));

        CameraPoint b1 = new CameraPoint(new Vector3f(1, 60, 3), new Vector3f(), 70, PointInterpolationType.BEZIER);
        CameraPoint b2 = new CameraPoint(new Vector3f(3, 60, 5), new Vector3f(), 70, PointInterpolationType.BEZIER);
        CameraPoint b3 = new CameraPoint(new Vector3f(5, 60, 0), new Vector3f(), 70, PointInterpolationType.BEZIER);
        CameraPoint b4 = new CameraPoint(new Vector3f(7, 60, 8), new Vector3f(), 70, PointInterpolationType.BEZIER);
        track.add(12, b1);
        track.add(13, b2);
        track.add(14, b3);
        track.add(15, b4);
        b1.getLeftBezierControl().add(0, 1, 0);
        b2.getLeftBezierControl().add(0, -1, 0);
        b3.getLeftBezierControl().add(1, 0, 1);
        b4.getLeftBezierControl().add(-1, 1, -1);
    }

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES || !render || track.getCount() < 1) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        PoseStack.Pose last = poseStack.last();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.enableDepthTest();

        // 获取相机位置
        Vector3f cameraPos = event.getCamera().getPosition().toVector3f();
        VertexConsumer buffer;

        //线条
        if (track.getCount() > 2) {
            buffer = bufferSource.getBuffer(RenderType.LINES);

            for (int i = 1, c = track.getCount(); i < c; i++) {
                CameraPoint p1 = track.getPoint(i - 1);
                CameraPoint p2 = track.getPoint(i);
                final Vector3f v1 = new Vector3f(p1.getPosition()).sub(cameraPos);
                final Vector3f v2 = new Vector3f(p2.getPosition()).sub(cameraPos);

                switch (p2.getType()) {
                    case LINEAR -> addLine(buffer, last, v1, v2, 0xffffffff);
                    case SMOOTH -> {
                        Vector3f v0;
                        Vector3f v3;

                        if (i > 1) {
                            CameraPoint p = track.getPoint(i - 2);
                            v0 = new Vector3f(p.getPosition()).sub(cameraPos);
                        } else {
                            v0 = v1;
                        }

                        if (i < c - 1) {
                            CameraPoint p = track.getPoint(i + 1);
                            v3 = new Vector3f(p.getPosition()).sub(cameraPos);
                        } else {
                            v3 = v2;
                        }

                        addSmoothLine(buffer, last, v0, v1, v2, v3, 0xffffffff);
                    }
                    case BEZIER ->
                            addBezierLine(buffer, last, v1, new Vector3f(p1.getRightBezierControl()).sub(cameraPos), new Vector3f(p2.getLeftBezierControl()).sub(cameraPos), v2, 0xffffffff, 0x7f98FB98);
                    case STEP -> addLine(buffer, last, v1, v2, 0xff7f7f7f);
                }
            }

            bufferSource.endBatch(RenderType.LINES);
        }

        //点
        buffer = bufferSource.getBuffer(RenderType.DEBUG_FILLED_BOX);

        for (int i = 0; i < track.getCount(); i++) {
            CameraPoint point = track.getPoint(i);
            addPoint(buffer, last, new Vector3f(point.getPosition()).sub(cameraPos), 0.1f, 0xff000000);

            if (point.getType() == PointInterpolationType.BEZIER && i > 0) {

                addPoint(buffer, last, new Vector3f(track.getPoint(i - 1).getRightBezierControl()).sub(cameraPos), 0.05f, 0x7f98FB98);
                addPoint(buffer, last, new Vector3f(point.getLeftBezierControl()).sub(cameraPos), 0.05f, 0x7f98FB98);
            }
        }

        bufferSource.endBatch(RenderType.DEBUG_FILLED_BOX);

        //-----------------------------
        /*// 假设有这两个点
        Vector3f pos1 = new Vector3f(1, 56, 3).sub(cameraPos);
        Vector3f pos2 = new Vector3f(3, 56, 5).sub(cameraPos);
        Vector3f pos3 = new Vector3f(5, 56, 0).sub(cameraPos);
        Vector3f pos4 = new Vector3f(7f, 56, 6f).sub(cameraPos);

        //渲染线条
        buffer = bufferSource.getBuffer(RenderType.LINES);
        addSmoothLine(buffer, last, pos1, pos1, pos2, pos3, 0xffffffff);
        addSmoothLine(buffer, last, pos1, pos2, pos3, pos4, 0xffffffff);
        addSmoothLine(buffer, last, pos2, pos3, pos4, pos4, 0xffffffff);
        bufferSource.endBatch(RenderType.LINES);

        //渲染点（用小方块表示）
        //纯色实心方块
        Vector3f center = new Vector3f(0, 57f, 0).sub(cameraPos);
        buffer = bufferSource.getBuffer(RenderType.DEBUG_FILLED_BOX);
        addPoint(buffer, last, center, 0xffffffff);
        bufferSource.endBatch(RenderType.DEBUG_FILLED_BOX);

        //三个片
        Vector3f c1 = new Vector3f(0, 57f, 6).sub(cameraPos);
        buffer = bufferSource.getBuffer(RenderType.DEBUG_QUADS);
        addPoint(buffer, last, c1);
        bufferSource.endBatch(RenderType.DEBUG_QUADS);*/

        RenderSystem.disableDepthTest();
    }

    private static void addLine(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pos1, Vector3f pos2, int color) {
        Vector3f normalize = new Vector3f(pos2).sub(pos1).normalize();
        buffer.addVertex(pose, pos1).setColor(color).setNormal(pose, normalize);
        buffer.addVertex(pose, pos2).setColor(color).setNormal(pose, normalize);
    }

    private static void addSmoothLine(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pre, Vector3f p1, Vector3f p2, Vector3f after, int color) {
        Vector3f pos1 = new Vector3f(p1);
        Vector3f pos2 = new Vector3f();

        for (float i = 1; i <= 20; i++) {
            float f = 0.05f * i;
            catmullRom(f, pre, p1, p2, after, pos2);
            addLine(buffer, pose, pos1, pos2, color);
            pos1.set(pos2);
        }

        addLine(buffer, pose, pos2, p2, color);
    }

    private static void addBezierLine(VertexConsumer buffer, PoseStack.Pose pose, Vector3f p1, Vector3f c1, Vector3f c2, Vector3f p2, int color, int controlColor) {
        Vector3f pos1 = new Vector3f(p1);
        Vector3f pos2 = new Vector3f();

        addLine(buffer, pose, p1, c1, controlColor);
        addLine(buffer, pose, p2, c2, controlColor);

        for (int i = 0; i < 20; i++) {
            float t = 0.05f * i;
            bezier(t, p1, c1, c2, p2, pos2);
            addLine(buffer, pose, pos1, pos2, color);
            pos1.set(pos2);
        }

        addLine(buffer, pose, pos2, p2, color);
    }

    private static void addPoint(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pos, float size, int color) {
        Vector3f vec = new Vector3f();
        /*Vector3f p1 = vec.set(pos).add(-half, -half, half);//后右下
        Vector3f p2 = vec.set(pos).add(half, -half, half);//后左下
        Vector3f p3 = vec.set(pos).add(-half, half, half);//后右上
        Vector3f p4 = vec.set(pos).add(half, half, half);//后左上
        Vector3f p5 = vec.set(pos).add(half, half, -half);//左上
        Vector3f p6 = vec.set(pos).add(half, -half, -half);//左下
        Vector3f p7 = vec.set(pos).add(-half, -half, -half);//右下
        Vector3f p8 = vec.set(pos).add(-half, half, -half);//右上*/

        buffer.addVertex(pose, vec.set(pos).add(-size, size, -size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(-size, size, -size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(size, -size, -size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(-size, -size, -size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(-size, -size, size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(-size, size, -size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(-size, size, size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(size, size, size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(-size, -size, size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(size, -size, size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(size, -size, -size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(size, size, size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(size, size, -size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(-size, size, -size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(size, -size, -size)).setColor(color);
        buffer.addVertex(pose, vec.set(pos).add(size, -size, -size)).setColor(color);
    }

    private static void addPoint(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pos) {
        float half = 0.2f;
        Vector3f vec = new Vector3f();
        buffer.addVertex(pos).setColor(0x77fd3043);
        buffer.addVertex(pose, vec.set(pos).add(0, 0, half)).setColor(0x77fd3043);
        buffer.addVertex(pose, vec.set(pos).add(0, half, half)).setColor(0x77fd3043);
        buffer.addVertex(pose, vec.set(pos).add(0, half, 0)).setColor(0x77fd3043);

        buffer.addVertex(pose, vec.set(pos).add(half, half, 0)).setColor(0x772d5ee8);
        buffer.addVertex(pose, vec.set(pos).add(half, 0, 0)).setColor(0x772d5ee8);
        buffer.addVertex(pose, pos).setColor(0x772d5ee8);
        buffer.addVertex(pose, vec.set(pos).add(0, half, 0)).setColor(0x772d5ee8);

        buffer.addVertex(pose, pos).setColor(0x7726ec45);
        buffer.addVertex(pose, vec.set(pos).add(0, 0, half)).setColor(0x7726ec45);
        buffer.addVertex(pose, vec.set(pos).add(half, 0, half)).setColor(0x7726ec45);
        buffer.addVertex(pose, vec.set(pos).add(half, 0, 0)).setColor(0x7726ec45);
    }

    public static Vector3f catmullRom(float delta, Vector3f pre, Vector3f p1, Vector3f p2, Vector3f after, Vector3f dest) {
        return dest.set(
                Mth.catmullrom(delta, pre.x, p1.x, p2.x, after.x),
                Mth.catmullrom(delta, pre.y, p1.y, p2.y, after.y),
                Mth.catmullrom(delta, pre.z, p1.z, p2.z, after.z)
        );
    }

    public static Vector3f bezier(float t, Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f dest) {
        float oneMinusT = 1.0f - t;

        dest.x = oneMinusT * oneMinusT * oneMinusT * p0.x +
                3 * oneMinusT * oneMinusT * t * p1.x +
                3 * oneMinusT * t * t * p2.x +
                t * t * t * p3.x;

        dest.y = oneMinusT * oneMinusT * oneMinusT * p0.y +
                3 * oneMinusT * oneMinusT * t * p1.y +
                3 * oneMinusT * t * t * p2.y +
                t * t * t * p3.y;

        dest.z = oneMinusT * oneMinusT * oneMinusT * p0.z +
                3 * oneMinusT * oneMinusT * t * p1.z +
                3 * oneMinusT * t * t * p2.z +
                t * t * t * p3.z;

        return dest;
    }
}
