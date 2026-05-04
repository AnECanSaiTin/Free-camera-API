package cn.anecansaitin.freecameraapi.starup;

import cn.anecansaitin.freecameraapi.api.CameraModifier;
import cn.anecansaitin.freecameraapi.api.Plugin;
import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.ModifierPriority;
import cn.anecansaitin.freecameraapi.core.Modifier;
import cn.anecansaitin.freecameraapi.core.ModifierRegistry;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.modscan.ModAnnotation;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import oshi.util.tuples.Triplet;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class AnnotationFinder {
    public static void clientLoading() {
        loadPlugin();
    }

    private static void loadPlugin() {
        for (Triplet<CameraModifier, CameraPlugin, ModifierPriority> triplet : AnnotationFinder.findPlugin()) {
            ModifierRegistry.INSTANCE.register(triplet.getB(), triplet.getC(), triplet.getA());
        }
    }

    private static List<Triplet<CameraModifier, CameraPlugin, ModifierPriority>> findPlugin() {
        Type type = Type.getType(Plugin.class);
        ArrayList<Triplet<CameraModifier, CameraPlugin, ModifierPriority>> plugins = new ArrayList<>();
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
                    // region 读取id
                    String value = annotation.annotationData().get("value").toString();

                    if (!dev && value.equals("dev")) {
                        continue;
                    }

                    String namespace = ModList.get().getMods().get(i).getNamespace();
                    Identifier id = Identifier.fromNamespaceAndPath(namespace, value);
                    // endregion
                    // region 读取优先级
                    ModAnnotation.EnumHolder priorityHolder = (ModAnnotation.EnumHolder) annotation.annotationData().get("priority");
                    ModifierPriority priority = ModifierPriority.NORMAL;

                    if (priorityHolder != null) {
                        priority = ModifierPriority.valueOf(priorityHolder.value());
                    }
                    // endregion
                    // region 读取modifier
                    String modifierClass = (String) annotation.annotationData().get("modifier");
                    CameraModifier modifier;

                    if (modifierClass != null) {
                        try {
                            modifier = Class.forName(modifierClass)
                                    .asSubclass(CameraModifier.class)
                                    .getConstructor(Identifier.class)
                                    .newInstance(id);
                        }  catch (ClassNotFoundException e) {
                            throw CameraPluginInitializeException.modifierClassNotFound(name);
                        } catch (NoSuchMethodException e) {
                            throw CameraPluginInitializeException.modifierNoSuchConstructor(name);
                        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                            throw CameraPluginInitializeException.modifierInvocationTarget(name);
                        }
                    } else {
                        modifier = new Modifier(id);
                    }
                    // endregion

                    name = annotation.memberName();
                    CameraPlugin plugin = Class
                            .forName(name)
                            .asSubclass(CameraPlugin.class)
                            .getConstructor(CameraModifier.class)
                            .newInstance(modifier);
                    plugins.add(new Triplet<>(modifier, plugin, priority));

                } catch (ClassNotFoundException e) {
                    throw CameraPluginInitializeException.pluginClassNotFound(name);
                } catch (NoSuchMethodException e) {
                    throw CameraPluginInitializeException.pluginNoSuchConstructor(name);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    throw CameraPluginInitializeException.pluginInvocationTarget(name);
                }
            }
        }

        return plugins;
    }
}
