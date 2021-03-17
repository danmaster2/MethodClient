package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.utils.TimerUtils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static Method.Client.Main.setmgr;

public class ChestStealer extends Module {
    private final TimerUtils Timer = new TimerUtils();

    Setting delay;
    Setting Entity;
    Setting Shulker;
    public static Setting Mode;


    public ChestStealer() {
        super("ChestStealer", Keyboard.KEY_NONE, Category.PLAYER, "ChestStealer");
    }

    @Override
    public void setup() {
        setmgr.add(Mode = new Setting("Mode", this, "Steal", "Steal","Store","Drop"));
        setmgr.add(delay = new Setting("Delay", this, 3, 0, 20, true));
        setmgr.add(Shulker = new Setting("Take Shulker", this, false));
        setmgr.add(Entity = new Setting("Entitys Chest", this, true));
    }


    @Override
    public void onClientTick(ClientTickEvent event) {
        if (!Timer.isDelay((long) (delay.getValDouble() * 100)))
            return;
        Timer.setLastMS();
        if (mc.currentScreen instanceof GuiChest) {
            GuiChest guiChest = (GuiChest) mc.currentScreen;
            Quickhandle(guiChest, guiChest.lowerChestInventory.getSizeInventory());
        } else if (mc.currentScreen instanceof GuiScreenHorseInventory && Entity.getValBoolean()) {
            GuiScreenHorseInventory horseInventory = (GuiScreenHorseInventory) mc.currentScreen;
            Quickhandle(horseInventory, horseInventory.horseInventory.getSizeInventory());
        } else if (mc.currentScreen instanceof GuiShulkerBox && Shulker.getValBoolean()) {
            GuiShulkerBox shulkerBox = (GuiShulkerBox) mc.currentScreen;
            Quickhandle((GuiShulkerBox) mc.currentScreen, shulkerBox.inventory.getSizeInventory());
        } else {
            ModuleManager.getModuleByName("ChestStealer").toggle();
        }
    }

    private void Quickhandle(GuiContainer guiContainer, int size) {
        for (int i = 0; i < size; ++i) {
            ItemStack stack = guiContainer.inventorySlots.getInventory().get(i);
            if ((stack.isEmpty() || stack.getItem() == Items.AIR) && Mode.getValString().equalsIgnoreCase("Store")) {
                HandleStoring(guiContainer.inventorySlots.windowId, size - 9);
                return;
            }
            if (StealorDrop(guiContainer.inventorySlots.windowId, i, stack)) {
                break;
            }
        }
    }

    private void HandleStoring(int pWindowId, int stack) {
        for (int i = 9; i < mc.player.inventoryContainer.inventorySlots.size() - 1; ++i) {
            ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();

            if (itemStack.isEmpty() || itemStack.getItem() == Items.AIR)
                continue;

            if (Shulker.getValBoolean() && !(itemStack.getItem() instanceof ItemShulkerBox))
                continue;

            mc.playerController.windowClick(pWindowId, i + stack, 0, ClickType.QUICK_MOVE, mc.player);
            return;
        }
    }

    private boolean StealorDrop(int windowId, int i, ItemStack stack) {
        if (stack.isEmpty() || (Shulker.getValBoolean() && !(stack.getItem() instanceof ItemShulkerBox)))
            return false;
        if (Mode.getValString().equalsIgnoreCase("Steal")) {
            mc.playerController.windowClick(windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
            return true;
        } else if (Mode.getValString().equalsIgnoreCase("Drop")) {
            mc.playerController.windowClick(windowId, i, 1, ClickType.THROW, mc.player);
            return true;
        }
        return false;
    }


}
