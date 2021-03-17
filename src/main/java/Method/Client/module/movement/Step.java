package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Step extends Module {
    public int ticks = 0;
    Setting mode = setmgr.add(new Setting("STEP", this, "Vanilla", "Vanilla", "ACC", "Packet",
            "FastAAC", "NCP", "Hop", "SPAM", "Step"));
    Setting Height = setmgr.add(new Setting("Height", this, 1, .5, 4, true));
    Setting Entity = setmgr.add(new Setting("Entity", this, true));
    Setting Timer = setmgr.add(new Setting("Timer", this, true, mode, "Packet", 3));

    public Step() {
        super("Step", Keyboard.KEY_NONE, Category.MOVEMENT, "Allows you to step up.");
    }
    
    @Override
    public void onEnable() {
        ticks = 0;
        super.onEnable();
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Entity.getValBoolean()) {
            if (mc.player.getRidingEntity() != null && mc.player.getRidingEntity().stepHeight != (int) Height.getValDouble()) {
                mc.player.getRidingEntity().stepHeight = (int) Height.getValDouble();
            }
        }
        if (mode.getValString().equalsIgnoreCase("Step")) {
            if (mc.player.collidedHorizontally && mc.player.onGround)
                mc.player.jump();
            if (mc.player.collidedHorizontally && mc.player.onGround && mc.player.posY + 1.065 < mc.player.posY)
                mc.player.setVelocity(mc.player.motionX, -0.1, mc.player.motionZ);
        }
        if (mode.getValString().equalsIgnoreCase("FastAAC")) {
            BlockPos pos1 = new BlockPos(mc.player.posX + 1.0, mc.player.posY + 1.0, mc.player.posZ);
            BlockPos pos2 = new BlockPos(mc.player.posX - 1.0, mc.player.posY + 1.0, mc.player.posZ);
            BlockPos pos3 = new BlockPos(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ + 1.0);
            BlockPos pos4 = new BlockPos(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ - 1.0);
            Block block1 = mc.world.getBlockState(pos1).getBlock();
            Block block2 = mc.world.getBlockState(pos2).getBlock();
            Block block3 = mc.world.getBlockState(pos3).getBlock();
            Block block4 = mc.world.getBlockState(pos4).getBlock();
            if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() && mc.player.collidedHorizontally && (block1 == Blocks.AIR || block2 == Blocks.AIR || block3 == Blocks.AIR || block4 == Blocks.AIR)) {
                if (mc.player.onGround) {
                    mc.player.jump();
                    mc.player.motionY = 0.386;
                } else {
                    toFwd(0.26);
                }
            }
        }
        if (mode.getValString().equalsIgnoreCase("Spam")) {
            Spam();
        }
        if (mode.getValString().equalsIgnoreCase("Packet")) {
            if ( mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown()) {
                return;
            }
            if (Timer.getValBoolean()) {
                if (this.ticks == 0) {
                    mc.timer.tickLength = 50;
                } else {
                    --this.ticks;
                }
            }
            if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder()) {
                for (double y = 0.0; y < Height.getValDouble() + 0.5; y += 0.01) {
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                        mc.player.motionY = -10.0;
                        break;
                    }
                }
            }
            double[] dir = Utils.directionSpeed(.1);
            boolean twofive = false;
            boolean two = false;
            boolean onefive = false;
            boolean one = false;
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.6, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.4, dir[1])).isEmpty()) {
                twofive = true;
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.1, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.9, dir[1])).isEmpty()) {
                two = true;
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.6, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.4, dir[1])).isEmpty()) {
                onefive = true;
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.0, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 0.6, dir[1])).isEmpty()) {
                one = true;
            }
            if (mc.player.collidedHorizontally && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
                if (one && Height.getValDouble() >= 1.0) {
                    final double[] oneOffset = {0.42, 0.753};
                    for (double v : oneOffset) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }
                    if (Timer.getValBoolean()) {
                        mc.timer.tickLength = 50.0f / .6f;
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ);
                    this.ticks = 1;
                }
                if (onefive && Height.getValDouble() >= 1.5) {
                    final double[] oneFiveOffset = {0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
                    for (double v : oneFiveOffset) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }
                    if (Timer.getValBoolean()) {
                        mc.timer.tickLength = 50.0f / .35f;
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.5, mc.player.posZ);
                    this.ticks = 1;
                }
                if (two && Height.getValDouble() >= 2.0) {
                    final double[] twoOffset = {0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
                    for (double v : twoOffset) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }
                    if (Timer.getValBoolean()) {
                        mc.timer.tickLength = 50.0f / .25f;
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ);
                    this.ticks = 2;
                }
                if (twofive && Height.getValDouble() >= 2.5) {
                    final double[] twoFiveOffset = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
                    for (double v : twoFiveOffset) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }
                    if (Timer.getValBoolean()) {
                        mc.timer.tickLength = 50.0f / .15f;

                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.5, mc.player.posZ);
                    this.ticks = 2;
                }
            }

        }


        if (mode.getValString().equalsIgnoreCase("ACC")) {
            EntityPlayerSP player = mc.player;
            if (player.collidedHorizontally) {
                switch (ticks) {
                    case 0:
                        if (player.onGround)
                            player.jump();
                        break;
                    case 7:
                        player.motionY = 0;
                        break;
                    case 8:
                        if (!player.onGround)
                            player.setPosition(player.posX, player.posY + 1, player.posZ);
                        break;
                }
                ticks++;
            } else {
                ticks = 0;
            }
        } else if (mode.getValString().equalsIgnoreCase("Vanilla")) {
            mc.player.stepHeight = (float) Height.getValDouble();
        }else if (mode.getValString().equalsIgnoreCase("NCP")) {
            if ((mc.player.collidedHorizontally) && (mc.player.onGround) && (mc.player.collidedVertically) && (mc.player.collided)) {
                StepRun();
            }
        } else if (mode.getValString().equalsIgnoreCase("Hop")) {
            if (mc.gameSettings.keyBindJump.pressed && mc.player.collidedHorizontally) {
                mc.player.setPosition(mc.player.lastTickPosX, mc.player.serverPosY + mc.player.lastTickPosY + 0.09F, mc.player.serverPosZ + mc.player.posZ);
            }
        }
        super.onClientTick(event);
    }

    public double get_n_normal() {
        mc.player.stepHeight = 0.5F;
        double max_y = -1.0D;
        AxisAlignedBB grow = mc.player.getEntityBoundingBox().offset(0.0D, 0.05D, 0.0D).grow(0.05D);
        if (!mc.world.getCollisionBoxes(mc.player, grow.offset(0.0D, 2.0D, 0.0D)).isEmpty()) {
            return 100.0D;
        } else {
            for (AxisAlignedBB aabb : mc.world.getCollisionBoxes(mc.player, grow)) {
                if (aabb.maxY > max_y) {
                    max_y = aabb.maxY;
                }
            }
            return max_y - mc.player.posY;
        }
    }

    public static void toFwd(double speed) {
        float yaw = mc.player.rotationYaw * 0.017453292f;
        mc.player.motionX -= (double) MathHelper.sin(yaw) * speed;
        mc.player.motionZ += (double) MathHelper.cos(yaw) * speed;
    }


    private void Spam() {
        if (mc.player.onGround && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.movementInput.jump && !mc.player.noClip) {
            if (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) {
                double n = this.get_n_normal();
                if (n < 0.0D || n > 2.0D) {
                    return;
                }

                if (n == 2.0D) {
                    Sendpos(0.42D);
                    Sendpos(0.78D);
                    Sendpos(0.63D);
                    Sendpos(0.51D);
                    Sendpos(0.9D);
                    Sendpos(1.21D);
                    Sendpos(1.45D);
                    Sendpos(1.43D);

                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ);
                }

                if (n == 1.5D) {
                    Sendpos(0.41999998688698D);
                    Sendpos(0.7531999805212D);
                    Sendpos(1.00133597911214D);
                    Sendpos(1.16610926093821D);
                    Sendpos(1.24918707874468D);
                    Sendpos(1.1707870772188D);
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
                }

                if (n == 1.0D) {
                    Sendpos(0.41999998688698D);
                    Sendpos(0.7531999805212D);
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
                }

            }
        }
    }

    private void Sendpos(double pos) {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + pos, mc.player.posZ, mc.player.onGround));
    }

    private void StepRun() {
        mc.player.setSprinting(true);
        Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42D, mc.player.posZ, mc.player.onGround));
        mc.player.setSprinting(true);
        Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.753D, mc.player.posZ, mc.player.onGround));
        mc.player.setSprinting(true);
        mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
        mc.player.setSprinting(true);
    }


    @Override
    public void onDisable() {
        mc.player.stepHeight = .5F;
        mc.timer.tickLength = 50;
        super.onDisable();
        try {
            if (mc.player.getRidingEntity() != null)
                mc.player.getRidingEntity().stepHeight = 1;
        } catch (Exception ignored) {
        }
    }


}
