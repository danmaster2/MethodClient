package Method.Client.module.misc;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.BlockSlime;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class NoSlowdown extends Module {

    public NoSlowdown() {
        super("NoSlowdown", Keyboard.KEY_NONE, Category.MISC, "No more slow");
    }

    private boolean sneaking;

    Setting web = setmgr.add(new Setting("webs", this, false));
    Setting Webfall = setmgr.add(new Setting("Webfall", this, false));
    Setting Eat = setmgr.add(new Setting("Eat", this, false));
    Setting Slowdownbypass = setmgr.add(new Setting("Slowdown Bypass", this, false));
    Setting Breakdelay = setmgr.add(new Setting("Breakdelay", this, false));
    Setting Slimeblock = setmgr.add(new Setting("Slimeblock", this, false));
    Setting NoIceSlip = setmgr.add(new Setting("NoIceSlip", this, false));

    @Override
    public void onDisable() {
        Blocks.ICE.slipperiness = 0.98F;
        Blocks.PACKED_ICE.slipperiness = 0.98F;
        Blocks.FROSTED_ICE.slipperiness = 0.98F;
    }

    @Override
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (Slowdownbypass.getValBoolean()) {
            if (this.sneaking && !mc.player.isHandActive()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.sneaking = false;
            }
            if (!this.sneaking && mc.player.isHandActive()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                this.sneaking = true;
            }
        }
        if (NoIceSlip.getValBoolean()) {
            Blocks.ICE.slipperiness = 0F;
            Blocks.PACKED_ICE.slipperiness = 0F;
            Blocks.FROSTED_ICE.slipperiness = 0F;
        }
        if (Slimeblock.getValBoolean()) {
            BlockPos pos = new BlockPos(Math.floor(mc.player.posX), Math.ceil(mc.player.posY), Math.floor(mc.player.posZ));
            if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() instanceof BlockSlime && mc.player.onGround) {
                mc.player.motionY = 1.5;
            }
        }


        if (mc.player.isHandActive() && Eat.getValBoolean()) {
            mc.player.moveForward *= 5.0f;
            mc.player.moveStrafing *= 5.0f;
            mc.playerController.syncCurrentPlayItem();
            Wrapper.INSTANCE.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
        }
        if (web.getValBoolean()) {
            mc.player.isInWeb = false;

        }
        if (Webfall.getValBoolean()) {
            if (!mc.player.onGround && mc.player.fallDistance > 3.0F)
                mc.player.motionY = -0.22000000000000003;
        }

        if (Breakdelay.getValBoolean()) {
            mc.playerController.blockHitDelay = 0;
        }
    }

}
