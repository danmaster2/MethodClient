package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemBoat;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.directionSpeed;

public class BoatFly extends Module {
    public BoatFly() {
        super("BoatFly", Keyboard.KEY_NONE, Category.MOVEMENT, "Boat Fly");
    }

    Setting Gravity = setmgr.add(new Setting("Gravity", this, true));
    Setting AllEntities = setmgr.add(new Setting("All Entities", this, true));
    Setting mode = setmgr.add(new Setting("Boat Mode", this, "Vanilla", "Vanilla", "Fast", "Packet"));
    Setting FakePackets = setmgr.add(new Setting("Fake Packet Spam", this, false));
    Setting BoatClip = setmgr.add(new Setting("BoatClip", this, "None", "None", "Vanilla", "Fast"));
    Setting BoatClipSpeed = setmgr.add(new Setting("BoatClipSpeed", this, 1, .5, 5, false, BoatClip, 13));
    Setting bypass = setmgr.add(new Setting("bypass Mode", this, "None", "Packet", "Vanilla", "None"));
    Setting Tickdelay = setmgr.add(new Setting("Tickdelay", this, 1, 0, 20, true, bypass, "Packet", 13));
    Setting UpYmotion = setmgr.add(new Setting("UpYmotion", this, .2, .1f, 2, false));
    Setting DownYmotion = setmgr.add(new Setting("Fallmotion", this, .1, 0, 2, false));
    Setting PlaceBypass = setmgr.add(new Setting("PlaceBypass", this, true));
    Setting ComplexMotion = setmgr.add(new Setting("Complex Y Motion", this, true));
    Setting ignoreVehicleMove = setmgr.add(new Setting("No Boat Motion", this, false));
    Setting NoKick = setmgr.add(new Setting("NoKick", this, false));
    Setting ignorePlayerPosRot = setmgr.add(new Setting("No Player Rotation", this, false));
    Setting Fakerotdist = setmgr.add(new Setting("Fakerotdist", this, 1, .5, 10, false, ignorePlayerPosRot, 14));
    Setting Ncptoggle = setmgr.add(new Setting("Ncptoggle", this, true, bypass, "Packet", 13));
    Setting PacketJump = setmgr.add(new Setting("PacketJump", this, false));

    public static Setting BoatRender;
    public static Setting Boatblend;
    double FakerotX;
    double FakerotZ;
    boolean aBoolean = false;
    String updatetexture = "NULL";

    int tpId = 0;

    private int PacketLazyTimer;
    int ClipLazyTimer = 0;


    public static ResourceLocation[] BOAT_TEXTURES = new ResourceLocation[]{
            new ResourceLocation("textures/entity/boat/boat_oak.png"),
            new ResourceLocation("textures/entity/boat/boat_spruce.png"),
            new ResourceLocation("textures/entity/boat/boat_birch.png"),
            new ResourceLocation("textures/entity/boat/boat_jungle.png"),
            new ResourceLocation("textures/entity/boat/boat_acacia.png"),
            new ResourceLocation("textures/entity/boat/boat_darkoak.png")
    };

    @Override
    public void setup() {
        setmgr.add(BoatRender = new Setting("Render", this, "Defualt", "Defualt", "Vanish", "Rainbow", "Carpet"));
        setmgr.add(Boatblend = new Setting("Boatblend", this, false));
    }

