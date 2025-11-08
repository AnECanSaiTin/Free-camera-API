package cn.anecansaitin.freecameraapi.zoom;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public class Zoom {
    public static void clientInit(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ZoomConfig.Client.SPEC, "zoom-client.toml");
    }

    public static void serverInit(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, ZoomConfig.Server.SPEC, "zoom-server.toml");
    }
}
