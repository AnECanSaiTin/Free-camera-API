package cn.anecansaitin.freecameraapi;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = FreeCamera.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConf {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> PLAYER_ORDER = BUILDER
//            .comment("")
            .defineList("order", List.of(), ModConf::validate);
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> REMOVED = BUILDER
//            .comment("")
            .defineList("removed", List.of(), ModConf::validate);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static boolean validate(Object obj) {
        return obj instanceof String;
    }

    public static void setPlayerOrder(List<String> list) {
        PLAYER_ORDER.set(list);
    }

    public static void setRemoved(List<String> list) {
        REMOVED.set(list);
    }

    public static void save() {
        SPEC.save();
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        CameraModifierManager.getPlayerOrder().clear();
        CameraModifierManager.getPlayerOrder().addAll(PLAYER_ORDER.get());
        CameraModifierManager.getPlayerRemovedBackground().clear();
        CameraModifierManager.getPlayerRemovedBackground().addAll(REMOVED.get());
    }
}
