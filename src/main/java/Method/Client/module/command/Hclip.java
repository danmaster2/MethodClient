package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;

import java.math.BigInteger;

public class Hclip extends Command {
    public Hclip() {
        super("Hclip");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            long dir = new BigInteger(args[0]).longValue();
            long dir2 = new BigInteger(args[1]).longValue();

            double y = mc.player.posY;
            float yaw = mc.player.rotationYaw;

            double newX = -Math.sin(Math.toRadians(yaw)) * dir + mc.player.posX;
            double newZ = Math.cos(Math.toRadians(yaw)) * dir2 + mc.player.posZ;

            mc.player.setPosition(newX, y, newZ);

                ChatUtils.message("Zoomed " + dir + " blocks.");
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Teleports you In the H.";
    }

    @Override
    public String getSyntax() {
        return "Hclip <X> <Z>";
    }
}