package cn.anecansaitin.zoom.client.registers;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.zoom.Zoom;
import cn.anecansaitin.zoom.client.gui.overlay.ZoomOverlay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ZoomOverlays {
    @SubscribeEvent
    public static void register(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(Zoom.MODID, "free_mode"), new ZoomOverlay());
    }
}
