package cn.anecansaitin.freecameraapi.core.network;

import cn.anecansaitin.freecameraapi.FreeCamera;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = FreeCamera.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModPayload {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0.0");
        registrar
                .optional()
                .playToServer(
                        LoadingChunk.TYPE,
                        LoadingChunk.CODEC,
                        LoadingChunk::handle
                )
                .playToServer(
                        UnloadingChunk.TYPE,
                        UnloadingChunk.CODEC,
                        UnloadingChunk::handle
                );
    }
}
