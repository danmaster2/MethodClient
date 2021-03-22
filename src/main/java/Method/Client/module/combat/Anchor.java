package Method.Client.module.combat;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Anchor extends Module {
    Setting maxheight = setmgr.add(maxheight = new Setting("max height", this, 15, 0, 255, false));
    Setting JumpOut = setmgr.add(JumpOut = new Setting("JumpOut", this, true));
    Setting Onerun = setmgr.add(Onerun = new Setting("Run Once", this, true));
    BlockPos playerPos;

    private final TimerUtils timer = new TimerUtils();
    private boolean WasJump = false;

    public Anchor() {
        super("Anchor", Keyboard.KEY_NONE, Category.COMBAT, "Anchor to Holes");
    }


    @Override
    public void onClientTick(ClientTickEvent event) {
        if (WasJump) {
            if (timer.isDelay(1800)) {
                timer.setLastMS();
                WasJump = false;
            }
            return;
        }

        if (mc.player.posY < 0) {
            return;
        }

        if (mc.player.posY > maxheight.getValDouble()) {
            return;
        }

        double newX = mc.player.posX;
        double newZ = mc.player.posZ;

        newX = mc.player.posX > Math.round(mc.player.posX) ? Math.round(mc.player.posX) + 0.5 : newX;
        newX = mc.player.posX < Math.round(mc.player.posX) ? Math.round(mc.player.posX) - 0.5 : newX;

        newZ = mc.player.posZ > Math.round(mc.player.posZ) ? Math.round(mc.player.posZ) + 0.5 : newZ;
        newZ = mc.player.posZ < Math.round(mc.player.posZ) ? Math.round(mc.player.posZ) - 0.5 : newZ;

        //specifies the x and z coordinates to be centered- should prevent people from getting stuck up on side blocks

        playerPos = new BlockPos(newX, mc.player.posY, newZ);

        if (mc.world.getBlockState(playerPos).getBlock() != Blocks.AIR) {
            return;
        }

        if (JumpOut.getValBoolean())
            if (mc.gameSettings.keyBindJump.isPressed())
                if (mc.world.getBlockState(playerPos.east()).getBlock() != Blocks.AIR
                        && mc.world.getBlockState(playerPos.west()).getBlock() != Blocks.AIR
                        && mc.world.getBlockState(playerPos.north()).getBlock() != Blocks.AIR
                        && mc.world.getBlockState(playerPos.south()).getBlock() != Blocks.AIR) {
                    WasJump = true;
                    return;
                }
        //looks to see if the block below the player is "surrounded"
        if (mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.AIR //1 block
                && mc.world.getBlockState(playerPos.down().east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down().west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down().north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down().south()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2)).getBlock() != Blocks.AIR) {

            double lMotionX = (Math.floor(mc.player.posX) + .5) - mc.player.posX;
            double lMotionZ = (Math.floor(mc.player.posZ) + .5) - mc.player.posZ;
            mc.player.motionX = lMotionX / 2;
            mc.player.motionZ = lMotionZ / 2;
        } else if (mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.AIR //2 block
                && mc.world.getBlockState(playerPos.down(2)).getBlock() == Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).south()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(3)).getBlock() != Blocks.AIR) {

            double lMotionX = (Math.floor(mc.player.posX) + .5) - mc.player.posX;
            double lMotionZ = (Math.floor(mc.player.posZ) + .5) - mc.player.posZ;
            mc.player.motionX = lMotionX / 2;
            mc.player.motionZ = lMotionZ / 2;
        }
        if (Onerun.getValBoolean())
            this.toggle();
        super.onClientTick(event);
    }


}
