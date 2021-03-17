package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.isMoving;

public class Speed extends Module {
    public Speed() {
        super("Speed", Keyboard.KEY_NONE, Category.PLAYER, "Speed");
    }

    public Setting mode = setmgr.add(new Setting("Speed Mode", this, "Jump", "Jump", "Forward", "Ice", "GroundHop",
            "Y-Port", "MoveTry", "AAC", "Hypixel BHop", "AAC_ZOOM", "Packet"));
    Setting Icespeed = setmgr.add(new Setting("Icespeed", this, 1, .1, 5, false, mode, "Ice", 2));
    Setting motiony = setmgr.add(new Setting("motiony", this, 0, 0, 1.5, false, mode, "Forward", 2));
    Setting groundmultiplier = setmgr.add(new Setting("groundmultiplier", this, .2, .001, .5, false, mode, "Forward", 3));
    Setting airmultiplier = setmgr.add(new Setting("airmultiplier", this, 1.0064, 1, 1.1, false, mode, "Forward", 4));
    Setting motionzp = setmgr.add(new Setting("motionzp", this, 1, .5, 4, false, mode, "Packet", 2));

    int counter;
    public static double G;

    public boolean sink = false;

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (mode.getValString().equalsIgnoreCase("packet")) {
            if (isMoving(mc.player) && mc.player.onGround) {
                boolean step = ModuleManager.getModuleByName("step").isToggled();
                double posX = mc.player.posX;
                double posY = mc.player.posY;
                double posZ = mc.player.posZ;
                double[] dir1 = Utils.directionSpeed(.5);
                BlockPos pos = new BlockPos(posX + dir1[0], posY, posZ + dir1[1]);
                Block block = mc.world.getBlockState(pos).getBlock();
                if (step && !(block instanceof BlockAir)) {
                    setSpeed(mc.player, 0);
                    return;
                }
                if (mc.world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock() instanceof BlockAir)
                    return;
                setSpeed(mc.player, 4);

                mc.player.connection.sendPacket(new CPacketPlayer.Position(posX + mc.player.motionX, mc.player.posY <= 10 ? 255 : 1, posZ + mc.player.motionZ, true));
            }
        }

    }

    public static void setSpeed(final EntityLivingBase entity, final double speed) {
        double[] dir = Utils.directionSpeed(speed);
        entity.motionX = dir[0];
        entity.motionZ = dir[1];
    }

    @Override
    public void onClientTick(ClientTickEvent event) {

        if (mode.getValString().equalsIgnoreCase("Fast")) {
            if (mc.player.onGround && mc.player.moveForward > 0) {

                double yaw = Math.toRadians(mc.player.rotationYaw);
                double motionX = -Math.sin(yaw);
                double motionZ = Math.cos(yaw);
                mc.player.motionX = motionX * 5;
                mc.player.motionZ = motionZ * 5;
                mc.player.cameraYaw = 0.15F;
                mc.player.setSprinting(true);

            }
        }
        if (mode.getValString().equalsIgnoreCase("MoveTry")) {
            if ((mc.player.isSneaking())
                    || ((mc.player.moveForward == 0.0F)
                    && (mc.player.moveStrafing == 0.0F))) {
                return;
            }
            if ((mc.player.moveForward > 0.0F)
                    && (!mc.player.collidedHorizontally)) {
                mc.player.setSprinting(true);
            }
            if (mc.player.onGround) {
                mc.player.motionY += 0.1D;
                mc.player.motionX *= 1.8D;
                mc.player.motionZ *= 1.8D;
                double currentSpeed = Math.sqrt(Math.pow(mc.player.motionX, 2.0D)
                        + Math.pow(mc.player.motionZ, 2.0D));

                double maxSpeed = 0.6600000262260437D;
                if (currentSpeed > maxSpeed) {
                    mc.player.motionX = (mc.player.motionX / currentSpeed
                            * maxSpeed);
                    mc.player.motionZ = (mc.player.motionZ / currentSpeed
                            * maxSpeed);

                }
            }
        }
        if (mode.getValString().equalsIgnoreCase("Forward")) {
            this.Forward(motiony.getValDouble(), (float) groundmultiplier.getValDouble(), airmultiplier.getValDouble());
        }

        if (mode.getValString().equalsIgnoreCase("Jump")) {
            boolean boost = Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90;

            if (mc.player.moveForward > 0 && mc.player.hurtTime < 5) {
                if (mc.player.onGround) {
                    mc.player.motionY = 0.405;
                    float f = Utils.getDirection();
                    mc.player.motionX -= MathHelper.sin(f) * 0.2F;
                    mc.player.motionZ += MathHelper.cos(f) * 0.2F;
                } else {
                    double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
                    double speed = boost ? 1.0064 : 1.001;

                    double direction = Utils.getDirection();

                    mc.player.motionX = -Math.sin(direction) * speed * currentSpeed;
                    mc.player.motionZ = Math.cos(direction) * speed * currentSpeed;
                }
            }
            super.onClientTick(event);
        }
        if (mode.getValString().equalsIgnoreCase("Ice")) {
            Blocks.ICE.slipperiness = 0.4f * (float) (0.4f * Icespeed.getValDouble());
            Blocks.PACKED_ICE.slipperiness = 0.4f * (float) (0.4f * Icespeed.getValDouble());
            Blocks.FROSTED_ICE.slipperiness = 0.4f * (float) (0.4f * Icespeed.getValDouble());
        }


        if (mode.getValString().equalsIgnoreCase("GroundHop")) {
            if ((mc.player.onGround)) {
                this.sink = (!this.sink);
                this.counter += 1;
                if (this.counter > 3.189546D) {
                    mc.player.motionX *= 0.009999999776482582D;
                    mc.player.motionZ *= 0.009999999776482582D;
                    mc.player.getHeldItemMainhand().damageItem(0, mc.player);
                    mc.player.hurtTime = 62284;

                    this.counter = 0;
                }
                mc.player.motionX *= 1.8300000429153442D;
                mc.player.motionZ *= 1.8300000429153442D;
                mc.player.hurtResistantTime = 1;
                mc.player.motionY =
                        (mc.player.isSneaking() ? -0.02D : mc.gameSettings.keyBindJump.pressed ? 0.43D : this.sink ? 0.37D : 0.25D);
                if (!(mc.player.moveForward > 0)) {
                    mc.player.motionX = 0.0D;
                    mc.player.motionZ = 0.0D;
                }
            }
        }

        if (mode.getValString().equalsIgnoreCase("Y-Port")) {
            if (isOnLiquid())
                return;
            if (mc.player.onGround && (mc.player.moveForward != 0 || mc.player.moveStrafing != 0)) {
                if (mc.player.ticksExisted % 2 != 0)
                    mc.player.posY += .4;
                setSpeed(mc.player.ticksExisted % 2 == 0 ? .45F : .2F);
            }

        }

        if (mode.getValString().equalsIgnoreCase("ACC")) {
            boolean boost = Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90;
            if (mc.player.moveForward > 0 && mc.player.hurtTime < 5) {
                MoveSpeed(boost);
            }
        }
        if (mode.getValString().equalsIgnoreCase("Hypixel BHop")) {
            mc.player.setSprinting(true);
            if (mc.world != null && mc.player != null) {
                mc.gameSettings.keyBindJump.pressed = false;
                if (mc.gameSettings.keyBindForward.pressed && !mc.player.collidedHorizontally) {
                    if (mc.player.onGround) {
                        mc.player.jump();
                        mc.timer.elapsedTicks = (int) 1.05F;
                        mc.player.motionX *= 1.07F;
                        mc.player.motionZ *= 1.07F;
                    } else {
                        mc.player.jumpMovementFactor = 0.0265F;
                        mc.timer.elapsedTicks = (int) 1.4F;
                        double direction = getDirection();
                        double speed = 1;
                        double currentMotion = Math.sqrt(
                                mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);

                        mc.player.motionX = -Math.sin(direction) * speed * currentMotion;
                        mc.player.motionZ = Math.cos(direction) * speed * currentMotion;
                    }
                }
            }
        }
        if (mode.getValString().equalsIgnoreCase("AAC_ZOOM")) {
            boolean boost = Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90;
            if (mc.player.moveForward > 0 && mc.player.hurtTime < 5) {
                mc.timer.elapsedTicks = (int) 1.025f;
                MoveSpeed(boost);
            }
        }

    }

    private void MoveSpeed(boolean boost) {
        if (mc.player.onGround) {
            mc.player.motionY = 0.405;
            float f = (float) getDirection();
            mc.player.motionX -= MathHelper.sin(f) * 0.2F;
            mc.player.motionZ += MathHelper.cos(f) * 0.2F;
        } else {
            double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
            double speed = boost ? 1.0064 : 1.001;

            double direction = getDirection();
            mc.player.motionX = -Math.sin(direction) * speed * currentSpeed;
            mc.player.motionZ = Math.cos(direction) * speed * currentSpeed;
        }
    }

    @Override
    public void onDisable() {
        Blocks.ICE.slipperiness = 0.98f;
        Blocks.PACKED_ICE.slipperiness = 0.98f;
        Blocks.FROSTED_ICE.slipperiness = 0.98f;
        mc.timer.elapsedTicks = 1;

        super.onDisable();
    }

    private boolean isOnLiquid() {
        boolean onLiquid = false;
        final int y = (int) (mc.player.getEntityBoundingBox().minY - .01);
        for (int x = floor_double(mc.player.getEntityBoundingBox().minX); x < floor_double(mc.player.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = floor_double(mc.player.getEntityBoundingBox().minZ); z < floor_double(mc.player.getEntityBoundingBox().maxZ) + 1; ++z) {
                Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (!(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid))
                        return false;
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public void setSpeed(float speed) {
        mc.player.motionX = (-(Math.sin(getDirection()) * speed));
        mc.player.motionZ = (Math.cos(getDirection()) * speed);
    }


    public static double getDirection() {
        float var1 = mc.player.rotationYaw;

        if (mc.player.moveForward < 0.0F) { // If the player walks backward
            var1 += 180.0F;
        }

        float forward = 1.0F;

        if (mc.player.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (mc.player.moveForward > 0.0F) {
            forward = 0.5F;
        }

        if (mc.player.moveStrafing > 0.0F) {
            var1 -= 90.0F * forward;
        }

        if (mc.player.moveStrafing < 0.0F) {
            var1 += 90.0F * forward;
        }
        var1 *= 0.017453292F; // Faster version of Math.toRadians (x * 1 / 180 * PI)
        return var1;
    }

    public static int floor_double(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    private void Forward(final double motionY, final float groundmultiplier, final double airmultiplier) {
        if (Speed.mc.player.moveForward != 0.0f || Speed.mc.player.moveStrafing != 0.0f) {
            Speed.mc.player.setSprinting(true);
            if (Speed.mc.player.onGround) {
                if (motionY > 0.01)
                    Speed.mc.player.motionY = motionY;
                final float a = Converter();
                mc.player.motionX -= MathHelper.sin(a) * groundmultiplier;
                mc.player.motionZ += MathHelper.cos(a) * groundmultiplier;
            } else {
                final double sqrt = Math.sqrt(Speed.mc.player.motionX * Speed.mc.player.motionX + Speed.mc.player.motionZ * Speed.mc.player.motionZ);
                final double n3 = Converter();
                Speed.mc.player.motionX = -Math.sin(n3) * airmultiplier * sqrt;
                Speed.mc.player.motionZ = Math.cos(n3) * airmultiplier * sqrt;
            }
        }
    }

    public static float Converter() {
        float rotationYaw = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float n = 1.0f;
        if (mc.player.moveForward < 0.0f) {
            n = -0.5f;
        } else if (mc.player.moveForward > 0.0f) {
            n = 0.5f;
        }
        if (mc.player.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * n;
        }
        if (mc.player.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * n;
        }
        return rotationYaw * 0.017453292f;
    }


}
