package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.ClientUtil;
import cn.anecansaitin.freecameraapi.FreeCameraClient;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = FreeCameraClient.MODID, value = Dist.CLIENT)
public class ManagerTicker {
    public static void tick() {
        Camera camera = ClientUtil.camera();
        camera.setFov(getFov(camera));
        ModifierRegistry.INSTANCE.updateController();
        ModifierManager.INSTANCE.modify();
    }

    private static float getFov(Camera camera) {
        double fov;
        float partialTick = camera.getPartialTickTime();
        GameRenderer gameRenderer = ClientUtil.gameRenderer();
        fov = ClientUtil.fov();
        fov *= Mth.lerp(partialTick, gameRenderer.oldFov, gameRenderer.fov);

        if (camera.getEntity() instanceof LivingEntity && ((LivingEntity) camera.getEntity()).isDeadOrDying()) {
            float f = Math.min((float) ((LivingEntity) camera.getEntity()).deathTime + partialTick, 20.0F);
            fov /= (1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F;
        }

        FogType fogtype = camera.getFluidInCamera();
        if (fogtype == FogType.LAVA || fogtype == FogType.WATER) {
            fov *= Mth.lerp(ClientUtil.fovEffectScale(), 1.0, 0.85714287F);
        }

        return (float) fov;
    }

    @SubscribeEvent
    public static void computeFov(ViewportEvent.ComputeFov event) {
        if (!ModifierManager.INSTANCE.isStateEnabledAnd(ModifierStates.FOV | ModifierStates.ENABLE)) {
            return;
        }

        event.setFOV(ClientUtil.camera().getFov());
    }
}
