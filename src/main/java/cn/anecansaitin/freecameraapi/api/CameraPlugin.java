package cn.anecansaitin.freecameraapi.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/// All {@link ICameraPlugin} must have this annotation and a constructor with no arguments.
@Target(ElementType.TYPE)
public @interface CameraPlugin {
    /// Plugin id. If id is "dev", it will only be loaded in dev environment.
    String value();

    /// Priority.
    ModifierPriority priority() default ModifierPriority.NORMAL;
}
