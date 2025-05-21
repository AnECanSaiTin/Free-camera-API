package cn.anecansaitin.freecameraapi.common;

import cn.anecansaitin.freecameraapi.FreeCamera;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

@EventBusSubscriber(modid = FreeCamera.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.ConfigValue<List<? extends String>> PLAYER_ORDER = BUILDER
//            .comment("")
            .defineList("order", List.of(), null, ModConfig::validate);
    private static final ModConfigSpec.ConfigValue<List<? extends String>> REMOVED = BUILDER
//            .comment("")
            .defineList("removed", List.of(), null, ModConfig::validate);

    public static final ModConfigSpec SPEC = BUILDER.build();

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
    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        ModifierRegistry.INSTANCE.freeze((List<String>) PLAYER_ORDER.get(), (List<String>) REMOVED.get());
    }
}
