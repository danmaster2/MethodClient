package Method.Client.module.combat;


import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import org.lwjgl.input.Keyboard;


public class MoreKnockback extends Module {

    public MoreKnockback() {
        super("KnockbackModifier", Keyboard.KEY_NONE, Category.COMBAT, "Increase knockback");
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (mc.player.onGround) {
            if (side == Connection.Side.OUT) {
                if (packet instanceof CPacketUseEntity) {
                    CPacketUseEntity attack = (CPacketUseEntity) packet;
                    if (attack.getAction() == CPacketUseEntity.Action.ATTACK) {
                        Entity entity = mc.world.getEntityByID(attack.entityId);
                        if (entity != mc.player && entity !=null) {
                            if (entity.getDistance(mc.player) < 4) {
                                boolean oldSprint = mc.player.isSprinting();
                                Wrapper.INSTANCE.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                                Wrapper.INSTANCE.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                                mc.player.setSprinting(oldSprint);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

}
