package Method.Client.module.render;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.module.combat.TotemPop;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

import static Method.Client.Main.setmgr;
import static org.lwjgl.opengl.GL11.*;

public class NameTags extends Module {

    public static boolean toggled = false;

    public NameTags() {
        super("NameTags", Keyboard.KEY_NONE, Category.RENDER, "NameTags");
    }

    public Setting Scale = setmgr.add( new Setting("Scale", this, 2, 0, 4, false));
    public Setting armor = setmgr.add( new Setting("armor", this, true));
    public Setting Background = setmgr.add( new Setting("Background", this, 0, 1, .01, .22));
    public  Setting TextColor = setmgr.add( new Setting("Name", this, 0, 1, 1, 1));
    public Setting ScaleMode = setmgr.add( new Setting("Armor Mode", this, "H", "H", "V"));
    public Setting Gamemode = setmgr.add( new Setting("Gamemode", this, true));
    public Setting Player = setmgr.add( new Setting("Player", this, true));
    public Setting Ping = setmgr.add( new Setting("Ping", this, true));
    public Setting Mob = setmgr.add( new Setting("Mob", this, false));
    public Setting Hostile = setmgr.add( new Setting("Hostile", this, false));
    public Setting Totem = setmgr.add( new Setting("Totem Pops", this, false));
    public Setting Healthmode = setmgr.add( new Setting("Health Mode", this, "Number", "Number", "Bar"));

    @Override
    public void onDisable() {
        toggled = false;
    }

