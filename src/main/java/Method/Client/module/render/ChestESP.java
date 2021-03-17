package Method.Client.module.render;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.block.BlockChest;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;
import static Method.Client.utils.visual.RenderUtils.Standardbb;

public class ChestESP extends Module {

    public boolean shouldInform = true;
    Setting Chest = setmgr.add(new Setting("Chest", this, true));
    Setting Shulker = setmgr.add(new Setting("Shulker", this, true));
    Setting Trappedchest = setmgr.add(new Setting("Trapped", this, true));
    Setting EnderChest = setmgr.add(new Setting("Ender", this, true));
    Setting MinecartChest = setmgr.add(new Setting("Minecart", this, true));
    Setting ChestColor = setmgr.add(new Setting("ChestC", this, .22, 1, 1, .5, Chest, 7));
    Setting TrappedchestColor = setmgr.add(new Setting("TrappedC", this, 0, 1, 1, .5, Trappedchest, 7));
    Setting EnderChestColor = setmgr.add(new Setting("EnderC", this, .44, 1, 1, .5, EnderChest, 7));
    Setting MinecartChestColor = setmgr.add(new Setting("MinecartC", this, .88, 1, 1, .5, MinecartChest, 7));
    Setting ShulkerColor = setmgr.add(new Setting("ShulkerColor", this, .96, 1, 1, .5, MinecartChest, 7));
    Setting Mode = setmgr.add(new Setting("Draw Mode", this, "Outline", BlockEspOptions()));
    Setting LineWidth = setmgr.add(new Setting("LineWidth", this, 1, 0, 3, false));
    Setting ChangeSeen = setmgr.add(new Setting("Has been opened", this, true));
    Setting OpenedColor = setmgr.add(new Setting("OpenedColor", this, 0, 1, 1, .5, ChangeSeen, 7));
    Setting Notify = setmgr.add(new Setting("Notify", this, 50, 100, 2000, true));
    Setting maxChests = setmgr.add(new Setting("maxChests", this, 1000, 100, 2000, true));


    ArrayList<BlockPos> Openedpos = new ArrayList<>();

    public ChestESP() {
        super("ChestESP", Keyboard.KEY_NONE, Category.RENDER, "ChestESP");
    }

    @Override
    public void onEnable() {
        shouldInform = true;
        Openedpos.clear();
        super.onEnable();
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {

        int chests = 0;

        for (TileEntity entity : mc.world.loadedTileEntityList) {
            if (entity instanceof TileEntityChest) {
                if (chests >= maxChests.getValDouble() && shouldInform)
                    break;
                chests++;
                TileEntityChest chest = (TileEntityChest) entity;

                //EntityPlayerMP.chest


                if (ChangeSeen.getValBoolean()) {
                    if (chest.numPlayersUsing > 0)
                        Openedpos.add(chest.getPos());
                    if (Openedpos.contains(chest.getPos())) {
                        RenderBlock(Mode.getValString(), Standardbb(chest.getPos()), OpenedColor.getcolor(), LineWidth.getValDouble());
                        continue;
                    }
                }
                if (chest.getChestType() == BlockChest.Type.TRAP && Trappedchest.getValBoolean()) {
                    RenderBlock(Mode.getValString(), Standardbb(chest.getPos()), TrappedchestColor.getcolor(), LineWidth.getValDouble());
                    continue;
                }
                if (Chest.getValBoolean()) {
                    RenderBlock(Mode.getValString(), Standardbb(chest.getPos()), ChestColor.getcolor(), LineWidth.getValDouble());
                    continue;
                }

            }
            if (entity instanceof TileEntityEnderChest && EnderChest.getValBoolean()) {
                chests++;
                RenderBlock(Mode.getValString(), Standardbb(entity.getPos()), EnderChestColor.getcolor(), LineWidth.getValDouble());
            }
            if (entity instanceof TileEntityShulkerBox && Shulker.getValBoolean()) {
                chests++;
                RenderBlock(Mode.getValString(), Standardbb(entity.getPos()), ShulkerColor.getcolor(), LineWidth.getValDouble());
            }
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (chests >= maxChests.getValDouble())
                break;

            if (entity instanceof EntityMinecartChest && MinecartChest.getValBoolean()) {
                chests++;
                RenderBlock(Mode.getValString(), Standardbb(entity.getPosition()), MinecartChestColor.getcolor(), LineWidth.getValDouble());
            }
        }

        if (shouldInform) {
            if (chests >= Notify.getValDouble()) {
                ChatUtils.warning("Found " + chests + " chests.");
                mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f));
            }
            if (chests >= maxChests.getValDouble()) {

                ChatUtils.warning("To prevent lag, it will only show the first " + maxChests.getValDouble() + " chests.");
                shouldInform = false;
            }
        } else if (chests < maxChests.getValDouble()) {
            shouldInform = true;
        }
        super.onRenderWorldLast(event);
    }

    @Nullable
    public ILockableContainer getLockableContainer(World worldIn, BlockPos pos) {
        return this.getContainer(worldIn, pos, false);
    }

    @Nullable
    public ILockableContainer getContainer(World worldIn, BlockPos pos, boolean allowBlocking) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (!(tileentity instanceof TileEntityChest)) {
            return null;
        } else {
            ILockableContainer ilockablecontainer = (TileEntityChest) tileentity;
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                BlockPos blockpos = pos.offset(enumfacing);
                TileEntity tileentity1 = worldIn.getTileEntity(blockpos);
                if (tileentity1 instanceof TileEntityChest) {
                    if (enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH) {
                        ilockablecontainer = new InventoryLargeChest("container.chestDouble", ilockablecontainer, (TileEntityChest) tileentity1);
                    } else {
                        ilockablecontainer = new InventoryLargeChest("container.chestDouble", (TileEntityChest) tileentity1, ilockablecontainer);
                    }
                }
            }
            return ilockablecontainer;
        }
    }


}




