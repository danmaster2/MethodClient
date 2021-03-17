package Method.Client.managers;

import Method.Client.Main;
import Method.Client.clickgui.component.Frame;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.module.Profiles.Profiletem;
import Method.Client.utils.Screens.Custom.Search.SearchGuiSettings;
import Method.Client.utils.Screens.Custom.Xray.XrayGuiSettings;
import Method.Client.utils.system.Wrapper;
import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Method.Client.Main.setmgr;
import static Method.Client.clickgui.ClickGui.frames;

public class FileManager {

    private static final Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();

    private static final JsonParser jsonParser = new JsonParser();
    public static final File SaveDir = new File(String.format("%s%s%s-%s-%s%s", Wrapper.INSTANCE.mc().gameDir, File.separator, Main.NAME, Main.MCVERSION, Main.VERSION, File.separator));

    private static final File Mods = new File(SaveDir, "mods.json");
    private static final File XRAYDATA = new File(SaveDir, "xraydata.json");
    private static final File FRIENDS = new File(SaveDir, "friends.json");
    private static final File ONSCREEN = new File(SaveDir, "onscreen.json");
    private static final File SEARCH = new File(SaveDir, "search.json");
    private static final File PROFILES = new File(SaveDir, "profiles.json");


    public FileManager() {
        if (!SaveDir.exists()) {
            SaveDir.mkdir();
        }
        if (!Mods.exists()) {
            SaveMods();
        }
        if (!ONSCREEN.exists()) {
            saveframes();
        } else {
            Loadpos();
        }

        if (!XRAYDATA.exists()) {
            saveXRayData();
        } else {
            loadXRayData();
        }
        if (!PROFILES.exists()) {
            savePROFILES();
        }

        if (!SEARCH.exists()) {
            saveSearchData();
        } else {
            loadSearchData();
        }

        if (!FRIENDS.exists()) {
            saveFriends();
        } else {
            loadFriends();
        }
    }

