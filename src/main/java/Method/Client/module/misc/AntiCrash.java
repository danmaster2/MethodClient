
package Method.Client.module.misc;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;

public class AntiCrash extends Module {
    public AntiCrash() {
        super("AntiCrash", Keyboard.KEY_NONE, Category.MISC, "Anti Crash");
    }

    Setting slime = setmgr.add(new Setting("slime", this, true));
    Setting offhand = setmgr.add(new Setting("offhand", this, true));
    Setting Sound = setmgr.add(new Setting("Sound", this, true));

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.IN && packet instanceof SPacketSoundEffect && offhand.getValBoolean()) {
            return ((SPacketSoundEffect) packet).getSound() != SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
        }
        if (packet instanceof SPacketSoundEffect && Sound.getValBoolean()) {
            final SPacketSoundEffect packet2 = (SPacketSoundEffect) packet;
            return packet2.getCategory() != SoundCategory.PLAYERS || packet2.getSound() != SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
        }
        return true;
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Objects.nonNull(mc.world) && slime.getValBoolean()) {
            mc.world.loadedEntityList.forEach(e -> {
                if (e instanceof EntitySlime) {
                    EntitySlime slime = (EntitySlime) e;
                    if (slime.getSlimeSize() > 4) {
                        mc.world.removeEntity(e);
                    }
                }
            });
        }
    }
}
