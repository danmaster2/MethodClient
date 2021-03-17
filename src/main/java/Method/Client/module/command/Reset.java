package Method.Client.module.command;

import Method.Client.managers.FileManager;
import Method.Client.managers.Setting;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.utils.visual.ChatUtils;

import static Method.Client.Main.setmgr;
import static Method.Client.module.ModuleManager.getModuleByName;

public class Reset extends Command {
    public Reset() {
        super("Reset");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            Module m = getModuleByName(args[0]);
            if (m != null) {
                if (m.isToggled())
                    m.toggle();

                if (setmgr.getSettingsByMod(m) != null) {
                    for (Setting SET : setmgr.getSettingsByMod(m)) {
                        setmgr.getSettings().remove(SET);
                    }
                    m.setup();
                    FileManager.SaveMods();
                    FileManager.saveframes();
                }
                ModuleManager.addModule(m);
                ChatUtils.message(m + " Returned to Factory");
            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Resets a module to factory defaults";
    }

    @Override
    public String getSyntax() {
        return "Reset <module>";
    }
}