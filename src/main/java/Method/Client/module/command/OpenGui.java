package Method.Client.module.command;


import Method.Client.Main;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.util.text.TextFormatting;

public class OpenGui extends Command {
    public OpenGui() {
        super("Gui");
    }

    Thread t;

    @Override
    public void runCommand(String s, String[] args) {
        try {
            mc.displayGuiScreen(null);
            Thread t = new Thread(new ThreadDemo());

            t.start();
            mc.mouseHelper.ungrabMouseCursor();
            ChatUtils.message(TextFormatting.GOLD + "Tried to open Gui");


        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }


    @Override
    public String getDescription() {
        return "Opens gui";
    }

    @Override
    public String getSyntax() {
        return "gui";
    }

    private static class ThreadDemo implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mc.displayGuiScreen(Main.ClickGui);
        }
    }
}