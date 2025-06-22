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
                .playToServer(CameraPoseUpdate.TYPE, CameraPoseUpdate.CODEC, CameraPoseUpdate::handle)
                /*.playToServer(
                        CameraState.TYPE,
                        CameraState.CODEC,
                        CameraState::handle
                )
                .playToServer(
                        CameraPos.TYPE,
                        CameraPos.CODEC,
                        CameraPos::handle
                )
                .playToClient(
                        CameraEnable.TYPE,
                        CameraEnable.CODEC,
                        CameraEnable::handle
                )
                .playToClient(
                        CameraDisable.TYPE,
                        CameraDisable.CODEC,
                        CameraDisable::handle
                )*/;
    }
}
