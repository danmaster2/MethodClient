package Method.Client.module.command;

import Method.Client.managers.FriendManager;
import Method.Client.utils.Utils;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;

public class Friend extends Command {
    public Friend() {
        super("friend");
    }


    @Override
    public void runCommand(String s, String[] args) {
        try {
            if (args[0].equalsIgnoreCase("add")) {
                if (args[1].equalsIgnoreCase("all")) {
                    for (Object object : mc.world.loadedEntityList) {
                        if (object instanceof EntityPlayer) {
                            EntityPlayer player = (EntityPlayer) object;
                            if (!player.isInvisible())
                                FriendManager.addFriend(Utils.getPlayerName(player));
                        }
                    }
                } else {
                    FriendManager.addFriend(args[1]);
                }
            }
            if (args[0].equalsIgnoreCase("list")) {
                ChatUtils.message(FriendManager.friendsList.toString());
            } else if (args[0].equalsIgnoreCase("remove")) {
                FriendManager.removeFriend(args[1]);
            } else if (args[0].equalsIgnoreCase("clear")) {
                FriendManager.clear();
            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Friend manager.";
    }

    @Override
    public String getSyntax() {
        return "friend <add/remove/list/clear> <nick>";
    }
}