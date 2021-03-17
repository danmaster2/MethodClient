package Method.Client.module.combat;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.visual.Executer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;

public class HoleEsp extends Module {

    /////////////////////
    Setting Radius = setmgr.add(new Setting("Radius", this, 8, 0, 32, true));
    Setting Void = setmgr.add(new Setting("Void", this, .85, 1, 1, .75));
    Setting Bedrock = setmgr.add(new Setting("Bedrock", this, .55, 1, 1, .75));
    Setting obby = setmgr.add(new Setting("obby", this, .22, 1, 1, .75));
    Setting Burrow = setmgr.add(new Setting("Burrow", this, .4, 1, 1, .75));
    Setting OwnHole = setmgr.add(new Setting("Ignore Own", this, true));
    Setting Timer = setmgr.add(new Setting("Timer", this, 250, 0, 500, true));
    Setting Mode = setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
    Setting LineWidth = setmgr.add(new Setting("LineWidth", this, 1, 0, 3, false));
    Setting BurrowDetect = setmgr.add(new Setting("Burrow Detect", this, true));

    Vec3i playerPos;
    TimerUtils timer = new TimerUtils();
    public final List<Hole> holes = new ArrayList<>();

    public HoleEsp() {
        super("HoleEsp", Keyboard.KEY_NONE, Category.COMBAT, "HoleEsp");
    }


    @Override
    public void onEnable() {
        Executer.init();
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (timer.isDelay((long) Timer.getValDouble())) {
            if (!Mode.getValString().equalsIgnoreCase("None")) {
                playerPos = new Vec3i(mc.player.posX, mc.player.posY, mc.player.posZ);
                this.holes.clear();
                Executer.execute(() -> {
                            if (BurrowDetect.getValBoolean())
                                for (Entity entity : mc.world.loadedEntityList)
                                    if (entity instanceof EntityPlayer) {
                                        EntityPlayer entityPlayer = (EntityPlayer) entity;
                                        if (isInBurrow(entityPlayer)) {
                                            BlockPos b = new BlockPos(entityPlayer);
                                            this.holes.add(new Hole(b.x, b.y, b.z, b, Hole.HoleTypes.Burrow, false));
                                        }
                                    }

                            for (int x = (int) (playerPos.getX() - Radius.getValDouble()); x < playerPos.getX() + Radius.getValDouble(); x++) {
                                for (int z = (int) (playerPos.getZ() - Radius.getValDouble()); z < playerPos.getZ() + Radius.getValDouble(); z++) {
                                    for (int y = (playerPos.getY() + 6); y > playerPos.getY() - 6; y--) {

                                        final BlockPos blockPos = new BlockPos(x, y, z);

                                        if (OwnHole.getValBoolean() && mc.player.getDistanceSq(blockPos) <= 1)
                                            continue;


                                        Hole.HoleTypes l_Type = isHoleValid(mc.world.getBlockState(blockPos), blockPos);

                                        if (l_Type != Hole.HoleTypes.None) {
                                            if (l_Type == Hole.HoleTypes.Void) {
                                                this.holes.add(new Hole(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos, Hole.HoleTypes.Void, true));
                                                continue;
                                            }
                                            final IBlockState downBlockState = mc.world.getBlockState(blockPos.down());
                                            if (downBlockState.getBlock() == Blocks.AIR) {
                                                final BlockPos downPos = blockPos.down();
                                                l_Type = isHoleValid(downBlockState, blockPos);
                                                if (l_Type != Hole.HoleTypes.None) {
                                                    this.holes.add(new Hole(downPos.getX(), downPos.getY(), downPos.getZ(), downPos, l_Type, true));
                                                }
                                            } else {
                                                this.holes.add(new Hole(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos, l_Type, false));
                                            }
                                        }
                                    }

                                }
                            }
                        }
                );
                timer.setLastMS();
            }
        }
    }

    private boolean isInBurrow(EntityPlayer entityPlayer) {
        BlockPos playerPos = new BlockPos(Math.floor(entityPlayer.posX + .5), entityPlayer.posY, Math.floor(entityPlayer.posZ + .5));

        return MC.world.getBlockState(playerPos).getBlock() == Blocks.OBSIDIAN
                || MC.world.getBlockState(playerPos).getBlock() == Blocks.ENDER_CHEST
                || MC.world.getBlockState(playerPos).getBlock() == Blocks.ANVIL;
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!Mode.getValString().equalsIgnoreCase("None")) {
            for (Hole hole : holes) {
                double renderPosX = hole.getX() - mc.getRenderManager().viewerPosX;
                double renderPosY = hole.getY() - mc.getRenderManager().viewerPosY;
                double renderPosZ = hole.getZ() - mc.getRenderManager().viewerPosZ;

                final AxisAlignedBB bb = new AxisAlignedBB(renderPosX, renderPosY, renderPosZ,
                        renderPosX + 1,
                        renderPosY + (hole.isTall() ? 2 : 1),
                        renderPosZ + 1);

                RenderBlock(Mode.getValString(), bb, hole.GetHoleType() == Hole.HoleTypes.Bedrock ? Bedrock.getcolor() : hole.GetHoleType() == Hole.HoleTypes.Obsidian ? obby.getcolor() : hole.GetHoleType() == Hole.HoleTypes.Burrow ? Burrow.getcolor() : Void.getcolor(), LineWidth.getValDouble());

            }
        }
    }

    public static Hole.HoleTypes isHoleValid(IBlockState blockState, BlockPos blockPos) {
        if (blockState.getBlock() != Blocks.AIR)
            return Hole.HoleTypes.None;

        if ((blockState.getBlock() == Blocks.AIR) && blockPos.y == 0)
            return Hole.HoleTypes.Void;

        if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR)
            return Hole.HoleTypes.None;

        if (mc.world.getBlockState(blockPos.up(2)).getBlock() != Blocks.AIR)
            return Hole.HoleTypes.None;

        if (mc.world.getBlockState(blockPos.down()).getBlock() == Blocks.AIR)

            return Hole.HoleTypes.None;


        final BlockPos[] touchingBlocks = new BlockPos[]
                {blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()};

        // True is Bedrock false if Obby
        boolean AllBedrock = true;

        for (BlockPos touching : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(touching);
            if ((touchingState.getBlock() != Blocks.AIR) && touchingState.isFullBlock()) {
                if (touchingState.getBlock() == Blocks.OBSIDIAN) {
                    AllBedrock = false;
                    continue;
                }
                if (touchingState.getBlock() != Blocks.BEDROCK)
                    return Hole.HoleTypes.None;
            } else {
                return Hole.HoleTypes.None;
            }
        }
        return AllBedrock ? Hole.HoleTypes.Bedrock : Hole.HoleTypes.Obsidian;
    }

}

class Hole extends Vec3i {
    private final boolean tall;
    private HoleTypes HoleType;

    public enum HoleTypes {
        None,
        Obsidian,
        Bedrock,
        Void,
        Burrow
    }

    public Hole(int x, int y, int z, final BlockPos pos, HoleTypes p_Type, boolean tall) {
        super(x, y, z);
        this.tall = tall;
        SetHoleType(p_Type);
    }

    public boolean isTall() {
        return tall;
    }


    public HoleTypes GetHoleType() {
        return HoleType;
    }

    public void SetHoleType(HoleTypes holeType) {
        HoleType = holeType;
    }
}

