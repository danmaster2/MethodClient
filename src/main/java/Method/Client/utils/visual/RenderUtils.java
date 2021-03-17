package Method.Client.utils.visual;

import Method.Client.module.misc.ModSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static Method.Client.utils.visual.ColorUtils.colorcalc;
import static Method.Client.utils.visual.ColorUtils.glColor;
import static net.minecraft.client.Minecraft.getMinecraft;
import static org.lwjgl.opengl.GL11.*;

public class RenderUtils {
    protected static Minecraft mc = getMinecraft();
    private static final ICamera camera = new Frustum();


    public static void OpenGl() {
        // If you are here and really lost on some render stuff you can run the mc profiler to show potential lag stuff
        //   mc.profiler.startSection("Start-Method");

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();

        GlStateManager.depthMask(false);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL32.GL_DEPTH_CLAMP);

        // I dont need this but you may
        // GlStateManager.disableAlpha();


    }

    public static void ReleaseGl() {
        //mc.profiler.endSection();

        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        GlStateManager.depthMask(true);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL32.GL_DEPTH_CLAMP);
        GlStateManager.shadeModel(GL_FLAT);
        // I dont need this but you may
        // GlStateManager.enableAlpha();
    }

    public static void RenderBlock(String s, final AxisAlignedBB bb, int c, Double width) {

        if (!s.equalsIgnoreCase("Tracer")) {
            if (!ModSettings.Rendernonsee.getValBoolean()) {
                camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
                if (!camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, -10, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, 2500, bb.maxZ + mc.getRenderManager().viewerPosZ)))
                    return;
            }
        }

        OpenGl();
        GlStateManager.glLineWidth((float) (1.5f * (width + .0001)));

        float a = colorcalc(c, 24);
        float r = colorcalc(c, 16);
        float g = colorcalc(c, 8);
        float b = colorcalc(c, 0);

        switch (s) {
            case "Shape":
                Sphere sph = new Sphere();

                sph.setDrawStyle(GLU.GLU_SILHOUETTE);
                GlStateManager.color(r, g, b, a);
                GlStateManager.translate((bb.maxX + bb.minX) / 2, (bb.maxY + bb.minY) / 2, (bb.maxZ + bb.minZ) / 2);
                sph.draw(1, (int) ModSettings.Spherelines.getValDouble(), (int) ModSettings.SphereDist.getValDouble());
                break;
            case "Flat":
                RenderGlobal.renderFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ, r, g, b, a);
                break;
            case "FlatOutline":
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ, r, g, b, a);
                break;
            case "Full":
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, r, g, b, a);
                RenderGlobal.renderFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, r, g, b, a);
                break;
            case "Outline":
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, r, g, b, a);
                break;
            case "Beacon":
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, r, g, b, a);
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, (0 - bb.maxY) + 255, bb.maxZ, r, g, b, a / 2);
                RenderGlobal.renderFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, (0 - bb.maxY) + 255, bb.maxZ, r, g, b, a / 4);
                break;
            case "Tracer":
                GlStateManager.glBegin(GL11.GL_LINES);
                GlStateManager.color(r, g, b, a);
                Vec3d eyes = ActiveRenderInfo.getCameraPosition();
                GL11.glVertex3d(eyes.x, eyes.y, eyes.z);
                GL11.glVertex3d(bb.getCenter().x, bb.getCenter().y, bb.getCenter().z);
                GlStateManager.glEnd();
                break;
            case "Xspot":
                BufferBuilder BBuild2 = Tessellator.getInstance().getBuffer();

                BBuild2.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                BBuild2.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
                BBuild2.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
                BBuild2.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, 0).endVertex();
                BBuild2.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();

                Tessellator.getInstance().draw();
                break;
            default:
                break;
        }
        ReleaseGl();
    }


    public static AxisAlignedBB Standardbb(BlockPos pos) {
        double renderPosX = pos.getX() - mc.getRenderManager().viewerPosX;
        double renderPosY = pos.getY() - mc.getRenderManager().viewerPosY;
        double renderPosZ = pos.getZ() - mc.getRenderManager().viewerPosZ;
        return new AxisAlignedBB(renderPosX, renderPosY, renderPosZ, renderPosX + 1.0, renderPosY + 1.0, renderPosZ + 1.0);
    }

    public static AxisAlignedBB Boundingbb(Entity entity, double x, double y, double z, double x1, double y1, double z1) {
        double renderPosX = entity.getEntityBoundingBox().minX - mc.getRenderManager().viewerPosX;
        double renderPosY = entity.getEntityBoundingBox().minY - mc.getRenderManager().viewerPosY;
        double renderPosZ = entity.getEntityBoundingBox().minZ - mc.getRenderManager().viewerPosZ;
        double renderPosX2 = entity.getEntityBoundingBox().maxX - mc.getRenderManager().viewerPosX;
        double renderPosY2 = entity.getEntityBoundingBox().maxY - mc.getRenderManager().viewerPosY;
        double renderPosZ2 = entity.getEntityBoundingBox().maxZ - mc.getRenderManager().viewerPosZ;

        return new AxisAlignedBB(renderPosX + x, renderPosY + y, renderPosZ + z, renderPosX2 + x1, renderPosY2 + y1, renderPosZ2 + z1);
    }


    public static AxisAlignedBB Modifiedbb(BlockPos pos, int x, int y, int z) {
        double renderPosX = pos.getX() - mc.getRenderManager().viewerPosX;
        double renderPosY = pos.getY() - mc.getRenderManager().viewerPosY;
        double renderPosZ = pos.getZ() - mc.getRenderManager().viewerPosZ;
        return new AxisAlignedBB(renderPosX + x, renderPosY + y, renderPosZ + z, renderPosX + 1.0 + x, renderPosY + 1.0 + y, renderPosZ + 1.0 + z);
    }

    public static void RenderLine(List<Vec3d> List, int Color, double width, boolean valBoolean) {

        OpenGl();
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glLineWidth((float) width);
        ColorUtils.glColor(Color);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        RenderManager renderManager = mc.getRenderManager();


        for (Vec3d blockPos : List) {
            if (valBoolean) {
                BlockPos snap = new BlockPos(blockPos);
                GL11.glVertex3d(snap.x - renderManager.viewerPosX, snap.y - renderManager.viewerPosY, snap.z - renderManager.viewerPosZ);
            } else
                GL11.glVertex3d(blockPos.x - renderManager.viewerPosX, blockPos.y - renderManager.viewerPosY, blockPos.z - renderManager.viewerPosZ);
        }

        GL11.glEnd();
        RenderUtils.ReleaseGl();
        GL11.glDisable(GL13.GL_MULTISAMPLE);
    }


    public static Vec3d getInterpolatedRenderPos(Vec3d pos) {
        return new Vec3d(pos.x, pos.y, pos.z).subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
    }

    public static void SimpleNametag(Vec3d pos, String str) {
        RenderUtils.OpenGl();

        boolean isThirdPersonFrontal = mc.getRenderManager().options.thirdPersonView == 2;
        Vec3d interp = getInterpolatedRenderPos(pos);
        GlStateManager.translate(interp.x + .5, interp.y + 0.75F, interp.z + .5);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

        float m = (float) (Math.pow(1.04, mc.player.getDistance(pos.x, pos.y + 0.5F, pos.z)));
        GlStateManager.scale(m, m, m);

        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        int i = mc.fontRenderer.getStringWidth(str) / 2;

        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.enableTexture2D();
        mc.fontRenderer.drawStringWithShadow(str, -i, 9, Color.WHITE.getRGB());
        GlStateManager.disableTexture2D();
        GlStateManager.glNormal3f(0.0F, 0.0F, 0.0F);
        RenderUtils.ReleaseGl();
    }

    public static void drawRectOutline(double left, double top, double right, double bottom, double width, int color) {
        double l = left - width;
        double t = top - width;
        double r = right + width;
        double b = bottom + width;
        drawRectDouble(l, t, r, top, color);
        drawRectDouble(right, top, r, b, color);
        drawRectDouble(l, bottom, right, b, color);
        drawRectDouble(l, top, left, bottom, color);
    }

    public static void drawRectDouble(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        glColor(color);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }


}