    ///////////////////
    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.OUT && PlaceBypass.getValBoolean()) {
            if (packet instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItemMainhand().getItem() instanceof ItemBoat || mc.player.getHeldItemOffhand().getItem() instanceof ItemBoat) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                return false;
            }
        }
        if (mc.player.getRidingEntity() instanceof EntityBoat || (AllEntities.getValBoolean() && mc.player.getRidingEntity() != null)) {
            Entity e = mc.player.getRidingEntity();
            if (e == null) return true;


            if (side == Connection.Side.OUT) {
                if (mode.getValString().equalsIgnoreCase("Packet")) {
                    if (!(packet instanceof CPacketVehicleMove) && !(packet instanceof CPacketSteerBoat) && !(packet instanceof CPacketPlayer)) {
                        if (packet instanceof CPacketEntityAction) {
                            CPacketEntityAction.Action Getaction = ((CPacketEntityAction) packet).getAction();
                            if (Getaction != CPacketEntityAction.Action.OPEN_INVENTORY) {
                                return false;
                            }
                        }
                    }
                }

                if (bypass.getValString().equalsIgnoreCase("Packet") && mc.player != null) {
                    if (Ncptoggle.getValBoolean()) {
                        if (packet instanceof CPacketInput && !mc.gameSettings.keyBindSprint.isKeyDown() && !mc.player.getRidingEntity().onGround) {
                            this.PacketLazyTimer++;
                            if (PacketLazyTimer > Tickdelay.getValDouble()) {
                                PacketLazyTimer = 0;
                                mc.player.connection.sendPacket(new CPacketUseEntity(e, EnumHand.MAIN_HAND));
                            }
                        }
                    } else {
                        if (packet instanceof CPacketVehicleMove && this.PacketLazyTimer++ >= Tickdelay.getValDouble()) {
                            mc.player.connection.sendPacket(new CPacketUseEntity(e, EnumHand.MAIN_HAND));
                            this.PacketLazyTimer = 0;
                        } else {
                            if (packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketInput) {
                                return false;
                            }
                        }
                    }
                }
                if (ignorePlayerPosRot.getValBoolean()) {
                    if (mc.player.ticksExisted % 5 == 0) {
                        MC.player.connection.sendPacket(new CPacketUseEntity(mc.player.getRidingEntity(), EnumHand.OFF_HAND));
                        MC.player.connection.sendPacket(new CPacketVehicleMove(mc.player.getRidingEntity()));
                        MC.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, MC.player.onGround));
                        MC.player.connection.sendPacket(new CPacketConfirmTeleport(tpId));
                    }
                }

                if (bypass.getValString().equalsIgnoreCase("Vanilla")) {
                    if (packet instanceof CPacketVehicleMove) {
                        CPacketVehicleMove cPacketVehicleMove = (CPacketVehicleMove) packet;

                        cPacketVehicleMove.pitch = mc.player.getRidingEntity().prevRotationPitch;
                        cPacketVehicleMove.yaw = mc.player.getRidingEntity().prevRotationYaw;

                        cPacketVehicleMove.x = Double.parseDouble(null);
                        cPacketVehicleMove.y = Double.parseDouble(null);
                        cPacketVehicleMove.z = Double.parseDouble(null);
                    }
                    if (packet instanceof CPacketSteerBoat) {
                        mc.player.motionY = 0;
                        e.isAirBorne = false;
                        return false;
                    }

                }
            }

            if (side == Connection.Side.IN) {
                Entity entity;

                if (ignoreVehicleMove.getValBoolean()) {
                    if (packet instanceof SPacketEntityVelocity) {
                        entity = mc.world.getEntityByID((((SPacketEntityVelocity) packet).getEntityID()));
                        if (entity == mc.player || entity == mc.player.getRidingEntity()) {
                            return false;
                        }
                    }
                    if (packet instanceof SPacketEntity) {
                        entity = ((SPacketEntity) packet).getEntity(mc.world);
                        if (entity == mc.player || entity == mc.player.getRidingEntity()) {
                            return false;
                        }
                    }

                    if (packet instanceof SPacketEntityHeadLook) {
                        entity = ((SPacketEntityHeadLook) packet).getEntity(mc.world);
                        if (entity == mc.player || entity == mc.player.getRidingEntity()) {
                            return false;
                        }
                    }

                    if (packet instanceof SPacketEntityTeleport) {
                        entity = mc.world.getEntityByID(((SPacketEntityTeleport) packet).getEntityId());
                        if (entity == mc.player || entity == mc.player.getRidingEntity()) {
                            return false;
                        }
                    }

                    if (packet instanceof SPacketMoveVehicle && mc.player.getRidingEntity() instanceof EntityBoat) {
                        return false;
                    }
                }
                if (packet instanceof SPacketPlayerPosLook && ignorePlayerPosRot.getValBoolean()) {

                    SPacketPlayerPosLook pp = (SPacketPlayerPosLook) packet;
                    tpId = pp.getTeleportId();
                    double d = Math.sqrt(Math.pow(FakerotX - pp.getX(), 2.0D) + Math.pow(FakerotZ - pp.getZ(), 2.0D));
                    if (d >= Fakerotdist.getValDouble()) {
                        this.respondToPosLook(packet);
                        this.FakerotX = pp.getX();
                        this.FakerotZ = pp.getZ();
                        return false;
                    } else if (mc.player.isRiding() && isBorderingChunk(mc.player.getRidingEntity(), 0.0D, 0.0D)) {
                        this.respondToPosLook(packet);
                        this.FakerotX = pp.getX();
                        this.FakerotZ = pp.getZ();
                        return false;
                    } else {
                        return false;
                    }
                }


                if (bypass.getValString().equalsIgnoreCase("Packet")) {
                    if (packet instanceof SPacketMoveVehicle || packet instanceof SPacketPlayerPosLook) {
                        return false;
                    }
                }

                if (mode.getValString().equalsIgnoreCase("Packet")) {
                    if (packet instanceof SPacketMoveVehicle) {
                        SPacketMoveVehicle VehicleMove = (SPacketMoveVehicle) packet;
                        return !(mc.player.getDistance(VehicleMove.getX(), VehicleMove.getY(), VehicleMove.getZ()) <= ((double) 15));
                    }
                    if (packet instanceof SPacketSetPassengers) {
                        SPacketSetPassengers Setpassengers = (SPacketSetPassengers) packet;
                        if (Setpassengers.getEntityId() == e.getEntityId()) {
                            int[] passengerIds = Setpassengers.getPassengerIds();
                            for (int i : passengerIds) {
                                if (i != mc.player.getEntityId()) {
                                    if (mc.player.isEntityAlive()) {
                                        if (mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), false)) {
                                            if (!(mc.currentScreen instanceof GuiDownloadTerrain)) {
                                                if (!mc.player.isRiding()) {
                                                    return false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        return true;
    }

    @Override
    public void onDisable() {
        if (mc.player.getRidingEntity() instanceof EntityBoat || (AllEntities.getValBoolean() && mc.player.getRidingEntity() != null)) {
            if (Gravity.getValBoolean() && mc.player.getRidingEntity().hasNoGravity())
                mc.player.getRidingEntity().setNoGravity(false);
            mc.player.getRidingEntity().noClip = false;
        }
    }


    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!BoatRender.getValString().equalsIgnoreCase(updatetexture)) {
            updatetexture = BoatRender.getValString();
            if (BoatRender.getValString().equalsIgnoreCase("Defualt")) {
                RenderBoat.BOAT_TEXTURES = BOAT_TEXTURES;
            }
            if (BoatRender.getValString().equalsIgnoreCase("Carpet")) {
                RenderBoat.BOAT_TEXTURES = new ResourceLocation[]{
                        new ResourceLocation(Main.MODID, "carpet.png"),
                        new ResourceLocation(Main.MODID, "carpet.png"),
                        new ResourceLocation(Main.MODID, "carpet.png"),
                        new ResourceLocation(Main.MODID, "carpet.png"),
                        new ResourceLocation(Main.MODID, "carpet.png"),
                        new ResourceLocation(Main.MODID, "carpet.png")};
            }
        }


        if (mc.player.getRidingEntity() instanceof EntityBoat || (AllEntities.getValBoolean() && mc.player.getRidingEntity() != null)) {
            Entity e = mc.player.getRidingEntity();
            if (e == null) return;
            e.setNoGravity(Gravity.getValBoolean());
            e.inWater = true;
            e.isAirBorne = false;
            if (mode.getValString().equalsIgnoreCase("Fast")) {
                final double[] directionSpeedVanilla = directionSpeed(0.20000000298023224);
                e.motionX = directionSpeedVanilla[0];
                e.motionZ = directionSpeedVanilla[1];
                mc.player.motionY = 0.0;
                e.motionY = 0.0;
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(e.posX + e.motionX, e.posY, e.posZ + e.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
                e.motionY = 0.0;
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(e.posX + e.motionX, e.posY - 2, e.posZ + e.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
                e.posY -= .2;
            }

            if (ComplexMotion.getValBoolean())
                e.motionY = mc.gameSettings.keyBindSprint.pressed ? -UpYmotion.getValDouble() :
                        (mc.player.ticksExisted % 2 != 0 ? -DownYmotion.getValDouble() / 10.0D :
                                (mc.gameSettings.keyBindJump.pressed ? getUpyMotion() : DownYmotion.getValDouble() / 10.0D));
            else {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    e.motionY += getUpyMotion() / 20.0F;
                } else if (mc.gameSettings.keyBindSprint.isKeyDown()) {
                    e.motionY -= DownYmotion.getValDouble() / 20.0F;
                } else {
                    e.motionY = 0;
                }
            }

            if (this.NoKick.getValBoolean()) {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    if (mc.player.ticksExisted % 8 < 2) {
                        mc.player.getRidingEntity().motionY = -0.03999999910593033D;
                    }
                } else if (mc.player.ticksExisted % 8 < 4) {
                    mc.player.getRidingEntity().motionY = -0.07999999821186066D;
                }
            }

            if (FakePackets.getValBoolean())
                FakePackets();
            if (BoatClip.getValString().equalsIgnoreCase("Vanilla")) {
                e.noClip = true;
                e.onGround = false;
                e.entityCollisionReduction = 1;
            }

            if (BoatClip.getValString().equalsIgnoreCase("Fast"))
                Boatclip(e);

        }
        super.onClientTick(event);
    }

    private double getUpyMotion() {
        if (PacketJump.getValBoolean())
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_RIDING_JUMP));
        return UpYmotion.getValDouble();
    }

    private void FakePackets() {
        mc.player.connection.sendPacket(new CPacketVehicleMove());
        mc.player.connection.sendPacket(new CPacketSteerBoat(true, true));
        mc.player.connection.sendPacket(new CPacketVehicleMove());
        mc.player.connection.sendPacket(new CPacketSteerBoat(true, true));
    }


    public void respondToPosLook(Object packet) {
        if (mc.world != null && mc.player != null) {
            if (packet instanceof SPacketPlayerPosLook && Objects.requireNonNull(mc.getConnection()).doneLoadingTerrain) {
                SPacketPlayerPosLook packetIn = (SPacketPlayerPosLook) packet;
                double d0 = packetIn.getX();
                double d2 = packetIn.getZ();

                if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X)) {
                    d0 += mc.player.posX;
                }

                if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z)) {
                    d2 += mc.player.posZ;
                }

                mc.getConnection().sendPacket(new CPacketConfirmTeleport(packetIn.getTeleportId()));
                mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(d0, mc.player.getEntityBoundingBox().minY, d2, packetIn.yaw, packetIn.pitch, false));
            }
        }
    }


    private void Boatclip(Entity e) {
        CPacketVehicleMove packetVehicleMove = new CPacketVehicleMove(e);

        e.onGround = false;

        if (mc.gameSettings.keyBindBack.isKeyDown()) {
            packetVehicleMove.y = -2;
            if (mc.player.posY > 0.0D) {
                mc.player.motionX -= getMotionX(mc.player.rotationYaw);
                mc.player.motionZ -= getMotionZ(mc.player.rotationYaw);
            }
        }

        Packet();
        aBoolean = (!aBoolean);
        Objects.requireNonNull(mc.getConnection()).sendPacket(packetVehicleMove);


        if (mc.player.posY < 0.0D) {
            ClipLazyTimer += 1;
        } else {
            ClipLazyTimer = 0;
        }


        if ((ClipLazyTimer > 20) && (mc.player.posY < 0.0D)) {
            ClipLazyTimer = 21;
            if (mc.player.isRiding()) {
                mc.player.dismountRidingEntity();
            }
            double oldMotionX = mc.player.motionX;
            double oldMotionY = mc.player.motionY;
            double oldMotionZ = mc.player.motionZ;


            if (((mc.gameSettings.keyBindForward.isKeyDown()) || (mc.gameSettings.keyBindLeft.isKeyDown()) || (mc.gameSettings.keyBindRight.isKeyDown()) || (mc.gameSettings.keyBindBack.isKeyDown())) && (!mc.gameSettings.keyBindJump.isKeyDown())) {
                mc.player.motionX = (getMotionX(mc.player.cameraYaw) * 0.26D);
                mc.player.motionZ = (getMotionZ(mc.player.cameraYaw) * 0.26D);
            }

            Packet();
            mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, 3.0D + mc.player.posY, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            mc.player.motionX = oldMotionX;
            mc.player.motionY = oldMotionY;
            mc.player.motionZ = oldMotionZ;
        }


    }

    public boolean isBorderingChunk(Entity entity, double motX, double motZ) {
        return mc.world.getChunk((int) (entity.posX + motX) >> 4, (int) (entity.posZ + motZ) >> 4) instanceof EmptyChunk;
    }

    private void Packet() {
        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY + (aBoolean ? 0.0625D : mc.gameSettings.keyBindJump.isKeyDown() ? 0.0624D : 1.0E-8D) - (aBoolean ? 0.0625D : mc.gameSettings.keyBindSneak.isKeyDown() ? 0.0624D : 2.0E-8D), mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
    }

    private double getMotionX(float yaw) {
        return MathHelper.sin(-yaw * 0.017453292F * 1.0F) * (BoatClipSpeed.getValDouble());
    }

    private double getMotionZ(float yaw) {
        return MathHelper.cos(yaw * 0.017453292F) * 1.0F * (BoatClipSpeed.getValDouble());
    }

}
