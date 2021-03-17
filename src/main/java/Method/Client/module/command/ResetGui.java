package Method.Client.module.command;


import Method.Client.clickgui.ClickGui;
import Method.Client.clickgui.component.Frame;
import Method.Client.utils.visual.ChatUtils;

public class ResetGui extends Command {
    public ResetGui() {
        super("ResetGui");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            int xOffset = 5;
            for (Frame frame : ClickGui.frames) {
                frame.setY(20);
                frame.setX(xOffset + 10);
                xOffset = xOffset + frame.getWidth();
            }
            ChatUtils.message("Guireset!");

        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }


    @Override
    public String getDescription() {
        return "ResetGui";
    }

    @Override
    public String getSyntax() {
        return "ResetGui";
    }
}