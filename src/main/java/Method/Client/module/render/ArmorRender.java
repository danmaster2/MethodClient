package Method.Client.module.render;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;


public class ArmorRender extends Module {

    public static Setting enableColoredGlint;
    public static Setting useRuneTexture;
    public static Setting CustomColor;
    public static Setting Color;
    public static Setting RenderArmor;


    public ArmorRender() {
        super("ArmorRender", Keyboard.KEY_NONE, Category.RENDER, "ArmorRender");
    }

    @Override
    public void setup() {
        setmgr.add(enableColoredGlint = new Setting("enableColoredGlint", this, false));
        setmgr.add(CustomColor = new Setting("CustomColor", this, false));
        setmgr.add(Color = new Setting("OverlayColor", this, 0, 1, 1, .56, CustomColor, 2));
        setmgr.add(useRuneTexture = new Setting("useRuneTexture", this, false));
        setmgr.add(RenderArmor = new Setting("RenderArmor", this, true));
    }

    public static int BANE_OF_ARTHROPODS = 0xcc00ff;
    public static int FIRE_ASPECT = 0xff4000;
    public static int KNOCKBACK = 0x6600ff;
    public static int LOOTING = 0xffe066;
    public static int SHARPNESS = 0xff9933;
    public static int SMITE = 0x00ccff;
    public static int SWEEPING = 0xccff33;
    public static int UNBREAKING = 0x00cc66;

    // Bow enchantments
    public static int FLAME = 0xff4000;
    public static int INFINITY = 0xcc00ff;
    public static int POWER = 0xff9933;
    public static int PUNCH = 0x6600ff;

    // Tool enchantments
    public static int EFFICIENCY = 0x33ccff;
    public static int FORTUNE = 0xffe066;
    public static int SILK_TOUCH = 0xccff99;

    // Fishing rod enchantments
    public static int LUCK_OF_THE_SEA = 0xffe066;
    public static int LURE = 0x33ccff;

    // Armor enchantments
    public static int AQUA_AFFINITY = 0x3366ff;
    public static int BLAST_PROTECTION = 0xcc6699;
    public static int DEPTH_STRIDER = 0x6666ff;
    public static int FEATHER_FALLING = 0xccff99;
    public static int FIRE_PROTECTION = 0xff4000;
    public static int FROST_WALKER = 0xccffff;
    public static int MENDING = 0xffe066;
    public static int PROJECTILE_PROTECTION = 0xcc99ff;
    public static int PROTECTION = 0x00cc99;
    public static int RESPIRATION = 0x3366ff;
    public static int THORNS = 0xff9933;

    // Curses
    public static int VANISHING_CURSE = 0x6600cc;
    public static int BINDING_CURSE = 0xffffff;

    public static int DEFAULT = -8372020;


}

