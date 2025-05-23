package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.common.CameraConfig;
import cn.anecansaitin.freecameraapi.common.ModifierPriority;
import cn.anecansaitin.freecameraapi.common.ModifierRegistry;
import cn.anecansaitin.freecameraapi.starup.IPlugin;
import cn.anecansaitin.freecameraapi.starup.PluginFinder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import oshi.util.tuples.Triplet;

@Mod(FreeCamera.MODID)
public class FreeCamera {
    public static final String MODID = "freecameraapi";

    public FreeCamera(IEventBus modEventBus, ModContainer modContainer) {
        PluginFinder.loadPlugin();
        modContainer.registerConfig(ModConfig.Type.CLIENT, CameraConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
