package Method.Client.module.render;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.RenderBlockModelEvent;
import Method.Client.utils.Patcher.Events.RenderTileEntityEvent;
import Method.Client.utils.system.Connection;
import net.minecraft.block.*;
import net.minecraft.network.play.server.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static Method.Client.Main.setmgr;

public class NoBlockLag extends Module {

    public NoBlockLag() {
        super("NoBlockLag", Keyboard.KEY_NONE, Category.RENDER, "NoBlockLag");
    }

    Setting StopCollision = setmgr.add( new Setting("StopCollision", this, false));
    Setting antiSound = setmgr.add( new Setting("antiSound", this, true));
    Setting antiPiston = setmgr.add(new Setting("antiPiston", this, false));
    Setting antiSign = setmgr.add( new Setting("antiSign", this, false));
    Setting Storage = setmgr.add(new Setting("Storage", this, false));
    Setting Spawner = setmgr.add( new Setting("Spawner", this, false));
    Setting Beacon = setmgr.add(new Setting("Beacon", this, false));
    Setting MobHead = setmgr.add(new Setting("MobHead", this, false));
    Setting Falling = setmgr.add( new Setting("Falling", this, false));
    Setting Banner = setmgr.add( new Setting("Banner", this, false));
    Setting Gentity = setmgr.add( new Setting("Global Entity", this, false));
    Setting Object = setmgr.add( new Setting("objects", this, false));
    Setting Grass = setmgr.add( new Setting("Grass", this, false));
    Setting Painting = setmgr.add( new Setting("Paintings", this, false));
    Setting Fire = setmgr.add( new Setting("Fire", this, false));
    Setting NoChunk = setmgr.add( new Setting("NoChunk", this, false));
    ArrayList<BlockPos> modifiedsigns = new ArrayList<>();
    private Set<SoundEvent> sounds;

    @Override
    public void setup() {
        sounds = new HashSet<>();
    }


    @Override
    public void onRenderTileEntity(RenderTileEntityEvent event) {
        final Block block = event.getTileEntity().getBlockType().getBlockState().getBlock();
        if (BlockCheck(block))
            event.setCanceled(true);
    }

    @Override
    public void onRenderBlockModel(RenderBlockModelEvent event) {
        final Block block = event.getState().getBlock();
        if (BlockCheck(block))
            event.setCanceled(true);
    }

    @Override
    public void DrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
        if (StopCollision.getValBoolean())
            if (BlockCheck(mc.world.getBlockState(event.getTarget().getBlockPos()).getBlock()))
                event.setCanceled(true);
    }

    public boolean BlockCheck(Block block) {
        if (antiPiston.getValBoolean() && (block instanceof BlockPistonMoving || block instanceof BlockPistonExtension))
            return true;
        if (Storage.getValBoolean() && (block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockDispenser || block instanceof BlockFurnace || block instanceof BlockHopper || block instanceof BlockShulkerBox || block instanceof BlockBrewingStand))
            return true;
        if (Spawner.getValBoolean() && (block instanceof BlockMobSpawner))
            return true;
        if (Beacon.getValBoolean() && (block instanceof BlockBeacon))
            return true;
        if (Banner.getValBoolean() && block instanceof BlockBanner)
            return true;
        if (Fire.getValBoolean() && (block instanceof BlockFire))
            return true;
        if (Grass.getValBoolean() && (block instanceof BlockDoublePlant || block instanceof BlockTallGrass || block instanceof BlockDeadBush))
            return true;
        if (MobHead.getValBoolean() && (block instanceof BlockSkull))
            return true;
        return Falling.getValBoolean() && (block instanceof BlockFalling);
    }

    @Override
    public void onEnable() {
        modifiedsigns.clear();
        mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (antiSign.getValBoolean()) {
            for (final TileEntity tileEntity : mc.world.loadedTileEntityList) {
                if (tileEntity instanceof TileEntitySign) {
                    final TileEntitySign tileEntitySign = (TileEntitySign) tileEntity;
                    if (!modifiedsigns.contains(tileEntity.getPos())) {
                        modifiedsigns.add(tileEntity.getPos());
                        int lenght = 0;
                        for (ITextComponent line : tileEntitySign.signText) {
                            lenght = +line.getUnformattedText().length();
                        }
                        final String[] array = {"METHOD", "Sign length", "" + lenght + "", ""};
                        for (int i = 0; i < tileEntitySign.signText.length; ++i) {
                            tileEntitySign.signText[i] = new TextComponentString(array[i]);
                        }
                    } else {
                        for (ITextComponent line : tileEntitySign.signText) {
                            if (!line.getUnformattedText().startsWith("METHOD")) {
                                modifiedsigns.remove(tileEntity.getPos());
                            }
                            break;
                        }
                    }
                }
            }
        }
        super.onRenderWorldLast(event);
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (packet instanceof SPacketSpawnGlobalEntity && this.Gentity.getValBoolean() || (packet instanceof SPacketSpawnObject && this.Object.getValBoolean()) || (packet instanceof SPacketSpawnPainting && this.Painting.getValBoolean())) {
            return false;
        }
        if (antiSound.getValBoolean() && packet instanceof SPacketSoundEffect) {
            final SPacketSoundEffect sPacketSoundEffect = (SPacketSoundEffect) packet;
            if (sounds.contains(sPacketSoundEffect.getSound())) {
                return false;
            } else {
                sounds.add(sPacketSoundEffect.getSound());
            }
        }
        if (side == Connection.Side.IN) {
            return !(packet instanceof SPacketChunkData) || !NoChunk.getValBoolean();
        }
        return true;
    }

}
