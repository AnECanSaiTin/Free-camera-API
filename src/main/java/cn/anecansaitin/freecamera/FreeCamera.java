package cn.anecansaitin.freecamera;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(FreeCamera.MODID)
public class FreeCamera {
    public static final String MODID = "freecamera";

    public static final CameraModifier.Modifier modifier = CameraModifier.createBackgroundModifier(MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredHolder<Item, TestItem> TEST = ITEMS.register("test", TestItem::new);

    public FreeCamera(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
    }
}
