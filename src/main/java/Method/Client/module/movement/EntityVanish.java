
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.OptionalInt;

import static Method.Client.Main.setmgr;

public class EntityVanish extends Module {

    Setting noDismountPlugin = setmgr.add( new Setting("no Dismount Plugin", this, true));
    Setting dismountEntity = setmgr.add( new Setting("dismoun tEntity", this, true));
    Setting removeEntity = setmgr.add( new Setting("remove Entity", this, true));
    Setting respawnEntity = setmgr.add( new Setting("respawn Entity", this, true));
    Setting sendMovePackets = setmgr.add( new Setting("send Move Packets", this, true));
    Setting forceOnGround = setmgr.add( new Setting("force On Ground", this, true));
    Setting setMountPosition = setmgr.add( new Setting("set MountPosition", this, true));
    private Entity originalRidingEntity;

    public EntityVanish() {
        super("EntityVanish", Keyboard.KEY_NONE, Category.MOVEMENT, "Entity Vanish");
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.IN) {
            if (packet instanceof SPacketSetPassengers) {
                if (this.hasOriginalRidingEntity() && Minecraft.getMinecraft().world != null) {
                    SPacketSetPassengers packetSetPassengers = (SPacketSetPassengers) packet;
                    if (this.originalRidingEntity.equals(Minecraft.getMinecraft().world.getEntityByID(packetSetPassengers.getEntityId()))) {
                        OptionalInt isPlayerAPassenger = Arrays.stream(packetSetPassengers.getPassengerIds()).filter(value -> Minecraft.getMinecraft().world.getEntityByID(value) == Minecraft.getMinecraft().player).findAny();
                        if (!isPlayerAPassenger.isPresent()) { // local player is not a passenger
                            ChatUtils.message("You Have Been Dismounted.");
                            this.toggle(); // toggle the module
                        }
                    }
                }
            }

            if (packet instanceof SPacketDestroyEntities) {
                SPacketDestroyEntities packetDestroyEntities = (SPacketDestroyEntities) packet;
                boolean isEntityNull = Arrays.stream(packetDestroyEntities.getEntityIDs()).filter(value -> value == originalRidingEntity.getEntityId()).findAny().isPresent();
                if (isEntityNull) { // found the entity in the packet
                    ChatUtils.message("Your riding entity has been destroyed.");
                }
            }
        }
        if (side == Connection.Side.OUT) {
            if (this.noDismountPlugin.getValBoolean()) {
                if (packet instanceof CPacketPlayer.Position) {
                    CPacketPlayer.Position cPacketPlayer = (CPacketPlayer.Position) packet;
                    Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayer.PositionRotation(cPacketPlayer.x, cPacketPlayer.y, cPacketPlayer.z, cPacketPlayer.yaw, cPacketPlayer.pitch, cPacketPlayer.onGround));
                    return false;
                }
                return !(packet instanceof CPacketPlayer) || packet instanceof CPacketPlayer.PositionRotation;
            }
        }
        return true;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.originalRidingEntity = null;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.world != null) {
            if (mc.player.isRiding()) {
                this.originalRidingEntity = mc.player.getRidingEntity();

                if (this.dismountEntity.getValBoolean()) {
                    mc.player.dismountRidingEntity();
                    ChatUtils.message("Dismounted entity.");
                }

                if (this.removeEntity.getValBoolean()) {
                    mc.world.removeEntity(this.originalRidingEntity);
                    ChatUtils.message("Removed entity from world.");
                }
            } else {

                ChatUtils.message("Please mount an entity before enabling this module.");
                this.toggle(); // disable module
            }
        }
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.world != null && mc.player != null) {
            if (!mc.player.isRiding() && this.hasOriginalRidingEntity()) { // the local (client) player is not riding something, but server-side we have a mount
                if (this.forceOnGround.getValBoolean())
                    mc.player.onGround = true; // force on ground

                if (this.setMountPosition.getValBoolean())
                    this.originalRidingEntity.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ); // begin moving

                if (this.sendMovePackets.getValBoolean())
                    mc.player.connection.sendPacket(new CPacketVehicleMove(this.originalRidingEntity)); // send movement packets for the entity
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.hasOriginalRidingEntity()) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (this.respawnEntity.getValBoolean())
                this.originalRidingEntity.isDead = false; // bring the riding entity to life client-side

            // if we aren't riding now, we need to remount
            if (!mc.player.isRiding()) {
                mc.world.spawnEntity(this.originalRidingEntity); // spawn the entity back in
                mc.player.startRiding(this.originalRidingEntity, true); // begin riding the entity (forced)
                ChatUtils.message("Spawned & mounted original entity."); // notify the player we successfully remounted
            }

            // delete the old entity now
            this.originalRidingEntity = null;
        }
    }

    private boolean hasOriginalRidingEntity() {
        return this.originalRidingEntity != null;
    }

}
