package Method.Client.module.player;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.BlockUtils;
import Method.Client.utils.system.Wrapper;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;

public class FastBreak extends Module {
    public FastBreak() {
        super("FastBreak", Keyboard.KEY_NONE, Category.PLAYER, "FastBreak");
    }

    Setting mode = setmgr.add(new Setting("break mode", this, "potion", "Potion", "Vanilla", "Packet", "INSTANT", "NoDelay"));

    PotionEffect Haste = new PotionEffect(Objects.requireNonNull(Potion.getPotionById(3)));

    @Override
    public void onClientTick(ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("NoDelay")) {
            mc.playerController.blockHitDelay = 0;
        }
        if (mode.getValString().equalsIgnoreCase("Vanilla")) {

            mc.playerController.blockHitDelay = 0;
            mc.player.removeActivePotionEffect(Haste.getPotion());
        }

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
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), Objects.requireNonNull(event.getFace())));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFace()));
            mc.playerController.onPlayerDestroyBlock(event.getPos());
            mc.world.setBlockToAir(event.getPos());
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
