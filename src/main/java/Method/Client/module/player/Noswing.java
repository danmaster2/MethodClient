package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.PlayerDamageBlockEvent;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Noswing extends Module {

    Setting mode = setmgr.add(new Setting("No swing", this, "Vanilla", "Vanilla", "Packet", "BlockClick", "PacketSwing", "Clientonly"));
    Setting Nobreakani = setmgr.add(new Setting("Nobreakani", this, false));

    EnumFacing lastFacing;
    BlockPos lastPos;
    boolean isMining;

    public Noswing() {
        super("Noswing", Keyboard.KEY_NONE, Category.PLAYER, "Noswing");
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("Clientonly")) {
            ItemRenderer itemRenderer = mc.entityRenderer.itemRenderer;

            itemRenderer.equippedProgressMainHand = 1.0f;
            itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();

        }
        if (mode.getValString().equalsIgnoreCase("Vanilla"))
            if (mc.player.swingProgress <= 0) {
                mc.player.swingProgressInt = 5;
            }
        if (Nobreakani.getValBoolean()) {
            if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                resetMining();
            } else if (isMining && lastPos != null && lastFacing != null) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, lastPos, lastFacing));
            }
        }

    }

    public void resetMining() {
        isMining = false;
        lastPos = null;
        lastFacing = null;
    }

    @Override
    public void DamageBlock(PlayerDamageBlockEvent event) {
        if (mode.getValString().equalsIgnoreCase("PacketSwing"))
            Wrapper.INSTANCE.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, event.getPos(), event.getFacing()));
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (packet instanceof CPacketPlayerDigging && side == Connection.Side.OUT && Nobreakani.getValBoolean()) {
            final CPacketPlayerDigging packet2 = (CPacketPlayerDigging) packet;
            for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(packet2.getPosition()))) {
                if (entity instanceof EntityEnderCrystal) {
                    resetMining();
                    continue;
                }

                if (entity instanceof EntityLivingBase) {
                    resetMining();
                }
            }
            if (packet2.getAction().equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                isMining = true;
                setMiningInfo(packet2.getPosition(), packet2.getFacing());
            }

            if (packet2.getAction().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                resetMining();
            }
        }


        return !(packet instanceof CPacketAnimation) || !mode.getValString().equalsIgnoreCase("packet");
    }

    @Override
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (mode.getValString().equalsIgnoreCase("BlockClick")) {
            Blockclick(event);
        }

    }

    @Override
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (mode.getValString().equalsIgnoreCase("BlockClick")) {
            Blockclick(event);
        }
    }

    void Blockclick(Event event) {

        if (mc.objectMouseOver.entityHit == null) {
            final BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            Wrapper.INSTANCE.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));
            Wrapper.INSTANCE.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.UP));
            event.setCanceled(true);
        }
        if (mc.objectMouseOver.entityHit != null) {
            mc.playerController.attackEntity(mc.player, mc.objectMouseOver.entityHit);
            event.setCanceled(true);
        }
    }

    private void setMiningInfo(BlockPos blockPos, EnumFacing enumFacing) {
        lastPos = blockPos;
        lastFacing = enumFacing;
    }

}
