package cn.anecansaitin.freecameraapi.starup;

import cn.anecansaitin.freecameraapi.common.ModifierPriority;
import cn.anecansaitin.freecameraapi.common.ModifierRegistry;
import cn.anecansaitin.freecameraapi.starup.exception.CameraPluginInitializeException;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.modscan.ModAnnotation;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import oshi.util.tuples.Triplet;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class PluginFinder {
    public static void loadPlugin() {
        for (Triplet<ResourceLocation, IPlugin, ModifierPriority> triplet : PluginFinder.find()) {
            ModifierRegistry.INSTANCE.register(triplet.getA(), triplet.getB(), triplet.getC());
        }
    }

    private static List<Triplet<ResourceLocation, IPlugin, ModifierPriority>> find() {
        Type type = Type.getType(CameraPlugin.class);
        ArrayList<Triplet<ResourceLocation, IPlugin, ModifierPriority>> plugins = new ArrayList<>();


        List<ModFileScanData> allScanData = ModList.get().getAllScanData();
        for (int i = 0, allScanDataSize = allScanData.size(); i < allScanDataSize; i++) {
            ModFileScanData data = allScanData.get(i);

            for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                if (!annotation.annotationType().equals(type)) {
                    continue;
                }

                String name = null;

                try {
                    String namespace = ModList.get().getMods().get(i).getNamespace();
                    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, annotation.annotationData().get("value").toString());
                    ModAnnotation.EnumHolder priorityHolder = (ModAnnotation.EnumHolder) annotation.annotationData().get("priority");
                    ModifierPriority priority = ModifierPriority.NORMAL;

                    if (priorityHolder != null) {
                        priority = ModifierPriority.valueOf(priorityHolder.value());
                    }

                    name = annotation.memberName();
                    IPlugin plugin = Class
                            .forName(name)
                            .asSubclass(IPlugin.class)
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
            }
        }

        return plugins;
    }
}
