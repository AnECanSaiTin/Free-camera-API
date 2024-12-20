package cn.anecansaitin.freecameraapi.client;

import cn.anecansaitin.freecameraapi.FreeCamera;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModKeyMapping {
    public static final Lazy<KeyMapping> CAMERA_MODIFIER_SCREEN_KEY = Lazy.of(() ->
            new KeyMapping(
                    "key.freecamera.camera_modifier_screen",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories.free_camera"
            ));

    public static final Lazy<KeyMapping> SAVE_GLOBAL_CAMERA_POINT = Lazy.of(() ->
            new KeyMapping(
                    "key.freecamera.save_global_camera_point",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories.free_camera"
            ));

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(CAMERA_MODIFIER_SCREEN_KEY.get());
    }
}
