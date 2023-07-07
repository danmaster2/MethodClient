package Method.Client.utils.visual;

import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ColorUtils {

    public static Color rainbow() {
        long offset = 999999999999L;
        float hue = (float) (System.nanoTime() + offset) / 1.0E10f % 1.0f;
        return new Color((int) Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16));
    }

    public static Color wave(double wave, double satur, double bright) {
        float hue = (float) (System.nanoTime()) / 1.0E10f % 1.0f;
        hue = (float) (hue + (wave / 10));
        int color = Color.HSBtoRGB(hue, (float) satur, (float) bright);
        return new Color(color);
    }

    public static Color randomColor(boolean nice) {
        if (nice)
            return Color.getHSBColor(new Random().nextFloat(), (float) ThreadLocalRandom.current().nextDouble(0.6,  0.8), 0.9f);
        else
            return new Color((int) (Math.random() * 0x1000000));
    }

    public static void glColor(int color) {
        GlStateManager.color((float) (color >> 16 & 255) / 255F, (float) (color >> 8 & 255) / 255F, (float) (color & 255) / 255F, (float) (color >> 24 & 255) / 255F);
    }

    public static int rgbToInt(int r, int g, int b, int a) {
        return (r << 16) | (g << 8) | (b) | (a << 24);
    }

    static public float colorcalc(int c, int location) {
        return (c >> location & 0xFF) / 255.0F;
    }

    public static int rainbow(double saturation, double brightness, double alpha) {
        long offset = 999999999999L;
        float fade = 1.0f;
        float hue2 = (float) (System.nanoTime() + offset) / 1.0E10f % 1.0f;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue2, (float) saturation, (float) brightness)), 16);
        Color c = new Color((int) color);
        return new Color((float) c.getRed() / 255.0f * fade, (float) c.getGreen() / 255.0f * fade, (float) c.getBlue() / 255.0f * fade, (float) alpha).getRGB();
    }
}
