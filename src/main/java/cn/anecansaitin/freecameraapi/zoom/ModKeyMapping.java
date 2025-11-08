package cn.anecansaitin.freecameraapi.zoom;

import cn.anecansaitin.freecameraapi.FreeCameraClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = FreeCameraClient.MODID, value = Dist.CLIENT)
public class ModKeyMapping {
    public static final Lazy<KeyMapping> ZOOM_MODE = Lazy.of(() -> new KeyMapping(
            "key." + "zoom" + ".free_mode",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.categories." + "zoom"
    ));

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(ZOOM_MODE.get());
    }

    @SubscribeEvent
    public static void keyPress(ClientTickEvent.Post event) {
        while (ModKeyMapping.ZOOM_MODE.get().consumeClick()) {
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
