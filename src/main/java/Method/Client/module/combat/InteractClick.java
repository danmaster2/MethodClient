package Method.Client.module.combat;


import Method.Client.managers.FriendManager;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InteractClick extends Module {
    public InteractClick() {
        super("InteractClick", Keyboard.KEY_NONE, Category.COMBAT, "InteractClick");
    }

    @Override
    public void onClientTick(ClientTickEvent event) {
        RayTraceResult object = Wrapper.INSTANCE.mc().objectMouseOver;
        if (object == null) {
            return;
        }
        if (object.typeOfHit == RayTraceResult.Type.ENTITY) {
            Entity entity = object.entityHit;
            if (entity instanceof EntityPlayer && !mc.player.isDead && mc.player.canEntityBeSeen(entity)) {
                EntityPlayer player = (EntityPlayer) entity;
                String ID = Utils.getPlayerName(player);
                if (Mouse.isButtonDown(2) && Wrapper.INSTANCE.mc().currentScreen == null) {
                    FriendManager.addFriend(ID);
                } else if (Mouse.isButtonDown(1) && Wrapper.INSTANCE.mc().currentScreen == null) {
                    FriendManager.removeFriend(ID);
                }
            }
        }
        super.onClientTick(event);
    }

}
