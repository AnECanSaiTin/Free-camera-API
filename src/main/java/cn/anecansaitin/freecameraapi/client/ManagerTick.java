package cn.anecansaitin.freecameraapi.client;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.common.ModifierManager;
import cn.anecansaitin.freecameraapi.common.ModifierRegistry;
import net.minecraft.client.Camera;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class ManagerTick {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tick(ViewportEvent.ComputeFov event) {
        Camera camera = event.getCamera();
        camera.setFov(event.getFOV());
        ModifierRegistry.INSTANCE.updateController();
        ModifierManager.INSTANCE.modify();
        event.setFOV(camera.getFov());
    }
}
