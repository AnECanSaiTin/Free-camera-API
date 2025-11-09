package cn.anecansaitin.freecameraapi.zoom;

import cn.anecansaitin.freecameraapi.FreeCameraClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = FreeCameraClient.MODID, value = Dist.CLIENT)
public class ZoomKeyMapping {
    private static final KeyMapping.Category CATEGORY = new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath("zoom", "key"));

    public static final Lazy<KeyMapping> ZOOM_MODE = Lazy.of(() -> new KeyMapping(
            "key." + "zoom" + ".free_mode",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            CATEGORY
    ));

    @SubscribeEvent
    public static void registerCategory(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
    }

    @SubscribeEvent
    public static void registerKey(RegisterKeyMappingsEvent event) {
        event.register(ZOOM_MODE.get());
    }

    @SubscribeEvent
    public static void keyPress(ClientTickEvent.Post event) {
        while (ZoomKeyMapping.ZOOM_MODE.get().consumeClick()) {
            if (ZoomPlugin.enabled()) {
                ZoomPlugin.instance.disable();
                ZoomGuiLayer.flash();
            } else {
                ZoomPlugin.instance.enable();
                ZoomGuiLayer.flash();
            }
        }
    }
}
