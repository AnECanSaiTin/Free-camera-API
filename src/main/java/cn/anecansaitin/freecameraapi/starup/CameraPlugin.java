package cn.anecansaitin.freecameraapi.starup;

import cn.anecansaitin.freecameraapi.common.ModifierPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface CameraPlugin {
    /// CameraPlugin id.
    String value();
    ModifierPriority priority() default ModifierPriority.NORMAL;
}
