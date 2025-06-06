package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.FreeCamera;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;

import java.util.UUID;

@EventBusSubscriber(modid = FreeCamera.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModTicketController {
    public static final TicketController TICKET_CONTROLLER = new TicketController(ResourceLocation.fromNamespaceAndPath(FreeCamera.MODID, "ticket_controller"), (level, helper) -> {
        ticketHelper = helper;

        for (UUID uuid : helper.getEntityTickets().keySet()) {
            helper.removeAllTickets(uuid);
        }
    });

    public static TicketHelper ticketHelper;

    @SubscribeEvent
    public static void register(RegisterTicketControllersEvent event) {
        event.register(TICKET_CONTROLLER);
    }

    public static void addChunk(ServerLevel level, Entity owner, int x, int y) {
        TICKET_CONTROLLER.forceChunk(level, owner, x, y, true, true);
    }

    public static void removeAllChunk(UUID owner) {
        ticketHelper.removeAllTickets(owner);
    }
}
