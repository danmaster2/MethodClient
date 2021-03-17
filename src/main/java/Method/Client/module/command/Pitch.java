package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;

import java.math.BigInteger;

public class Pitch extends Command {
    public Pitch() {
        super("Pitch");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            long Pitch = new BigInteger(args[0]).longValue();
            mc.player.rotationPitch=Pitch;

                ChatUtils.message("Pitch =" + Pitch );
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Set Pitch";
    }

    @Override
    public String getSyntax() {
        return "Pitch <Num>";
    }
}