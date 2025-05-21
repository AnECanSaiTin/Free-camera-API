package cn.anecansaitin.freecameraapi.starup;

import cn.anecansaitin.freecameraapi.common.ModifierPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface CameraPlugin {
    String id();
    ModifierPriority priority() default ModifierPriority.NORMAL;
}
