package cn.anecansaitin.freecameraapi.core.attachment;

import cn.anecansaitin.freecameraapi.FreeCamera;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachment {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FreeCamera.MODID);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CameraChunk>> CAMERA_CHUNK = ATTACHMENT_TYPES.register("camera_chunk", () -> AttachmentType.builder(CameraChunk::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CameraChunk>> CAMERA_CHUNK_OLD = ATTACHMENT_TYPES.register("camera_chunk_old", () -> AttachmentType.builder(CameraChunk::new).build());
}
