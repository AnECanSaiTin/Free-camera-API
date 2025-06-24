package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.core.CameraConfig;
import cn.anecansaitin.freecameraapi.starup.PluginFinder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(FreeCamera.MODID)
public class FreeCamera {
    public static final String MODID = "free_camera_api";

    public FreeCamera(IEventBus modEventBus, ModContainer modContainer) {
        PluginFinder.loadPlugin();
        modContainer.registerConfig(ModConfig.Type.CLIENT, CameraConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
