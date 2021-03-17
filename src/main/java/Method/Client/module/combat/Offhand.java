package Method.Client.module.combat;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Offhand extends Module {

    Setting switch_mode = setmgr.add(new Setting("switch_mode", this, "Totem", "Totem", "Crystal", "Gapple"));
    Setting totem_switch = setmgr.add(new Setting("totem_switch", this, 16, 0, 36, true));
    Setting gapple_in_hole = setmgr.add(new Setting("gapple_in_hole", this, true));
    Setting gapple_hole_hp = setmgr.add(new Setting("gapple_hole_hp", this, 8, 0, 36, true));
    Setting delay = setmgr.add(new Setting("delay", this, true));

    boolean switching = false;
    int last_slot;

    public Offhand() {
        super("Offhand", Keyboard.KEY_NONE, Category.COMBAT, "Offhand Item");
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory) {
            if (switching) {
                swap_items(last_slot, 2);
                return;
            }

            float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();

            if (hp > totem_switch.getValDouble()) {
                if (switch_mode.getValString().equalsIgnoreCase("Crystal") && ModuleManager.getModuleByName("CrystalAura").isToggled()) {
                    swap_items(get_item_slot(Items.END_CRYSTAL), 0);
                    return;
                }
                if (gapple_in_hole.getValBoolean() && hp > gapple_hole_hp.getValDouble() && is_in_hole()) {
                    swap_items(get_item_slot(Items.GOLDEN_APPLE), delay.getValBoolean() ? 1 : 0);
                    return;
                }
                if (switch_mode.getValString().equalsIgnoreCase("Totem")) {
                    swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.getValBoolean() ? 1 : 0);
                    return;
                }
                if (switch_mode.getValString().equalsIgnoreCase("Gapple")) {
                    swap_items(get_item_slot(Items.GOLDEN_APPLE), delay.getValBoolean() ? 1 : 0);
                    return;
                }
                if (switch_mode.getValString().equalsIgnoreCase("Crystal") && !ModuleManager.getModuleByName("CrystalAura").isToggled()) {
                    swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), 0);
                    return;
                }
            } else {
                swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.getValBoolean() ? 1 : 0);
                return;
            }

            if (mc.player.getHeldItemOffhand().getItem() == Items.AIR) {
                swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.getValBoolean() ? 1 : 0);
            }

        }
    }

    public void swap_items(int slot, int step) {
        if (slot == -1) return;
        if (step == 0) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        }
        if (step == 1) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            switching = true;
            last_slot = slot;
        }
        if (step == 2) {
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            switching = false;
        }

        mc.playerController.updateController();
    }

    private boolean is_in_hole() {

        BlockPos player_block = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));

        return mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.south()).getBlock() != Blocks.AIR;
    }


    private int get_item_slot(Item input) {
        if (input == mc.player.getHeldItemOffhand().getItem()) return -1;
        for (int i = 36; i >= 0; i--) {
            final Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == input) {
                if (i < 9) {
                    if (input == Items.GOLDEN_APPLE) {
                        return -1;
                    }
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }

}
