package cn.anecansaitin.freecameraapi;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FreeCamera.MODID)
public class FreeCamera {
    public static final String MODID = "freecameraapi";

    public FreeCamera(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, ModConf.SPEC);
    }
}
