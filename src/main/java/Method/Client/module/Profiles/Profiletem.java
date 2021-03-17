package Method.Client.module.Profiles;

import Method.Client.managers.FileManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static Method.Client.Main.setmgr;
import static Method.Client.module.ModuleManager.toggledModules;

public class Profiletem extends Module {
    public String name;


    public Profiletem(String name) {
        super(name, Keyboard.KEY_NONE, Category.PROFILES, name);
        this.name = name;
    }

    @Override
    public void setsave() {
        // Remove all Stored
        this.StoredModules.clear();
        // Set stored to toggled
        StoredModules.addAll(toggledModules);
        StoredModules.removeIf(module -> module.getCategory().equals(Category.PROFILES) || module.getCategory().equals(Category.ONSCREEN));

        //clear settings
        StoredSettings.clear();
        // add each setting to settings.
        for (Module storedModule : StoredModules) {
            for (Setting setting : setmgr.getSettingsByMod(storedModule)) {
                this.StoredSettings.add(new Setting(setting));
            }
        }
        FileManager.savePROFILES();
    }

    @Override
    public void setdelete() {
        StoredModules.clear();
        StoredSettings.clear();
    }

    @Override
    public void onEnable() {
        ArrayList<Module> remove = new ArrayList<>();
        toggledModules.forEach(module -> {
            if (!module.getCategory().equals(Category.PROFILES) && !module.getCategory().equals(Category.ONSCREEN)) {
                remove.add(module);
            }
        });
        remove.forEach(module -> {
            if (!module.getCategory().equals(Category.PROFILES) && !module.getCategory().equals(Category.ONSCREEN)) {
                module.setToggled(false);
            }
        });
        toggledModules.removeAll(remove);

        for (Module N : StoredModules) {
            if (!N.getCategory().equals(Category.PROFILES) && !N.getCategory().equals(Category.ONSCREEN)) {
                N.toggle();
                ArrayList<Setting> change = new ArrayList<>();
                for (Setting setting : StoredSettings) {
                    if (setting.getParentMod().equals(N))
                        change.add(setting);
                }
                setmgr.setSettingsByMod(N, change);
            }
        }
        this.toggle();
    }

}


