package Method.Client.module.command;


import Method.Client.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class OpenFolder extends Command {
    public OpenFolder() {
        super("OpenFolder ");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            Desktop.getDesktop().open(Minecraft.getMinecraft().gameDir);
            ChatUtils.message("Local .Minecraft Folder Opened");
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }


    @Override
    public String getDescription() {
        return "Opens Folder for .minecraft";
    }

    @Override
    public String getSyntax() {
        return "OpenFolder";
    }
}