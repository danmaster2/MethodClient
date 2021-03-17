package Method.Client.module.render;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class NoRender extends Module {

    Setting stopParticles = setmgr.add(new Setting("stopParticles", this, false));
    Setting stopExplosions = setmgr.add(new Setting("stop Explosions", this, false));
    Setting helmet = setmgr.add(new Setting("helmet", this, false));
    Setting portal = setmgr.add(new Setting("portal", this, false));
    Setting crosshair = setmgr.add(new Setting("crosshair", this, false));
    Setting bosshealth = setmgr.add(new Setting("bosshealth", this, false));
    Setting bossinfo = setmgr.add(new Setting("bossinfo", this, false));
    Setting armor = setmgr.add(new Setting("armor", this, false));
    Setting health = setmgr.add(new Setting("health", this, false));
    Setting food = setmgr.add(new Setting("food", this, false));
    Setting air = setmgr.add(new Setting("air", this, false));
    Setting hotbar = setmgr.add(new Setting("hotbar", this, false));
    Setting experience = setmgr.add(new Setting("experience", this, false));
    Setting horsehealth = setmgr.add(new Setting("horsehealth", this, false));
    Setting horsejump = setmgr.add(new Setting("horsejump", this, false));
    Setting chat = setmgr.add(new Setting("chat", this, false));
    Setting playerlist = setmgr.add(new Setting("playerlist", this, false));
    Setting potionicon = setmgr.add(new Setting("potionicon", this, false));
    Setting subtitles = setmgr.add(new Setting("subtitles", this, false));
    Setting fpsgraph = setmgr.add(new Setting("fpsgraph", this, false));
    Setting vignette = setmgr.add(new Setting("vignette", this, false));
    Setting Blockoverlay = setmgr.add(new Setting("Liquidoverlay", this, false));
    Setting Liquidoverlay = setmgr.add(new Setting("Blockoverlay", this, false));

    public NoRender() {
        super("NoRender", Keyboard.KEY_NONE, Category.RENDER, "NoRender");
    }


    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (packet instanceof SPacketExplosion && stopExplosions.getValBoolean()) {
            return false;
        }
        return !this.stopParticles.getValBoolean() || !(packet instanceof SPacketParticles);
    }

    @Override
    public void fogDensity(EntityViewRenderEvent.FogDensity event) {
        if (Liquidoverlay.getValBoolean()) {
            if (event.getState().getMaterial().equals(Material.WATER) || event.getState().getMaterial().equals(Material.LAVA)) {
                event.setDensity(0);
                event.setCanceled(true);
            }
        }

    }

    @Override
    public void RenderBlockOverlayEvent(RenderBlockOverlayEvent event) {
        if (Blockoverlay.getValBoolean()) {
            event.setCanceled(true);
        }
    }

    @Override
    public void RendergameOverlay(RenderGameOverlayEvent event) {
        switch (event.getType()) {
            case HELMET: {
                event.setCanceled(helmet.getValBoolean());
                break;
            }
            case PORTAL: {
                event.setCanceled(portal.getValBoolean());
                break;
            }
            case CROSSHAIRS: {
                event.setCanceled(crosshair.getValBoolean());
                break;
            }
            case BOSSHEALTH: {
                event.setCanceled(bosshealth.getValBoolean());
                break;
            }
            case BOSSINFO: {
                event.setCanceled(bossinfo.getValBoolean());
                break;
            }
            case ARMOR: {
                event.setCanceled(armor.getValBoolean());
                break;
            }
            case HEALTH: {
                event.setCanceled(health.getValBoolean());
                break;
            }
            case FOOD: {
                event.setCanceled(food.getValBoolean());
                break;
            }
            case AIR: {
                event.setCanceled(air.getValBoolean());
                break;
            }
            case HOTBAR: {
                event.setCanceled(hotbar.getValBoolean());
                break;
            }
            case EXPERIENCE: {
                event.setCanceled(experience.getValBoolean());
                break;
            }
            case HEALTHMOUNT: {
                if (mc.player.isRiding())
                    event.setCanceled(horsehealth.getValBoolean());
                break;
            }
            case JUMPBAR: {
                if (mc.player.isRiding())
                    event.setCanceled(horsejump.getValBoolean());
                break;
            }
            case CHAT: {
                event.setCanceled(chat.getValBoolean());
                break;
            }
            case PLAYER_LIST: {
                event.setCanceled(playerlist.getValBoolean());
                break;
            }
            case POTION_ICONS: {
                event.setCanceled(potionicon.getValBoolean());
                break;
            }
            case SUBTITLES: {
                event.setCanceled(subtitles.getValBoolean());
                break;
            }
            case FPS_GRAPH: {
                event.setCanceled(fpsgraph.getValBoolean());
                break;
            }
            case VIGNETTE: {
                event.setCanceled(vignette.getValBoolean());
                break;
            }
        }
    }
}



