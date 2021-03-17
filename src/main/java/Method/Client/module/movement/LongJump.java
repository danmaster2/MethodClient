
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.PlayerMoveEvent;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.isMoving;

public class LongJump extends Module {
    private int airTicks;
    private int groundTicks;
    Setting mode = setmgr.add(new Setting("Jump mode", this, "Vanilla", "Vanilla", "AAC", "Damage", "Long",
            "Legit", "Quack", "AAC4", "Mineplex", "Hypixel", "NeruxVace", "NeruxVace2"));
    Setting boostval = setmgr.add(new Setting("boostval", this, 1, 0, 3, false, mode, "Long", 3));
    Setting Lagback = setmgr.add(new Setting("Lagback", this, true));
    private double moveSpeed, lastDist;
    private int level;
    private final TimerUtils timer = new TimerUtils();
    private final TimerUtils aac = new TimerUtils();

    int delay2 = 0;
    double y = 0.0D;
    double speed = 0.0D;
    boolean teleported = false;
    private float air;
    private int stage;

    private boolean jump;

    public LongJump() {
        super("LongJump", Keyboard.KEY_NONE, Category.MOVEMENT, "Jump Far");
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (packet instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook pac = (SPacketPlayerPosLook) packet;
            if (Lagback.getValBoolean()) {
                ChatUtils.warning("Lagback checks!");
                mc.player.onGround = false;
                mc.player.motionX *= 0;
                mc.player.motionZ *= 0;
                mc.player.jumpMovementFactor = 0;
                this.toggle();
            } else if (timer.hasReached(300)) {
                pac.yaw = mc.player.rotationYaw;
                pac.pitch = mc.player.rotationPitch;
            }
            timer.reset();
        }
        return true;
    }


    @Override
    public void onDisable() {
        if (mc.player != null) {
            moveSpeed = getBaseMoveSpeed();

        }
        lastDist = 0.0D;

        assert mc.player != null;
        mc.player.speedInAir = 0.02F;


        if (mode.getValString().equalsIgnoreCase("NeruxVace2")) {
            this.setMotion(0.2D);
        }

        if (mode.getValString().equalsIgnoreCase("NeruxVace")) {
            this.teleported = false;
            this.setMotion(0.22D);
        }

        this.speed = 0.0D;
        this.delay2 = 0;
        mc.timer.tickLength = 50;
    }

    @Override
    public void onEnable() {
        this.teleported = false;
        this.groundTicks = -5;
        if (mc.player == null || mc.world == null) return;
        level = 0;
        lastDist = 0.0D;
        if (mode.getValString().equalsIgnoreCase("Hypixel")) {
            this.setMotion(0.15D);
            this.speed = 0.4D;
            this.y = 0.02D;
        }
    }

