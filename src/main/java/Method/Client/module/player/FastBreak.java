package Method.Client.module.player;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.BlockUtils;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;

public class FastBreak extends Module {
    public FastBreak() {
        super("FastBreak", Keyboard.KEY_NONE, Category.PLAYER, "FastBreak");
    }

    Setting mode = setmgr.add(new Setting("break mode", this, "potion", "Potion", "Packet", "INSTANT", "NoDelay"));

    PotionEffect Haste = new PotionEffect(Objects.requireNonNull(Potion.getPotionById(3)));


    Setting autoBreak = setmgr.add(new Setting("autoBreak", this, false, mode, "INSTANT", 1));
    Setting picOnly = setmgr.add(new Setting("picOnly", this, false, mode, "INSTANT", 2));
    Setting Blockair = setmgr.add(new Setting("Blockair", this, false, mode, "INSTANT", 3));
    Setting delay = setmgr.add(new Setting("delay", this, 1, 0, 5, true, mode, "INSTANT", 4));

    private BlockPos renderBlock;
    private BlockPos lastBlock;
    private boolean packetCancel = false;
    public static final TimerUtils timer = new TimerUtils();
    private EnumFacing direction;


    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (renderBlock != null) {
            //  drawBlock(renderBlock, 255, 0, 255, true);
        }
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (mode.getValString().equalsIgnoreCase("INSTANT"))
            if (side == Connection.Side.OUT) {
                if (packet instanceof CPacketPlayerDigging) {
                    CPacketPlayerDigging digPacket = (CPacketPlayerDigging) packet;
                    return digPacket.getAction() != CPacketPlayerDigging.Action.START_DESTROY_BLOCK || !packetCancel;
                }
            }
        return true;
    }

    private boolean canBreak(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().getBlockHardness(mc.world.getBlockState(pos), mc.world, pos) != -1;
    }

    public void setTarget(BlockPos pos) {
        renderBlock = pos;
        packetCancel = false;
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
        packetCancel = true;
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
        direction = EnumFacing.DOWN;
        lastBlock = pos;
    }

    @Override
    public void onClientTick(ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("INSTANT")) {
            if (renderBlock != null) {
                if (autoBreak.getValBoolean() && timer.isDelay((long) delay.getValDouble() * 1000)) {
                    if (picOnly.getValBoolean() && !(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.DIAMOND_PICKAXE))
                        return;
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, renderBlock, direction));
                    timer.setLastMS();
                }
            }
            mc.playerController.blockHitDelay = 0;
        }
        if (mode.getValString().equalsIgnoreCase("NoDelay"))
            mc.playerController.blockHitDelay = 0;
        if (mode.getValString().equalsIgnoreCase("potion")) {
            if (mc.player.onGround) {
                mc.player.addPotionEffect(Haste);
            }
        }
        if (mode.getValString().equalsIgnoreCase("Packet")) {
            mc.player.removeActivePotionEffect(Haste.getPotion());

            if (mc.playerController.curBlockDamageMP > 0.7F) {
                mc.playerController.curBlockDamageMP = 1.0F;
            }
            mc.playerController.blockHitDelay = 0;
        }

        super.onClientTick(event);
    }

    @Override
    public void onLeftClickBlock(LeftClickBlock event) {
        if (mode.getValString().equalsIgnoreCase("INSTANT")) {
            if (canBreak(event.getPos())) {
                if (lastBlock == null || event.getPos().x != lastBlock.x || event.getPos().y != lastBlock.y || event.getPos().z != lastBlock.z) {
                    packetCancel = false;
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), Objects.requireNonNull(event.getFace())));
                }
                packetCancel = true;
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), Objects.requireNonNull(event.getFace())));
                renderBlock = event.getPos();
                lastBlock = event.getPos();
                direction = event.getFace();
                if (Blockair.getValBoolean()) {
                    mc.playerController.onPlayerDestroyBlock(event.getPos());
                    mc.world.setBlockToAir(event.getPos());
                }
                event.setResult(Event.Result.DENY);
            }
        }
        if (mode.getValString().equalsIgnoreCase("packet")) {
            float progress = mc.playerController.curBlockDamageMP + BlockUtils.getHardness(event.getPos());
            if (progress >= 1) {
                return;
            }
            Wrapper.INSTANCE.sendPacket(new CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(),
                    mc.objectMouseOver.sideHit));

        }
        super.onLeftClickBlock(event);
    }

    @Override
    public void onDisable() {
        mc.player.removeActivePotionEffect(Haste.getPotion());
        super.onDisable();
    }

}
