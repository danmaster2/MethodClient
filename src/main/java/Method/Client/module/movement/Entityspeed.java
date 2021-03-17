
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.directionSpeed;
import static Method.Client.utils.Utils.isMovinginput;

public class Entityspeed extends Module {


    Setting mode = setmgr.add(new Setting("Mode", this, "Vanilla", "Vanilla", "Glide", "Tp", "TpUpdate"));
    Setting speed = setmgr.add(new Setting("Speed", this, 1, 0.01, 2, false));
    Setting foodview = setmgr.add(new Setting("foodbar view", this, true));
    Setting antiStuck = setmgr.add(new Setting("antiStuck", this, true));
    Setting Jump = setmgr.add(new Setting("Horse Jump", this, false));
    Setting isAirBorne = setmgr.add(new Setting("Airmode Mode", this, "Default", "Default", "False", "True"));
    Setting Modifyfalldist = setmgr.add(new Setting("No Falldist", this, false));
    Setting Yawlock = setmgr.add(new Setting("Yawlock", this, false));
    Setting boatInputsfalse = setmgr.add(new Setting("Dont Row Boat", this, false));


    public Entityspeed() {
        super("EntitySpeed", Keyboard.KEY_NONE, Category.MOVEMENT, "Entity Speed + Control");
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Jump.getValBoolean()) {
            mc.player.horseJumpPower = 1;
            mc.player.horseJumpPowerCounter = -10;
        }
        if (mc.world != null && mc.player.getRidingEntity() != null) {
            Entity riding = mc.player.getRidingEntity();
            if (Modifyfalldist.getValBoolean()) {
                riding.fallDistance = (float) 0;
            }

            riding.isAirBorne = isAirBorne.getValString().equalsIgnoreCase("Default") ? riding.isAirBorne : isAirBorne.getValString().equalsIgnoreCase("True");


            if (mc.player.isRiding()) {
                this.steerEntity(riding);
            }
        }
    }

    private void steerEntity(Entity entity) {
        final double[] directionSpeedVanilla = directionSpeed(speed.getValDouble());

        if ((mode.getValString().equalsIgnoreCase("Glide") && isMovinginput()) || mode.getValString().equalsIgnoreCase("Vanilla")) {
            entity.motionX = directionSpeedVanilla[0];
            entity.motionZ = directionSpeedVanilla[1];
        }
        if (mode.getValString().equalsIgnoreCase("Tp")) {
            entity.setPosition(entity.posX + directionSpeedVanilla[0], entity.posY, entity.posZ + directionSpeedVanilla[1]);
        }
        if (mode.getValString().equalsIgnoreCase("TpUpdate")) {
            entity.setPositionAndRotation(entity.posX + directionSpeedVanilla[0], entity.posY, entity.posZ + directionSpeedVanilla[1], entity.rotationYaw, entity.rotationPitch);
        }
        if (isBorderingChunk(entity, directionSpeedVanilla[0], directionSpeedVanilla[1])) {
            entity.motionX = 0;
            entity.motionZ = 0;
        }
        if (Yawlock.getValBoolean())
            entity.rotationYaw = (mc.player.rotationYaw);

        if (entity instanceof EntityBoat && boatInputsfalse.getValBoolean()) {
            ((EntityBoat) entity).updateInputs(false, false, false, false);
        }
    }

    public  boolean isBorderingChunk(Entity entity, double motX, double motZ) {
        return antiStuck.getValBoolean() && mc.world.getChunk((int) (entity.posX + motX) >> 4, (int) (entity.posZ + motZ) >> 4) instanceof EmptyChunk;
    }

    @Override
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        GuiIngameForge.renderFood = foodview.getValBoolean();
    }

}
