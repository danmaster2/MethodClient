package Method.Client.module.render;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.SeedViewer.WorldLoader;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;
import static Method.Client.utils.visual.RenderUtils.Standardbb;

public class SeedViewer extends Module {
    Setting OverlayColor = setmgr.add(new Setting("OverlayColor", this, 0, 1, 1, 1));
    Setting Mode = setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
    Setting LineWidth = setmgr.add(new Setting("LineWidth", this, 1, 0, 3, false));
    Setting BlockLimit = setmgr.add(new Setting("BlockLimit", this, 200, 0, 5000, false));
    Setting Fallingblock = setmgr.add(new Setting("Falling block", this, false));
    Setting Liquid = setmgr.add(new Setting("Liquid", this, false));
    Setting Tree = setmgr.add(new Setting("Tree", this, false));
    Setting Bush = setmgr.add(new Setting("Bush", this, false));
    Setting Distance = setmgr.add(new Setting("Distance", this, 6, 0, 15, true));
    Setting LavaMix = setmgr.add(new Setting("LavaMix", this, false));
    Setting FalsePositive = setmgr.add(new Setting("FalsePositive", this, false));
    Setting GrassSpread = setmgr.add(new Setting("GrassSpread", this, false));

    @Override
    public void setup() {
        executor = Executors.newSingleThreadExecutor();
        executor2 = Executors.newSingleThreadExecutor();
    }

    private static ExecutorService executor;
    private static ExecutorService executor2;
    public int currentdis = 0;

    public SeedViewer() {
        super("SeedViewer", Keyboard.KEY_NONE, Category.RENDER, "SeedViewer");
    }

