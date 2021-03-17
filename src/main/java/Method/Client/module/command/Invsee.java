package Method.Client.module.command;


import Method.Client.utils.visual.ChatUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;

public class Invsee extends Command {
    public Invsee() {
        super("Invsee");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if(mc.player.capabilities.isCreativeMode)
            {
                ChatUtils.error("Must Be Creative");
                return;
            }
            String id = args[0];
            for (final EntityPlayer entityPlayer : mc.world.playerEntities) {
                if (entityPlayer.getDisplayNameString().equalsIgnoreCase(id)) {
                    mc.displayGuiScreen(new GuiInventory(entityPlayer));
                    return;
                }
            }
            ChatUtils.error("Could not find player " + id);

        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "See inv of other players";
    }

    @Override
    public String getSyntax() {
        return "Invsee <Player>";
    }
}