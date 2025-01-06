package cn.anecansaitin.freecameraapi.client;

import cn.anecansaitin.freecameraapi.CameraModifierManager;
import cn.anecansaitin.freecameraapi.ModConf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CameraModifierScreen extends Screen {
    public CameraModifierScreen() {
        super(Component.translatable("camera_modifier_screen.title"));
    }

    @Override
    protected void init() {
        NormalModifierWidget normal = NormalModifierWidget.create(5, 15, 60, 151);
        PlayerOrderModifierWidget order = PlayerOrderModifierWidget.create(70, 15, 60, 151);
        BackgroundModifierWidget background = BackgroundModifierWidget.create(145, 15, 60, 151);
        PlayerRemovedModifierWidget removed = PlayerRemovedModifierWidget.create(215, 15, 60, 151);
        normal.playerOrder = order;
        order.normalModifier = normal;
        background.playerRemoved = removed;
        removed.backgroundModifier = background;
        addRenderableWidget(normal);
        addRenderableWidget(order);
        addRenderableWidget(background);
        addRenderableWidget(removed);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        super.onClose();
        ModConf.setPlayerOrder(CameraModifierManager.getPlayerOrder());
        ModConf.setRemoved(CameraModifierManager.getPlayerRemovedBackground());
        ModConf.save();
    }

    private static class ComponentSCRollWidget extends AbstractWidget {
        protected final List<MutableComponent> list;
        private final int maxCount;
        private int paneLength;
        private double roll;
        protected int selected = -1;

        public ComponentSCRollWidget(int x, int y, int width, int height, Component message, List<MutableComponent> list) {
            super(x, y, width, height, message);
            this.list = list;
            maxCount = (height - 8) / 11;

            if (list.size() <= maxCount) {
                paneLength = height - 3;
            } else {
                paneLength = (int) Math.max(3, Math.ceil((float) (height - 8) / Math.max(list.size() - maxCount, 1)));
            }
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            Font font = Minecraft.getInstance().font;
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getHeight();

            //底部文本
            guiGraphics.drawString(font, getMessage(), x + 5, y + height + 5, 0xffffffff);
            guiGraphics.vLine(x + 2, y + height + 2, y + height + 17, 0xffff8c94);
            guiGraphics.vLine(x + width - 2, y + height + 2, y + height + 17, 0xffff8c94);
            guiGraphics.hLine(x + 2, x + width - 2, y + height + 2, 0xffff8c94);
            guiGraphics.hLine(x + 2, x + width - 2, y + height + 17, 0xffff8c94);

            //边框
            guiGraphics.vLine(x, y, y + height, 0xffff8c94);
            guiGraphics.vLine(x + width, y, y + height, 0xffff8c94);
            guiGraphics.hLine(x, x + width, y, 0xffff8c94);
            guiGraphics.hLine(x, x + width, y + height, 0xffff8c94);

            x += 4;
            y += 4;
            width -= 8;
            height -= 8;
            int index = Math.min((int) roll, list.size() - 1);
            //滚动条
            guiGraphics.fill(x + width, y - 2, x + width + 3, y + height + 3, 0xffffffff);
            int paneStart = (int) (y - 2 + ((float) index) / Math.max(1, (list.size() - maxCount)) * (height + 5 - paneLength));
            paneStart = Math.max(y - 2, paneStart);
            guiGraphics.fill(x + width, paneStart, x + width + 3, paneStart + paneLength, 0xff000000);


            if (list.isEmpty()) {
                guiGraphics.drawCenteredString(font, "null", x + width / 2, y + height / 2, 0xffffff);
            } else {
                int count = maxCount;

                for (; index < list.size() && count > 0; index++, count--) {
                    MutableComponent literal = list.get(index).copy();

                    if (index == selected) {
                        //选中时突出
                        guiGraphics.fill(x - 1, y - 1, x + width - 2, y + 9, 0x77ffffff);
                        literal.withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD);
                        guiGraphics.drawString(font, literal, x, y, 0xffffffff);
                    } else {
                        guiGraphics.drawString(font, literal, x, y, 0xffffffff);
                    }

                    guiGraphics.hLine(x - 2, x + width - 2, y - 2, 0xffffaaa6);
                    guiGraphics.vLine(x - 2, y - 2, y + 9, 0xffffaaa6);
                    guiGraphics.vLine(x + width - 2, y - 2, y + 9, 0xffffaaa6);

                    y += 11;
                    //这里定了一个完整的格子是12，不包含下方的线
                }

                guiGraphics.hLine(x - 2, x + width - 2, y - 2, 0xffffaaa6);
            }

            if (mouseX > getX() + 2 && mouseX < getX() + getWidth() - 2 && mouseY > getY() + getHeight() + 2 && mouseY < getY() + getHeight() + 17) {
                ComponentContents contents = getMessage().getContents();

                if (contents instanceof TranslatableContents c) {
                    guiGraphics.renderComponentTooltip(font, List.of(Component.translatable(c.getKey() + ".tip1"), Component.translatable(c.getKey() + ".tip2")), mouseX, mouseY);
                }
            }
        }

        @Override
        public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
            roll = org.joml.Math.clamp(roll - pDelta, 0, Math.max(list.size() - maxCount, 0));
            return true;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.active && this.visible && button == 0) {
                int x = getX() + 2;
                int y = getY() + 2;
                int width = getWidth() - 7;
                int height = maxCount * 11 + 1;

                if (!(mouseX >= x) || !(mouseX <= x + width) || !(mouseY >= y) || !(mouseY <= y + height)) {
                    selected = -1;
                    return false;
                }

                int i = Math.min((int) (mouseY - y) / 11, maxCount - 1) + Math.min((int) roll, list.size() - 1);

                if (i >= list.size()) {
                    selected = -1;
                    return true;
                } else if (selected == i) {
                    onDoubleSelected(i);
                } else {
                    selected = i;
                }

                return true;
            }

            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }

        protected void onDoubleSelected(int index) {

        }

        protected void onListChanged() {
            if (list.size() <= maxCount) {
                paneLength = height - 3;
            } else {
                paneLength = (int) Math.max(3, Math.ceil((float) (height - 8) / Math.max(list.size() - maxCount, 1)));
            }
        }
    }

    private static class NormalModifierWidget extends ComponentSCRollWidget {
        private final List<String> ids;
        private PlayerOrderModifierWidget playerOrder;

        public NormalModifierWidget(int x, int y, int width, int height, Component message, List<String> ids, List<MutableComponent> list) {
            super(x, y, width, height, message, list);
            this.ids = ids;
        }

        @Override
        protected void onDoubleSelected(int index) {
            String id = ids.get(index);

            if (CameraModifierManager.getPlayerOrder().contains(id)) {
                return;
            }

            CameraModifierManager.getPlayerOrder().add(id);
            list.remove(index);
            ids.remove(index);
            playerOrder.reload();
            onListChanged();
        }

        protected void reload() {
            ids.clear();
            list.clear();
            loadModifier(ids, list);
            onListChanged();
        }

        private static NormalModifierWidget create(int x, int y, int width, int height) {
            ArrayList<String> ids = new ArrayList<>();
            ArrayList<MutableComponent> components = new ArrayList<>();
            loadModifier(ids, components);

            return new NormalModifierWidget(x, y, width, height, Component.translatable("camera_modifier_screen.normal_modifier"), ids, components);
        }

        private static void loadModifier(List<String> ids, List<MutableComponent> components) {
            HashMap<String, CameraModifierManager.Modifier> h = new HashMap<>(CameraModifierManager.getModifiersH());
            HashMap<String, CameraModifierManager.Modifier> l = new HashMap<>(CameraModifierManager.getModifiersL());

            //剔除已经被玩家指定优先的操作器
            for (String order : CameraModifierManager.getPlayerOrder()) {
                h.remove(order);
                l.remove(order);
            }

            for (String id : h.keySet()) {
                ids.add(id);
                components.add(Component.translatable("freecamera.modifier." + id));
            }

            for (String id : l.keySet()) {
                ids.add(id);
                components.add(Component.translatable("freecamera.modifier." + id));
            }
        }
    }

    private static class PlayerOrderModifierWidget extends ComponentSCRollWidget {
        private NormalModifierWidget normalModifier;

        public PlayerOrderModifierWidget(int x, int y, int width, int height, Component message, List<MutableComponent> list) {
            super(x, y, width, height, message, list);
        }

        @Override
        protected void onDoubleSelected(int index) {
            CameraModifierManager.getPlayerOrder().remove(index);
            list.remove(index);
            normalModifier.reload();
            onListChanged();
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (selected < 0) {
                return false;
            }

            List<String> playerOrder = CameraModifierManager.getPlayerOrder();

            switch (keyCode) {
                case GLFW.GLFW_KEY_UP -> {
                    if (selected == 0) {
                        return false;
                    }

                    String temple = playerOrder.get(selected);
                    playerOrder.set(selected, playerOrder.get(selected - 1));
                    playerOrder.set(selected - 1, temple);
                    selected--;
                }

                case GLFW.GLFW_KEY_DOWN -> {
                    if (selected == list.size() - 1) {
                        return false;
                    }

                    String temple = playerOrder.get(selected);
                    playerOrder.set(selected, playerOrder.get(selected + 1));
                    playerOrder.set(selected + 1, temple);
                    selected++;
                }
            }

            reload();
            return true;
        }

        private static PlayerOrderModifierWidget create(int x, int y, int width, int height) {
            List<String> playerOrder = CameraModifierManager.getPlayerOrder();
            ArrayList<MutableComponent> components = new ArrayList<>();

            for (String id : playerOrder) {
                components.add(Component.translatable("freecamera.modifier." + id));
            }

            return new PlayerOrderModifierWidget(x, y, width, height, Component.translatable("camera_modifier_screen.player_order"), components);
        }

        protected void reload() {
            list.clear();

            for (String id : CameraModifierManager.getPlayerOrder()) {
                list.add(Component.translatable("freecamera.modifier." + id));
            }

            onListChanged();
        }
    }

    private static class BackgroundModifierWidget extends ComponentSCRollWidget {
        private final List<String> ids;
        private PlayerRemovedModifierWidget playerRemoved;

        public BackgroundModifierWidget(int x, int y, int width, int height, Component message, List<String> ids, List<MutableComponent> list) {
            super(x, y, width, height, message, list);
            this.ids = ids;
        }

        @Override
        protected void onDoubleSelected(int index) {
            String id = ids.get(index);

            if (CameraModifierManager.getPlayerRemovedBackground().contains(id)) {
                return;
            }

            CameraModifierManager.getPlayerRemovedBackground().add(id);
            list.remove(index);
            ids.remove(index);
            playerRemoved.reload();
            onListChanged();
        }

        protected void reload() {
            ids.clear();
            list.clear();
            loadModifier(ids, list);
            onListChanged();
        }

        private static BackgroundModifierWidget create(int x, int y, int width, int height) {
            ArrayList<String> ids = new ArrayList<>();
            ArrayList<MutableComponent> components = new ArrayList<>();
            loadModifier(ids, components);

            return new BackgroundModifierWidget(x, y, width, height, Component.translatable("camera_modifier_screen.background_modifier"), ids, components);
        }

        private static void loadModifier(List<String> ids, List<MutableComponent> components) {
            HashMap<String, CameraModifierManager.Modifier> b = new HashMap<>(CameraModifierManager.getModifiersB());

            //剔除已经被玩家指定优先的操作器
            for (String order : CameraModifierManager.getPlayerRemovedBackground()) {
                b.remove(order);
            }

            for (String id : b.keySet()) {
                ids.add(id);
                components.add(Component.translatable("freecamera.modifier." + id));
            }
        }
    }

    private static class PlayerRemovedModifierWidget extends ComponentSCRollWidget {
        private BackgroundModifierWidget backgroundModifier;

        public PlayerRemovedModifierWidget(int x, int y, int width, int height, Component message, List<MutableComponent> list) {
            super(x, y, width, height, message, list);
        }

        @Override
        protected void onDoubleSelected(int index) {
            CameraModifierManager.getPlayerRemovedBackground().remove(index);
            list.remove(index);
            backgroundModifier.reload();
            onListChanged();
        }

        private static PlayerRemovedModifierWidget create(int x, int y, int width, int height) {
            List<String> playerOrder = CameraModifierManager.getPlayerRemovedBackground();
            ArrayList<MutableComponent> components = new ArrayList<>();

            for (String id : playerOrder) {
                components.add(Component.translatable("freecamera.modifier." + id));
            }

            return new PlayerRemovedModifierWidget(x, y, width, height, Component.translatable("camera_modifier_screen.removed_modifier"), components);
        }

        protected void reload() {
            list.clear();

            for (String id : CameraModifierManager.getPlayerRemovedBackground()) {
                list.add(Component.translatable("freecamera.modifier." + id));
            }

            onListChanged();
        }
    }
}
