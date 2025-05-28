package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.FreeCamera;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;

@EventBusSubscriber(modid = FreeCamera.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModTicketController {
    public static final TicketController TICKET_CONTROLLER = new TicketController(ResourceLocation.fromNamespaceAndPath(FreeCamera.MODID, "ticket_controller"), (level, helper) -> {
        for (var uuid : helper.getEntityTickets().keySet()) {
            if (level.getEntity(uuid) == null) {
                helper.removeAllTickets(uuid);
            }
        }
    });

    @SubscribeEvent
    public static void register(RegisterTicketControllersEvent event) {
        event.register(TICKET_CONTROLLER);
    }
}
