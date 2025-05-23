package cn.anecansaitin.freecameraapi.api;

import cn.anecansaitin.freecameraapi.core.ModifierPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/// All {@link ICameraPlugin} must have this annotation and a constructor with no arguments.
@Target(ElementType.TYPE)
public @interface CameraPlugin {
    /// Id.
    String value();

    /// Priority.
    ModifierPriority priority() default ModifierPriority.NORMAL;
}
