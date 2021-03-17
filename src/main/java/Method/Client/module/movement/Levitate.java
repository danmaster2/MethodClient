
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static Method.Client.Main.setmgr;

public class Levitate extends Module {

    /////////////////////
    Setting mode=setmgr.add(new Setting("Fly Mode", this, "Normal", "Normal","Weird","Old","MoonWalk"));
    private double startY;
    int counter;

    public Levitate() {
        super("Levitate", Keyboard.KEY_NONE, Category.MOVEMENT, "Levitate");
    }

    ///////////////////

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("Moonwalk")) {
            if (mc.player.onGround && Wrapper.mc.gameSettings.keyBindJump.isPressed()) {
                mc.player.motionY = 0.25;
            } else if (mc.player.isAirBorne && !mc.player.isInWater() && !mc.player.isOnLadder()
                    && !mc.player.isInsideOfMaterial(Material.LAVA)) {
                mc.player.motionY = 1.0E-6;
                final EntityPlayerSP player = mc.player;
                mc.player.jumpMovementFactor *= 1.21337f;
            }
        }
        if (mode.getValString().equalsIgnoreCase("Normal")) {
            mc.player.motionY = 0.0;
            if (mc.gameSettings.keyBindSneak.pressed) {
                mc.player.motionY = -0.1;
            }
            if (mc.gameSettings.keyBindJump.pressed) {
                mc.player.motionY = 0.1;
            }
        }
        if (mode.getValString().equalsIgnoreCase("Old")) {
            if (!mc.player.onGround) {
                if (mc.gameSettings.keyBindJump.pressed) {
                    mc.player.motionY = mc.player.posY < this.startY - 1.0 ? 0.2 : -0.05;
                }
            }
        }

        if (mode.getValString().equalsIgnoreCase("Weird")) {
            ++this.counter;
            if ((double) this.counter > 3.2) {
                mc.gameSettings.keyBindSneak.pressed = true;
                mc.player.motionX *= 1.2;
                mc.player.attackedAtYaw = 1.0f;
                mc.player.motionZ *= 1.2;
                this.counter = 0;
            } else {
                ++this.counter;
            }
            if ((double) this.counter > 3.7) {
                mc.gameSettings.keyBindSneak.pressed = false;
                this.counter = 0;
            }
            mc.player.onGround = true;
            mc.player.motionY = 0.0;
            mc.player.motionX *= 0.2;
            mc.player.attackedAtYaw = 1.0f;
            mc.player.motionZ *= 0.2;
            mc.player.resetPositionToBB();
            mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0E-9, mc.player.posZ);
            if (mc.player.ticksExisted % 3 == 0 && mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 0.2, mc.player.posZ)).getBlock() instanceof BlockAir) {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY + -0.0, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
            }
        }
        super.onClientTick(event);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.startY = mc.player.posY;
        if (mode.getValString().equalsIgnoreCase("Weird")) {
            mc.player.motionY = 0.42;
            int i2 = 1;
            while (i2 < 4) {
                mc.player.maxHurtTime = 9;
                mc.player.performHurtAnimation();
                mc.player.fallDistance = 0.0f;
                ++i2;
            }
        }
    }
}
