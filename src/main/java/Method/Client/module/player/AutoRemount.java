package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.*;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Comparator;

import static Method.Client.Main.setmgr;

public class AutoRemount extends Module {

    Setting Bypass = setmgr.add(new Setting("Bypass", this, true));
    Setting boat = setmgr.add(new Setting("boat", this, true));
    Setting Minecart = setmgr.add(new Setting("Minecart", this, true));
    Setting horse = setmgr.add(new Setting("horse", this, true));
    Setting skeletonHorse = setmgr.add(new Setting("skeletonHorse", this, true));
    Setting donkey = setmgr.add(new Setting("donkey", this, true));
    Setting mule = setmgr.add(new Setting("mule", this, true));
    Setting pig = setmgr.add(new Setting("pig ", this, true));
    Setting llama = setmgr.add(new Setting("llama", this, true));
    Setting range = setmgr.add(new Setting("range", this, 2, 0, 10, true));

    public AutoRemount() {
        super("AutoRemount", Keyboard.KEY_NONE, Category.PLAYER, "AutoRemount");
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!mc.player.isRiding()) {
            mc.world.loadedEntityList.stream()
                    .filter(this::isValidEntity)
                    .min(Comparator.comparing(en -> mc.player.getDistance(en)))
                    .ifPresent(entity -> mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND));
        }
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (packet instanceof CPacketUseEntity && Bypass.getValBoolean()) {
            final CPacketUseEntity packet2 = (CPacketUseEntity) packet;
            if (isValidEntity(packet2.getEntityFromWorld(mc.world))) {
                return !packet2.action.equals(CPacketUseEntity.Action.INTERACT_AT);
            }
        }
        return true;
    }

    private boolean isValidEntity(Entity entity) {
        if (mc.player.isRiding()) return false;

        if (entity.getDistance(mc.player) > range.getValDouble()) return false;

        if (entity instanceof AbstractHorse) {
            AbstractHorse horse = (AbstractHorse) entity;
            if (horse.isChild()) return false;
        }
        if (entity instanceof EntityDonkey) {
            EntityDonkey entityDonkey = (EntityDonkey) entity;
            if (entityDonkey.isChild()) return false;
        }
        if (entity instanceof EntityMule) {
            EntityMule entityDonkey = (EntityMule) entity;
            if (entityDonkey.isChild()) return false;
        }

        if (entity instanceof EntityBoat && boat.getValBoolean()) return true;

        if (entity instanceof EntityMinecart && Minecart.getValBoolean()) return true;

        if (entity instanceof EntityHorse && horse.getValBoolean()) return true;

        if (entity instanceof EntitySkeletonHorse && skeletonHorse.getValBoolean()) return true;

        if (entity instanceof EntityDonkey && donkey.getValBoolean()) return true;

        if (entity instanceof EntityMule && mule.getValBoolean()) return true;

        if (entity instanceof EntityPig && pig.getValBoolean()) {
            EntityPig pig = (EntityPig) entity;
            if (pig.isChild()) return false;
            return pig.getSaddled();
        }

        return entity instanceof EntityLlama && llama.getValBoolean();
    }


}
