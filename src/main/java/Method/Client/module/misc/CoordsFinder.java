
package Method.Client.module.misc;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;


public class CoordsFinder extends Module {

    /////////////////////

    public CoordsFinder() {
        super("CoordsFinder", Keyboard.KEY_NONE, Category.MISC, "Coords Finder exploit");
    }

    Setting BossDetector = setmgr.add(new Setting("Boss detector", this, true));
    Setting logLightning = setmgr.add(new Setting("logLightning", this, true));
    Setting minLightningDist = setmgr.add(new Setting("minLightningDist", this, 32, 0, 100, true, logLightning, 3));
    Setting logWolf = setmgr.add(new Setting("logWolf", this, true));
    Setting minWolfDist = setmgr.add(new Setting("minWolfDist", this, 256, 0, 1024, true, logWolf, 3));
    Setting logPlayer = setmgr.add(new Setting("logPlayer", this, true));
    Setting minPlayerDist = setmgr.add(new Setting("minPlayerDist", this, 256, 0, 1024, true, logPlayer, 3));

    private boolean pastDistance(EntityPlayer player, BlockPos pos, double dist) {
        return player.getDistanceSqToCenter(pos) >= Math.pow(dist, 2);
    }


    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (BossDetector.getValBoolean()) {
            if (packet instanceof SPacketEffect) {
                final SPacketEffect sPacketEffect = (SPacketEffect) packet;
                BlockPos pos = new BlockPos(sPacketEffect.getSoundPos().x, sPacketEffect.getSoundPos().y, sPacketEffect.getSoundPos().z);

                switch (sPacketEffect.getSoundType()) {
                    case 1023: {
                        ChatUtils.message("Wither Spawned " + pos.x + " Y:" + pos.y + " Z:" + pos.z);
                        break;
                    }
                    case 1028: {
                        ChatUtils.message("Ender Dragon Defeated " + pos.x + " Y:" + pos.y + " Z:" + pos.z);
                        break;
                    }
                    case 1038: {
                        ChatUtils.message("End Portal Activated " + pos.x + " Y:" + pos.y + " Z:" + pos.z);
                        break;
                    }
                }
            }
        }

        if (logLightning.getValBoolean() && packet instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet2 = (SPacketSoundEffect) packet;

            if (packet2.getSound() != SoundEvents.ENTITY_LIGHTNING_THUNDER) {
                return true;
            }

            BlockPos pos = new BlockPos(packet2.getX(), packet2.getY(), packet2.getZ());

            if (pastDistance(mc.player, pos, minLightningDist.getValDouble())) {
                ChatUtils.warning("Lightning strike At X:" + pos.x + " Y:" + pos.y + " Z:" + pos.z);
            }
        } else if (packet instanceof SPacketEntityTeleport) {
            SPacketEntityTeleport sPacketEntityTeleport = (SPacketEntityTeleport) packet;
            Entity teleporting = mc.world.getEntityByID(sPacketEntityTeleport.getEntityId());
            BlockPos pos = new BlockPos(sPacketEntityTeleport.getX(), sPacketEntityTeleport.getY(), sPacketEntityTeleport.getZ());

            if (logWolf.getValBoolean() && teleporting instanceof EntityWolf) {
                if (pastDistance(mc.player, pos, minWolfDist.getValDouble())) {
                    ChatUtils.warning("Wolf Teleport At X:" + pos.x + " Y:" + pos.y + " Z:" + pos.z);
                }
            } else if (logPlayer.getValBoolean() && teleporting instanceof EntityPlayer) {
                if (pastDistance(mc.player, pos, minPlayerDist.getValDouble())) {
                    ChatUtils.warning(teleporting.getName() + " Teleported to X:" + pos.x + " Y:" + pos.y + " Z:" + pos.z);
                }
            }
        }

        return true;
    }

}