    @Override
    public void onEnable() {
        toggled = true;
    }


    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        for (Entity object : mc.world.loadedEntityList) {
            if ((Player.getValBoolean() && object instanceof EntityPlayer)
                    || (Mob.getValBoolean() && object instanceof IAnimals)
                    || (Hostile.getValBoolean() && object instanceof IMob)) {
                assert object instanceof EntityLivingBase;
                if (object != mc.player)
                    this.Runnametag((EntityLivingBase) object);
            }
        }
    }

    private void Runnametag(EntityLivingBase e) {
        double scale = Math.max(Scale.getValDouble() * (mc.player.getDistance(e) / 20), 2);

        StringBuilder healthBuilder = new StringBuilder();
        for (int i = 0; i < e.getHealth(); i++) healthBuilder.append(ChatFormatting.GREEN + "|");
        StringBuilder health = new StringBuilder(healthBuilder.toString());
        for (int i = 0; i < MathHelper.clamp(e.getAbsorptionAmount(), 0, e.getMaxHealth() - e.getHealth()); i++)
            health.append(ChatFormatting.RED + "|");
        for (int i = 0; i < e.getMaxHealth() - (e.getHealth() + e.getAbsorptionAmount()); i++)
            health.append(ChatFormatting.YELLOW + "|");
        if (e.getAbsorptionAmount() - (e.getMaxHealth() - e.getHealth()) > 0) {
            health.append(ChatFormatting.BLUE + " +").append((int) (e.getAbsorptionAmount() - (e.getMaxHealth() - e.getHealth())));
        }

        String tag = "";
        if (Totem.getValBoolean()) {
            if (e instanceof EntityPlayer && ModuleManager.getModuleByName("TotemPop").isToggled()) {
                tag += " T:" + TotemPop.getpops(e) + " ";
            }
        }
        if (Ping.getValBoolean()) {
            if (e instanceof EntityPlayer) {
                try {
                    tag += " " + (int) MathHelper.clamp((float) Objects.requireNonNull(mc.getConnection()).getPlayerInfo(e.getUniqueID()).getResponseTime(), 1.0f, 300.0f) + " P ";
                } catch (NullPointerException ignored) {
                }
            }
        }
        if (Gamemode.getValBoolean()) {
            if (e instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer) e;
                if (entityPlayer.isCreative()) {
                    tag += "[C]";
                }
                if (entityPlayer.isSpectator()) {
                    tag += " [I]";
                }
                if (!entityPlayer.isAllowEdit() && !entityPlayer.isSpectator()) {
                    tag += " [A]";
                }
                if (!entityPlayer.isCreative() && !entityPlayer.isSpectator() && entityPlayer.isAllowEdit()) {
                    tag += " [S]";
                }
            }
        }

        /* Drawing Nametags */
        if (Healthmode.getValString().equalsIgnoreCase("Number")) {
            Processtext(e.getName() + " [" + (int) (e.getHealth() + e.getAbsorptionAmount()) + "/" + (int) e.getMaxHealth() + "]" + tag,
                    mc.fontRenderer.getStringWidth(e.getName() + tag + "[]") / 2,
                    TextColor, e, e.height + (0.5f * scale), scale);
        } else if (Healthmode.getValString().equalsIgnoreCase("Bar")) {
            Processtext(e.getName() + tag, mc.fontRenderer.getStringWidth(e.getName() + tag) / 2, TextColor, e, e.height + (0.5f * scale), scale);
            Processtext(health.toString(), mc.fontRenderer.getStringWidth(health.toString()) / 2, TextColor, e, e.height + (0.75f * scale), scale);
        }

        /* Drawing Items */
        if (armor.getValBoolean()) {
            double c = 0;
            double higher = Healthmode.getValString().equalsIgnoreCase("Bar") ? 0.25 : 0;
            if (ScaleMode.getValString().equalsIgnoreCase("H")) {
                drawItem(e.posX, e.posY + e.height + ((0.75 + higher) * scale), e.posZ, -2.5, 0, scale, e.getHeldItemMainhand());
                drawItem(e.posX, e.posY + e.height + ((0.75 + higher) * scale), e.posZ, 2.5, 0, scale, e.getHeldItemOffhand());

                for (ItemStack i : e.getArmorInventoryList()) {
                    drawItem(e.posX, e.posY + e.height + ((0.75 + higher) * scale), e.posZ, c + 1.5, 0, scale, i);
                    c--;
                }
            } else if (ScaleMode.getValString().equalsIgnoreCase("V")) {
                drawItem(e.posX, e.posY + e.height + ((0.75 + higher) * scale), e.posZ, -1.25, 0, scale, e.getHeldItemMainhand());
                drawItem(e.posX, e.posY + e.height + ((0.75 + higher) * scale), e.posZ, 1.25, 0, scale, e.getHeldItemOffhand());

                for (ItemStack i : e.getArmorInventoryList()) {
                    if (i.getCount() < 1) continue;
                    drawItem(e.posX, e.posY + e.height + ((0.75 + higher) * scale), e.posZ, 0, c, scale, i);
                    c++;
                }
            }
        }

    }

    private void Processtext(String s, int stringWidth, Setting getcolor, Entity entity, double rofl, double scale) {
        try {
            glSetup(entity.posX, entity.posY + rofl, entity.posZ);
            GlStateManager.scale(-0.025 * scale, -0.025 * scale, 0.025 * scale);

            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(-stringWidth - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, .25f).endVertex();
            bufferbuilder.pos(-stringWidth - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, .25f).endVertex();
            bufferbuilder.pos(stringWidth + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, .25f).endVertex();
            bufferbuilder.pos(stringWidth + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, .25f).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();

            mc.fontRenderer.drawStringWithShadow(s, -stringWidth, 0, getcolor.getcolor());

            glCleanup();
        } catch (Exception ignored) {
        }
    }

    public static void drawItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {

        glSetup(x, y, z);

        GlStateManager.scale(0.4 * scale, 0.4 * scale, 0);

        GlStateManager.translate(offX, offY, 0);
        mc.itemRenderer.renderItemSide(mc.player, item, ItemCameraTransforms.TransformType.NONE, false);
        GlStateManager.enableTexture2D();

        GlStateManager.disableLighting();

        GlStateManager.scale(-0.05F, -0.05F, 0);

        try {
            if (item.getCount() > 0) {
                int w = mc.fontRenderer.getStringWidth("x" + item.getCount()) / 2;
                mc.fontRenderer.drawStringWithShadow("x" + item.getCount(), 7 - w, 5, 0xffffff);
            }

            GlStateManager.scale(0.85F, 0.85F, 0.85F);

            int c = 0;
            for (Map.Entry<Enchantment, Integer> m : EnchantmentHelper.getEnchantments(item).entrySet()) {
                int w1 = mc.fontRenderer.getStringWidth(I18n.format(m.getKey().getName().substring(0, 2)) + m.getValue() / 2);
                mc.fontRenderer.drawStringWithShadow(
                        I18n.format(m.getKey().getName()).substring(0, 2) + m.getValue(), -4 - w1 + 3, c * 10 - 1,
                        m.getKey() == Enchantments.VANISHING_CURSE || m.getKey() == Enchantments.BINDING_CURSE
                                ? 0xff5050 : 0xffb0e0);
                c--;

            }

            GlStateManager.scale(0.6F, 0.6F, 0.6F);
            String dur = item.getMaxDamage() - item.getItemDamage() + "";
            int color = MathHelper.hsvToRGB(((float) (item.getMaxDamage() - item.getItemDamage()) / item.getMaxDamage()) / 3.0F, 1.0F, 1.0F);
            if (item.isItemStackDamageable())
                mc.fontRenderer.drawStringWithShadow(dur, -8 - dur.length() * 3, 15, new Color(color >> 16 & 255, color >> 8 & 255, color & 255).getRGB());


        } catch (Exception ignored) {
        }
        glCleanup();
    }

    public static void glSetup(double x, double y, double z) {
        GlStateManager.pushMatrix();
        RenderManager renderManager = mc.getRenderManager();
        GlStateManager.translate(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.gameSettings.thirdPersonView == 2 ? -renderManager.playerViewX : renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.disableLighting();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
    }

    public static void glCleanup() {
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.enableDepth();
        GL11.glTranslatef(-.5f, 0, 0);
        GlStateManager.popMatrix();
    }
}
