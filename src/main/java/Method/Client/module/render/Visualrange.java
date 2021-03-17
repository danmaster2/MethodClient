package Method.Client.module.render;


import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.ChatUtils;
import Method.Client.utils.visual.ColorUtils;
import Method.Client.utils.visual.RenderUtils;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;

public class Visualrange extends Module {

    Setting playSound = setmgr.add(new Setting("playSound", this, true));
    Setting leaving = setmgr.add(new Setting("leaving", this, true));
    Setting Box = setmgr.add(new Setting("Box", this, true));
    Setting color = setmgr.add(new Setting("Logoff", this, 1, 1, 1, 1));
    Setting Mode = setmgr.add(new Setting("Mode", this, "Outline", BlockEspOptions()));
    Setting LineWidth = setmgr.add(new Setting("LineWidth", this, 1, 0, 3, false));
    Setting ShowCoords = setmgr.add(new Setting("ShowCoords", this, true));
    private List<EntityPlayer> knownPlayers;
    public static List<EntityPlayer> logoutPositions = Lists.newArrayList();
    public Visualrange() {
        super("Visualrange", Keyboard.KEY_NONE, Category.RENDER, "Visualrange");
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        List<EntityPlayer> tempplayer = new ArrayList<>();
        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer entity1 = (EntityPlayer) entity;
                if (entity1.getName().equals(mc.player.getName())) continue;
                if (!knownPlayers.contains(entity1)) {
                    if (Box.getValBoolean())
                        logoutPositions.remove(entity1);
                    knownPlayers.add(entity1);
                    if (playSound.getValBoolean())
                        mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f));
                    ChatUtils.message((FriendManager.isFriend(entity1.getName()) ? ChatFormatting.GREEN.toString() : ChatFormatting.RED.toString() + entity1.getName() + ChatFormatting.RESET.toString() + (ShowCoords.getValBoolean() ? " Joined at, x: " + entity1.getPosition().getX() + " y: " + entity1.getPosition().getY() + " z: " + entity1.getPosition().getZ() : " Joined!")));
                }
                tempplayer.add(entity1);
            }
        }
        for (EntityPlayer knownPlayer : knownPlayers) {
            if (!(tempplayer.contains(knownPlayer))) {
                knownPlayers.remove(knownPlayer);
                if (leaving.getValBoolean()) {
                    if (Box.getValBoolean())
                        logoutPositions.add(knownPlayer);
                    if (playSound.getValBoolean())
                        mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f));
                    ChatUtils.message((FriendManager.isFriend(knownPlayer.getName()) ? ChatFormatting.GREEN.toString() : ChatFormatting.RED.toString() + knownPlayer.getName() + ChatFormatting.RESET.toString() + (ShowCoords.getValBoolean() ? " Left at, x: " + knownPlayer.getPosition().getX() + " y: " + knownPlayer.getPosition().getY() + " z: " + knownPlayer.getPosition().getZ() : " Left!")));
                }
            }
        }

    }

    @Override
    public void onEnable() {
        this.knownPlayers = new ArrayList<>();
        if (mc.player != null) {
            if (mc.isSingleplayer())
                this.toggle();
        }
    }


    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (Box.getValBoolean())
            for (EntityPlayer logoutPosition : logoutPositions) {
                double renderPosX = logoutPosition.posX - mc.getRenderManager().viewerPosX;
                double renderPosY = logoutPosition.posY - mc.getRenderManager().viewerPosY;
                double renderPosZ = logoutPosition.posZ - mc.getRenderManager().viewerPosZ;

                final AxisAlignedBB bb = new AxisAlignedBB(renderPosX, renderPosY, renderPosZ,
                        renderPosX + 1,
                        renderPosY + 2,
                        renderPosZ + 1);
                RenderUtils.SimpleNametag(logoutPosition.getPositionVector(), logoutPosition.getName() + (ShowCoords.getValBoolean() ? "X: " + (int) logoutPosition.posX + " Y: " + (int) logoutPosition.posY + " Z: " + (int) logoutPosition.posZ : ""));
                RenderBlock(Mode.getValString(), bb, FriendManager.isFriend(logoutPosition.getName()) ? ColorUtils.rainbow().getRGB() : color.getcolor(), LineWidth.getValDouble());
            }
    }


}
