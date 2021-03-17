package Method.Client.module.combat;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.OptionalInt;

import static Method.Client.Main.setmgr;

public class AutoTotem extends Module {

    private final int OFFHAND_SLOT = 45;

    Setting allowGui = setmgr.add(new Setting("Use in Gui", this, true));
    Setting health = setmgr.add(new Setting("health for switch", this, 10, 0, 35, true));
    Setting needheal = setmgr.add(new Setting("Use Health", this, false));
    Setting predict = setmgr.add(new Setting("Predict", this, false));

    private boolean predicted = false;
    public AutoTotem() {
        super("Auto Totem", Keyboard.KEY_NONE, Category.COMBAT, "Auto Totem");
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if ((getOffhand().getItem() == Items.TOTEM_OF_UNDYING)) {
            return; // if there's an item in offhand slot
        }
        if (mc.currentScreen != null && !allowGui.getValBoolean()) {
            return; // if in inventory
        }

        if (needheal.getValBoolean()) {  // If player health is used
            if (!(mc.player.getHealth() >= health.getValDouble())) {  // if health is lower switch
                findItem().ifPresent(slot -> {
                    invPickup(slot);
                    invPickup(OFFHAND_SLOT);
                });
            }
        } else if ((predict.getValBoolean())) { // else if Predict is on
            if (predicted)
                findItem().ifPresent(slot -> {
                    invPickup(slot);
                    invPickup(OFFHAND_SLOT);
                });
        } else  // if not health and not Predict aka default
            findItem().ifPresent(slot -> {
                invPickup(slot);
                invPickup(OFFHAND_SLOT);
            });

        if (predicted) {
            ChatUtils.warning("Predicted Totem!");
            predicted = false;
        }
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (predict.getValBoolean())
            if (side == Connection.Side.IN && packet instanceof SPacketUpdateHealth) {
                SPacketUpdateHealth packet2 = (SPacketUpdateHealth) packet;
                if (packet2.getHealth() <= 0) predicted = true;
            }
        return true;
    }

    private void invPickup(final int slot) {
        MC.playerController.windowClick(0, slot, 0, ClickType.PICKUP, MC.player);
    }

    private static OptionalInt findItem() {
        for (int i = 9; i <= 44; i++)
            if (MC.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.TOTEM_OF_UNDYING)
                return OptionalInt.of(i);
        return OptionalInt.empty();
    }

    private static ItemStack getOffhand() {
        return MC.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
    }

}
