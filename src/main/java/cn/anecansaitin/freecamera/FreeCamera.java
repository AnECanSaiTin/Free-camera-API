package cn.anecansaitin.freecamera;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(FreeCamera.MODID)
public class FreeCamera {
    public static final String MODID = "freecamera";

    public FreeCamera(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ModConf.SPEC);
    }
}
