package cn.anecansaitin.freecameraapi.client.listener;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.common.GlobalCameraTrack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class RenderGlobalTrack {
    public static boolean render;
    public static String trackName;
    public static GlobalCameraTrack track;

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        PoseStack.Pose last = poseStack.last();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.LINES);
        buffer.addVertex(last, 1, 1, 1).setColor(0xffff0000).setNormal(last, 0, 1, 0);
        buffer.addVertex(last, 1, 3, 1).setColor(0xffff0000).setNormal(last, 0, 1, 0);
        bufferSource.endBatch(RenderType.LINES);
        poseStack.popPose();
    }
}
