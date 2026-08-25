package cn.anecansaitin.freecameraapi.starup;

public class CameraPluginInitializeException extends RuntimeException {
    public CameraPluginInitializeException(String message) {
        super(message);
    }

    public static CameraPluginInitializeException pluginClassNotFound(String className) {
        return new CameraPluginInitializeException("Plugin class \"" + className + "\" not found");
    }

    public static CameraPluginInitializeException pluginNoSuchConstructor(String className) {
        return new CameraPluginInitializeException("Plugin class \"" + className + "\" no constructor with CameraModifier arguments");
    }

    public static CameraPluginInitializeException pluginInvocationTarget(String className) {
        return new CameraPluginInitializeException("Plugin class \"" + className + "\" execute constructor failed");
    }

    public static CameraPluginInitializeException pluginMissingModid(String className) {
        return new CameraPluginInitializeException("Plugin class \"" + className + "\" missing required @Plugin(modid = ...)");
    }

    public static CameraPluginInitializeException modifierClassNotFound(String className) {
        return new CameraPluginInitializeException("Modifier class \"" + className + "\" not found");
    }

    public static CameraPluginInitializeException modifierNoSuchConstructor(String className) {
        return new CameraPluginInitializeException("Modifier class \"" + className + "\" no constructor with Identify arguments");
    }

    public static CameraPluginInitializeException modifierInvocationTarget(String className) {
        return new CameraPluginInitializeException("Modifier class \"" + className + "\" execute constructor failed");
    }
}
