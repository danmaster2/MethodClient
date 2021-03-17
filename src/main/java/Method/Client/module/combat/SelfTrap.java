package Method.Client.module.combat;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.valid;

public class SelfTrap extends Module {
    public SelfTrap() {
        super("SelfTrap", Keyboard.KEY_NONE, Category.COMBAT, "SelfTrap");
    }

    private BlockPos trap_pos;

    public Setting place_mode = setmgr.add(new Setting("cage", this, "Extra", "Extra", "Face", "Normal", "Feet"));
    public Setting rotate = setmgr.add(new Setting("rotate", this, false));
    public Setting Hand = setmgr.add(new Setting("Hand", this, "Mainhand", "Mainhand", "Offhand", "Both", "None"));

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        final Vec3d pos = Utils.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        trap_pos = new BlockPos(pos.x, pos.y + 2, pos.z);
        if (is_trapped()) {
            toggle();
            return;
        }

        Utils.ValidResult result = valid(trap_pos);

        if (result == Utils.ValidResult.AlreadyBlockThere && !mc.world.getBlockState(trap_pos).getMaterial().isReplaceable()) {
            return;
        }

        if (result == Utils.ValidResult.NoNeighbors) {

            BlockPos[] tests = {trap_pos.north(), trap_pos.south(), trap_pos.east(),
                    trap_pos.west(), trap_pos.up(), trap_pos.down().west()};

            for (BlockPos pos_ : tests) {

                Utils.ValidResult result_ = valid(pos_);

                if (result_ == Utils.ValidResult.NoNeighbors || result_ == Utils.ValidResult.NoEntityCollision)
                    continue;

                if (Utils.placeBlock(pos_, rotate.getValBoolean(), Hand)) {
                    return;
                }

            }

            return;


        }

        Utils.placeBlock(trap_pos, rotate.getValBoolean(), Hand);
    }


    public boolean is_trapped() {

        if (trap_pos == null) return false;

        IBlockState state = mc.world.getBlockState(trap_pos);

        return state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.WATER && state.getBlock() != Blocks.LAVA;

    }


}
