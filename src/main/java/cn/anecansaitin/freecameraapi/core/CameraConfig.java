package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = FreeCamera.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CameraConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.ConfigValue<List<? extends String>> PLAYER_ORDER;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> REMOVED;

    static {
        PLAYER_ORDER = BUILDER
                .comment("  The order of the camera modifiers.", "  If some plugins are not displayed, you can click \"Reset\" to refresh.")
                .defineListAllowEmpty("order", defaultOrderList(), () -> "example:id", CameraConfig::validate);

        REMOVED = BUILDER
                .comment("  The modifiers that are removed.")
                .defineListAllowEmpty("removed", new ArrayList<>(), () -> "example:id", CameraConfig::validate);
    }

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
    @SuppressWarnings("unchecked")
    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        checkConfig();
        ModifierRegistry.INSTANCE.freeze((List<String>) PLAYER_ORDER.get(), (List<String>) REMOVED.get());
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        checkConfig();
        ModifierRegistry.INSTANCE.resetOrder((List<String>) PLAYER_ORDER.get(), (List<String>) REMOVED.get());
    }

    private static void checkConfig() {
        checkId(PLAYER_ORDER.get());
        checkId(REMOVED.get());
    }

    private static void checkId(List<? extends String> ids) {
        for (int i = ids.size() - 1; i >= 0; i--) {
            String s = ids.get(i);

            if (s.isBlank()) {
                ids.remove(i);
                continue;
            }

            String[] split = s.split(":");

            if (split.length != 2) {
                ids.remove(i);
                continue;
            }

            if (!ResourceLocation.isValidNamespace(split[0]) || !ResourceLocation.isValidPath(split[1])) {
                ids.remove(i);
            }
        }
    }

    private static List<String> defaultOrderList() {
        List<ICameraModifier> list = ModifierRegistry.INSTANCE.getAllMoModifiers();
        ArrayList<String> defaultList = new ArrayList<>();

        for (ICameraModifier modifier : list) {
            defaultList.add(modifier.getId().toString());
        }

        return defaultList;
    }
}
