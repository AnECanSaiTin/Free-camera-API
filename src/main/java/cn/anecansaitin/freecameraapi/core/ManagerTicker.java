package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.ClientUtil;
import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.api.CameraStates;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class ManagerTicker {
    public static void tick() {
        Camera camera = ClientUtil.camera();
        camera.setFov(getFov(camera));
        ModifierRegistry.INSTANCE.updateController();
        ModifierManager.INSTANCE.modify();
    }

    private static float getFov(Camera camera) {
        float partialTick = camera.getPartialTickTime();
        GameRenderer gameRenderer = ClientUtil.gameRenderer();
        float f = ClientUtil.fov();
        f *= Mth.lerp(partialTick, gameRenderer.oldFovModifier, gameRenderer.fovModifier);


        if (camera.entity() instanceof LivingEntity livingentity && livingentity.isDeadOrDying()) {
            float f1 = Math.min(livingentity.deathTime + partialTick, 20.0F);
            f /= (1.0F - 500.0F / (f1 + 500.0F)) * 2.0F + 1.0F;
        }

        FogType fogtype = camera.getFluidInCamera();
        if (fogtype == FogType.LAVA || fogtype == FogType.WATER) {
            float f2 = ClientUtil.fovEffectScale();
            f *= Mth.lerp(f2, 1.0F, 0.85714287F);
        }

        return f;
    }

    @SubscribeEvent
    public static void computeFov(ViewportEvent.ComputeFov event) {
        if (!ModifierManager.INSTANCE.isStateEnabledAnd(CameraStates.FOV | CameraStates.ENABLE)) {
            return;
        }

        event.setFOV(ClientUtil.camera().getFov());
    }
}