    public static void loadPROFILES() {
        try {
            BufferedReader loadJson = new BufferedReader(new FileReader(PROFILES));
            JsonObject moduleJason = (JsonObject) jsonParser.parse(loadJson);
            loadJson.close();

            for (Map.Entry<String, JsonElement> entry : moduleJason.entrySet()) {

                Module m = new Profiletem(entry.getKey());
                ModuleManager.addModule(m);


                JsonObject jsonMod = (JsonObject) entry.getValue();
                m.setKey(jsonMod.get("key").getAsInt());
                m.visible = (jsonMod.get("Visible").getAsBoolean());
                ArrayList<Module> Modstore = new ArrayList<>();

                for (Module module : ModuleManager.modules) {
                    if (!module.getCategory().equals(Category.ONSCREEN) && !module.getCategory().equals(Category.PROFILES))
                        if (jsonMod.has(module.getName()))
                            Modstore.add(module);
                }

                m.setStoredModules(Modstore);
                ArrayList<Setting> Setstore = new ArrayList<>();

                for (Module module : Modstore) {
                    for (Setting s : setmgr.getSettingsByMod(module)) {
                        if (s.getMode().equals("Slider")) {
                            s.setValDouble(jsonMod.get(s.getName()).getAsDouble());
                        }
                        if (s.getMode().equals("Check")) {
                            s.setValBoolean(jsonMod.get(s.getName()).getAsBoolean());
                        }
                        if (s.getMode().equals("Combo")) {
                            s.setValString(jsonMod.get(s.getName()).getAsString());
                        }
                        if (s.getMode().equals("Color")) {
                            s.setValDouble(jsonMod.get(s.getName() + "c").getAsDouble());
                            s.setsaturation((float) jsonMod.get(s.getName() + "s").getAsDouble());
                            s.setbrightness((float) jsonMod.get(s.getName() + "b").getAsDouble());
                            s.setAlpha((float) jsonMod.get(s.getName() + "a").getAsDouble());
                        }
                        Setstore.add(s);
                    }
                }

                m.setStoredSettings(Setstore);

            }

        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePROFILES() {
        try {
            JsonObject json = new JsonObject();
            for (Module m : ModuleManager.getModulesInCategory(Category.PROFILES)) {

                JsonObject JsonMod = new JsonObject();

                JsonMod.addProperty("key", m.getKey());
                JsonMod.addProperty("Visible", m.visible);

                json.add(m.getName(), JsonMod);

                for (Setting s : m.getStoredSettings()) {
                    if (s.getMode().equals("Slider")) {
                        JsonMod.addProperty(s.getName(), s.getValDouble());
                    }
                    if (s.getMode().equals("Check")) {
                        JsonMod.addProperty(s.getName(), s.getValBoolean());
                    }
                    if (s.getMode().equals("Combo")) {
                        JsonMod.addProperty(s.getName(), s.getValString());
                    }
                    if (s.getMode().equals("Color")) {
                        JsonMod.addProperty(s.getName() + "c", s.getValDouble());
                        JsonMod.addProperty(s.getName() + "s", s.getSat());
                        JsonMod.addProperty(s.getName() + "b", s.getBri());
                        JsonMod.addProperty(s.getName() + "a", s.getAlpha());
                    }
                }
                for (Module storedModule : m.getStoredModules()) {
                    JsonMod.addProperty(storedModule.getName(), "Toggled");
                }
            }
            PrintWriter saveJson = new PrintWriter(new FileWriter(PROFILES));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void SaveMods() {
        try {
            JsonObject json = new JsonObject();

            ModuleManager.getModules().forEach(module -> Save(json, module));

            PrintWriter saveJson = new PrintWriter(new FileWriter(Mods));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void Save(JsonObject json, Module m) {
        JsonObject JsonMod = new JsonObject();
        JsonMod.addProperty("toggled", m.isToggled());
        JsonMod.addProperty("key", m.getKey());
        JsonMod.addProperty("Visible", m.visible);
        json.add(m.getName(), JsonMod);

        for (Setting s : setmgr.getSettingsByMod(m)) {
            if (s.getMode().equals("Slider")) {
                JsonMod.addProperty(s.getName(), s.getValDouble());
            }
            if (s.getMode().equals("Check")) {
                JsonMod.addProperty(s.getName(), s.getValBoolean());
            }
            if (s.getMode().equals("Combo")) {
                JsonMod.addProperty(s.getName(), s.getValString());
            }
            if (s.getMode().equals("Color")) {
                JsonMod.addProperty(s.getName() + "c", s.getValDouble());
                JsonMod.addProperty(s.getName() + "s", s.getSat());
                JsonMod.addProperty(s.getName() + "b", s.getBri());
                JsonMod.addProperty(s.getName() + "a", s.getAlpha());
            }
        }
    }

    public static void LoadMods() {
        try {
            BufferedReader loadJson = new BufferedReader(new FileReader(Mods));
            JsonObject moduleJason = (JsonObject) jsonParser.parse(loadJson);
            loadJson.close();

            for (Map.Entry<String, JsonElement> entry : moduleJason.entrySet()) {
                Module mods = ModuleManager.getModuleByName(entry.getKey());
                Load(entry, mods);
            }

        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

    }

    private static void Load(Map.Entry<String, JsonElement> entry, Module m) {
        if (m != null) {
            JsonObject jsonMod = (JsonObject) entry.getValue();
            m.setKey(jsonMod.get("key").getAsInt());
            m.visible = (jsonMod.get("Visible").getAsBoolean());
            for (Setting s : setmgr.getSettingsByMod(m)) {
                if (s.getMode().equals("Slider")) {
                    s.setValDouble(jsonMod.get(s.getName()).getAsDouble());
                }
                if (s.getMode().equals("Check")) {
                    s.setValBoolean(jsonMod.get(s.getName()).getAsBoolean());
                }
                if (s.getMode().equals("Combo")) {
                    s.setValString(jsonMod.get(s.getName()).getAsString());
                }
                if (s.getMode().equals("Color")) {
                    s.setValDouble(jsonMod.get(s.getName() + "c").getAsDouble());
                    s.setsaturation((float) jsonMod.get(s.getName() + "s").getAsDouble());
                    s.setbrightness((float) jsonMod.get(s.getName() + "b").getAsDouble());
                    s.setAlpha((float) jsonMod.get(s.getName() + "a").getAsDouble());
                }
            }
            if (jsonMod.get("toggled").getAsBoolean()) {
                ModuleManager.FileManagerLoader.add(m);
                m.setToggled(true);
            }

        }
    }


    public static void loadSearchData() {
        try {
            BufferedReader loadJson = new BufferedReader(new FileReader(SEARCH));
            JsonArray json = (JsonArray) jsonParser.parse(loadJson);
            loadJson.close();
            SearchGuiSettings.fromJson(json);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSearchData() {
        try {
            JsonElement json = SearchGuiSettings.toJson();
            PrintWriter saveJson = new PrintWriter(new FileWriter(SEARCH));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void Loadpos() {
        try {
            BufferedReader loadJson = new BufferedReader(new FileReader(ONSCREEN));
            JsonObject moduleJason = (JsonObject) jsonParser.parse(loadJson);
            loadJson.close();

            for (Map.Entry<String, JsonElement> entry : moduleJason.entrySet()) {
                JsonObject jsonMod = (JsonObject) entry.getValue();
                for (Frame frame : frames) {
                    if (entry.getKey().equals(frame.getName())) {
                        frame.setX(jsonMod.get("x").getAsInt());
                        frame.setY(jsonMod.get("y").getAsInt());
                        frame.setOpen(jsonMod.get("open").getAsBoolean());
                    }
                }
            }

        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

    }

    public static void loadFriends() {
        final List<String> friends = read(FRIENDS);
        for (String name : friends) {
            FriendManager.addFriend(name);
        }
    }

    public static void saveXRayData() {
        try {
            JsonElement json = XrayGuiSettings.toJson();
            PrintWriter saveJson = new PrintWriter(new FileWriter(XRAYDATA));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadXRayData() {
        try {
            BufferedReader loadJson = new BufferedReader(new FileReader(XRAYDATA));
            JsonArray json = (JsonArray) jsonParser.parse(loadJson);
            loadJson.close();

            XrayGuiSettings.fromJson(json);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveframes() {
        try {
            JsonObject json = new JsonObject();
            for (Frame frame : frames) {
                JsonObject jsonHack = new JsonObject();
                jsonHack.addProperty("x", frame.getX());
                jsonHack.addProperty("y", frame.getY());
                jsonHack.addProperty("open", frame.isOpen());
                json.add(frame.getName(), jsonHack);
            }
            PrintWriter saveJson = new PrintWriter(new FileWriter(ONSCREEN));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveFriends() {
        write(FRIENDS, FriendManager.friendsList, true, true);
    }

    public static void write(File outputFile, List<String> writeContent, boolean newline, boolean overrideContent) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputFile, !overrideContent));
            for (final String outputLine : writeContent) {
                writer.write(outputLine);
                writer.flush();
                if (newline) {
                    writer.newLine();
                }
            }
        } catch (Exception ex) {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static List<String> read(File inputFile) {
        ArrayList<String> readContent = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = reader.readLine()) != null) {
                readContent.add(line);
            }
        } catch (Exception ex) {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ignored) {
            }
        }
        return readContent;
    }
}
