package cn.anecansaitin.freecameraapi.zoom;

import cn.anecansaitin.freecameraapi.ClientUtil;
import cn.anecansaitin.freecameraapi.FreeCameraClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = FreeCameraClient.MODID, value = Dist.CLIENT)
public class ZoomGuiLayer implements LayeredDraw.Layer {
    private static final Component enabled = Component.translatable("zoom.gui_layer.enabled");
    private static final Component disabled = Component.translatable("zoom.gui_layer.disabled");
    private static int countdown = 0;
    private static final int maxCountdown = 60;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (countdown > 0) {
            guiGraphics.drawCenteredString(ClientUtil.font(), ZoomPlugin.enabled() ? enabled : disabled, guiGraphics.guiWidth() / 2, guiGraphics.guiHeight() - 70, 0xffffffff);
        }
    }

    @SubscribeEvent
    public static void register(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.TITLE, ResourceLocation.fromNamespaceAndPath(FreeCameraClient.MODID, "zoom"), new ZoomGuiLayer());
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) {
        if (countdown > 0) {
            countdown--;
        }
    }

    public static void flash() {
        countdown = maxCountdown;
    }
}
