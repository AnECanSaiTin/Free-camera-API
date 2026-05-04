package cn.anecansaitin.freecameraapi.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/// All {@link CameraPlugin} must have this annotation and a constructor with no arguments.
@Target(ElementType.TYPE)
public @interface Plugin {
    /// Plugin id. If id is "dev", it will only be loaded in dev environment.
    String value();

    /// Modifier full class name. If name is empty, it will use the default modifier.
    String modifier() default "";

    /// Priority.
    ModifierPriority priority() default ModifierPriority.NORMAL;
}
