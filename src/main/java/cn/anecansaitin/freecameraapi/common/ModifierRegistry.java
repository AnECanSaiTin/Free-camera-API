package cn.anecansaitin.freecameraapi.common;

import java.util.*;

public class ModifierRegistry {
    private Map<ModifierPriority,  List<ICameraModifier>> priorityMap;
    private final Map<String, ICameraModifier> modifierMap;
    private final List<ICameraModifier> modifierList;
    private final List<ICameraModifier> removedList;
    private boolean isFreeze = false;

    public ModifierRegistry() {
        priorityMap = new EnumMap<>(ModifierPriority.class);
        modifierMap = new HashMap<>();
        modifierList = new ArrayList<>();
        removedList = new ArrayList<>();

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

    public void move(int index, int newIndex) {
        modifierList.add(newIndex, modifierList.remove(index));
    }

    public void setOrderById(List<String> ids, List<String> removed) {
        ArrayList<ICameraModifier> modifiers = new ArrayList<>();

        for (String id : ids) {
            ICameraModifier modifier = modifierMap.get(id);

            if (modifier == null) {
                continue;
            }

            modifiers.add(modifier);
        }

        for (int i = modifierList.size() - 1; i >= 0; i--) {
            modifierList.removeAll(modifiers);
        }

        modifierList.addAll(0, modifiers);
    }

    public List<ICameraModifier> getModifiers() {
        return modifierList;
    }

    public ICameraModifier getActiveModifier() {
        return null;
    }
}