    public static void toFwd(double speed) {
        float f = mc.player.rotationYaw * 0.017453292F;
        mc.player.motionX -= (double) MathHelper.sin(f) * speed;
        mc.player.motionZ += (double) MathHelper.cos(f) * speed;
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {

        double xDist = mc.player.posX - mc.player.prevPosX;
        double zDist = mc.player.posZ - mc.player.prevPosZ;
        lastDist = Math.sqrt((xDist * xDist) + (zDist * zDist));

        if (mode.getValString().equalsIgnoreCase("AAC")) {
            mc.gameSettings.keyBindForward.pressed = false;
            if (mc.player.onGround) {
                this.jump = true;
            }

            if (mc.player.onGround && this.aac.isDelay(500L)) {
                mc.player.motionY = 0.42D;
                toFwd(2.3D);
                this.timer.setLastMS();
            } else if (!mc.player.onGround && this.jump) {
                mc.player.motionX = mc.player.motionZ = 0.0D;
                this.jump = false;
            }
        }
        if (mode.getValString().equalsIgnoreCase("Quack")) {
            boolean moving = (mc.gameSettings.keyBindForward.isKeyDown()) || (mc.gameSettings.keyBindBack.isKeyDown());
            if (!moving) {
                return;
            }
            double forward = mc.player.movementInput.moveForward;
            float yaw = mc.player.rotationYaw;
            if (forward != 0.0D) {
                if (forward > 0.0D) {
                    forward = 1.0D;
                } else if (forward < 0.0D) {
                    forward = -1.0D;
                }
            } else {
                forward = 0.0D;
            }
            float[] motion = {0.4206065F, 0.4179245F, 0.41525924F, 0.41261F, 0.409978F, 0.407361F, 0.404761F, 0.402178F, 0.399611F, 0.39706F, 0.394525F, 0.392F, 0.3894F, 0.38644F, 0.383655F, 0.381105F, 0.37867F, 0.37625F, 0.37384F, 0.37145F, 0.369F, 0.3666F, 0.3642F, 0.3618F, 0.35945F, 0.357F, 0.354F, 0.351F, 0.348F, 0.345F, 0.342F, 0.339F, 0.336F, 0.333F, 0.33F, 0.327F, 0.324F, 0.321F, 0.318F, 0.315F, 0.312F, 0.309F, 0.307F, 0.305F, 0.303F, 0.3F, 0.297F, 0.295F, 0.293F, 0.291F, 0.289F, 0.287F, 0.285F, 0.283F, 0.281F, 0.279F, 0.277F, 0.275F, 0.273F, 0.271F, 0.269F, 0.267F, 0.265F, 0.263F, 0.261F, 0.259F, 0.257F, 0.255F, 0.253F, 0.251F, 0.249F, 0.247F, 0.245F, 0.243F, 0.241F, 0.239F, 0.237F};
            float[] glide = {0.3425F, 0.5445F, 0.65425F, 0.685F, 0.675F, 0.2F, 0.895F, 0.719F, 0.76F};
            final double cos = Math.cos(Math.toRadians(yaw + 90.0F));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0F));
            if ((!mc.player.collidedVertically) && (!mc.player.onGround)) {
                this.airTicks += 1;
                this.groundTicks = -5;
                if ((this.airTicks - 6 >= 0) && (this.airTicks - 6 < glide.length)) {
                    mc.player.motionY *= glide[(this.airTicks - 6)];
                }
                if ((mc.player.motionY < -0.2D) && (mc.player.motionY > -0.24D)) {
                    mc.player.motionY *= 0.7D;
                } else if ((mc.player.motionY < -0.25D) && (mc.player.motionY > -0.32D)) {
                    mc.player.motionY *= 0.8D;
                } else if ((mc.player.motionY < -0.35D) && (mc.player.motionY > -0.8D)) {
                    mc.player.motionY *= 0.98D;
                }
                if ((this.airTicks - 1 >= 0) && (this.airTicks - 1 < motion.length)) {
                    mc.player.motionX = (forward * motion[(this.airTicks - 1)] * 3.0D * cos);
                    mc.player.motionZ = (forward * motion[(this.airTicks - 1)] * 3.0D * sin);
                } else {
                    mc.player.motionX = 0.0D;
                    mc.player.motionZ = 0.0D;
                }
            } else {
                this.airTicks = 0;
                this.groundTicks += 1;
                if (this.groundTicks <= 2) {
                    mc.player.motionX = (forward * 0.009999999776482582D * cos);
                    mc.player.motionZ = (forward * 0.009999999776482582D * sin);
                } else {
                    mc.player.motionX = (forward * 0.30000001192092896D * cos);
                    mc.player.motionZ = (forward * 0.30000001192092896D * sin);
                    mc.player.motionY = 0.42399999499320984D;
                }
            }
        }
        if (mode.getValString().equalsIgnoreCase("Damage")) {
            if (mc.player.onGround) {
                Wrapper.INSTANCE.sendPacket((new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.300001D, mc.player.posZ, false)));
                Wrapper.INSTANCE.sendPacket((new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false)));
                Wrapper.INSTANCE.sendPacket((new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true)));
            }

            if (mc.player.hurtTime > 0) {
                Movemulti(5);
            } else {
                Movemulti(0.0D);
            }
        }
        if (mode.getValString().equalsIgnoreCase("Vanilla")) {
            if ((mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()
                    || mc.gameSettings.keyBindBack.isKeyDown()) && mc.gameSettings.keyBindJump.isKeyDown()) {
                float dir = mc.player.rotationYaw + ((mc.player.moveForward < 0) ? 180 : 0) + ((mc.player.moveStrafing > 0) ? (-90F * ((mc.player.moveForward < 0) ? -.5F : ((mc.player.moveForward > 0) ? .4F : 1F))) : 0);
                float xDir = (float) Math.cos((dir + 90F) * Math.PI / 180);
                float zDir = (float) Math.sin((dir + 90F) * Math.PI / 180);
                if (mc.player.collidedVertically && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()
                        || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) && mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.motionX = xDir * .29F;
                    mc.player.motionZ = zDir * .29F;
                }
                if (mc.player.motionY == .33319999363422365 && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()
                        || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown())) {
                    mc.player.motionX = xDir * 1.261;
                    mc.player.motionZ = zDir * 1.261;

                }
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (mc.player == null) return;
        if (mc.player.isInWater()) return;


        if (mode.getValString().equalsIgnoreCase("NeruxVace2")) {
            mc.timer.tickLength = 30;
            if (mc.player.onGround) {
                mc.player.jump();
                mc.player.motionY = 1.0D;
                setMotion(9.5D);
            }
        }

        if (mode.getValString().equalsIgnoreCase("NeruxVace")) {
            mc.player.speedInAir = 0.025F;
            mc.player.motionX *= 1.08D;
            mc.player.motionZ *= 1.08D;
            if (mc.player.onGround) {
                mc.player.jump();
            }
            mc.player.motionY += 0.072D;
        }

        double var3;
        if (mode.getValString().equalsIgnoreCase("Hypixel")) {
            boolean var1 = false;
            if (this.y > 0.0D) {
                this.y *= 0.9D;
            }

            float var7 = mc.player.fallDistance;
            mc.player.setPosition(mc.player.posX * 1.0D, mc.player.posY + 0.035423123132D, mc.player.posZ * 1.0D);
            float var2 = 0.7F + (float) this.getSpeedEffect() * 0.45F;
            if ((mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) && mc.player.onGround) {
                this.setMotion(0.15D);
                mc.player.jump();
                this.stage = 1;
            }

            if (mc.player.onGround) {
                this.air = 0.0F;
            } else {
                if (mc.player.collidedVertically) {
                    this.stage = 0;
                }

                var3 = 0.95D + (double) this.getSpeedEffect() * 0.2D - (double) (this.air / 25.0F);

                if (var3 < this.defaultSpeed() - 0.05D) {
                    var3 = this.defaultSpeed() - 0.05D;
                }

                this.setMotion(var3 * 0.75D);
                if (this.stage > 0) {
                    this.stage |= 1;
                }

                this.air += var2;
            }
        }

        if (mode.getValString().equalsIgnoreCase("AAC4")) {
            if (mc.player.onGround) {
                mc.player.jump();
                mc.player.motionY += 0.2D;
                this.speed = 0.5972999999999999D;
            } else {
                mc.player.motionY += 0.03D;
                this.speed *= 0.99D;
            }

            this.setMotion(this.speed);
            if (!mc.player.onGround) {
                this.delay2 |= 1;
            }

        }

        if (mode.getValString().equalsIgnoreCase("Mineplex")) {
            if (mc.player.onGround) {
                mc.player.jump();
                mc.player.motionY += 0.1D;
                this.speed = 0.65D;
            } else {
                mc.player.motionY += 0.03D;
                this.speed *= 0.992D;
            }

            if (!mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed) {
                this.setMotion(this.speed);
            } else {
                this.setMotion(this.speed * 0.7D);
            }

            if (mc.player.onGround) {
                this.setMotion(0.0D);
            }
        }


        if (mode.getValString().equalsIgnoreCase("legit")) {
            if (isMoving(mc.player)) {
                if (mc.player.onGround) {
                    mc.player.motionY = (mc.player.motionY = 0.41);
                }
            } else {
                mc.player.motionX = 0.0;
                mc.player.motionZ = 0.0;
            }
        }
        if (mode.getValString().equalsIgnoreCase("long")) {
            double forward = mc.player.movementInput.moveForward;
            double strafe = mc.player.movementInput.moveStrafe;
            float yaw = mc.player.rotationYaw;
            if (forward == 0.0F && strafe == 0.0F) {
                mc.player.motionX = (0);
                mc.player.motionZ = (0);
            }
            if (forward != 0 && strafe != 0) {
                forward = forward * Math.sin(Math.PI / 4);
                strafe = strafe * Math.cos(Math.PI / 4);
            }
            if (level != 1 || mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F) {
                if (level == 2) {
                    ++level;
                    double motionY = 0.40123128;
                    if ((mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) && mc.player.onGround) {
                        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST))
                            motionY += ((Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1F);
                        mc.player.motionY = (mc.player.motionY = motionY);
                        moveSpeed *= 2.149;
                    }
                } else if (level == 3) {
                    ++level;
                    double difference = 0.763D * (lastDist - getBaseMoveSpeed());
                    moveSpeed = lastDist - difference;
                } else {
                    if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, mc.player.motionY, 0.0D)).size() > 0 || mc.player.collidedVertically) {
                        level = 1;
                    }
                    moveSpeed = lastDist - lastDist / 159.0D;
                }
            } else {
                level = 2;
                double boost = mc.player.isPotionActive(MobEffects.SPEED) ? boostval.getValDouble() : boostval.getValDouble() + 1.1;
                moveSpeed = boost * getBaseMoveSpeed() - 0.01D;
            }
            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
            final double mx = -Math.sin(Math.toRadians(yaw));
            final double mz = Math.cos(Math.toRadians(yaw));
            mc.player.motionX = (forward * moveSpeed * mx + strafe * moveSpeed * mz);
            mc.player.motionZ = (forward * moveSpeed * mz - strafe * moveSpeed * mx);
        }
    }

    public static void Movemulti(double moveSpeed) {
        float forward = mc.player.moveForward;
        float strafe = mc.player.moveStrafing;
        float yaw = mc.player.rotationYaw;
        if ((double) forward == 0.0D && (double) strafe == 0.0D) {
            mc.player.motionX = 0.0D;
            mc.player.motionZ = 0.0D;
        }

        int d = 45;
        if ((double) forward != 0.0D) {
            if ((double) strafe > 0.0D) {
                yaw += (float) ((double) forward > 0.0D ? -d : d);
            } else if ((double) strafe < 0.0D) {
                yaw += (float) ((double) forward > 0.0D ? d : -d);
            }

            strafe = 0.0F;
            if ((double) forward > 0.0D) {
                forward = 1.0F;
            } else if ((double) forward < 0.0D) {
                forward = -1.0F;
            }
        }
        final double cos = Math.cos(Math.toRadians(yaw + 90.0F));
        final double sin = Math.sin(Math.toRadians(yaw + 90.0F));
        double xDist = (double) forward * moveSpeed * cos + (double) strafe * moveSpeed * sin;
        double zDist = (double) forward * moveSpeed * sin - (double) strafe * moveSpeed * cos;
        mc.player.motionX = xDist;
        mc.player.motionZ = zDist;
    }

    public void setMotion(double var1) {
        double var3 = mc.player.movementInput.moveForward;
        double var5 = mc.player.movementInput.moveStrafe;
        float var7 = mc.player.rotationYaw;
        if (mode.getValString().equalsIgnoreCase("aac4")) {
            var5 = 0.0D;
            var7 = 0.0F;
        }

        if (var3 == 0.0D && var5 == 0.0D) {
            mc.player.motionX = 0.0D;
            mc.player.motionZ = 0.0D;
        } else {
            if (var3 != 0.0D) {
                if (var5 > 0.0D) {
                    var7 += (float) (var3 > 0.0D ? -45 : 45);
                } else if (var5 < 0.0D) {
                    var7 += (float) (var3 > 0.0D ? 45 : -45);
                }

                var5 = 0.0D;
                if (var3 > 0.0D) {
                    var3 = 1.0D;
                } else if (var3 < 0.0D) {
                    var3 = -1.0D;
                }
            }

            mc.player.motionX = var3 * var1 * Math.cos(Math.toRadians(var7 + 90.0F)) + var5 * var1 * Math.sin(Math.toRadians(var7 + 90.0F));
            mc.player.motionZ = var3 * var1 * Math.sin(Math.toRadians(var7 + 90.0F)) - var5 * var1 * Math.cos(Math.toRadians(var7 + 90.0F));
        }

    }

    public int getSpeedEffect() {
        return mc.player.isPotionActive(MobEffects.SPEED) ? Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() | 1 : 0;
    }

    public double defaultSpeed() {
        double var1 = 0.2873D;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int var3 = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            var1 *= 1.0D + 0.2D * (double) (var3 | 1);
        }

        return var1;
    }


    private double getBaseMoveSpeed() {
        double n = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            n *= 1.0 + 0.2 * (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1);
        }
        return n;
    }


}
