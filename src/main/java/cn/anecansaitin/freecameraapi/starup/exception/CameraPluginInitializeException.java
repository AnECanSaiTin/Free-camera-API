package cn.anecansaitin.freecameraapi.starup.exception;

public class CameraPluginInitializeException extends RuntimeException {
    public CameraPluginInitializeException(String message) {
        super(message);
    }

    public static CameraPluginInitializeException classNotFound(String className) {
        return new CameraPluginInitializeException("Plugin class \"" + className + "\" not found");
    }

    public static CameraPluginInitializeException noSuchMethod(String className) {
        return new CameraPluginInitializeException("Plugin class \"" + className + "\" no constructor with no arguments");
    }

    public static CameraPluginInitializeException invocationTarget(String className) {
        return new CameraPluginInitializeException("Plugin class \"" + className + "\" execute constructor failed");
    }
}
