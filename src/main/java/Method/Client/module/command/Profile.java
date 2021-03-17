package Method.Client.module.command;

import Method.Client.clickgui.component.Frame;
import Method.Client.module.ModuleManager;
import Method.Client.module.Profiles.Profiletem;
import Method.Client.utils.visual.ChatUtils;

import static Method.Client.clickgui.ClickGui.frames;
import static Method.Client.module.ModuleManager.addModule;
import static Method.Client.module.ModuleManager.getModuleByName;

public class Profile extends Command {
    public Profile() {
        super("Profile");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if (args[0].equalsIgnoreCase("Remove")) {
                ModuleManager.toggledModules.remove(getModuleByName(args[1]));
                ModuleManager.modules.remove(getModuleByName(args[1]));
                ChatUtils.message("Removed Profile" + args[1]);
            }
            if (args[0].equalsIgnoreCase("Add")) {
                addModule(new Profiletem(args[1]));
                ChatUtils.message("Added Profile" + args[1]);
            }
            for (Frame frame : frames) {
                if (frame.getName().equalsIgnoreCase("PROFILES")) {
                    frame.updateRefresh();
                }
            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Modify Profile Add/Remove";
    }

    @Override
    public String getSyntax() {
        return "profile <Add/Remove> [Name]";
    }
}