    private ArrayList<ChunkData> chunks = new ArrayList<>();
    private ArrayList<int[]> tobesearch = new ArrayList<>();
    private final TimerUtils timer = new TimerUtils();

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (timer.isDelay(500)) {
            if (mc.player.dimension != currentdis) {
                ChatUtils.warning("You must reenable on dimension change!");
                this.toggle();
            }
            searchViewDistance();
            runviewdistance();
            timer.setLastMS();
        }
        int[] remove = null;
        for (int[] vec2d : tobesearch) {
            remove = vec2d;
            executor.execute(() -> WorldLoader.CreateChunk(vec2d[0], vec2d[1], mc.player.dimension));
        }
        tobesearch.remove(remove);
    }


    @Override
    public void onEnable() {
        if (mc.isSingleplayer()) {
            ChatUtils.warning("Only for multiplayer");
            this.toggle();
        }
        if (WorldLoader.seed == 44776655) {
            ChatUtils.message("Set Seed using the" + TextFormatting.GOLD + " @WorldSeed" + TextFormatting.RESET + " Command");
            this.toggle();
            return;
        }
        currentdis = mc.player.dimension;
        executor = Executors.newSingleThreadExecutor();
        executor2 = Executors.newSingleThreadExecutor();
        WorldLoader.setup();
        ChatUtils.warning("Still Working on this.");
        chunks = new ArrayList<>();
        searchViewDistance();
    }

    private void searchViewDistance() {
        executor.execute(() -> {
            for (int x = mc.player.chunkCoordX - (int) Distance.getValDouble(); x <= mc.player.chunkCoordX + (int) Distance.getValDouble(); x++) {
                for (int z = mc.player.chunkCoordZ - (int) Distance.getValDouble(); z <= mc.player.chunkCoordZ + (int) Distance.getValDouble(); z++) {
                    if (havenotsearched(x, z))
                        if (mc.world.isChunkGeneratedAt(x, z)) {
                            boolean found = false;
                            for (int[] vec2d : tobesearch) {
                                if ((int) vec2d[0] == x && (int) vec2d[1] == z) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found)
                                tobesearch.add(new int[]{x, z});
                        }
                }
            }
        });
    }

    private void runviewdistance() {
        for (int x = mc.player.chunkCoordX - (int) Distance.getValDouble(); x <= mc.player.chunkCoordX + (int) Distance.getValDouble(); x++) {
            for (int z = mc.player.chunkCoordZ - (int) Distance.getValDouble(); z <= mc.player.chunkCoordZ + (int) Distance.getValDouble(); z++) {
                if (mc.world.isChunkGeneratedAt(x, z)) {
                    if (WorldLoader.fakeworld.isChunkGeneratedAt(x, z) && WorldLoader.fakeworld.isChunkGeneratedAt(x + 1, z) && WorldLoader.fakeworld.isChunkGeneratedAt(x, z + 1)
                            && WorldLoader.fakeworld.isChunkGeneratedAt(x + 1, z + 1)) {
                        if (havenotsearched(x, z)) {
                            ChunkData data = new ChunkData(new ChunkPos(x, z), false);
                            searchChunk(mc.world.getChunk(x, z), data);
                            chunks.add(data);
                        }
                    }
                }
            }
        }
    }

    private boolean havenotsearched(int x, int z) {
        for (ChunkData chunk : chunks) {
            if (chunk.chunkPos.x == x && chunk.chunkPos.z == z) {
                return false;
            }
        }
        return true;
    }


    private void searchChunk(Chunk chunk, ChunkData data) {
        executor2.execute(() -> {
            try {
                for (int x = chunk.getPos().getXStart(); x <= chunk.getPos().getXEnd(); x++) {
                    for (int z = chunk.getPos().getZStart(); z <= chunk.getPos().getZEnd(); z++) {
                        for (int y = 0; y < 255; y++) {
                            if (BlockFast(new BlockPos(x, y, z), WorldLoader.fakeworld.getBlockState(new BlockPos(x, y, z)).getBlock(), chunk.getBlockState(x, y, z).getBlock()))
                                data.blocks.add(new BlockPos(x, y, z));
                        }
                    }
                }
                data.Searched = true;
            } catch (Exception ignored) {
            }
        });
    }


    private boolean BlockFast(BlockPos blockPos, Block FakeChunk, Block RealChunk) {
        if (RealChunk instanceof BlockSnow)
            return false;
        if (FakeChunk instanceof BlockSnow)
            return false;
        if (RealChunk instanceof BlockVine)
            return false;
        if (FakeChunk instanceof BlockVine)
            return false;
        if (!Fallingblock.getValBoolean()) {
            if (RealChunk instanceof BlockFalling)
                return false;
            if (FakeChunk instanceof BlockFalling)
                return false;
        }
        if (!Liquid.getValBoolean()) {
            if (RealChunk instanceof BlockLiquid)
                return false;
            if (FakeChunk instanceof BlockLiquid)
                return false;
            if (mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockLiquid)
                return false;
            if (mc.world.getBlockState(blockPos.down(2)).getBlock() instanceof BlockLiquid)
                return false;
        }
        if (!Tree.getValBoolean()) {
            if (FakeChunk instanceof BlockGrass)
                if (Treeroots(blockPos))
                    return false;
            if (RealChunk instanceof BlockLog || RealChunk instanceof BlockLeaves)
                return false;
            if (FakeChunk instanceof BlockLog || FakeChunk instanceof BlockLeaves)
                return false;
        }
        if (!GrassSpread.getValBoolean()) {
            if (RealChunk instanceof BlockGrass && FakeChunk instanceof BlockDirt)
                return false;
            if (RealChunk instanceof BlockDirt && FakeChunk instanceof BlockGrass)
                return false;
        }
        if (!Bush.getValBoolean()) {
            if (RealChunk instanceof BlockBush)
                return false;
            if (RealChunk instanceof BlockReed)
                return false;
            if (FakeChunk instanceof BlockBush)
                return false;
        }
        if (!LavaMix.getValBoolean()) {
            if (RealChunk instanceof BlockObsidian || RealChunk.equals(Blocks.COBBLESTONE))
                if (Lavamix(blockPos))
                    return false;
        }
        if (!FalsePositive.getValBoolean()) {
            if (FakeChunk instanceof BlockOre && (RealChunk instanceof BlockStone || RealChunk instanceof BlockMagma || RealChunk instanceof BlockNetherrack || RealChunk instanceof BlockDirt))
                return false;
            if (RealChunk instanceof BlockOre && (FakeChunk instanceof BlockStone || FakeChunk instanceof BlockMagma || FakeChunk instanceof BlockNetherrack || FakeChunk instanceof BlockDirt))
                return false;

            // Redstone ore is not in ore list???????
            if (FakeChunk instanceof BlockRedstoneOre && (RealChunk instanceof BlockStone || RealChunk instanceof BlockDirt))
                return false;
            if (RealChunk instanceof BlockRedstoneOre && (FakeChunk instanceof BlockStone || FakeChunk instanceof BlockDirt))
                return false;

            if (FakeChunk instanceof BlockGlowstone && (RealChunk instanceof BlockAir))
                return false;
            if (RealChunk instanceof BlockGlowstone && (FakeChunk instanceof BlockAir))
                return false;

            if (FakeChunk instanceof BlockMagma && RealChunk instanceof BlockNetherrack)
                return false;
            if (RealChunk instanceof BlockMagma && FakeChunk instanceof BlockNetherrack)
                return false;
            if (RealChunk instanceof BlockFire || FakeChunk instanceof BlockFire)
                return false;
            if (RealChunk instanceof BlockOre && FakeChunk instanceof BlockOre)
                return false;
            if (RealChunk.getLocalizedName().equals(Blocks.MONSTER_EGG.getLocalizedName()) && FakeChunk instanceof BlockStone)
                return false;
            if ((FakeChunk instanceof BlockStone && RealChunk instanceof BlockDirt) || (FakeChunk instanceof BlockDirt && RealChunk instanceof BlockStone))
                return false;
            if (!(FakeChunk instanceof BlockAir) && RealChunk instanceof BlockAir)
                if (!mc.world.getBlockState(blockPos).getBlock().getLocalizedName().equals(RealChunk.getLocalizedName()))
                    return false;
        }

        if (!FakeChunk.getLocalizedName().equals(RealChunk.getLocalizedName())) {
            return true;
        }
        return false;
    }

    public boolean Treeroots(BlockPos b) {
        if (mc.world.getBlockState(b.up()).getBlock() instanceof BlockLog) {
            return true;
        }
        return false;
    }

    public boolean Lavamix(BlockPos b) {
        if (mc.world.getBlockState(b.up()).getBlock() instanceof BlockLiquid) {
            return true;
        }
        if (mc.world.getBlockState(b.down()).getBlock() instanceof BlockLiquid) {
            return true;
        }
        if (mc.world.getBlockState(b.add(1, 0, 0)).getBlock() instanceof BlockLiquid) {
            return true;
        }
        if (mc.world.getBlockState(b.add(0, 0, 1)).getBlock() instanceof BlockLiquid) {
            return true;
        }
        if (mc.world.getBlockState(b.add(-1, 0, 0)).getBlock() instanceof BlockLiquid) {
            return true;
        }
        if (mc.world.getBlockState(b.add(0, 0, -1)).getBlock() instanceof BlockLiquid) {
            return true;
        }
        return false;
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        try {
            int blocklimit = 0;
            ArrayList<ChunkData> Remove = new ArrayList<>();
            for (ChunkData chunk : chunks) {
                if (chunk.Searched) {
                    if (mc.player.getDistance(chunk.chunkPos.getXEnd(), 100, chunk.chunkPos.getZEnd()) > 2000)
                        Remove.add(chunk);
                    for (BlockPos block : chunk.blocks) {
                        if (blocklimit > BlockLimit.getValDouble())
                            break;
                        RenderBlock(Mode.getValString(), Standardbb(new BlockPos(block.x, block.y, block.z)), OverlayColor.getcolor(), LineWidth.getValDouble());
                        blocklimit++;
                    }

                }
            }
            chunks.removeAll(Remove);
        } catch (Exception ignored) {
        }
        super.onRenderWorldLast(event);
    }


    public static class ChunkData {
        private boolean Searched;

        public List<BlockPos> getBlocks() {
            return blocks;
        }

        public final List<BlockPos> blocks = new ArrayList<>();
        private ChunkPos chunkPos;

        public ChunkData(ChunkPos chunkPos, boolean Searched) {
            this.chunkPos = chunkPos;
            this.Searched = Searched;
        }
    }

}
