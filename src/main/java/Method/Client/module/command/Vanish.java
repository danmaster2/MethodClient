package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import net.minecraft.entity.Entity;

public class Vanish extends Command {
    public Vanish() {
        super("Vanish");
    }

    private static Entity vehicle;

    @Override
    public void runCommand(String s, String[] args) {
        try {

            if (mc.player.getRidingEntity() != null && vehicle == null) {
                vehicle = mc.player.getRidingEntity();
                mc.player.dismountRidingEntity();
                mc.world.removeEntityFromWorld(vehicle.getEntityId());
                ChatUtils.message("Vehicle " + vehicle.getName() + " removed.");
            } else if (vehicle != null) {
                vehicle.isDead = false;
                mc.world.addEntityToWorld(vehicle.getEntityId(), vehicle);
                mc.player.startRiding(vehicle, true);
                ChatUtils.message("Vehicle " + vehicle.getName() + " created.");
                vehicle = null;
            } else {
                ChatUtils.message("No Vehicle.");
            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }


    @Override
    public String getDescription() {
        return "Vanish in a entity";
    }

    @Override
    public String getSyntax() {
        return "Vanish  ";
    }
}