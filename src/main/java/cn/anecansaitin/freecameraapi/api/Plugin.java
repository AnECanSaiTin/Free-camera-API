package cn.anecansaitin.freecameraapi.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/// All {@link CameraPlugin} must have this annotation and a single-argument constructor accepting {@link CameraModifier}.
@Target(ElementType.TYPE)
public @interface Plugin {
    /// Plugin id. If id is "dev", it will only be loaded in dev environment.
    String value();

    /// Owner mod id used to construct the modifier {@link net.minecraft.resources.Identifier}.
    ///
    /// Must match the mod id declared in your mod's metadata (e.g. mods.toml / neoforge.mods.toml).
    /// FreeCameraAPI will not infer it from ModList scan order.
    String modid();

    /// Modifier full class name. If empty, the default {@code Modifier} implementation is used.
    String modifier() default "";

    /// Priority.
    ModifierPriority priority() default ModifierPriority.NORMAL;
}
