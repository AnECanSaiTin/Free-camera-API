package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.core.network.CameraPoseUpdate;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.core.SectionPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class ManagerTicker {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void framesTick(ViewportEvent.ComputeFov event) {
        Camera camera = event.getCamera();
        camera.setFov(event.getFOV());
        ModifierRegistry.INSTANCE.updateController();
        ModifierManager.INSTANCE.modify();
        event.setFOV(camera.getFov());
    }

    @SubscribeEvent
    public static void levelTick(ClientTickEvent.Post event) {
        ModifierManager.INSTANCE.updateChunkLoader();
    }
}
