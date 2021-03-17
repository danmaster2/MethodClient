package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;

import java.math.BigInteger;

public class Yaw extends Command {
    public Yaw() {
        super("Yaw");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            long Yaw = new BigInteger(args[0]).longValue();
            mc.player.rotationYaw=Yaw;

                ChatUtils.message("Yaw =" + Yaw );
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Set Yaw";
    }

    @Override
    public String getSyntax() {
        return "Yaw <Num>";
    }
}