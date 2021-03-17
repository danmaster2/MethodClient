
package Method.Client.module.misc;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Pickupmod extends Module {

    /////////////////////

    public Pickupmod() {
        super("Pickupmod", Keyboard.KEY_NONE, Category.MISC, "Pickup tools");
    }

    Setting Fast = setmgr.add(Fast = new Setting("Fast", this, true));
    Setting Antipickup = setmgr.add(new Setting("Antipickup", this, false));
    Setting RemoveDrops = setmgr.add(new Setting("RemoveDrops", this, false));
    Setting LongRange = setmgr.add(new Setting("LongRange", this, true));
    Setting Distance = setmgr.add(new Setting("Distance", this, 4, 0, 10, false));


    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        for (EntityItem entityItem : mc.world.getEntitiesWithinAABB(EntityItem.class, mc.player.getEntityBoundingBox().grow(Distance.getValDouble(), Distance.getValDouble(), Distance.getValDouble()))) {
            if (Antipickup.getValBoolean()) {
                if (entityItem.ticksExisted > 30) {
                    entityItem.ticksExisted = 0;
                    Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(entityItem.posX, entityItem.posY + 2, entityItem.posZ, false));
                }
                entityItem.owner = "NULL";
                entityItem.collided = false;
                entityItem.chunkCoordX = 0;
                entityItem.chunkCoordY = 0;
                entityItem.chunkCoordZ = 0;
                entityItem.dimension = 57;
                entityItem.lifespan = -1;
                entityItem.collidedHorizontally = false;
                entityItem.collidedVertically = false;
            }
            if (LongRange.getValBoolean()) {
                if (entityItem.ticksExisted > 30) {
                    entityItem.ticksExisted = 10;
                    Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(entityItem.posX, entityItem.posY, entityItem.posZ, mc.player.onGround));
                }
            }
            if (RemoveDrops.getValBoolean()) {
                entityItem.setDead();
                entityItem.onRemovedFromWorld();
            }

            if (Fast.getValBoolean()) {
                entityItem.ticksExisted = 45;
                entityItem.setNoPickupDelay();
                entityItem.collidedHorizontally = true;
                entityItem.collidedVertically = true;
                entityItem.collided = true;
            }
        }

    }


    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (Antipickup.getValBoolean())
            return !(packet instanceof PlayerEvent.ItemPickupEvent) && !(packet instanceof EntityItemPickupEvent) && !(packet instanceof SPacketCollectItem);

        return true;
    }

    @Override
    public void onItemPickup(EntityItemPickupEvent event) {
        if (Antipickup.getValBoolean())
            event.setCanceled(true);
    }
}
