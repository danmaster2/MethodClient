
package Method.Client.module.misc;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.module.render.NameTags;
import Method.Client.utils.visual.RenderUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static Method.Client.Main.setmgr;

public class Shulkerspy extends Module {

    /////////////////////

    public Shulkerspy() {
        super("Shulkerspy", Keyboard.KEY_NONE, Category.MISC, "See others last held Shulker");
    }

    public static ConcurrentHashMap<String, TileEntityShulkerBox> shulkerMap = new ConcurrentHashMap<>();

    public Setting Mode = setmgr.add(new Setting(" Mode", this, "Dynamic", "Dynamic", "Static"));
    public Setting X = setmgr.add(new Setting("X", this, 1, 0, 1000, false, Mode, "Static", 2));
    public Setting Y = setmgr.add(new Setting("Y", this, 1, 0, 1000, false, Mode, "Static", 3));
    public Setting Scale = setmgr.add(new Setting("Scale", this, 1, 0, 4, false, Mode, "Dynamic", 2));
    public Setting Background = setmgr.add(new Setting("Background", this, true));
    public Setting BgColor = setmgr.add(new Setting("BgColor", this, .22, .88, .22, .22, Background, 2));

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (Mode.getValString().equalsIgnoreCase("Dynamic"))
            for (Entity object : mc.world.loadedEntityList) {
                if (object instanceof EntityOtherPlayerMP)
                    if (shulkerMap.containsKey(object.getName().toLowerCase())) {
                        IInventory inv = Shulkerspy.shulkerMap.get(object.getName().toLowerCase());
                        DrawBox((EntityLivingBase) object, inv);
                    }
            }
    }

    @Override
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (Mode.getValString().equalsIgnoreCase("Static")) {
            int Players = 0;
            for (Entity object : mc.world.loadedEntityList) {
                if (object instanceof EntityOtherPlayerMP)
                    if (shulkerMap.containsKey(object.getName().toLowerCase())) {
                        IInventory inv = Shulkerspy.shulkerMap.get(object.getName().toLowerCase());
                        DrawSbox(inv, Players, (EntityLivingBase) object);
                        Players++;
                    }
            }
        }
    }

    private void DrawSbox(IInventory inv, int players, EntityLivingBase object) {
        RenderHelper.enableGUIStandardItemLighting();
        if (Background.getValBoolean()) {
            RenderUtils.drawRectDouble(X.getValDouble(), Y.getValDouble() - (players * 100), X.getValDouble() + 88 + 60, Y.getValDouble() + 50 - (players * 100), BgColor.getcolor()); // background
        }
        mc.fontRenderer.drawStringWithShadow(object.getName() + "'s Shulker", (float) X.getValDouble() + 45, (float) Y.getValDouble() - 10, -1);

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack = inv.getStackInSlot(i);
            int offsetX = (int) (X.getValDouble() + (i % 9) * 16);
            int offsetY = (int) (Y.getValDouble() + (i / 9) * 16) - (players * 100);
            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX, offsetY, null);
        }
        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = 0.0F;
    }

    public void DrawBox(EntityLivingBase e, IInventory inv) {
        int morey = ModuleManager.getModuleByName("NameTags").isToggled() ? -6 : 0;

        double scale = Math.max(Scale.getValDouble() * (mc.player.getDistance(e) / 20), 2);
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            NameTags.drawItem(e.posX, e.posY + e.height + ((0.75) * scale), e.posZ, -2.5 + ((i % 9)), -((i / 9) * 1.2) - morey, scale, inv.getStackInSlot(i));
        }
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        List<Entity> valids = mc.world.getLoadedEntityList()
                .stream()
                .filter(en -> en instanceof EntityOtherPlayerMP)
                .filter(mp -> ((EntityOtherPlayerMP) mp).getHeldItemMainhand().getItem() instanceof ItemShulkerBox)
                .collect(Collectors.toList());


        for (Entity valid : valids) {
            EntityOtherPlayerMP mp = (EntityOtherPlayerMP) valid;
            TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
            ItemShulkerBox shulker = (ItemShulkerBox) mp.getHeldItemMainhand().getItem();
            entityBox.blockType = shulker.getBlock();
            entityBox.setWorld(mc.world);
            ItemStackHelper.loadAllItems(Objects.requireNonNull(mp.getHeldItemMainhand().getTagCompound()).getCompoundTag("BlockEntityTag"), entityBox.items);
            entityBox.readFromNBT(mp.getHeldItemMainhand().getTagCompound().getCompoundTag("BlockEntityTag"));
            entityBox.setCustomName(mp.getHeldItemMainhand().hasDisplayName() ? mp.getHeldItemMainhand().getDisplayName() : mp.getName() + "'s current shulker box");
            shulkerMap.put(mp.getName().toLowerCase(), entityBox);
        }

    }

}
