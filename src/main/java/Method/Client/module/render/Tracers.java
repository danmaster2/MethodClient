package Method.Client.module.render;

import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.visual.ColorUtils;
import Method.Client.utils.visual.RenderUtils;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static Method.Client.Main.setmgr;

public class Tracers extends Module {

    public Tracers() {
        super("Tracers", Keyboard.KEY_NONE, Category.RENDER, "Tracers");
    }

    Setting Player;
    Setting invis;

    Setting Box;
    Setting BoxMode;
    Setting BoxWidth;
    Setting FriendColor;
    Setting PlayerColor;
    Setting inviscolor;
    Setting LineWidth;
    Setting Distance;
    Setting Mode;
    Setting Glow;
    Setting GlowWidth;

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Head");
        options.add("Body");
        options.add("Feet");
        setmgr.add(Mode = new Setting("Player mode", this, "Head", options));
        setmgr.add(Player = new Setting("Player", this, true));
        setmgr.add(invis = new Setting("Invis", this, false));
        setmgr.add(Glow = new Setting("Glow", this, true));
        setmgr.add(GlowWidth = new Setting("GlowWidth", this, 0, 0, 1, false));

        setmgr.add(Box = new Setting("Box", this, false));
        setmgr.add(BoxMode = new Setting("Box", this, "Outline", BlockEspOptions()));
        setmgr.add(BoxWidth = new Setting("BoxWidth", this, 1, 0, 3, false));

        setmgr.add(LineWidth = new Setting("LineWidth", this, 1, 0, 3, false));
        setmgr.add(Distance = new Setting("Distance", this, 500, 0, 500, true));
        setmgr.add(FriendColor = new Setting("FriendColor", this, 0, 1, 1, 1, Player, 7));
        setmgr.add(PlayerColor = new Setting("PlayerColor", this, .44, 1, 1, 1, Player, 8));
        setmgr.add(inviscolor = new Setting("Inviscolor", this, .88, 1, 1, 1, invis, 9));

    }

    @Override
    public void onDisable() {
        for (Object object : mc.world.loadedEntityList) {
            if (object instanceof EntityPlayer) {
                EntityPlayer entity = (EntityPlayer) object;
                if (entity.isGlowing())
                    entity.setGlowing(false);
            }
        }
        super.onDisable();
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        for (Object object : mc.world.loadedEntityList) {
            if (object instanceof EntityLivingBase && !(object instanceof EntityArmorStand)) {
                EntityLivingBase entity = (EntityLivingBase) object;
                if (mc.player.getDistance(entity) < Distance.getValDouble())
                    this.render(entity);
            }
        }
        super.onRenderWorldLast(event);
    }

    void render(EntityLivingBase entity) {
        if (entity == mc.player) {
            return;
        }
        if (entity instanceof EntityPlayer && Player.getValBoolean()) {
            int y = 0;
            if (Mode.getValString().equalsIgnoreCase("Head"))
                y++;
            if (Mode.getValString().equalsIgnoreCase("Feet"))
                y--;

            if (entity.hurtTime > 0) {
                RenderUtils.RenderBlock("Tracer", RenderUtils.Boundingbb(entity, 0, y, 0, 0, y, 0), ColorUtils.rainbow().getRGB(), LineWidth.getValDouble());
                if (Box.getValBoolean())
                    RenderUtils.RenderBlock(BoxMode.getValString(), RenderUtils.Boundingbb(entity, 0, 0, 0, 0, 0, 0), ColorUtils.rainbow().getRGB(), LineWidth.getValDouble());
                return;
            }
            EntityPlayer player = (EntityPlayer) entity;
            String ID = Utils.getPlayerName(player);
            if (Glow.getValBoolean()) {
                player.setGlowing(true);
                mc.renderGlobal.entityOutlineShader.listShaders.forEach(shader -> {
                    ShaderUniform outlineRadius = shader.getShaderManager().getShaderUniform("Radius");
                    if (outlineRadius != null) outlineRadius.set((float) GlowWidth.getValDouble());
                });
            }

            if (FriendManager.friendsList.contains(ID)) {
                if (Box.getValBoolean())
                    RenderUtils.RenderBlock(BoxMode.getValString(), RenderUtils.Boundingbb(entity, 0, 0, 0, 0, 0, 0), FriendColor.getcolor(), LineWidth.getValDouble());
                RenderUtils.RenderBlock("Tracer", RenderUtils.Boundingbb(entity, 0, y, 0, 0, y, 0), FriendColor.getcolor(), LineWidth.getValDouble());
                return;
            }
            RenderUtils.RenderBlock("Tracer", RenderUtils.Boundingbb(entity, 0, y, 0, 0, y, 0), PlayerColor.getcolor(), LineWidth.getValDouble());
            if (Box.getValBoolean())
                RenderUtils.RenderBlock(BoxMode.getValString(), RenderUtils.Boundingbb(entity, 0, 0, 0, 0, 0, 0), PlayerColor.getcolor(), LineWidth.getValDouble());
            if (entity.isInvisible() && invis.getValBoolean()) {
                RenderUtils.RenderBlock("Tracer", RenderUtils.Boundingbb(entity, 0, y, 0, 0, y, 0), inviscolor.getcolor(), LineWidth.getValDouble());
                if (Box.getValBoolean())
                    RenderUtils.RenderBlock(BoxMode.getValString(), RenderUtils.Boundingbb(entity, 0, 0, 0, 0, 0, 0), inviscolor.getcolor(), LineWidth.getValDouble());
            }
        }

    }


}
