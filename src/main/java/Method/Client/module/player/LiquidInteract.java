package Method.Client.module.player;


import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.EventCanCollide;
import Method.Client.utils.Patcher.Events.GetLiquidCollisionBoxEvent;
import org.lwjgl.input.Keyboard;

public class LiquidInteract extends Module {
    public LiquidInteract() {
        super("LiquidInteract", Keyboard.KEY_NONE, Category.PLAYER, "LiquidInteract");
    }


    @Override
    public void EventCanCollide(EventCanCollide event) {
        event.setCanceled(true);
    }

    @Override
    public void GetLiquidCollisionBoxEvent(GetLiquidCollisionBoxEvent event) {
        event.setSolidCollisionBox();
    }


}
