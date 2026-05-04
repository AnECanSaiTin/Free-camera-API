package cn.anecansaitin.freecameraapi.core;

public class ManagerTicker {
    public static void update(float partialTicks) {
        ModifierRegistry.INSTANCE.updateController(partialTicks);
        ModifierManager.INSTANCE.modify();
    }
}
