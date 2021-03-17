package Method.Client.module.movement;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static Method.Client.Main.setmgr;

public class AntiFall extends Module {

    public AntiFall() {
        super("NoFall", Keyboard.KEY_NONE, Category.MOVEMENT, "Take no fall damage");
    }

    private int state;
    private double fall;
    Setting mode = setmgr.add( new Setting("Mode", this, "Vanilla", "Vanilla", "LAAC", "Hypixel", "SpoofGround",
            "NoGround", "AAC", "AAC3.3.15", "Spartan", "Quick", "NCP"));
    public TimerUtils timer = new TimerUtils();

    @Override
    public void onEnable() {
        super.onEnable();
        this.fall = 0.0D;
    }

    public double getDistanceToGround() {
        double var1;

        for (int var3 = 0; var3 < 256; ++var3) {
            BlockPos var4 = new BlockPos(mc.player.posX, mc.player.posY - (double) var3, mc.player.posZ);
            if (mc.world.getBlockState(var4).getBlock() != Blocks.AIR && mc.world.getBlockState(var4).getBlock() != Blocks.GRASS && mc.world.getBlockState(var4).getBlock() != Blocks.TALLGRASS && mc.world.getBlockState(var4).getBlock() != Blocks.RED_FLOWER && mc.world.getBlockState(var4).getBlock() != Blocks.YELLOW_FLOWER) {
                var1 = mc.player.posY - (double) var4.getY();
                return var1 - 1.0D;
            }
        }

        return 256.0D;
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (mode.getValString().equalsIgnoreCase("NCP")) {
            Block block = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 6.0d, mc.player.posZ)).getBlock();
            Block block2 = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 5.0d, mc.player.posZ)).getBlock();
            Block block3 = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 4.0d, mc.player.posZ)).getBlock();
            if (!(block == Blocks.AIR && block2 == Blocks.AIR && block3 == Blocks.AIR) && mc.player.fallDistance > 2.0f) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1d, mc.player.posZ, false));
                mc.player.motionY = -10.0d;
                mc.player.fallDistance = MathHelper.SQRT_2;
            }
        }
        if (mode.getValString().equalsIgnoreCase("Vanilla"))
            if (mc.player.fallDistance > 2)
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer(true));

        if (mode.getValString().equalsIgnoreCase("Quick") && (double) mc.player.fallDistance > 3.1D) {
            if (this.getDistanceToGround() > 40.0D) {
                return;
            }

            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.5D, mc.player.posZ, true));
            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.5D, mc.player.posZ, true));
            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, -9.0D, mc.player.posZ, true));
            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + this.getDistanceToGround(), mc.player.posZ, true));
            mc.player.motionY += 0.3D;


        }

        if (mode.getValString().equalsIgnoreCase("LAAC")) {
            if (mc.player != null && mc.world != null && mc.player.fallDistance > 2F) {
                if (mc.player.ticksExisted % 6 == 0) {
                    mc.player.setPosition(mc.player.posX, mc.player.posY - mc.player.fallDistance, mc.player.posZ);
                }
            }
        }
        if (mode.getValString().equalsIgnoreCase("AAC")) {
            if (mc.player.fallDistance > 2) {
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer(true));
                state = 2;
            } else if (state == 2 && mc.player.fallDistance < 2) {
                mc.player.motionY = 0.1D;
                state = 3;
            }
            switch (state) {
                case 3:
                    mc.player.motionY = 0.1D;
                    state = 4;
                    break;
                case 4:
                    mc.player.motionY = 0.1D;
                    state = 5;
                    break;
                case 5:
                    mc.player.motionY = 0.1D;
                    state = 1;
                    break;
            }
        }

        if (mode.getValString().equalsIgnoreCase("aac3.3.15")) {
            if (mc.player.fallDistance > 2) {
                if (!mc.isIntegratedServerRunning())
                    Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, Double.NaN, mc.player.posZ, false));
                mc.player.fallDistance = -9999;
            }
        }
        if (mode.getValString().equalsIgnoreCase("spartan")) {
            timer.reset();
            if (mc.player.fallDistance > 1.5 && timer.hasReached(10)) {
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX,
                        mc.player.posY + 10, mc.player.posZ, true));
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX,
                        mc.player.posY - 10, mc.player.posZ, true));
                timer.reset();
            }

        }
        if (mode.getValString().equalsIgnoreCase("hypixel")) {
            if (!mc.player.onGround) {
                if (mc.player.motionY < -0.08D)
                    this.fall -= mc.player.motionY;
                if (this.fall > 2.0D) {
                    this.fall = 0.0D;

                    mc.player.onGround = false;
                }
            }
            this.fall = 0.0D;

        }
        super.onClientTick(event);
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (packet instanceof CPacketPlayer) {
            final CPacketPlayer playerPacket = (CPacketPlayer) packet;
            if (mode.getValString().equalsIgnoreCase("SpoofGround"))
                playerPacket.onGround = true;
            if (mode.getValString().equalsIgnoreCase("NoGround"))
                playerPacket.onGround = false;
        }
        return true;
    }
}
