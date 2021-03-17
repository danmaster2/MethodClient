package Method.Client.managers;

import Method.Client.module.Module;
import Method.Client.utils.visual.ColorUtils;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Setting {

    private String name;
    private final Module parent;
    private final String mode;
    private ArrayList<String> options;
    private GuiScreen screen;
    private Setting Dependant = null;
    private boolean onlyint = false;
    private double index;

    private String selected;

    private String sval; // Selected String value for combo
    private boolean bval; // Selected Boolean value for switch
    private double dval;  //double value for slider
    private double min;  // min slider
    private double max;  // max slider
    private double saval; //color saturation
    private double brval;  // color brightness
    private double Alval; // color alpha

    public Setting(Setting setting) {
        this.name = setting.getName();
        this.parent = setting.getParentMod();
        this.mode = setting.getMode();
        this.options = setting.getOptions();
        this.screen = setting.getScreen();
        this.Dependant = setting.getDependant();
        this.onlyint = setting.onlyint;
        this.index = setting.index;
        this.selected = setting.getselected();

        this.dval = setting.getValDouble();
        this.min = setting.getMin();
        this.max = setting.getMax();
        this.saval = setting.getSat();
        this.brval = setting.getBri();
        this.Alval = setting.getAlpha();
        this.Dependant = setting.getDependant();

        this.sval = setting.getValString();
        this.bval = setting.getValBoolean();
    }


    public void setall(Setting inputsetting) {
        this.selected = inputsetting.getselected();
        this.sval = inputsetting.getValString();
        this.dval = inputsetting.getValDouble();
        this.bval = inputsetting.getValBoolean();
        this.min = inputsetting.getMin();
        this.max = inputsetting.getMax();
        this.saval = inputsetting.getSat();
        this.brval = inputsetting.getBri();
        this.Alval = inputsetting.getAlpha();
    }

    // Standard combo a,b,c
    public Setting(String name, Module parent, String sval, ArrayList<String> options) {
        this.name = name;
        this.parent = parent;
        this.sval = sval;
        this.options = options;
        this.mode = "Combo";
    }

    // Standard combo a,b,c
    public Setting(String name, Module parent, String sval, String... modes) {
        this.name = name;
        this.name = name;
        this.parent = parent;
        this.sval = sval;
        this.options = new ArrayList<>(Arrays.asList(modes));
        this.mode = "Combo";
    }

    // Standard check On,Off
    public Setting(String name, Module parent, boolean bval) {
        this.name = name;
        this.parent = parent;
        this.bval = bval;
        this.mode = "Check";
    }

    // Gui toggle
    public Setting(String name, Module parent, GuiScreen screen) {
        this.name = name;
        this.parent = parent;
        this.screen = screen;
        this.mode = "Screen";
    }

    // Standard Slider 1-20
    public Setting(String name, Module parent, double dval, double min, double max, boolean onlyint) {
        this.name = name;
        this.parent = parent;
        this.dval = dval;
        this.min = min;
        this.max = max;
        this.onlyint = onlyint;
        this.mode = "Slider";
    }

    // Standard Color RED
    public Setting(String name, Module parent, double HUE, double Saturation, double Brightness, double Alpha) {
        this.name = name;
        this.parent = parent;
        this.dval = HUE;  // HUE
        this.min = 0;   // If min is reached Rainbow = true;
        this.max = 1;
        this.saval = Saturation; //Saturation
        this.brval = Brightness; //Brightness
        this.Alval = Alpha; //Alpha
        this.mode = "Color";
    }

    // Standard Color Dependant
    public Setting(String name, Module parent, double HUE, double Saturation, double Brightness, double Alpha, Setting dependant, int index) {
        this.name = name;
        this.parent = parent;
        this.dval = HUE;  // HUE
        this.min = 0;   // If min is reached Rainbow = true;
        this.max = 1;
        this.saval = Saturation; //Saturation
        this.brval = Brightness; //Brightness
        this.Alval = Alpha; //Alpha
        this.Dependant = dependant;
        this.index = index;
        this.mode = "Color";
    }

    // Option for dependant slider!
    public Setting(String name, Module parent, double dval, double min, double max, boolean onlyint, Setting Dependant, int index) {
        this.name = name;
        this.parent = parent;
        this.dval = dval;
        this.min = min;
        this.max = max;
        this.Dependant = Dependant;
        this.onlyint = onlyint;
        this.index = index;
        this.mode = "Slider";
    }

    // Option for dependant boolean
    public Setting(String name, Module parent, boolean bval, Setting Dependant, int index) {
        this.name = name;
        this.parent = parent;
        this.bval = bval;
        this.Dependant = Dependant;
        this.index = index;
        this.mode = "Check";
    }

    // Option for dependant bool for string
    public Setting(String name, Module parent, boolean bval, Setting Dependant, String selected, int index) {
        this.name = name;
        this.parent = parent;
        this.selected = selected;
        this.bval = bval;
        this.Dependant = Dependant;
        this.index = index;
        this.mode = "Check";
    }

    public Setting(String name, Module parent, double dval, double min, double max, boolean onlyint, Setting Dependant, String selected, int index) {
        this.name = name;
        this.parent = parent;
        this.Dependant = Dependant;
        this.selected = selected;
        this.dval = dval;
        this.min = min;
        this.max = max;
        this.onlyint = onlyint;
        this.index = index;
        this.mode = "Slider";
    }


    public String getName() {
        return name;
    }

    public void setName(String nename) {
        this.name = nename;
    }

    final public double GetIndex() {
        return index;
    }

    public Module getParentMod() {
        return parent;
    }

    public String getValString() {
        return this.sval;
    }

    public void setValString(String in) {
        this.sval = in;
    }

    public ArrayList<String> getOptions() {
        return this.options;
    }

    public String getTooltip() {
        return "CTRL + Click For Exact Input";
    }

    public boolean getValBoolean() {
        return this.bval;
    }

    public void setValBoolean(boolean in) {
        this.bval = in;
    }

    public double getValDouble() {
        if (this.onlyint)
            this.dval = (int) dval;
        return this.dval;
    }

    public double getSat() {
        return this.saval;
    }

    public double getBri() {
        return this.brval;
    }

    public double getAlpha() {
        return this.Alval;
    }

    public int getcolor() {
        double saturation = saval; //saturation
        double brightness = brval; //brightness
        if (dval == 0)
            return ColorUtils.rainbow(saturation, brightness, Alval);

        int rgba = Color.HSBtoRGB((float) dval, (float) saturation, (float) brightness);
        float red = (rgba >> 16 & 0xFF) / 255.0F;
        float green = (rgba >> 8 & 0xFF) / 255.0F;
        float blue = (rgba & 0xFF) / 255.0F;

        Color c = new Color(red, green, blue, (float) Alval);
        return c.getRGB();
    }


    public void setValDouble(double in) {
        this.dval = in;
    }

    public void setScreen(GuiScreen in) {
        this.screen = in;
    }

    public void setsaturation(float in) {
        this.saval = in;
    }

    public void setbrightness(float in) {
        this.brval = in;
    }

    public void setAlpha(float in) {
        this.Alval = in;
    }

    public double getMin() {
        return this.min;
    }

    public GuiScreen getScreen() {
        return this.screen;
    }

    public String getMode() {
        return this.mode;
    }

    public Setting getDependant() {
        return this.Dependant;
    }

    public String getselected() {
        return this.selected;
    }

    public double getMax() {
        return this.max;
    }

    public boolean isCombo() {
        return this.mode.equalsIgnoreCase("Combo");
    }

    public boolean isCheck() {
        return this.mode.equalsIgnoreCase("Check");
    }

    public boolean isSlider() {
        return this.mode.equalsIgnoreCase("Slider");
    }

    public boolean isGui() {
        return this.mode.equalsIgnoreCase("Screen");
    }

    public boolean isColor() {
        return this.mode.equalsIgnoreCase("Color");
    }

    public boolean onlyInt() {
        return this.onlyint;
    }


}
