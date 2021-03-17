package Method.Client.module.combat;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection.Side;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static Method.Client.Main.setmgr;

public class AutoArmor extends Module {

    private int timer;
    Setting useEnchantments = setmgr.add(new Setting("Enchantments", this, true));
    Setting swapWhileMoving = setmgr.add(new Setting("SwapWhileMoving", this, true));
    Setting delay = setmgr.add(new Setting("Delay", this, 1, 0, 5, true));
    Setting nocurse = setmgr.add(new Setting("No Binding", this, true));
    Setting Elytra = setmgr.add(new Setting("Elytra Over Chest", this, true));
    Setting Edam = setmgr.add(new Setting("Elytra Damage", this, 2, 0, 320, true));
    boolean ElytraSwitch = false;

    public AutoArmor() {
        super("Auto Armor", Keyboard.KEY_NONE, Category.COMBAT, "Puts on any Armor");
    }

    @Override
    public void onEnable() {
        ElytraSwitch = false;
        this.timer = 0;
        super.onEnable();
    }

    @Override
    public void onClientTick(ClientTickEvent event) {
        if (timer > 0) {
            timer--;
            return;
        }
        if (Wrapper.INSTANCE.mc().currentScreen instanceof GuiContainer
                && !(Wrapper.INSTANCE.mc().currentScreen instanceof InventoryEffectRenderer))
            return;

        InventoryPlayer inventory = mc.player.inventory;

        if (!swapWhileMoving.getValBoolean()
                && (mc.player.movementInput.moveForward != 0
                || mc.player.movementInput.moveStrafe != 0))
            return;


        int[] bestArmorSlots = new int[4];
        int[] bestArmorValues = new int[4];


        for (int type = 0; type < 4; type++) {
            bestArmorSlots[type] = -1;

            ItemStack stack = inventory.armorItemInSlot(type);
            if (Elytra.getValBoolean() && type == 2)
                if (stack.getItem() instanceof ItemElytra) {
                    if (stack.isEmpty())
                        ElytraSwitch = false;
                    if (stack.getItem().getDamage(stack) > stack.getItem().getMaxDamage(stack) - Edam.getValDouble()) {
                        ElytraSwitch = false;
                    }
                    continue;
                }

            if (isNullOrEmpty(stack)
                    || !(stack.getItem() instanceof ItemArmor))
                continue;
            ItemArmor item = (ItemArmor) stack.getItem();
            bestArmorValues[type] = getArmorValue(item, stack);
        }


        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.getItem() instanceof ItemElytra && Elytra.getValBoolean() && !ElytraSwitch) {
                if (stack.getItem().getDamage(stack) > stack.getItem().getMaxDamage(stack) - Edam.getValDouble()) {
                    continue;
                }
                bestArmorSlots[2] = slot;
                ElytraSwitch = true;
                continue;
            }
            if (isNullOrEmpty(stack)
                    || !(stack.getItem() instanceof ItemArmor))
                continue;
            if (nocurse.getValBoolean())
                if (EnchantmentHelper.hasBindingCurse(stack))
                    continue;

            ItemArmor item = (ItemArmor) stack.getItem();
            int armorType = item.armorType.getIndex();
            int armorValue = getArmorValue(item, stack);

            if (armorValue > bestArmorValues[armorType]) {
                bestArmorSlots[armorType] = slot;
                bestArmorValues[armorType] = armorValue;
            }
        }


        ArrayList<Integer> types = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(types);
        for (int type : types) {

            int slot = bestArmorSlots[type];
            if (slot == -1)
                continue;
            if (inventory.armorItemInSlot(type).getItem() instanceof ItemElytra && Elytra.getValBoolean()) {
                ItemStack stack = inventory.armorItemInSlot(type);
                if (stack.getItem().getDamage(stack) > stack.getItem().getMaxDamage(stack) - Edam.getValDouble()) {
                    Wrapper.INSTANCE.mc().playerController.windowClick(0, 8 - type, 0, ClickType.QUICK_MOVE,
                            mc.player);
                    ElytraSwitch = false;
                } else continue;
            }
            ItemStack oldArmor = inventory.armorItemInSlot(type);
            if (!isNullOrEmpty(oldArmor)
                    && inventory.getFirstEmptyStack() == -1)
                continue;

            if (slot < 9)
                slot += 36;

            if (!isNullOrEmpty(oldArmor))
                Wrapper.INSTANCE.mc().playerController.windowClick(0, 8 - type, 0, ClickType.QUICK_MOVE,
                        mc.player);


            Wrapper.INSTANCE.mc().playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE,
                    mc.player);

            break;
        }
        super.onClientTick(event);
    }

    @Override
    public boolean onPacket(Object packet, Side side) {
        if (side == Side.OUT && packet instanceof CPacketClickWindow) {
            this.timer = (int) delay.getValDouble();
        }
        return true;
    }

    public static boolean isNullOrEmpty(ItemStack stack) {
        return stack == null || stack.isEmpty();
    }

    int getArmorValue(ItemArmor item, ItemStack stack) {
        int armorPoints = item.damageReduceAmount;
        int prtPoints = 0;
        int armorToughness = (int) item.toughness;
        int armorType = item.getArmorMaterial().getDamageReductionAmount(EntityEquipmentSlot.LEGS);

        if (useEnchantments.getValBoolean()) {
            Enchantment protection = Enchantments.PROTECTION;
            int prtLvl = EnchantmentHelper.getEnchantmentLevel(protection, stack);

            DamageSource dmgSource = DamageSource.causePlayerDamage(mc.player);
            prtPoints = protection.calcModifierDamage(prtLvl, dmgSource);
        }

        return armorPoints * 5 + prtPoints * 3 + armorToughness + armorType;
    }

}
