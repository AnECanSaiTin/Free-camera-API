package cn.anecansaitin.zoom;

import cn.anecansaitin.freecameraapi.ClientUtil;
import cn.anecansaitin.freecameraapi.FreeCamera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class ZoomGuiLayer implements GuiLayer {
    private static final Component enabled = Component.translatable("zoom.gui_layer.enabled");
    private static final Component disabled = Component.translatable("zoom.gui_layer.disabled");
    private static int countdown = 0;
    private static final int maxCountdown = 60;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        int color;

        if (countdown > 40) {
            color = 0xffffffff;
        } else if (countdown > 0) {
            color = (255 * countdown / 40) << 24 | 0xffffff;
        } else {
            color = 0;
        }

        guiGraphics.drawCenteredString(ClientUtil.font(), ZoomPlugin.enabled() ? enabled : disabled, guiGraphics.guiWidth() / 2, guiGraphics.guiHeight() - 70, color);
    }

    @SubscribeEvent
    public static void register(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.TITLE, ResourceLocation.fromNamespaceAndPath(FreeCamera.MODID, "zoom"), new ZoomGuiLayer());
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
