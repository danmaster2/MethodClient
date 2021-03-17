package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.proxy.Overrides.EntityRenderMixin;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;


public class NoEffect extends Module {


    Setting hurtcam = setmgr.add( new Setting("hurtcam", this, false));
    Setting Levitate = setmgr.add( new Setting("Levitate", this, false));
    Setting weather = setmgr.add( new Setting("weather", this, false));
    Setting Time = setmgr.add( new Setting("Time", this, 0, 0, 18000, true));
    Setting Settime = setmgr.add( new Setting("Settime", this, false));
    Setting fire = setmgr.add( new Setting("fire", this, false));
    Setting push = setmgr.add( new Setting("push", this, false));
    Setting NoVoid = setmgr.add( new Setting("NoVoid", this, false));

    static public Setting NoScreenEvents;      //DONE

    public NoEffect() {
        super("NoEffect", Keyboard.KEY_NONE, Category.PLAYER, "Prevent effects such as weather");
    }

    @Override
    public void setup() {
        setmgr.add(NoScreenEvents = new Setting("NoScreenEvents", this, false));
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        return !(packet instanceof SPacketTimeUpdate) || !Settime.getValBoolean();
    }


    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {

        EntityRenderMixin.Camswitch = !hurtcam.getValBoolean();

        if (weather.getValBoolean()) {
            mc.world.getWorldInfo().setRaining(false);
            mc.world.setRainStrength(0.0f);
            mc.world.getWorldInfo().setThunderTime(0);
            mc.world.getWorldInfo().setThundering(false);
        }
        if (push.getValBoolean())
            mc.player.entityCollisionReduction = 1.0F;

        if (Levitate.getValBoolean())
            if (mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(25))))
                mc.player.removeActivePotionEffect(Potion.getPotionById(25));

        if (NoVoid.getValBoolean()) {
            if (mc.player.posY <= 0.5D) {
                mc.player.moveVertical = 10.0F;
                mc.player.jump();
            }
            mc.player.motionY += .1;
        }

    }

    @Override
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (mc.player.isBurning() && fire.getValBoolean()) {
            mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
            event.getEntityLiving().extinguish();
            mc.player.setFire(0);

        }
        if (Settime.getValBoolean()) {
            mc.world.setWorldTime((long) Time.getValDouble());
        }
    }


}
