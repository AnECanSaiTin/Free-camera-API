package cn.anecansaitin.freecameraapi.common;

import java.util.*;

public class ModifierRegistry {
    private final Map<String, ICameraModifier> modifierMap;
    private final List<ICameraModifier> modifierList;
    private Map<ModifierPriority,  List<ICameraModifier>> priorityMap;
    private boolean isFreeze = false;

    public ModifierRegistry() {
        modifierMap = new HashMap<>();
        modifierList = new ArrayList<>();
        priorityMap = new EnumMap<>(ModifierPriority.class);

        for (ModifierPriority priority : ModifierPriority.values()) {
            priorityMap.put(priority, new ArrayList<>());
        }
    }

    public ICameraModifier register(String id) {
        return register(id, ModifierPriority.NORMAL);
    }

    public ICameraModifier register(String id, ModifierPriority priority) {
        return register(priority, new CameraModifierManager.Modifier(id));
    }

    public ICameraModifier register(ModifierPriority priority, ICameraModifier modifier) {
        if (isFreeze) {
            throw new IllegalStateException("ModifierRegistry is frozen");
        }

        if (!modifierMap.containsKey(modifier.getId())) {
            modifierMap.put(modifier.getId(), modifier);
            priorityMap.get(priority).add(modifier);
        }

        return modifier;
    }

    public void freeze() {
        isFreeze = true;
        sort();
    }

    private void sort() {
        for (ModifierPriority priority : ModifierPriority.values()) {
            modifierList.addAll(priorityMap.get(priority));
        }

        priorityMap = null;
    }
}
