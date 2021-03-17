package Method.Client.managers;

import Method.Client.Main;
import Method.Client.module.Module;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class SettingsManager {

    private final ArrayList<Setting> settings= new ArrayList<>();


    public Setting add(Setting in) {
        this.settings.add(in);
        return in;
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public ArrayList<Setting> getSettingsByMod(Module mod) {
        return (ArrayList<Setting>) this.settings.stream().filter(s -> s.getParentMod().equals(mod)).collect(Collectors.toList());
    }

    public void setSettingsByMod(Module mod, ArrayList<Setting> change) {
        for (Setting s : settings) {
            if (s.getParentMod().equals(mod)) {
                for (Setting Inputsetting : change) {
                    if (s.getName().equalsIgnoreCase(Inputsetting.getName())) {
                        s.setall(Inputsetting);
                    }
                }
            }
        }
    }

    public Setting getSettingByName(String name) {
        for (Setting set : getSettings()) {
            if (set.getName().equalsIgnoreCase(name)) {
                return set;
            }
        }
        System.err.println("[" + Main.NAME + "] Error Setting NOT found: '" + name + "'!");
        return null;
    }
}