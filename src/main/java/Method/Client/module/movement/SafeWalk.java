
package Method.Client.module.movement;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.managers.Setting;
import Method.Client.utils.Patcher.Events.PlayerMoveEvent;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static Method.Client.Main.setmgr;

public class SafeWalk extends Module {

    /////////////////////
    Setting mode=setmgr.add( new Setting("Mode", this, "Sneak", "Sneak","Normal"));
    Setting EdgeStop=setmgr.add( new Setting("Edge Stop", this, true, mode, "Normal", 2));
    Setting SlowOnEdge=setmgr.add( new Setting("Slow on Edge", this, false));

    public SafeWalk() {
        super("SafeWalk", Keyboard.KEY_NONE, Category.MOVEMENT, "SafeWalk, Safe ledge");
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event){


    }
    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("Normal")) {
            if (mc.player.onGround
                    && !mc.gameSettings.keyBindJump.isPressed()
                    && !isCollidable(mc.world
                    .getBlockState(new BlockPos(mc.player.getPositionVector().add(new Vec3d(0, -0.5, 0))))
                    .getBlock())) {
                if (SlowOnEdge.getValBoolean()) {
                    mc.player.motionX -= mc.player.motionX;
                    mc.player.motionZ -= mc.player.motionZ;
                }
                if (EdgeStop.getValBoolean())
                    mc.player.setPosition(mc.player.prevPosX, mc.player.posY, mc.player.prevPosZ);
            }
        }
        if (mode.getValString().equalsIgnoreCase("Sneak")) {
            if (mc.player.onGround
                    && !mc.gameSettings.keyBindJump.isPressed()
                    && !isCollidable(mc.world
                    .getBlockState(new BlockPos(mc.player.getPositionVector().add(new Vec3d(0, -0.5, 0))))
                    .getBlock())) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                if (SlowOnEdge.getValBoolean()) {
                    mc.player.motionX -= mc.player.motionX;
                    mc.player.motionZ -= mc.player.motionZ;
                }
            } else if (!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()))
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
    }

    public static boolean isCollidable(Block block) {
        return block != Blocks.AIR && block != Blocks.BEETROOTS && block != Blocks.CARROTS && block != Blocks.DEADBUSH
                && block != Blocks.DOUBLE_PLANT && block != Blocks.FLOWING_LAVA && block != Blocks.FLOWING_WATER
                && block != Blocks.LAVA && block != Blocks.MELON_STEM && block != Blocks.NETHER_WART
                && block != Blocks.POTATOES && block != Blocks.PUMPKIN_STEM && block != Blocks.RED_FLOWER
                && block != Blocks.RED_MUSHROOM && block != Blocks.REDSTONE_TORCH && block != Blocks.TALLGRASS
                && block != Blocks.TORCH && block != Blocks.UNLIT_REDSTONE_TORCH && block != Blocks.YELLOW_FLOWER
                && block != Blocks.VINE && block != Blocks.WATER && block != Blocks.WEB && block != Blocks.WHEAT;
    }

}
