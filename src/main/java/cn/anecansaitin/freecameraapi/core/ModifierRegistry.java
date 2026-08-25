package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.api.CameraModifier;
import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.ModifierPriority;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.*;

public class ModifierRegistry {
    public static final ModifierRegistry INSTANCE = new ModifierRegistry();
    private final Map<ModifierPriority, List<CameraModifier>> priorityMap;
    private final Map<Identifier, CameraModifier> modifierMap;
    private final List<CameraModifier> modifierList;
    private final List<CameraModifier> removedList;
    private final Map<CameraModifier, CameraPlugin> plugins;
    private boolean frozen = false;

    private ModifierRegistry() {
        priorityMap = new EnumMap<>(ModifierPriority.class);
        modifierMap = new HashMap<>();
        modifierList = new ArrayList<>();
        removedList = new ArrayList<>();
        plugins = new HashMap<>();

        for (ModifierPriority priority : ModifierPriority.values()) {
            priorityMap.put(priority, new ArrayList<>());
        }
    }

    public void register(Identifier id, CameraPlugin plugin) {
        register(id, plugin, ModifierPriority.NORMAL);
    }

    public void register(Identifier id, CameraPlugin plugin, ModifierPriority priority) {
        register(plugin, priority, new Modifier(id));
    }

    public void register(CameraPlugin plugin, ModifierPriority priority, CameraModifier modifier) {
        if (frozen) {
            throw new IllegalStateException("ModifierRegistry is frozen");
        }

        if (modifierMap.containsKey(modifier.getId())) {
            throw new IllegalArgumentException("Modifier with id " + modifier.getId() + " already registered");
        }

        modifierMap.put(modifier.getId(), modifier);
        priorityMap.get(priority).add(modifier);
        plugins.put(modifier, plugin);
    }

    public void freeze(List<String> order, List<String> removed) {
        if (frozen) {
            return;
        }

        frozen = true;
        sort();
        setOrderById(order, removed);
    }

    public void resetOrder(List<String> order, List<String> removed) {
        sort();
        setOrderById(order, removed);
    }

    private void sort() {
        for (ModifierPriority priority : ModifierPriority.values()) {
            modifierList.addAll(priorityMap.get(priority));
        }
    }

    private void setOrderById(List<String> order, List<String> removed) {
        ArrayList<CameraModifier> ordered = new ArrayList<>();
        ArrayList<CameraModifier> toRemove = new ArrayList<>();

        for (String id : order) {
            CameraModifier modifier = modifierMap.get(Identifier.parse(id));

            if (modifier == null) {
                continue;
            }

            ordered.add(modifier);
        }

        for (String id : removed) {
            CameraModifier modifier = modifierMap.get(Identifier.parse(id));

            if (modifier == null) {
                continue;
            }

            toRemove.add(modifier);
        }

        modifierList.removeAll(ordered);
        modifierList.addAll(0, ordered);
        modifierList.removeAll(toRemove);
        this.removedList.clear();
        this.removedList.addAll(toRemove);
    }

    /// 移动一个修改器到新位置
    public void move(int index, int newIndex) {
        modifierList.add(newIndex, modifierList.remove(index));
    }

    /// 从列表中移除一个修改器
    public void remove(int index) {
        removedList.add(modifierList.remove(index));
    }

    /// 从已移除列表取回修改器，放回 active modifierList 的 newIndex 位置
    public void moveBack(int index, int newIndex) {
        modifierList.add(newIndex, removedList.remove(index));
    }

    public List<CameraModifier> getAllMoModifiers() {
        ArrayList<CameraModifier> modifiers = new ArrayList<>();

        for (List<CameraModifier> value : priorityMap.values()) {
            modifiers.addAll(value);
        }

        return modifiers;
    }

    public void updateController(float partialTicks) {
        for (CameraModifier modifier : modifierList) {
            plugins.get(modifier).update(partialTicks);
        }
    }

    @Nullable
    public CameraModifier getActiveModifier() {
        for (CameraModifier modifier : modifierList) {
            if (modifier.isActive()) {
                return modifier;
            }
        }

        return null;
    }
}
