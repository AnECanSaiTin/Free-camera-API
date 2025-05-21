package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.common.ModConfig;
import cn.anecansaitin.freecameraapi.common.ModifierPriority;
import cn.anecansaitin.freecameraapi.common.ModifierRegistry;
import cn.anecansaitin.freecameraapi.starup.IPlugin;
import cn.anecansaitin.freecameraapi.starup.PluginFinder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import oshi.util.tuples.Triplet;

@Mod(FreeCamera.MODID)
public class FreeCamera {
    public static final String MODID = "freecameraapi";

    public FreeCamera(IEventBus modEventBus, ModContainer modContainer) {
        loadPlugin();
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, ModConfig.SPEC);
    }

    private void loadPlugin() {
        for (Triplet<String, IPlugin, ModifierPriority> triplet : PluginFinder.find()) {
            ModifierRegistry.INSTANCE.register(triplet.getA(), triplet.getB(), triplet.getC());
        }
    }
}
