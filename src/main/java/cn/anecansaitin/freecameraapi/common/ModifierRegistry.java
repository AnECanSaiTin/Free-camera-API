package cn.anecansaitin.freecameraapi.common;

import javax.annotation.Nullable;
import java.util.*;

public class ModifierRegistry {
    private Map<ModifierPriority, List<ICameraModifier>> priorityMap;
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

    public void freeze(List<String> order, List<String> removed) {
        isFreeze = true;
        sort();
        setOrderById(order, removed);
    }

    private void sort() {
        for (ModifierPriority priority : ModifierPriority.values()) {
            modifierList.addAll(priorityMap.get(priority));
        }

        priorityMap = null;
    }

    private void setOrderById(List<String> order, List<String> removed) {
        ArrayList<ICameraModifier> orderList = new ArrayList<>();
        ArrayList<ICameraModifier> removedList = new ArrayList<>();

        for (String id : order) {
            ICameraModifier modifier = modifierMap.get(id);

            if (modifier == null) {
                continue;
            }

            orderList.add(modifier);
        }

        for (String id : removed) {
            ICameraModifier modifier = modifierMap.get(id);

            if (modifier == null) {
                continue;
            }

            removedList.add(modifier);
        }

        modifierList.removeAll(orderList);
        modifierList.addAll(0, orderList);
        modifierList.removeAll(removedList);
        this.removedList.clear();
        this.removedList.addAll(removedList);
    }

    /// 移动一个修改器到新位置
    public void move(int index, int newIndex) {
        modifierList.add(newIndex, modifierList.remove(index));
    }

    /// 从列表中移除一个修改器
    public void remove(int index) {
        removedList.add(modifierList.remove(index));
    }

    /// 从已移除取回修改器
    public void moveBack(int index,  int newIndex) {
        modifierList.add(newIndex, modifierList.remove(index));
    }

    public List<ICameraModifier> getModifiers() {
        return modifierList;
    }

    public List<ICameraModifier> getRemovedModifiers() {
        return removedList;
    }

    @Nullable
    public ICameraModifier getActiveModifier() {
        for (ICameraModifier modifier : modifierList) {
            if (modifier.isActive()) {
                return modifier;
            }
        }

        return null;
    }
}
