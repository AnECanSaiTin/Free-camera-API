package cn.anecansaitin.freecameraapi.starup;

import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.CameraState;
import cn.anecansaitin.freecameraapi.api.ICameraPlugin;
import cn.anecansaitin.freecameraapi.api.ModifierPriority;
import cn.anecansaitin.freecameraapi.core.ModifierRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.modscan.ModAnnotation;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import oshi.util.tuples.Triplet;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

public final class AnnotationFinder {
    public static void clientLoading() {
        loadState();
        loadPlugin();
    }

    public static void commonLoading() {
        loadState();
    }

    private static void loadState() {
        try {
            for (String clazz : findState()) {
                Class.forName(clazz);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Collection<String> findState() {
        Type type = Type.getType(CameraState.class);
        TreeMap<String, String> stateClass = new TreeMap<>();
        List<ModFileScanData> allScanData = ModList.get().getAllScanData();

        for (int i = 0, allScanDataSize = allScanData.size(); i < allScanDataSize; i++) {
            ModFileScanData data = allScanData.get(i);

            for (var annotation : data.getAnnotations()) {
                if (!annotation.annotationType().equals(type)) {
                    continue;
                }

                String namespace = ModList.get().getMods().get(i).getNamespace();
                String className = annotation.memberName();
                stateClass.put(namespace, className);
                break;
            }
        }

        return stateClass.values();
    }

    private static void loadPlugin() {
        for (Triplet<ResourceLocation, ICameraPlugin, ModifierPriority> triplet : AnnotationFinder.findPlugin()) {
            ModifierRegistry.INSTANCE.register(triplet.getA(), triplet.getB(), triplet.getC());
        }
    }

    private static List<Triplet<ResourceLocation, ICameraPlugin, ModifierPriority>> findPlugin() {
        Type type = Type.getType(CameraPlugin.class);
        ArrayList<Triplet<ResourceLocation, ICameraPlugin, ModifierPriority>> plugins = new ArrayList<>();
        List<ModFileScanData> allScanData = ModList.get().getAllScanData();
        boolean dev = !FMLEnvironment.isProduction();

        for (int i = 0, allScanDataSize = allScanData.size(); i < allScanDataSize; i++) {
            ModFileScanData data = allScanData.get(i);

            for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                if (!annotation.annotationType().equals(type)) {
                    continue;
                }

                String name = null;

                try {
                    String value = annotation.annotationData().get("value").toString();

                    if (!dev && value.equals("dev")) {
                        continue;
                    }

                    String namespace = ModList.get().getMods().get(i).getNamespace();
                    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, value);
                    ModAnnotation.EnumHolder priorityHolder = (ModAnnotation.EnumHolder) annotation.annotationData().get("priority");
                    ModifierPriority priority = ModifierPriority.NORMAL;

                    if (priorityHolder != null) {
                        priority = ModifierPriority.valueOf(priorityHolder.value());
                    }

                    name = annotation.memberName();
                    ICameraPlugin plugin = Class
                            .forName(name)
                            .asSubclass(ICameraPlugin.class)
                            .getDeclaredConstructor()
                            .newInstance();
                    plugins.add(new Triplet<>(id, plugin, priority));

                } catch (ClassNotFoundException e) {
                    throw CameraPluginInitializeException.classNotFound(name);
                } catch (NoSuchMethodException e) {
                    throw CameraPluginInitializeException.noSuchMethod(name);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    throw CameraPluginInitializeException.invocationTarget(name);
                }

                break;
            }
        }

        return plugins;
    }
}
