package cn.anecansaitin.freecameraapi.starup;

import cn.anecansaitin.freecameraapi.common.ModifierPriority;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import oshi.util.tuples.Triplet;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PluginFinder {
    public static List<Triplet<String ,IPlugin, ModifierPriority>> find() {
        Type type = Type.getType(CameraPlugin.class);
        ArrayList<Triplet<String ,IPlugin, ModifierPriority>> plugins = new ArrayList<>();

        try {
            for (ModFileScanData data : ModList.get().getAllScanData()) {
                for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                    if (!annotation.annotationType().equals(type)) {
                        continue;
                    }

                    String id = annotation.annotationData().get("id").toString();
                    ModifierPriority priority = (ModifierPriority) annotation.annotationData().get("priority");
                    String name = annotation.memberName();
                    IPlugin plugin = Class
                            .forName(name)
                            .asSubclass(IPlugin.class)
                            .getDeclaredConstructor()
                            .newInstance();
                    plugins.add(new Triplet<>(id, plugin, priority));
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return plugins;
    }
}
