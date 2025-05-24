package cn.anecansaitin.freecameraapi.api;

import cn.anecansaitin.freecameraapi.core.ModifierPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface CameraPlugin {
    /// CameraPlugin id.
    String value();
    ModifierPriority priority() default ModifierPriority.NORMAL;
}
