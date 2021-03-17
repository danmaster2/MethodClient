
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.EntityPlayerJumpEvent;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Jump extends Module {
    /////////////////////

    Setting mode = setmgr.add(new Setting("Mode", this, "PotionHJ", "PotionHJ", "Ymotion", "JumpPos", "Random", "Packet", "None"));
    Setting height = setmgr.add(new Setting("height", this, 1, .5, 20, true, mode, "PotionHJ", 1));
    Setting Ymotion = setmgr.add(new Setting("Ymotion", this, 1, 0, 2, false, mode, "Ymotion", 1));
    Setting AirJump = setmgr.add(new Setting("AirJump", this, false));
    Setting RapidJump = setmgr.add(new Setting("RapidJump", this, false));


    public Jump() {
        super("Jump", Keyboard.KEY_NONE, Category.MOVEMENT, "Jump Mod");
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("PotionHJ")) {
            PotionEffect nv = new PotionEffect(MobEffects.JUMP_BOOST, 3, (int) height.getValDouble());
            mc.player.addPotionEffect(nv);
        }

        if (mode.getValString().equalsIgnoreCase("JumpPos") && mc.gameSettings.keyBindJump.pressed) {
            mc.player.setPosition(mc.player.lastTickPosX, mc.player.serverPosY + 0.139F, mc.player.serverPosZ);

        }

        if (mode.getValString().equalsIgnoreCase("Packet")) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));
        }
        if (AirJump.getValBoolean() && !mc.player.onGround)
            if (mc.gameSettings.keyBindJump.isPressed())
                mc.player.jump();
        if (RapidJump.getValBoolean() && mc.player.onGround && mc.gameSettings.keyBindJump.pressed) {
            mc.player.jump();
        }
    }

    @Override
    public void onPlayerJump(EntityPlayerJumpEvent event) {
        if (mode.getValString().equalsIgnoreCase("Random")) {
            mc.player.motionY += Math.random() / 10;
            mc.player.posY += Math.random() / 10;
        }
        if (mode.getValString().equalsIgnoreCase("Ymotion")) {
            mc.player.motionY = mc.player.motionY * Ymotion.getValDouble();
        }
    }

    @Override
    public void onDisable() {
        mc.player.removeActivePotionEffect(MobEffects.JUMP_BOOST);
        super.onDisable();
    }

}
