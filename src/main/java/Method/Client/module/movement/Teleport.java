
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;
import static Method.Client.utils.visual.RenderUtils.Standardbb;

public class Teleport extends Module {

    Setting mode = setmgr.add(new Setting("Tp Mode", this, "Reach", "Reach", "Flight"));
    Setting math = setmgr.add(new Setting("Speed", this, false));
    Setting Path = setmgr.add(new Setting("Path", this, 0, 1, 1, .22));
    Setting Land = setmgr.add(new Setting("Land", this, .22, 1, 1, .22));
    Setting TpMode = setmgr.add(new Setting("Tp Draw", this, "Outline", BlockEspOptions()));
    Setting LineWidth = setmgr.add(new Setting("LineWidth", this, 1, 0, 3, false));

    public boolean passPacket = false;
    private BlockPos teleportPosition = null;
    private boolean canDraw;
    private int delay;
    float reach = 0;

    public Teleport() {
        super("Teleport", Keyboard.KEY_NONE, Category.MOVEMENT, "Teleport around");
    }


    @Override
    public void onEnable() {
        if (mode.getValString().equalsIgnoreCase("Reach")) {
            reach = (float) mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mode.getValString().equalsIgnoreCase("Flight")) {
            mc.player.noClip = false;
            passPacket = false;
            teleportPosition = null;
            return;
        }
        canDraw = false;
        mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(500);

        super.onDisable();
    }


    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.OUT && mode.getValString().equalsIgnoreCase("Flight")) {
            if (packet instanceof CPacketPlayer) {
                return passPacket;
            }
        }
        return true;
    }


    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("Flight")) {
            RayTraceResult object = Wrapper.INSTANCE.mc().objectMouseOver;
            if (object == null) {
                return;
            }
            EntityPlayerSP player = mc.player;
            GameSettings settings = Wrapper.INSTANCE.mcSettings();
            if (!passPacket) {
                if (settings.keyBindAttack.isKeyDown() && object.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (Utils.isBlockMaterial(object.getBlockPos(), Blocks.AIR)) {
                        return;
                    }
                    teleportPosition = object.getBlockPos();
                    passPacket = true;
                }
                return;
            }
            player.noClip = false;
            if (settings.keyBindSneak.isKeyDown() && player.onGround) {
                Mathteleport();

            }
            return;
        }
        if ((!Mouse.isButtonDown(0) && Wrapper.INSTANCE.mc().inGameHasFocus || !Wrapper.INSTANCE.mc().inGameHasFocus) && mc.player.getItemInUseCount() == 0) {
            mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(100);
            canDraw = true;
        } else {
            canDraw = false;
            mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(reach);

        }
        if (teleportPosition != null && delay == 0 && Mouse.isButtonDown(1)) {
            Mathteleport();
            delay = 5;
        }

        if (delay > 0) {
            delay--;
        }
        super.onClientTick(event);
    }

    private void Mathteleport() {
        if (math.getValBoolean()) {
            double[] playerPosition = new double[]{mc.player.posX, mc.player.posY, mc.player.posZ};
            double[] blockPosition = new double[]{teleportPosition.getX() + 0.5F, teleportPosition.getY() + getOffset(mc.world.getBlockState(teleportPosition).getBlock(), teleportPosition) + 1.0F, teleportPosition.getZ() + 0.5F};

            Utils.teleportToPosition(playerPosition, blockPosition, 0.25D, 0.0D, true, true);
            mc.player.setPosition(blockPosition[0], blockPosition[1], blockPosition[2]);

            teleportPosition = null;
        } else {
            double x = teleportPosition.getX();
            double y = teleportPosition.getY() + 1;
            double z = teleportPosition.getZ();

            mc.player.setPosition(x, y, z);
            for (int i = 0; i < 1; i++) {
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(x, y, z, mc.player.onGround));
            }
        }
    }

    @Override
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!mode.getValString().equalsIgnoreCase("Flight")) {
            return;
        }
        EntityPlayerSP player = mc.player;
        GameSettings settings = Wrapper.INSTANCE.mcSettings();
        if (!passPacket) {
            player.noClip = true;
            player.fallDistance = 0;
            player.onGround = true;
            player.capabilities.isFlying = false;
            player.motionX = 0.0F;
            player.motionY = 0.0F;
            player.motionZ = 0.0F;
            float speed = 0.5f;
            if (settings.keyBindJump.isKeyDown()) {
                player.motionY += speed;
            }
            if (settings.keyBindSneak.isKeyDown()) {
                player.motionY -= speed;
            }
            double d7 = player.rotationYaw + 90F;
            boolean flag4 = settings.keyBindForward.isKeyDown();
            boolean flag6 = settings.keyBindBack.isKeyDown();
            boolean flag8 = settings.keyBindLeft.isKeyDown();
            boolean flag10 = settings.keyBindRight.isKeyDown();
            if (flag4) {
                if (flag8) {
                    d7 -= 45D;
                } else if (flag10) {
                    d7 += 45D;
                }
            } else if (flag6) {
                d7 += 180D;
                if (flag8) {
                    d7 += 45D;
                } else if (flag10) {
                    d7 -= 45D;
                }
            } else if (flag8) {
                d7 -= 90D;
            } else if (flag10) {
                d7 += 90D;
            }
            if (flag4 || flag8 || flag6 || flag10) {
                player.motionX = Math.cos(Math.toRadians(d7));
                player.motionZ = Math.sin(Math.toRadians(d7));
            }
        }
        super.onLivingUpdate(event);
    }


    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mode.getValString().equalsIgnoreCase("Flight")) {
            if (teleportPosition == null) {
                return;
            }
            if (teleportPosition.getY() == new BlockPos(mc.player).down().getY()) {
                RenderBlock(TpMode.getValString(), Standardbb(teleportPosition), Path.getcolor(), LineWidth.getValDouble());
                return;
            }
            RenderBlock(TpMode.getValString(), Standardbb(teleportPosition), Land.getcolor(), LineWidth.getValDouble());
            return;
        }
        RayTraceResult object = Wrapper.INSTANCE.mc().objectMouseOver;
        if (object == null) {
            return;
        }
        object.getBlockPos();
        if (canDraw) {
            for (float offset = -2.0F; offset < 18.0F; offset++) {
                double[] mouseOverPos = new double[]{object.getBlockPos().getX(), object.getBlockPos().getY() + offset, object.getBlockPos().getZ()};

                if (BlockTeleport(mouseOverPos)) break;
            }
        } else if (object.entityHit != null) {
            for (float offset = -2.0F; offset < 18.0F; offset++) {
                double[] mouseOverPos = new double[]{object.entityHit.posX, object.entityHit.posY + offset, object.entityHit.posZ};

                if (BlockTeleport(mouseOverPos)) break;
            }
        } else {
            teleportPosition = null;
        }
        super.onRenderWorldLast(event);
    }

    private boolean BlockTeleport(double[] mouseOverPos) {
        BlockPos blockBelowPos = new BlockPos(mouseOverPos[0], mouseOverPos[1], mouseOverPos[2]);

        if (canRenderBox(mouseOverPos)) {
            RenderBlock(TpMode.getValString(), Standardbb(new BlockPos(mouseOverPos[0], mouseOverPos[1], mouseOverPos[2])), Path.getcolor(), LineWidth.getValDouble());
            if (Wrapper.INSTANCE.mc().inGameHasFocus) {
                teleportPosition = blockBelowPos;
                return true;
            } else {
                teleportPosition = null;
            }
        }
        return false;
    }

    public boolean canRenderBox(double[] mouseOverPos) {
        boolean canTeleport = false;

        Block blockBelowPos = mc.world.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1] - 1.0F, mouseOverPos[2])).getBlock();
        Block blockPos = mc.world.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1], mouseOverPos[2])).getBlock();
        Block blockAbovePos = mc.world.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1] + 1.0F, mouseOverPos[2])).getBlock();

        boolean validBlockBelow = blockBelowPos.getCollisionBoundingBox(mc.world.getBlockState(
                new BlockPos(mouseOverPos[0], mouseOverPos[1] - 1.0F, mouseOverPos[2])),
                mc.world, new BlockPos(mouseOverPos[0], mouseOverPos[1] - 1.0F, mouseOverPos[2])) != null;


        boolean validBlock = isValidBlock(blockPos);
        boolean validBlockAbove = isValidBlock(blockAbovePos);

        if ((validBlockBelow && validBlock && validBlockAbove)) {
            canTeleport = true;
        }

        return canTeleport;
    }

    public double getOffset(Block block, BlockPos pos) {
        IBlockState state = mc.world.getBlockState(pos);

        double offset = 0;

        if (block instanceof BlockSlab && !((BlockSlab) block).isDouble()) {
            offset -= 0.5F;
        } else if (block instanceof BlockEndPortalFrame) {
            offset -= 0.2F;
        } else if (block instanceof BlockBed) {
            offset -= 0.44F;
        } else if (block instanceof BlockCake) {
            offset -= 0.5F;
        } else if (block instanceof BlockDaylightDetector) {
            offset -= 0.625F;
        } else if (block instanceof BlockRedstoneComparator || block instanceof BlockRedstoneRepeater) {
            offset -= 0.875F;
        } else if (block instanceof BlockChest || block == Blocks.ENDER_CHEST) {
            offset -= 0.125F;
        } else if (block instanceof BlockLilyPad) {
            offset -= 0.95F;
        } else if (block == Blocks.SNOW_LAYER) {
            offset -= 0.875F;
            offset += 0.125F * (state.getValue(BlockSnow.LAYERS) - 1);
        } else if (isValidBlock(block)) {
            offset -= 1.0F;
        }

        return offset;
    }

    public boolean isValidBlock(Block block) {
        return block == Blocks.PORTAL || block == Blocks.SNOW_LAYER || block instanceof BlockTripWireHook || block instanceof BlockTripWire || block instanceof BlockDaylightDetector || block instanceof BlockRedstoneComparator || block instanceof BlockRedstoneRepeater || block instanceof BlockSign || block instanceof BlockAir || block instanceof BlockPressurePlate || block instanceof BlockTallGrass || block instanceof BlockFlower || block instanceof BlockMushroom || block instanceof BlockDoublePlant || block instanceof BlockReed || block instanceof BlockSapling || block == Blocks.CARROTS || block == Blocks.WHEAT || block == Blocks.NETHER_WART || block == Blocks.POTATOES || block == Blocks.PUMPKIN_STEM || block == Blocks.MELON_STEM || block == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE || block == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE || block == Blocks.REDSTONE_WIRE || block instanceof BlockTorch || block == Blocks.LEVER || block instanceof BlockButton;
    }

}
