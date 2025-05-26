package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import cn.anecansaitin.freecameraapi.api.ICameraPlugin;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

public class ModifierRegistry {
    public static final ModifierRegistry INSTANCE = new ModifierRegistry();
    private final Map<ModifierPriority, List<ICameraModifier>> priorityMap;
    private final Map<ResourceLocation, ICameraModifier> modifierMap;
    private final List<ICameraModifier> modifierList;
    private final List<ICameraModifier> removedList;
    private final Map<ICameraModifier, ICameraPlugin> plugins;
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

    public void register(ResourceLocation id, ICameraPlugin plugin) {
        register(id, plugin, ModifierPriority.NORMAL);
    }

    public void register(ResourceLocation id, ICameraPlugin plugin, ModifierPriority priority) {
        register(plugin, priority, new Modifier(id));
    }

    public void register(ICameraPlugin plugin, ModifierPriority priority, ICameraModifier modifier) {
        if (frozen) {
            throw new IllegalStateException("ModifierRegistry is frozen");
        }

        if (modifierMap.containsKey(modifier.getId())) {
            throw new IllegalArgumentException("Modifier with id " + modifier.getId() + " already registered");
        }

        modifierMap.put(modifier.getId(), modifier);
        priorityMap.get(priority).add(modifier);
        plugins.put(modifier, plugin);
        plugin.initialize(modifier);
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
        ArrayList<ICameraModifier> orderList = new ArrayList<>();
        ArrayList<ICameraModifier> removedList = new ArrayList<>();

        for (String id : order) {
            ICameraModifier modifier = modifierMap.get(ResourceLocation.parse(id));

            if (modifier == null) {
                continue;
            }

            orderList.add(modifier);
        }

        for (String id : removed) {
            ICameraModifier modifier = modifierMap.get(ResourceLocation.parse(id));

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
    public void moveBack(int index, int newIndex) {
        modifierList.add(newIndex, modifierList.remove(index));
    }

    public List<ICameraModifier> getAllMoModifiers() {
        ArrayList<ICameraModifier> modifiers = new ArrayList<>();

        for (List<ICameraModifier> value : priorityMap.values()) {
            modifiers.addAll(value);
        }

        return modifiers;
    }

    public void updateController() {
        for (ICameraModifier modifier : modifierList) {
            plugins.get(modifier).update();
        }
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
